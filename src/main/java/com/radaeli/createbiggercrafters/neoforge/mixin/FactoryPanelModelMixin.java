package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.radaeli.createbiggercrafters.neoforge.FactoryPanelBlockEntityExtension;
import com.radaeli.createbiggercrafters.neoforge.client.FactoryGaugeColors;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelModel;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.Map;

@Mixin(FactoryPanelModel.class)
public abstract class FactoryPanelModelMixin {
    @Unique
    private static final ModelProperty<Map<PanelSlot, Integer>> createBiggerCrafters$capacities = new ModelProperty<>();

    @Inject(method = "gatherModelData", at = @At("RETURN"), cancellable = true)
    private void createBiggerCrafters$includePanelCapacities(ModelData.Builder builder, BlockAndTintGetter level,
                                                              BlockPos pos, BlockState state, ModelData modelData,
                                                              CallbackInfoReturnable<ModelData.Builder> cir) {
        EnumMap<PanelSlot, Integer> capacities = new EnumMap<>(PanelSlot.class);
        for (PanelSlot slot : PanelSlot.values()) {
            FactoryPanelBehaviour behaviour = FactoryPanelBehaviour.at(level, new FactoryPanelPosition(pos, slot));
            if (behaviour == null || !(behaviour.panelBE() instanceof FactoryPanelBlockEntityExtension extension))
                continue;
            int capacity = extension.createBiggerCrafters$getCapacity(slot);
            if (capacity == 6 || capacity == 9)
                capacities.put(slot, capacity);
        }
        cir.setReturnValue(cir.getReturnValue().with(createBiggerCrafters$capacities, Map.copyOf(capacities)));
    }

    @ModifyArg(method = "addPanel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/model/BakedQuad;<init>([IILnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Z)V"),
            index = 1)
    private int createBiggerCrafters$tintPanelForItsCapacity(int originalTintIndex,
                                                              @Local(argsOnly = true) PanelSlot slot,
                                                              @Local(argsOnly = true) ModelData modelData) {
        Map<PanelSlot, Integer> capacities = modelData.get(createBiggerCrafters$capacities);
        if (capacities == null)
            return originalTintIndex;
        return switch (capacities.getOrDefault(slot, 3)) {
            case 6 -> FactoryGaugeColors.EXPANDED_MODEL_TINT;
            case 9 -> FactoryGaugeColors.INDUSTRIAL_MODEL_TINT;
            default -> originalTintIndex;
        };
    }
}
