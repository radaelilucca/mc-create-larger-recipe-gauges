package com.radaeli.createbiggercrafters.neoforge;

import com.radaeli.createbiggercrafters.neoforge.item.CapacityFactoryPanelBlockItem;
import com.simibubi.create.AllBlocks;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateBiggerCrafters.MOD_ID);

    public static final DeferredItem<CapacityFactoryPanelBlockItem> FACTORY_GAUGE_6X6 =
            ITEMS.register("factory_gauge_6x6", () -> new CapacityFactoryPanelBlockItem(
                    AllBlocks.FACTORY_GAUGE.get(), new Item.Properties(), 6));
    public static final DeferredItem<CapacityFactoryPanelBlockItem> FACTORY_GAUGE_9X9 =
            ITEMS.register("factory_gauge_9x9", () -> new CapacityFactoryPanelBlockItem(
                    AllBlocks.FACTORY_GAUGE.get(), new Item.Properties(), 9));

    private ModItems() {}
}
