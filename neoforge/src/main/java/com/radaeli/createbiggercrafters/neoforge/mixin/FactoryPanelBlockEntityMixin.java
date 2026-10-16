package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.radaeli.createbiggercrafters.neoforge.FactoryPanelBehaviourExtension;
import com.radaeli.createbiggercrafters.neoforge.FactoryPanelBlockEntityExtension;
import com.radaeli.createbiggercrafters.neoforge.GaugeVariant;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.List;

@Mixin(value = FactoryPanelBlockEntity.class, remap = false)
public abstract class FactoryPanelBlockEntityMixin implements FactoryPanelBlockEntityExtension {
    @Shadow public EnumMap<PanelSlot, FactoryPanelBehaviour> panels;

    @Override
    public int createBiggerCrafters$getCapacity(PanelSlot slot) {
        FactoryPanelBehaviour panel = panels.get(slot);
        return panel == null ? 3 : ((FactoryPanelBehaviourExtension) panel).createBiggerCrafters$getCapacity();
    }

    @Override
    public void createBiggerCrafters$setCapacity(PanelSlot slot, int capacity) {
        ((FactoryPanelBehaviourExtension) panels.get(slot)).createBiggerCrafters$setCapacity(capacity);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void createBiggerCrafters$migrateLegacyCapacities(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket,
                                                              CallbackInfo ci) {
        CompoundTag legacy = tag.getCompound("CreateBiggerCraftersCapacities");
        panels.forEach((slot, panel) -> {
            if (panel.isActive() && legacy.contains(slot.name())
                    && !tag.getCompound(CreateLang.asId(slot.name())).contains(GaugeVariant.NBT_KEY))
                createBiggerCrafters$setCapacity(slot, legacy.getInt(slot.name()));
        });
    }

    @Redirect(method = "destroy", at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void createBiggerCrafters$dropRemainingGaugeVariants(Level level, BlockPos pos, ItemStack ignored) {
        List<ItemStack> gauges = GaugeVariant.activeStacks((FactoryPanelBlockEntity) (Object) this);
        // The loot table supplies the first active slot. Create supplies the rest here.
        for (int index = 1; index < gauges.size(); index++)
            Block.popResource(level, pos, gauges.get(index));
    }
}
