package manila.frogmod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * Created by swordfeng on 16-11-18.
 */
public class ConfigGui extends GuiConfig {
    static Config config;
    public ConfigGui(GuiScreen parent) {
        super(parent,
                new ConfigElement(Config.getInstance().configFile.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                "FrogMod", false, false, GuiConfig.getAbridgedConfigPath(Config.getInstance().configFile.toString()));
        config = Config.getInstance();
    }
}
