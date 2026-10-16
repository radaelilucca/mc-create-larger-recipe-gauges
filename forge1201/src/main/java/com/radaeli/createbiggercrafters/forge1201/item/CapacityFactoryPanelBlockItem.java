package com.radaeli.createbiggercrafters.forge1201.item;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.block.Block;

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
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltipComponents,
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

    public int capacity() {
        return capacity;
    }
}
