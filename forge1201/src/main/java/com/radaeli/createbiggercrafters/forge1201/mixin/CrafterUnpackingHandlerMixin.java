package com.radaeli.createbiggercrafters.forge1201.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.crafter.ConnectedInputHandler;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.impl.unpacking.CrafterUnpackingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = CrafterUnpackingHandler.class, remap = false)
public abstract class CrafterUnpackingHandlerMixin {

    @WrapOperation(method = "unpack", remap = false, at = @At(value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/crafter/ConnectedInputHandler$ConnectedInput;getInventories(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/util/List;"))
    private List<MechanicalCrafterBlockEntity.Inventory> createBiggerCrafters$mapPatternToCrafterPositions(
            ConnectedInputHandler.ConnectedInput input, Level level, BlockPos pos,
            Operation<List<MechanicalCrafterBlockEntity.Inventory>> original,
            @Local(argsOnly = true) PackageOrderWithCrafts order) {
        List<MechanicalCrafterBlockEntity.Inventory> inventories = original.call(input, level, pos);
        return createBiggerCrafters$mapPattern(inventories, order.getCraftingInformation());
    }

    private List<MechanicalCrafterBlockEntity.Inventory> createBiggerCrafters$mapPattern(
            List<MechanicalCrafterBlockEntity.Inventory> inventories, List<BigItemStack> pattern) {
        if (inventories.isEmpty() || pattern.isEmpty())
            return inventories;

        // Preserve Create's native ordering for its original 3x3 mechanical crafter.
        if (createBiggerCrafters$isNativeThreeByThree(inventories))
            return inventories;

        int gridSize = (int) Math.sqrt(pattern.size());
        if (gridSize * gridSize != pattern.size())
            return inventories;

        int firstColumn = gridSize;
        int firstRow = gridSize;
        boolean hasIngredients = false;
        for (int index = 0; index < pattern.size(); index++) {
            if (pattern.get(index).stack.isEmpty())
                continue;
            hasIngredients = true;
            firstColumn = Math.min(firstColumn, index % gridSize);
            firstRow = Math.min(firstRow, index / gridSize);
        }
        if (!hasIngredients)
            return inventories;

        Map<BlockPos, MechanicalCrafterBlockEntity.Inventory> byPosition = new HashMap<>();
        for (MechanicalCrafterBlockEntity.Inventory inventory : inventories) {
            MechanicalCrafterBlockEntity crafter = ((MechanicalCrafterInventoryAccessor) inventory)
                    .createBiggerCrafters$getBlockEntity();
            byPosition.put(crafter.getBlockPos(), inventory);
        }

        for (MechanicalCrafterBlockEntity.Inventory anchorInventory : inventories) {
            MechanicalCrafterBlockEntity anchorCrafter = ((MechanicalCrafterInventoryAccessor) anchorInventory)
                    .createBiggerCrafters$getBlockEntity();
            Direction facing = anchorCrafter.getBlockState().getValue(MechanicalCrafterBlock.HORIZONTAL_FACING);
            Direction.Axis horizontalAxis = facing.getClockWise().getAxis();
            int horizontalStep = facing.getAxisDirection().getStep()
                    * (horizontalAxis == Direction.Axis.Z ? -1 : 1);
            int stepX = horizontalAxis == Direction.Axis.X ? horizontalStep : 0;
            int stepZ = horizontalAxis == Direction.Axis.Z ? horizontalStep : 0;
            BlockPos anchor = anchorCrafter.getBlockPos();

            List<MechanicalCrafterBlockEntity.Inventory> mapped = new ArrayList<>(pattern.size());
            boolean fits = true;
            for (int index = 0; index < pattern.size(); index++) {
                BigItemStack ingredient = pattern.get(index);
                if (ingredient.stack.isEmpty()) {
                    mapped.add(inventories.get(0));
                    continue;
                }

                int column = index % gridSize;
                int row = index / gridSize;
                BlockPos target = anchor.offset(
                        stepX * (column - firstColumn), firstRow - row,
                        stepZ * (column - firstColumn));
                MechanicalCrafterBlockEntity.Inventory targetInventory = byPosition.get(target);
                if (targetInventory == null) {
                    fits = false;
                    break;
                }
                MechanicalCrafterBlockEntity targetCrafter = ((MechanicalCrafterInventoryAccessor) targetInventory)
                        .createBiggerCrafters$getBlockEntity();
                if (targetCrafter.getBlockState().getValue(MechanicalCrafterBlock.HORIZONTAL_FACING) != facing) {
                    fits = false;
                    break;
                }
                mapped.add(targetInventory);
            }
            if (fits)
                return mapped;
        }

        return List.of();
    }

    private boolean createBiggerCrafters$isNativeThreeByThree(
            List<MechanicalCrafterBlockEntity.Inventory> inventories) {
        if (inventories.size() != 9)
            return false;

        MechanicalCrafterBlockEntity firstCrafter = ((MechanicalCrafterInventoryAccessor) inventories.get(0))
                .createBiggerCrafters$getBlockEntity();
        Direction facing = firstCrafter.getBlockState().getValue(MechanicalCrafterBlock.HORIZONTAL_FACING);
        Direction.Axis horizontalAxis = facing.getClockWise().getAxis();

        int minHorizontal = Integer.MAX_VALUE;
        int maxHorizontal = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        Integer normalCoordinate = null;
        Set<BlockPos> positions = new HashSet<>();

        for (MechanicalCrafterBlockEntity.Inventory inventory : inventories) {
            MechanicalCrafterBlockEntity crafter = ((MechanicalCrafterInventoryAccessor) inventory)
                    .createBiggerCrafters$getBlockEntity();
            if (crafter.getBlockState().getValue(MechanicalCrafterBlock.HORIZONTAL_FACING) != facing)
                return false;

            BlockPos pos = crafter.getBlockPos();
            int horizontal = horizontalAxis == Direction.Axis.X ? pos.getX() : pos.getZ();
            int normal = horizontalAxis == Direction.Axis.X ? pos.getZ() : pos.getX();
            minHorizontal = Math.min(minHorizontal, horizontal);
            maxHorizontal = Math.max(maxHorizontal, horizontal);
            minY = Math.min(minY, pos.getY());
            maxY = Math.max(maxY, pos.getY());
            if (normalCoordinate != null && normalCoordinate != normal)
                return false;
            normalCoordinate = normal;
            positions.add(pos);
        }

        return positions.size() == 9
                && maxHorizontal - minHorizontal == 2
                && maxY - minY == 2;
    }
}
