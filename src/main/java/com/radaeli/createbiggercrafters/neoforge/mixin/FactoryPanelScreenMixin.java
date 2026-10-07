package com.radaeli.createbiggercrafters.neoforge.mixin;

import com.radaeli.createbiggercrafters.neoforge.FactoryPanelBlockEntityExtension;
import com.radaeli.createbiggercrafters.neoforge.AbstractSimiScreenAccess;
import com.radaeli.createbiggercrafters.neoforge.client.FactoryGaugeBackground;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.content.logistics.AddressEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(FactoryPanelScreen.class)
public abstract class FactoryPanelScreenMixin {
    @Shadow private FactoryPanelBehaviour behaviour;
    @Shadow private boolean restocker;
    @Shadow private BigItemStack outputConfig;
    @Shadow private List<BigItemStack> inputConfig;
    @Shadow private CraftingRecipe availableCraftingRecipe;
    @Shadow private boolean craftingActive;
    @Shadow private List<BigItemStack> craftingIngredients;
    @Shadow private IconButton activateCraftingButton;
    @Shadow private AddressEditBox addressBox;
    @Shadow protected abstract void init();
    @Unique private List<MechanicalCraftingRecipe> createBiggerCrafters$matches = List.of();
    @Unique private int createBiggerCrafters$matchIndex;

    @ModifyVariable(method = "init", at = @At(value = "STORE"), ordinal = 0)
    private int createBiggerCrafters$expandWindowWidth(int original) {
        return original + extraWidth();
    }

    @ModifyVariable(method = "init", at = @At(value = "STORE"), ordinal = 1)
    private int createBiggerCrafters$expandWindowHeight(int original) {
        return original + extraHeight();
    }

    @ModifyConstant(method = "renderInputItem", constant = @Constant(intValue = 3, ordinal = 0))
    private int createBiggerCrafters$useCapacityForColumns(int original) {
        return craftingActive ? gridColumns() : original;
    }

    @ModifyConstant(method = "renderInputItem", constant = @Constant(intValue = 3, ordinal = 1))
    private int createBiggerCrafters$useCapacityForRows(int original) {
        return craftingActive ? gridColumns() : original;
    }

    @ModifyConstant(method = "renderWindow", constant = @Constant(intValue = 160))
    private int createBiggerCrafters$moveOutputBesideExpandedGrid(int original) {
        return original + extraWidth();
    }

