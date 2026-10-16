package com.radaeli.createbiggercrafters.forge1201;

import com.radaeli.createbiggercrafters.forge1201.item.CapacityFactoryPanelBlockItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** The registered item identity of one panel, independent of its shared block position. */
public enum GaugeVariant {
    ORIGINAL(3), EXPANDED(6), INDUSTRIAL(9);

    public static final String NBT_KEY = "LargerRecipeGaugeCapacity";
    private final int capacity;

    GaugeVariant(int capacity) {
        this.capacity = capacity;
    }

    public int capacity() {
        return capacity;
    }

    public Item item() {
        return switch (this) {
            case ORIGINAL -> AllBlocks.FACTORY_GAUGE.asItem();
            case EXPANDED -> ModItems.FACTORY_GAUGE_6X6.get();
            case INDUSTRIAL -> ModItems.FACTORY_GAUGE_9X9.get();
        };
    }

    public ItemStack stack() {
        return item().getDefaultInstance();
    }

    public static GaugeVariant fromCapacity(int capacity) {
        return switch (capacity) {
            case 3 -> ORIGINAL;
            case 6 -> EXPANDED;
            case 9 -> INDUSTRIAL;
            default -> throw new IllegalArgumentException("Unknown factory gauge capacity: " + capacity);
        };
    }

    public static GaugeVariant fromStack(ItemStack stack) {
        return stack.getItem() instanceof CapacityFactoryPanelBlockItem item
                ? fromCapacity(item.capacity()) : ORIGINAL;
    }

    public static GaugeVariant fromPanel(FactoryPanelBlockEntity panel, PanelSlot slot) {
        return fromCapacity(((FactoryPanelBlockEntityExtension) panel).createBiggerCrafters$getCapacity(slot));
    }

    public static List<ItemStack> activeStacks(FactoryPanelBlockEntity panel) {
        List<ItemStack> stacks = new ArrayList<>();
        panel.panels.forEach((slot, behaviour) -> {
            if (behaviour.isActive())
                stacks.add(fromPanel(panel, slot).stack());
        });
        return stacks;
    }
}
