package com.radaeli.createbiggercrafters.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal NeoForge entry point for Create: Larger Recipe Gauges.
 */
@Mod(CreateBiggerCrafters.MOD_ID)
public final class CreateBiggerCrafters {
    public static final String MOD_ID = "create_larger_recipe_gauges";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public CreateBiggerCrafters(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::addToCreateTab);
        LOGGER.info("{} initialized on NeoForge", MOD_ID);
    }

    private void addToCreateTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(com.simibubi.create.AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())) {
            addGaugeToTab(event, ModItems.FACTORY_GAUGE_6X6.get().getDefaultInstance());
            addGaugeToTab(event, ModItems.FACTORY_GAUGE_9X9.get().getDefaultInstance());
        }
    }

    private void addGaugeToTab(BuildCreativeModeTabContentsEvent event, ItemStack gauge) {
        boolean inParent = event.getParentEntries().contains(gauge);
        boolean inSearch = event.getSearchEntries().contains(gauge);
        if (inParent && inSearch)
            return;
        if (inParent) {
            event.accept(gauge, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
        } else if (inSearch) {
            event.accept(gauge, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
        } else {
            event.accept(gauge);
        }
    }

}
