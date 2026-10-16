package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.radaeli.createbiggercrafters.neoforge.FactoryPanelBehaviourExtension;
import com.radaeli.createbiggercrafters.neoforge.GaugeVariant;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.foundation.utility.CreateLang;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Identity travels with the behaviour when Create relocates it between slots or blocks. */
@Mixin(value = FactoryPanelBehaviour.class, remap = false)
public abstract class FactoryPanelBehaviourMixin implements FactoryPanelBehaviourExtension {
    @Shadow public PanelSlot slot;
    @Shadow public boolean active;
    @Unique private int createBiggerCrafters$capacity = 3;

    @Override
    public int createBiggerCrafters$getCapacity() {
        return createBiggerCrafters$capacity;
    }

    @Override
    public void createBiggerCrafters$setCapacity(int capacity) {
        createBiggerCrafters$capacity = GaugeVariant.fromCapacity(capacity).capacity();
    }

    @Inject(method = "disable", at = @At("HEAD"))
    private void createBiggerCrafters$resetEmptySlot(CallbackInfo ci) {
        createBiggerCrafters$capacity = 3;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void createBiggerCrafters$writeIdentity(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket,
                                                   CallbackInfo ci) {
        createBiggerCrafters$writeSlotIdentity(tag);
    }

    @Inject(method = "writeSafe", at = @At("TAIL"))
    private void createBiggerCrafters$writeSchematicIdentity(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        createBiggerCrafters$writeSlotIdentity(tag);
    }

    @Unique
    private void createBiggerCrafters$writeSlotIdentity(CompoundTag tag) {
        if (active)
            tag.getCompound(CreateLang.asId(slot.name())).putInt(GaugeVariant.NBT_KEY, createBiggerCrafters$capacity);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void createBiggerCrafters$readIdentity(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket,
                                                  CallbackInfo ci) {
        CompoundTag slotTag = tag.getCompound(CreateLang.asId(slot.name()));
        createBiggerCrafters$setCapacity(active && slotTag.contains(GaugeVariant.NBT_KEY)
                ? slotTag.getInt(GaugeVariant.NBT_KEY) : 3);
    }

    @Redirect(method = "getRequiredItems", at = @At(value = "INVOKE",
            target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asItem()Lnet/minecraft/world/item/Item;"))
    private Item createBiggerCrafters$requireMatchingSchematicItem(BlockEntry<?> ignored) {
        return GaugeVariant.fromCapacity(createBiggerCrafters$capacity).item();
    }
}
