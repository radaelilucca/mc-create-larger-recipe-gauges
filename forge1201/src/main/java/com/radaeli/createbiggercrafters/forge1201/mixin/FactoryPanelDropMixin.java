package com.radaeli.createbiggercrafters.forge1201.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.radaeli.createbiggercrafters.forge1201.GaugeVariant;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FactoryPanelBlock.class, remap = false)
public abstract class FactoryPanelDropMixin {
    @WrapOperation(method = {"lambda$tryDestroySubPanelFirst$3", "lambda$onSneakWrenched$0"},
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;removePanel(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;)Z"))
    private static boolean createBiggerCrafters$captureRemovedGauge(FactoryPanelBlockEntity panel,
            FactoryPanelBlock.PanelSlot slot, Operation<Boolean> original,
            @Share("removedGauge") LocalRef<ItemStack> removedGauge) {
        // Capture before removePanel disables the behaviour and clears its identity.
        ItemStack gauge = GaugeVariant.fromPanel(panel, slot).stack();
        boolean removed = original.call(panel, slot);
        if (removed)
            removedGauge.set(gauge);
        return removed;
    }

    @Redirect(method = {"lambda$tryDestroySubPanelFirst$3", "lambda$onSneakWrenched$0"},
            at = @At(value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asStack()Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack createBiggerCrafters$returnCapturedGauge(BlockEntry<?> ignored,
            @Share("removedGauge") LocalRef<ItemStack> removedGauge) {
        return java.util.Objects.requireNonNull(removedGauge.get(), "Missing removed gauge identity");
    }
}
