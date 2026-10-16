package com.radaeli.createbiggercrafters.forge1201;

import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CreateBiggerCrafters.MOD_ID)
public final class CreateBiggerCrafters {
    public static final String MOD_ID = "create_larger_recipe_gauges";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public CreateBiggerCrafters() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        modBus.addListener(this::addToCreateTab);
        LOGGER.info("{} initialized on Forge", MOD_ID);
    }

    private void addToCreateTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())) {
            addGaugeToTab(event, ModItems.FACTORY_GAUGE_6X6.get().getDefaultInstance());
            addGaugeToTab(event, ModItems.FACTORY_GAUGE_9X9.get().getDefaultInstance());
        }
    }

    private void addGaugeToTab(BuildCreativeModeTabContentsEvent event, ItemStack gauge) {
        if (!event.getEntries().contains(gauge))
            event.accept(gauge);
    }

}

