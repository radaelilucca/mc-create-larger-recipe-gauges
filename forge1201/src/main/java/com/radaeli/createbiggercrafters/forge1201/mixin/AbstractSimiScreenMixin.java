package com.radaeli.createbiggercrafters.forge1201.mixin;

import com.radaeli.createbiggercrafters.forge1201.AbstractSimiScreenAccess;
import net.createmod.catnip.gui.AbstractSimiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractSimiScreen.class, remap = false)
public interface AbstractSimiScreenMixin extends AbstractSimiScreenAccess {
    @Override
    @Accessor(value = "guiLeft", remap = false)
    int createBiggerCrafters$getGuiLeft();

    @Override
    @Accessor(value = "guiTop", remap = false)
    int createBiggerCrafters$getGuiTop();
}