    @Redirect(method = "renderWindow", at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/gui/AllGuiTextures;getHeight()I"))
    private int createBiggerCrafters$moveFooterBelowExpandedGrid(AllGuiTextures texture) {
        return texture.getHeight() + (texture == AllGuiTextures.FACTORY_GAUGE_RECIPE ? extraHeight() : 0);
    }

    @Redirect(method = "renderWindow", at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/gui/AllGuiTextures;render(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    private void createBiggerCrafters$renderExpandedBackground(AllGuiTextures texture, GuiGraphics graphics,
                                                               int x, int y) {
        if (extraWidth() == 0 && extraHeight() == 0) {
            texture.render(graphics, x, y);
        } else if (texture == AllGuiTextures.FACTORY_GAUGE_RECIPE) {
            FactoryGaugeBackground.renderRecipe(graphics, x, y, gridColumns(), gridRows());
        } else if (texture == AllGuiTextures.FACTORY_GAUGE_BOTTOM) {
            FactoryGaugeBackground.renderBottom(graphics, x, y, extraWidth());
        } else {
            texture.render(graphics, x, y);
        }
    }

    @ModifyConstant(method = "renderWindow", constant = @Constant(intValue = 97))
    private int createBiggerCrafters$centerTitle(int original) {
        return original + extraWidth() / 2;
    }

    @ModifyConstant(method = "renderWindow", constant = {@Constant(intValue = 195), @Constant(intValue = 214)})
    private int createBiggerCrafters$movePreviewRight(int original) {
        return original + extraWidth();
    }

    @ModifyArg(method = "renderWindow", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 1), index = 1)
    private float createBiggerCrafters$movePreviewDown(float original) {
        return original + extraHeight();
    }

    @Unique
    private int extraWidth() {
        return (gridColumns() - 3) * 20;
    }

    @Unique
    private int extraHeight() {
        return (gridRows() - 3) * 20;
    }

    @Unique
    private int gridColumns() {
        int capacity = panelCapacity();
        if (!craftingActive || capacity <= 3 || !(availableCraftingRecipe instanceof ShapedRecipe shaped))
            return 3;
        return Math.max(3, shaped.getWidth());
    }

    @Unique
    private int gridRows() {
        int capacity = panelCapacity();
        if (!craftingActive || capacity <= 3 || !(availableCraftingRecipe instanceof ShapedRecipe shaped))
            return 3;
        return Math.max(3, shaped.getHeight());
    }

    @Inject(method = "searchForCraftingRecipe", at = @At("HEAD"), cancellable = true)
    private void createBiggerCrafters$searchMechanicalRecipes(CallbackInfo ci) {
        if (restocker || Minecraft.getInstance().level == null)
            return;

        int capacity = panelCapacity();
        if (capacity <= 3)
            return;

        List<MechanicalCraftingRecipe> matches = new ArrayList<>();
        var manager = Minecraft.getInstance().level.getRecipeManager();
        for (var holder : manager.getAllRecipesFor(AllRecipeTypes.MECHANICAL_CRAFTING.getType())) {
            if (!((Object) holder.value() instanceof MechanicalCraftingRecipe recipe))
                continue;
            if (recipe.getWidth() <= capacity && recipe.getHeight() <= capacity && matchesInputs(recipe))
                matches.add(recipe);
        }

        createBiggerCrafters$matches = List.copyOf(matches);
        if (matches.isEmpty()) {
            createBiggerCrafters$matchIndex = 0;
            availableCraftingRecipe = null;
        } else {
            createBiggerCrafters$matchIndex = Math.floorMod(createBiggerCrafters$matchIndex, matches.size());
            availableCraftingRecipe = matches.get(createBiggerCrafters$matchIndex);
        }
        ci.cancel();
    }

    private int panelCapacity() {
        if (behaviour == null)
            return 3;
        return ((FactoryPanelBlockEntityExtension) behaviour.panelBE())
                .createBiggerCrafters$getCapacity(behaviour.slot);
    }

    @Unique
    private AbstractSimiScreenAccess screenAccess() {
        return (AbstractSimiScreenAccess) this;
    }

    private boolean matchesInputs(MechanicalCraftingRecipe recipe) {
        if (outputConfig == null || outputConfig.stack.isEmpty()
                || !ItemStack.isSameItem(outputConfig.stack,
                recipe.getResultItem(Minecraft.getInstance().level.registryAccess())))
            return false;

        List<ItemStack> configured = inputConfig.stream()
                .map(entry -> entry.stack)
                .filter(stack -> !stack.isEmpty())
                .toList();
        if (configured.isEmpty())
            return false;

        Set<Item> inputItems = new HashSet<>();
        configured.forEach(stack -> inputItems.add(stack.getItem()));
        Set<Item> usedItems = new HashSet<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty())
                continue;
            ItemStack match = configured.stream().filter(ingredient::test).findFirst().orElse(ItemStack.EMPTY);
            if (match.isEmpty())
                return false;
            usedItems.add(match.getItem());
        }
        return usedItems.size() >= inputItems.size();
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void createBiggerCrafters$labelRecipeCycle(CallbackInfo ci) {
        addressBox.setWidth(108 + extraWidth());
        if (activateCraftingButton != null && createBiggerCrafters$matches.size() > 1)
            activateCraftingButton.setToolTip(Component.translatable("gui.create_bigger_crafters.cycle_recipe",
                    createBiggerCrafters$matchIndex + 1, createBiggerCrafters$matches.size()));
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void createBiggerCrafters$cycleRecipesOrEditIngredient(double mouseX, double mouseY, double scrollX,
                                                                   double scrollY, CallbackInfoReturnable<Boolean> cir) {
        if (panelCapacity() <= 3 || scrollY == 0)
            return;
        int guiLeft = screenAccess().createBiggerCrafters$getGuiLeft();
        int guiTop = screenAccess().createBiggerCrafters$getGuiTop();
        if (createBiggerCrafters$matches.size() > 1 && mouseX >= guiLeft + 31 && mouseX < guiLeft + 47
                && mouseY >= guiTop + 27 && mouseY < guiTop + 43) {
            createBiggerCrafters$matchIndex = Math.floorMod(createBiggerCrafters$matchIndex
                    + (scrollY > 0 ? 1 : -1), createBiggerCrafters$matches.size());
            availableCraftingRecipe = createBiggerCrafters$matches.get(createBiggerCrafters$matchIndex);
            craftingIngredients = FactoryPanelScreen.convertRecipeToPackageOrderContext(
                    availableCraftingRecipe, inputConfig, false);
            init();
            cir.setReturnValue(true);
            return;
        }
        if (!craftingActive || !(availableCraftingRecipe instanceof ShapedRecipe shaped))
            return;

        int columns = shaped.getWidth();
        int rows = shaped.getHeight();
        int column = (int) ((mouseX - guiLeft - 68) / 20);
        int row = (int) ((mouseY - guiTop - 28) / 20);
        if (column < 0 || column >= columns || row < 0 || row >= rows)
            return;
        int index = row * columns + column;
        List<Ingredient> ingredients = shaped.getIngredients();
        if (index >= ingredients.size() || ingredients.get(index).isEmpty())
            return;

        List<ItemStack> options = inputConfig.stream().map(entry -> entry.stack)
                .filter(stack -> !stack.isEmpty() && ingredients.get(index).test(stack)).toList();
        if (options.size() < 2)
            return;
        ItemStack current = craftingIngredients.get(index).stack;
        int currentIndex = 0;
        for (int i = 0; i < options.size(); i++) {
            if (ItemStack.isSameItemSameComponents(options.get(i), current)) {
                currentIndex = i;
                break;
            }
        }
        int next = Math.floorMod(currentIndex + (scrollY > 0 ? 1 : -1), options.size());
        craftingIngredients.set(index, new BigItemStack(options.get(next).copyWithCount(1), 1));
        cir.setReturnValue(true);
    }
}
