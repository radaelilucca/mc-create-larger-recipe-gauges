package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.radaeli.createbiggercrafters.neoforge.GaugeVariant;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/** Create drops the first panel through loot and the remaining panels through BE.destroy(). */
@Mixin(BlockBehaviour.class)
public abstract class FactoryPanelLootMixin {
    @Inject(method = "getDrops", at = @At("RETURN"), cancellable = true)
    private void createBiggerCrafters$preserveFirstGauge(BlockState state, LootParams.Builder loot,
                                                        CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!((Object) this instanceof FactoryPanelBlock)
                || !(loot.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FactoryPanelBlockEntity panel))
            return;

        List<ItemStack> gauges = GaugeVariant.activeStacks(panel);
        if (gauges.isEmpty())
            return;
        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack drop : cir.getReturnValue()) {
            if (drop.is(AllBlocks.FACTORY_GAUGE.asItem())) {
                ItemStack variant = gauges.get(0).copy();
                variant.setCount(drop.getCount());
                drops.add(variant);
            } else {
                drops.add(drop);
            }
        }
        cir.setReturnValue(drops);
    }
}
