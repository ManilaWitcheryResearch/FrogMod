package manila.frogmod.mcs;

import java.util.Optional;

/**
 * Created by swordfeng on 16-11-18.
 */
public abstract class MessageHandler {
    public abstract Optional<? extends Message> onMessage(Message message);
}
