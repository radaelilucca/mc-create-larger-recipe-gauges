package com.radaeli.createbiggercrafters.forge1201.mixin;

import com.radaeli.createbiggercrafters.forge1201.FactoryPanelBlockEntityExtension;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;

@Mixin(value = FactoryPanelBlockEntity.class, remap = false)
public abstract class FactoryPanelBlockEntityMixin implements FactoryPanelBlockEntityExtension {
    @Unique
    private final EnumMap<PanelSlot, Integer> createBiggerCrafters$capacities = new EnumMap<>(PanelSlot.class);

    @Override
    public int createBiggerCrafters$getCapacity(PanelSlot slot) {
        return createBiggerCrafters$capacities.getOrDefault(slot, 3);
    }

    @Override
    public void createBiggerCrafters$setCapacity(PanelSlot slot, int capacity) {
        if (capacity != 3 && capacity != 6 && capacity != 9)
            throw new IllegalArgumentException("Factory gauge capacity must be 3, 6 or 9");
        createBiggerCrafters$capacities.put(slot, capacity);
    }

    @Inject(method = "removePanel", remap = false, at = @At("RETURN"))
    private void createBiggerCrafters$clearRemovedPanelCapacity(PanelSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ())
            createBiggerCrafters$capacities.remove(slot);
    }

    @Inject(method = "write", remap = false, at = @At("TAIL"))
    private void createBiggerCrafters$writeCapacity(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        CompoundTag capacities = new CompoundTag();
        createBiggerCrafters$capacities.forEach((slot, capacity) -> capacities.putInt(slot.name(), capacity));
        tag.put("CreateBiggerCraftersCapacities", capacities);
    }

    @Inject(method = "read", remap = false, at = @At("TAIL"))
    private void createBiggerCrafters$readCapacity(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        createBiggerCrafters$capacities.clear();
        CompoundTag capacities = tag.getCompound("CreateBiggerCraftersCapacities");
        for (PanelSlot slot : PanelSlot.values()) {
            if (capacities.contains(slot.name()))
                createBiggerCrafters$capacities.put(slot, capacities.getInt(slot.name()));
        }
    }
}

