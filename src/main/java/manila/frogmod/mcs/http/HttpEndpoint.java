package manila.frogmod.mcs.http;

import manila.frogmod.Config;
import manila.frogmod.FrogMod;
import manila.frogmod.mcs.APIUriHandler;
import manila.frogmod.mcs.Endpoint;
import manila.frogmod.mcs.Message;
import manila.frogmod.mcs.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import manila.frogmod.mcs.simpleHttp.SimpleHttpServer;
import org.jdeferred.Promise;

import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * Created by swordfeng on 16-11-18.
 */
public class HttpEndpoint extends Endpoint {

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workGroup = null;
    private Channel channel = null;
    private int mPort;
    private String mRemoteAddr;
    private int mRemotePort;
    private MessageHandler mHandler;

    private static Logger logger = Logger.getLogger(FrogMod.MODID);

    public HttpEndpoint(int port, String remoteAddr, int remotePort, MessageHandler handler) {
        super(handler);
        mPort = port;
        mRemoteAddr = remoteAddr;
        mRemotePort = remotePort;
        mHandler = handler;
    }

    @Override
    public void start() throws InterruptedException {
        assert(channel == null);
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        // Create Netty HTTP Server
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new HttpRequestDecoder())
                                .addLast(new HttpResponseEncoder())
                                .addLast(new HttpServerHandler(mHandler));
                    }
                });
        try {
            channel = bootstrap.bind(mPort).sync().channel();
        } catch (InterruptedException e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            bossGroup = workGroup = null;
            throw e;
        }
    }

    @Override
    public void stop() {
        assert (channel != null);
        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "HTTP Server is not closed correctly");
        }
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        bossGroup = workGroup = null;
        channel = null;
    }

    @Override
    public Promise<Message, Exception, Object> send(Message message) {

        return null;
    }
}
