package com.datrat.tiab.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class TiabConfig {
    public static int maxTimeRatePower = 8;
    public static int eachUseDuration = 30;
    public static int averageUpdateRandomTick = 1365;
    public static int maxStoredTime = 622080000;
    public static String[] apiAccessBlacklist = new String[0];

    private static Set<String> blacklist = new HashSet<String>();

    private TiabConfig() {
    }

    public static void load(File file) {
        Configuration config = new Configuration(file);
        try {
            config.load();
            maxTimeRatePower = config.getInt("maxTimeRatePower", Configuration.CATEGORY_GENERAL, 8, 1, 12,
                    "Maximum time rate power. The maximum displayed speed is 2^this value.");
            eachUseDuration = config.getInt("eachUseDuration", Configuration.CATEGORY_GENERAL, 30, 1, 60,
                    "Duration in seconds for each bottle use.");
            averageUpdateRandomTick = config.getInt("averageUpdateRandomTick", Configuration.CATEGORY_GENERAL, 1365, 600, 2100,
                    "Average random tick interval used by accelerated random-tick blocks.");
            maxStoredTime = config.getInt("maxStoredTime", Configuration.CATEGORY_GENERAL, 622080000, 0, Integer.MAX_VALUE,
                    "Maximum stored time in ticks.");
            apiAccessBlacklist = config.getStringList("apiAccessBlacklist", Configuration.CATEGORY_GENERAL, new String[0],
                    "Mod ids that may read the API but may not mutate TIAB state.");
            blacklist = new HashSet<String>(Arrays.asList(apiAccessBlacklist));
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static boolean isApiBlacklisted(String modId) {
        return blacklist.contains(modId);
    }
}
