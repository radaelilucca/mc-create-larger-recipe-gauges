package com.radaeli.createbiggercrafters.neoforge.client;

import com.radaeli.createbiggercrafters.neoforge.CreateBiggerCrafters;
import com.radaeli.createbiggercrafters.neoforge.ModItems;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.createmod.ponder.foundation.PonderIndex;

@EventBusSubscriber(modid = CreateBiggerCrafters.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class FactoryGaugeClientEvents {
    private FactoryGaugeClientEvents() {
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex != 0)
                return 0xFFFFFF;
            Item item = stack.getItem();
            if (item == ModItems.FACTORY_GAUGE_6X6.get())
                return FactoryGaugeColors.EXPANDED_WOOD_BRASS;
            if (item == ModItems.FACTORY_GAUGE_9X9.get())
                return FactoryGaugeColors.INDUSTRIAL_IRON;
            return 0xFFFFFF;
        }, ModItems.FACTORY_GAUGE_6X6.get(), ModItems.FACTORY_GAUGE_9X9.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(FactoryGaugeClientEvents::blockTint, AllBlocks.FACTORY_GAUGE.get());
    }

    @SubscribeEvent
    public static void registerFactoryGaugePonderScenes(FMLClientSetupEvent event) {
        event.enqueueWork(() -> PonderIndex.addPlugin(new FactoryGaugePonderPlugin()));
    }

    private static int blockTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        return switch (tintIndex) {
            case FactoryGaugeColors.EXPANDED_MODEL_TINT -> FactoryGaugeColors.EXPANDED_WOOD_BRASS;
            case FactoryGaugeColors.INDUSTRIAL_MODEL_TINT -> FactoryGaugeColors.INDUSTRIAL_IRON;
            default -> 0xFFFFFF;
        };
    }
}
