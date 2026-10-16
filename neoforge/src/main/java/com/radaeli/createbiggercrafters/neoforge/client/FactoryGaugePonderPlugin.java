package com.radaeli.createbiggercrafters.neoforge.client;

import com.radaeli.createbiggercrafters.neoforge.CreateBiggerCrafters;
import com.simibubi.create.AllBlocks;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.StoryBoardEntry;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

/** Reuses Create's Factory Gauge ponder scenes for the capacity variants. */
public final class FactoryGaugePonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateBiggerCrafters.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ResourceLocation createGauge = AllBlocks.FACTORY_GAUGE.getId();
        List<StoryBoardEntry> createScenes = PonderIndex.getSceneAccess().getRegisteredEntries().stream()
                .filter(entry -> entry.getKey().equals(createGauge))
                .map(Map.Entry::getValue)
                .toList();
        if (createScenes.isEmpty())
            return;

        registerForItem(helper, ResourceLocation.fromNamespaceAndPath(CreateBiggerCrafters.MOD_ID, "factory_gauge_6x6"),
                createScenes);
        registerForItem(helper, ResourceLocation.fromNamespaceAndPath(CreateBiggerCrafters.MOD_ID, "factory_gauge_9x9"),
                createScenes);
    }

    private static void registerForItem(PonderSceneRegistrationHelper<ResourceLocation> helper,
                                        ResourceLocation itemId, List<StoryBoardEntry> scenes) {
        for (StoryBoardEntry scene : scenes) {
            helper.addStoryBoard(itemId, scene.getSchematicLocation(), scene.getBoard(),
                    scene.getTags().toArray(ResourceLocation[]::new));
        }
    }
}
