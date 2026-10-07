package com.radaeli.createbiggercrafters.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal NeoForge entry point for Create Bigger Crafters.
 */
@Mod(CreateBiggerCrafters.MOD_ID)
public final class CreateBiggerCrafters {
    public static final String MOD_ID = "create_bigger_crafters";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public CreateBiggerCrafters(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::addToCreateTab);
        NeoForge.EVENT_BUS.addListener(this::preserveGaugeVariantOnFinalBreak);
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

    private void preserveGaugeVariantOnFinalBreak(BlockDropsEvent event) {
        if (!event.getState().is(AllBlocks.FACTORY_GAUGE.get())
                || !(event.getBlockEntity() instanceof FactoryPanelBlockEntity panel)
                || panel.activePanels() != 1
                || !(panel instanceof FactoryPanelBlockEntityExtension extension))
            return;

        int capacity = panel.panels.entrySet().stream()
                .filter(entry -> entry.getValue().isActive())
                .mapToInt(entry -> extension.createBiggerCrafters$getCapacity(entry.getKey()))
                .findFirst().orElse(3);
        var variant = capacity == 6 ? ModItems.FACTORY_GAUGE_6X6.get().getDefaultInstance()
                : capacity == 9 ? ModItems.FACTORY_GAUGE_9X9.get().getDefaultInstance() : null;
        if (variant == null)
            return;

        event.getDrops().stream()
                .filter(drop -> drop.getItem().is(AllBlocks.FACTORY_GAUGE.get().asItem()))
                .findFirst()
                .ifPresent(drop -> drop.setItem(variant));
    }
}
