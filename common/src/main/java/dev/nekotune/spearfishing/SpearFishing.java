package dev.nekotune.spearfishing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.nekotune.spearfishing.shared.logic.FishermanTrades;
import dev.nekotune.spearfishing.shared.logic.SpearBehavior;
import dev.nekotune.spearfishing.shared.registry.ModRegistries;

public class SpearFishing {
    public static final String MOD_ID = "spearfishing";
    public static final String MOD_NAME = "Spear Fishing";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        ModRegistries.init();
        FishermanTrades.init();
        SpearBehavior.init();
    }
}
