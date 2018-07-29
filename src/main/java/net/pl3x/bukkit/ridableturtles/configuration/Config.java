package net.pl3x.bukkit.ridableturtles.configuration;

import net.pl3x.bukkit.ridableturtles.RidableTurtles;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static String LANGUAGE_FILE = "lang-en.yml";
    public static float SPEED_LAND = 1.0F;
    public static float SPEED_WATER = 1.0F;

    public static void reload() {
        RidableTurtles plugin = RidableTurtles.getPlugin(RidableTurtles.class);
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");
        SPEED_LAND = (float) config.getDouble("speed-modifiers.on-land", 1.0D);
        SPEED_WATER = (float) config.getDouble("speed-modifiers.in-water", 1.0D);
    }
}
