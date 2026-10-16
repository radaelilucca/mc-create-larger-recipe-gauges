package com.radaeli.createbiggercrafters.forge1201;

import com.radaeli.createbiggercrafters.forge1201.item.CapacityFactoryPanelBlockItem;
import com.simibubi.create.AllBlocks;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            net.minecraft.core.registries.Registries.ITEM, CreateBiggerCrafters.MOD_ID);

    public static final RegistryObject<CapacityFactoryPanelBlockItem> FACTORY_GAUGE_6X6 =
            ITEMS.register("factory_gauge_6x6", () -> new CapacityFactoryPanelBlockItem(
                    AllBlocks.FACTORY_GAUGE.get(), new Item.Properties(), 6));
    public static final RegistryObject<CapacityFactoryPanelBlockItem> FACTORY_GAUGE_9X9 =
            ITEMS.register("factory_gauge_9x9", () -> new CapacityFactoryPanelBlockItem(
                    AllBlocks.FACTORY_GAUGE.get(), new Item.Properties(), 9));

    private ModItems() {}
}

