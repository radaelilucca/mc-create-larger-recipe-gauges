package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.radaeli.createbiggercrafters.neoforge.AbstractSimiScreenAccess;
import net.createmod.catnip.gui.AbstractSimiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSimiScreen.class)
public interface AbstractSimiScreenMixin extends AbstractSimiScreenAccess {
    @Override
    @Accessor("guiLeft")
    int createBiggerCrafters$getGuiLeft();

    @Override
    @Accessor("guiTop")
    int createBiggerCrafters$getGuiTop();
}
