package com.radaeli.createbiggercrafters.forge1201;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;

public interface FactoryPanelBlockEntityExtension {
    int createBiggerCrafters$getCapacity(PanelSlot slot);

    void createBiggerCrafters$setCapacity(PanelSlot slot, int capacity);
}

