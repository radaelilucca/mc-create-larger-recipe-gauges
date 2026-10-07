package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.radaeli.createbiggercrafters.neoforge.FactoryPanelBlockEntityExtension;
import com.radaeli.createbiggercrafters.neoforge.ModItems;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FactoryPanelBlock.class)
public abstract class FactoryPanelDropMixin {
    @Redirect(method = "lambda$tryDestroySubPanelFirst$3", at = @At(value = "INVOKE",
            target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asStack()Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack createBiggerCrafters$dropTheMatchingGauge(
            com.tterrag.registrate.util.entry.BlockEntry<?> ignored,
            @Local(argsOnly = true) FactoryPanelBlock.PanelSlot slot,
            @Local(argsOnly = true) FactoryPanelBlockEntity panel) {
        return createBiggerCrafters$gaugeStack(slot, panel);
    }

    @Redirect(method = "lambda$onSneakWrenched$0", at = @At(value = "INVOKE",
            target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asStack()Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack createBiggerCrafters$dropTheMatchingWrenchedGauge(
            com.tterrag.registrate.util.entry.BlockEntry<?> ignored,
            @Local(argsOnly = true) FactoryPanelBlock.PanelSlot slot,
            @Local(argsOnly = true) FactoryPanelBlockEntity panel) {
        return createBiggerCrafters$gaugeStack(slot, panel);
    }

    @org.spongepowered.asm.mixin.Unique
    private static ItemStack createBiggerCrafters$gaugeStack(FactoryPanelBlock.PanelSlot slot,
                                                               FactoryPanelBlockEntity panel) {
        int capacity = ((FactoryPanelBlockEntityExtension) panel).createBiggerCrafters$getCapacity(slot);
        if (capacity == 6)
            return ModItems.FACTORY_GAUGE_6X6.get().getDefaultInstance();
        if (capacity == 9)
            return ModItems.FACTORY_GAUGE_9X9.get().getDefaultInstance();
        return com.simibubi.create.AllBlocks.FACTORY_GAUGE.asStack();
    }
}
