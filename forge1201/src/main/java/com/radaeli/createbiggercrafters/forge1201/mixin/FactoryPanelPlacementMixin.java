package com.radaeli.createbiggercrafters.forge1201.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.radaeli.createbiggercrafters.forge1201.FactoryPanelBlockEntityExtension;
import com.radaeli.createbiggercrafters.forge1201.GaugeVariant;
import com.radaeli.createbiggercrafters.forge1201.item.CapacityFactoryPanelBlockItem;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(FactoryPanelBlock.class)
public abstract class FactoryPanelPlacementMixin {
    @Redirect(method = {"canBeReplaced", "use"},
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean createBiggerCrafters$acceptCapacityGauge(BlockEntry<?> factoryGauge, ItemStack stack) {
        return factoryGauge.isIn(stack) || stack.getItem() instanceof CapacityFactoryPanelBlockItem;
    }

    @WrapOperation(method = "getStateForPlacement",
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;addPanel(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;Ljava/util/UUID;)Z"))
    private boolean createBiggerCrafters$placeIntoExistingBlock(FactoryPanelBlockEntity panel,
            FactoryPanelBlock.PanelSlot slot, UUID network, Operation<Boolean> original,
            @Local(argsOnly = true) BlockPlaceContext context) {
        return createBiggerCrafters$addWithVariant(panel, slot, network, original, context.getItemInHand());
    }

    @WrapOperation(method = "lambda$setPlacedBy$1", remap = false,
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;addPanel(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;Ljava/util/UUID;)Z"))
    private static boolean createBiggerCrafters$placeFirstPanel(FactoryPanelBlockEntity panel,
            FactoryPanelBlock.PanelSlot slot, UUID network, Operation<Boolean> original,
            @Local(argsOnly = true) ItemStack stack) {
        return createBiggerCrafters$addWithVariant(panel, slot, network, original, stack);
    }

    @WrapOperation(method = "lambda$use$2", remap = false,
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlockEntity;addPanel(Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBlock$PanelSlot;Ljava/util/UUID;)Z"))
    private boolean createBiggerCrafters$placeAdditionalPanel(FactoryPanelBlockEntity panel,
            FactoryPanelBlock.PanelSlot slot, UUID network, Operation<Boolean> original,
            @Local(argsOnly = true) ItemStack stack) {
        return createBiggerCrafters$addWithVariant(panel, slot, network, original, stack);
    }

    @Unique
    private static boolean createBiggerCrafters$addWithVariant(FactoryPanelBlockEntity panel,
            FactoryPanelBlock.PanelSlot slot, UUID network, Operation<Boolean> original, ItemStack stack) {
        var behaviour = panel.panels.get(slot);
        if (behaviour == null || behaviour.isActive())
            return original.call(panel, slot, network);

        FactoryPanelBlockEntityExtension extension = (FactoryPanelBlockEntityExtension) panel;
        int previousCapacity = extension.createBiggerCrafters$getCapacity(slot);
        // enable() sends a client update, so bind the item before activation, not after it.
        extension.createBiggerCrafters$setCapacity(slot, GaugeVariant.fromStack(stack).capacity());
        boolean added = original.call(panel, slot, network);
        if (added)
            panel.notifyUpdate();
        else
            extension.createBiggerCrafters$setCapacity(slot, previousCapacity);
        return added;
    }
}
