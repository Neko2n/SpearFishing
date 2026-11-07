package dev.nekotune.spearfishing.shared.logic;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import dev.nekotune.spearfishing.shared.registry.content.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;

public final class BuildCreativeTabs {

    /**
     * ResourceKeys copied from {@link CreativeModeTabs}
     */
    public static enum Tabs {
        COMBAT("combat"),
        TOOLS_AND_UTILITIES("tools_and_utilities");

        public final ResourceKey<CreativeModeTab> key;

        private Tabs(final String name) {
            this.key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.withDefaultNamespace(name));
        }
    }

    private static final Map<ResourceKey<CreativeModeTab>, List<Supplier<? extends ItemLike>>> ENTRIES;

    static {
        final Map<ResourceKey<CreativeModeTab>, List<Supplier<? extends ItemLike>>> entries = new LinkedHashMap<>();
        entries.put(Tabs.COMBAT.key, List.of(
                ModItems.WOODEN_SPEAR));
        ENTRIES = Collections.unmodifiableMap(entries);
    }

    /**
     * @return Set of all tabs that are given new items by the mod
     */
    public static Set<ResourceKey<CreativeModeTab>> modifiedTabs() {
        return ENTRIES.keySet();
    }

    /**
     * Adds a set of items to the given creative tab.
     * 
     * @param tab    The creative mode tab to add the items to
     * @param output The list of items in the creative tab, to be mutated by this
     *               method.
     */
    public static void addItems(final ResourceKey<CreativeModeTab> tab, final CreativeModeTab.Output output) {
        final List<Supplier<? extends ItemLike>> items = ENTRIES.get(tab);
        if (items != null) {
            items.forEach(item -> output.accept(item.get()));
        }
    }

    private BuildCreativeTabs() {
    }
}
