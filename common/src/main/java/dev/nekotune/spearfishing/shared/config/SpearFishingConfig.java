package dev.nekotune.spearfishing.shared.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configs using NeoForge's config API
 */
public final class SpearFishingConfig {

    /** Registered as {@code ModConfig.Type.STARTUP} */
    public static final ModConfigSpec STARTUP_SPEC;

    /** Registered as {@code ModConfig.Type.COMMON} */
    public static final ModConfigSpec SPEC;

    // Startup
    public static final ModConfigSpec.IntValue SPEAR_DURABILITY;

    // Common
    public static final ModConfigSpec.DoubleValue SPEAR_PREY_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue SPEAR_WATER_INERTIA;
    public static final ModConfigSpec.BooleanValue SPEAR_QUICK_PICKUP;
    public static final ModConfigSpec.BooleanValue FISHERMEN_SELL_SPEARS;

    static {
        final ModConfigSpec.Builder startup = new ModConfigSpec.Builder();

        // Read when items are registered, which is before common configs load
        SPEAR_DURABILITY = startup
                .comment("Durability of spears.")
                .translation("config.spearfishing.spear_durability")
                .gameRestart()
                .defineInRange("spear_durability", 64, 1, Integer.MAX_VALUE);

        STARTUP_SPEC = startup.build();
    }

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        SPEAR_PREY_DAMAGE_MULTIPLIER = builder
                .comment("Multiplier applied to damage dealt to fish and squid.")
                .translation("config.spearfishing.spear_prey_damage_multiplier")
                .defineInRange("spear_prey_damage_multiplier", 3.0D, 1.0D, Double.MAX_VALUE);

        SPEAR_WATER_INERTIA = builder
                .comment("Relative speed of thrown spears underwater.",
                        "Lower values make spears slow down faster in water.",
                        "A value of 1 makes spears travel through water as if it were air; a value of 0 makes spears instantly stop in water.")
                .translation("config.spearfishing.spear_water_inertia")
                .defineInRange("spear_water_inertia", 0.9D, 0.0D, 1.0D);

        SPEAR_QUICK_PICKUP = builder
                .comment(
                        "Whether items dropped by mobs should be instantly added to the player's inventory when using a spear.")
                .translation("config.spearfishing.spear_quick_pickup")
                .define("spear_quick_pickup", true);

        FISHERMEN_SELL_SPEARS = builder
                .comment("Whether fisherman villagers offer spears as a trade.")
                .translation("config.spearfishing.fishermen_sell_spears")
                .define("fisherman_sells_spears", true);

        SPEC = builder.build();
    }

    private SpearFishingConfig() {
    }
}
