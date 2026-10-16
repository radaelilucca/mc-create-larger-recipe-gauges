package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.radaeli.createbiggercrafters.neoforge.item.CapacityFactoryPanelBlockItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Custom gauges must pass through the packager's interaction to place or tune like other gauges. */
@Mixin(PackagerBlock.class)
public abstract class PackagerGaugeInteractionMixin {
    @Redirect(method = "useItemOn",
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean createBiggerCrafters$recognizeCustomGauge(BlockEntry<?> entry, ItemStack stack) {
        return entry.isIn(stack)
                || entry == AllBlocks.FACTORY_GAUGE && stack.getItem() instanceof CapacityFactoryPanelBlockItem;
    }
}
