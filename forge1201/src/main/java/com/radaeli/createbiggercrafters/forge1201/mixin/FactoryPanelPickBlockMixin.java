package com.radaeli.createbiggercrafters.forge1201.mixin;

import com.radaeli.createbiggercrafters.forge1201.GaugeVariant;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class FactoryPanelPickBlockMixin {
    @Inject(method = "getCloneItemStack", at = @At("RETURN"), cancellable = true)
    private void createBiggerCrafters$pickMatchingGauge(BlockGetter level, BlockPos pos, BlockState state,
                                                        CallbackInfoReturnable<ItemStack> cir) {
        HitResult hit = Minecraft.getInstance().hitResult;
        if (!((Object) this instanceof FactoryPanelBlock)
                || !(hit instanceof BlockHitResult blockHit)
                || !blockHit.getBlockPos().equals(pos)
                || !(level.getBlockEntity(pos) instanceof FactoryPanelBlockEntity panel))
            return;

        FactoryPanelBlock.PanelSlot slot = FactoryPanelBlock.getTargetedSlot(pos, state, hit.getLocation());
        cir.setReturnValue(GaugeVariant.fromPanel(panel, slot).stack());
    }
}
