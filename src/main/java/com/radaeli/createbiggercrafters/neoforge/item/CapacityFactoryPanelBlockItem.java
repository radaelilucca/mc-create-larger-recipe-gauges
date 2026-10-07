package com.radaeli.createbiggercrafters.neoforge.item;

import com.radaeli.createbiggercrafters.neoforge.FactoryPanelBlockEntityExtension;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import java.util.Map;
import java.util.List;

public final class CapacityFactoryPanelBlockItem extends FactoryPanelBlockItem {
    private final int capacity;

    public CapacityFactoryPanelBlockItem(Block block, Properties properties, int capacity) {
        super(block, properties);
        if (capacity != 6 && capacity != 9)
            throw new IllegalArgumentException("Factory gauge capacity must be 6 or 9");
        this.capacity = capacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        String key = capacity == 6
                ? "item.create_larger_recipe_gauges.factory_gauge_6x6.tooltip"
                : "item.create_larger_recipe_gauges.factory_gauge_9x9.tooltip";
        tooltipComponents.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Component getName(ItemStack stack) {
        String key = capacity == 6
                ? "item.create_larger_recipe_gauges.factory_gauge_6x6"
                : "item.create_larger_recipe_gauges.factory_gauge_9x9";
        return Component.translatable(key);
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        // This is an alternate item for Create's block, not its canonical BlockItem.
    }

    @Override
    public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item item) {
        // The canonical mapping belongs to Create and must survive variant removal.
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack,
                                                  BlockState state) {
        boolean updated = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FactoryPanelBlockEntity panelBlockEntity
                && panelBlockEntity instanceof FactoryPanelBlockEntityExtension extension) {
            if (player != null) {
                double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1;
                HitResult hit = player.pick(reach, 1, false);
                FactoryPanelBlock.PanelSlot slot = FactoryPanelBlock.getTargetedSlot(pos, state, hit.getLocation());
                extension.createBiggerCrafters$setCapacity(slot, capacity);
            }
            panelBlockEntity.setChanged();
            panelBlockEntity.sendData();
        }
        return updated;
    }
}
