package com.radaeli.createbiggercrafters.forge1201.mixin;

import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MechanicalCrafterBlockEntity.Inventory.class, remap = false)
public interface MechanicalCrafterInventoryAccessor {
    @Accessor(value = "blockEntity", remap = false)
    MechanicalCrafterBlockEntity createBiggerCrafters$getBlockEntity();
}
