package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.radaeli.createbiggercrafters.neoforge.FactoryPanelBlockEntityExtension;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;

@Mixin(FactoryPanelBlockEntity.class)
public abstract class FactoryPanelBlockEntityMixin implements FactoryPanelBlockEntityExtension {
    @Unique
    private final EnumMap<PanelSlot, Integer> createBiggerCrafters$capacities = new EnumMap<>(PanelSlot.class);

    @Override
    public int createBiggerCrafters$getCapacity(PanelSlot slot) {
        return createBiggerCrafters$capacities.getOrDefault(slot, 3);
    }

    @Override
    public void createBiggerCrafters$setCapacity(PanelSlot slot, int capacity) {
        createBiggerCrafters$capacities.put(slot, capacity);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void createBiggerCrafters$writeCapacity(CompoundTag tag, HolderLookup.Provider registries,
                                                    boolean clientPacket, CallbackInfo ci) {
        CompoundTag capacities = new CompoundTag();
        createBiggerCrafters$capacities.forEach((slot, capacity) -> capacities.putInt(slot.name(), capacity));
        tag.put("CreateBiggerCraftersCapacities", capacities);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void createBiggerCrafters$readCapacity(CompoundTag tag, HolderLookup.Provider registries,
                                                    boolean clientPacket, CallbackInfo ci) {
        createBiggerCrafters$capacities.clear();
        CompoundTag capacities = tag.getCompound("CreateBiggerCraftersCapacities");
        for (PanelSlot slot : PanelSlot.values()) {
            if (capacities.contains(slot.name()))
                createBiggerCrafters$capacities.put(slot, capacities.getInt(slot.name()));
        }
    }
}
