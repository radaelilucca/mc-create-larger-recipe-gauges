package com.radaeli.createbiggercrafters.neoforge;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;

public interface FactoryPanelBlockEntityExtension {
    int createBiggerCrafters$getCapacity(PanelSlot slot);

    void createBiggerCrafters$setCapacity(PanelSlot slot, int capacity);
}
