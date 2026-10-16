package com.radaeli.createbiggercrafters.forge1201.validation;

import com.radaeli.createbiggercrafters.forge1201.FactoryPanelBlockEntityExtension;
import com.radaeli.createbiggercrafters.forge1201.GaugeVariant;
import com.mojang.authlib.GameProfile;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.UUID;

@GameTestHolder("create_larger_recipe_gauges")
@PrefixGameTestTemplate(false)
public final class GaugeIdentityTests {
    private static final BlockPos POS = new BlockPos(3, 3, 3);

    @GameTest(template = "empty")
    public static void firstPlacement(GameTestHelper helper) {
        for (GaugeVariant variant : List.of(GaugeVariant.EXPANDED, GaugeVariant.INDUSTRIAL)) {
            BlockPos pos = helper.absolutePos(POS.offset(variant == GaugeVariant.EXPANDED ? -1 : 1, 0, 0));
            helper.getLevel().setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
            FakePlayer player = player(helper);
            Vec3 point = Vec3.atLowerCornerOf(pos).add(0.25, 0, 0.25);
            player.setPos(point.x, point.y + 1, point.z);
            player.lookAt(EntityAnchorArgument.Anchor.EYES, point);
            ItemStack stack = tuned(variant, player);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            BlockHitResult hit = new BlockHitResult(point, Direction.UP, pos.below(), false);
            ((BlockItem) stack.getItem()).place(new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, hit)));
            helper.assertTrue(stack.getCount() == 1, "First placement must consume exactly one custom item");
            FactoryPanelBlockEntity panel = panel(helper, pos);
            helper.assertTrue(panel.activePanels() == 1, "First placement must activate exactly one panel");
            assertStacks(helper, GaugeVariant.activeStacks(panel), 0,
                    variant == GaugeVariant.EXPANDED ? 1 : 0, variant == GaugeVariant.INDUSTRIAL ? 1 : 0);
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void additionalSlotsAndRejectedPlacement(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 3, 0, 0, 0);
        FakePlayer player = player(helper);
        PanelSlot[] slots = PanelSlot.values();
        placeAdditional(helper, panel, player, slots[1], GaugeVariant.EXPANDED, false);
        placeAdditional(helper, panel, player, slots[2], GaugeVariant.INDUSTRIAL, false);
        placeAdditional(helper, panel, player, slots[3], GaugeVariant.EXPANDED, true);
        assertStacks(helper, GaugeVariant.activeStacks(panel), 1, 2, 1);
        placeAdditional(helper, panel, player, slots[1], GaugeVariant.INDUSTRIAL, false);
        assertStacks(helper, GaugeVariant.activeStacks(panel), 1, 2, 1);
        helper.assertTrue(player.getMainHandItem().getCount() == 2, "Rejected placement must not consume or change the item");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void wrenchLastAndSharedPanels(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 6, 9, 3, 0);
        FakePlayer player = player(helper);
        PanelSlot[] slots = PanelSlot.values();
        wrench(helper, panel, player, slots[0]);
        helper.assertTrue(player.getInventory().countItem(GaugeVariant.EXPANDED.item()) == 1, "Wrench must refund 6x6");
        wrench(helper, panel, player, slots[2]);
        helper.assertTrue(player.getInventory().countItem(GaugeVariant.ORIGINAL.item()) == 1, "Original must remain original");
        wrench(helper, panel, player, slots[1]);
        helper.assertTrue(player.getInventory().countItem(GaugeVariant.INDUSTRIAL.item()) == 1, "Wrench must refund final 9x9");
        helper.assertTrue(helper.getLevel().getBlockState(panel.getBlockPos()).isAir(), "Last wrench removes the empty block");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void partialBreak(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 6, 9, 3, 0);
        FakePlayer player = player(helper);
        BlockHitResult hit = hit(panel, PanelSlot.values()[0]);
        aim(player, hit);
        boolean destroyed = AllBlocks.FACTORY_GAUGE.get().onDestroyedByPlayer(panel.getBlockState(),
                helper.getLevel(), panel.getBlockPos(), player, true, panel.getBlockState().getFluidState());
        helper.assertFalse(destroyed, "Breaking one of several slots must retain the block");
        assertStacks(helper, drops(helper, panel.getBlockPos()), 0, 1, 0);
        assertStacks(helper, GaugeVariant.activeStacks(panel), 1, 0, 1);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fullBlockDrops(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 6, 9, 3, 6);
        helper.getLevel().destroyBlock(panel.getBlockPos(), true);
        assertStacks(helper, drops(helper, panel.getBlockPos()), 1, 2, 1);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void singleGaugeDrop(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 9, 0, 0, 0);
        helper.getLevel().destroyBlock(panel.getBlockPos(), true);
        assertStacks(helper, drops(helper, panel.getBlockPos()), 0, 0, 1);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void supportLossDrops(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 6, 9, 3, 6);
        helper.getLevel().destroyBlock(panel.getBlockPos().south(), false);
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(helper.getLevel().getBlockState(panel.getBlockPos()).isAir(), "Unsupported gauge must break");
            assertStacks(helper, drops(helper, panel.getBlockPos()), 1, 2, 1);
            helper.succeed();
        });
    }

    @GameTest(template = "empty")
    public static void saveReloadClientAndSchematics(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 6, 9, 3, 6);
        CompoundTag full = panel.saveWithFullMetadata();
        assertStacks(helper, GaugeVariant.activeStacks(load(helper, panel, full)), 1, 2, 1);
        CompoundTag client = panel.writeClient(new CompoundTag());
        FactoryPanelBlockEntity clientCopy = load(helper, panel, full);
        clientCopy.readClient(client);
        assertStacks(helper, GaugeVariant.activeStacks(clientCopy), 1, 2, 1);
        CompoundTag schematic = new CompoundTag();
        panel.writeSafe(schematic);
        schematic.putString("id", full.getString("id"));
        assertStacks(helper, GaugeVariant.activeStacks(load(helper, panel, schematic)), 1, 2, 1);
        assertStacks(helper, panel.getRequiredItems(panel.getBlockState()).getRequiredItems().stream()
                .map(requirement -> requirement.stack).toList(), 1, 2, 1);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void legacyMigrationAndSlotReuse(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 6, 9, 3, 6);
        CompoundTag full = panel.saveWithFullMetadata();
        CompoundTag legacy = new CompoundTag();
        for (PanelSlot slot : PanelSlot.values()) {
            legacy.putInt(slot.name(), GaugeVariant.fromPanel(panel, slot).capacity());
            full.getCompound(CreateLang.asId(slot.name())).remove(GaugeVariant.NBT_KEY);
        }
        full.put("CreateBiggerCraftersCapacities", legacy);
        FactoryPanelBlockEntity copy = load(helper, panel, full);
        assertStacks(helper, GaugeVariant.activeStacks(copy), 1, 2, 1);
        PanelSlot slot = PanelSlot.values()[0];
        copy.removePanel(slot);
        copy.addPanel(slot, UUID.randomUUID());
        helper.assertTrue(GaugeVariant.fromPanel(copy, slot) == GaugeVariant.ORIGINAL, "Reused slot must not inherit a removed custom gauge");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void relocationPreservesIdentity(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 6, 9, 3, 0);
        FakePlayer player = player(helper);
        BlockPos destination = panel.getBlockPos().east(2);
        helper.getLevel().setBlockAndUpdate(destination.south(), Blocks.STONE.defaultBlockState());
        PanelSlot slot = PanelSlot.values()[0];
        PanelSlot temporarySlot = PanelSlot.values()[3];
        panel.panels.get(slot).moveTo(new FactoryPanelPosition(panel.getBlockPos(), temporarySlot), player);
        helper.assertTrue(GaugeVariant.fromPanel(panel, temporarySlot) == GaugeVariant.EXPANDED, "Moving between slots must retain identity");
        helper.assertTrue(GaugeVariant.fromPanel(panel, slot) == GaugeVariant.ORIGINAL, "Moved-out slot must be empty of custom identity");
        panel.panels.get(temporarySlot).moveTo(new FactoryPanelPosition(destination, slot), player);
        FactoryPanelBlockEntity moved = panel(helper, destination);
        assertStacks(helper, GaugeVariant.activeStacks(moved), 0, 1, 0);
        assertStacks(helper, GaugeVariant.activeStacks(panel), 1, 0, 1);
        wrench(helper, moved, player, slot);
        helper.assertTrue(player.getInventory().countItem(GaugeVariant.EXPANDED.item()) == 1, "Relocated gauge must still refund 6x6");
        helper.succeed();
    }


    @GameTest(template = "empty")
    public static void customGaugePassesThroughPackager(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(POS);
        BlockState state = AllBlocks.PACKAGER.getDefaultState();
        helper.getLevel().setBlockAndUpdate(pos, state);
        FakePlayer player = player(helper);
        for (GaugeVariant variant : List.of(GaugeVariant.ORIGINAL, GaugeVariant.EXPANDED, GaugeVariant.INDUSTRIAL)) {
            ItemStack stack = tuned(variant, player);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false);
            var result = state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
            helper.assertTrue(result == net.minecraft.world.InteractionResult.PASS, "Packager must pass the gauge interaction to its item");
            helper.assertTrue(stack.is(variant.item()), "Packager interaction must not change the registered gauge item");
        }
        helper.succeed();
    }


    @GameTest(template = "empty")
    public static void ctrlCopyAndRetuningKeepRegisteredItem(GameTestHelper helper) {
        FactoryPanelBlockEntity panel = fixture(helper, 6, 9, 3, 0);
        FakePlayer player = player(helper);
        CompoundTag full = panel.saveWithFullMetadata();
        for (GaugeVariant variant : GaugeVariant.values()) {
            ItemStack stack = variant.stack();
            BlockItem.setBlockEntityData(stack, panel.getType(), full.copy());
            com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem.fixCtrlCopiedStack(stack);
            helper.assertTrue(stack.is(variant.item()), "Ctrl-copy normalization must preserve the registered item");
            helper.assertTrue(LogisticallyLinkedBlockItem.networkFromStack(stack) != null, "Ctrl-copy must retain a valid network");
            UUID network = UUID.randomUUID();
            LogisticallyLinkedBlockItem.assignFrequency(stack, player, network);
            helper.assertTrue(stack.is(variant.item()), "Retuning must preserve the registered item");
            helper.assertTrue(network.equals(LogisticallyLinkedBlockItem.networkFromStack(stack)), "Retuning must update the network");
        }
        helper.succeed();
    }

    private static FactoryPanelBlockEntity fixture(GameTestHelper helper, int... capacities) {
        BlockPos pos = helper.absolutePos(POS);
        helper.getLevel().setBlockAndUpdate(pos.south(), Blocks.STONE.defaultBlockState());
        BlockState state = AllBlocks.FACTORY_GAUGE.getDefaultState()
                .setValue(FactoryPanelBlock.FACE, AttachFace.WALL).setValue(FactoryPanelBlock.FACING, Direction.NORTH);
        helper.getLevel().setBlockAndUpdate(pos, state);
        FactoryPanelBlockEntity panel = panel(helper, pos);
        for (int index = 0; index < capacities.length; index++) {
            if (capacities[index] == 0)
                continue;
            PanelSlot slot = PanelSlot.values()[index];
            panel.addPanel(slot, UUID.randomUUID());
            ((FactoryPanelBlockEntityExtension) panel).createBiggerCrafters$setCapacity(slot, capacities[index]);
        }
        return panel;
    }

    private static FactoryPanelBlockEntity panel(GameTestHelper helper, BlockPos pos) {
        BlockEntity entity = helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(entity instanceof FactoryPanelBlockEntity, "Expected a gauge block entity at " + pos);
        return (FactoryPanelBlockEntity) entity;
    }

    private static FakePlayer player(GameTestHelper helper) {
        FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "GaugeIdentity"));
        player.setGameMode(GameType.SURVIVAL);
        return player;
    }

    private static ItemStack tuned(GaugeVariant variant, FakePlayer player) {
        ItemStack stack = variant.stack();
        stack.setCount(2);
        LogisticallyLinkedBlockItem.assignFrequency(stack, player, UUID.randomUUID());
        return stack;
    }

    private static BlockHitResult hit(FactoryPanelBlockEntity panel, PanelSlot slot) {
        BlockPos pos = panel.getBlockPos();
        for (double x : new double[]{0.25, 0.75}) {
            for (double y : new double[]{0.25, 0.75}) {
                Vec3 point = Vec3.atLowerCornerOf(pos).add(x, y, 0);
                if (FactoryPanelBlock.getTargetedSlot(pos, panel.getBlockState(), point) == slot)
                    return new BlockHitResult(point, Direction.NORTH, pos, false);
            }
        }
        throw new AssertionError("No hit coordinate for " + slot);
    }

    private static void aim(FakePlayer player, BlockHitResult hit) {
        Vec3 point = hit.getLocation();
        player.setPos(point.x, point.y - player.getEyeHeight(), point.z - 2);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, point);
    }

    private static void placeAdditional(GameTestHelper helper, FactoryPanelBlockEntity panel,
            FakePlayer player, PanelSlot slot, GaugeVariant variant, boolean sneak) {
        boolean occupied = panel.panels.get(slot).isActive();
        ItemStack stack = tuned(variant, player);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockHitResult hit = hit(panel, slot);
        aim(player, hit);
        player.setShiftKeyDown(sneak);
        if (sneak) {
            ((BlockItem) stack.getItem()).place(new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, hit)));
        } else {
            BlockState state = panel.getBlockState();
            state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
        }
        player.setShiftKeyDown(false);
        if (!occupied) {
            helper.assertTrue(stack.getCount() == 1, "Additional placement must consume exactly one item");
            helper.assertTrue(GaugeVariant.fromPanel(panel, slot) == variant, "Placed slot must use the held item's identity");
        }
    }

    private static void wrench(GameTestHelper helper, FactoryPanelBlockEntity panel, FakePlayer player, PanelSlot slot) {
        AllBlocks.FACTORY_GAUGE.get().onSneakWrenched(panel.getBlockState(),
                new UseOnContext(player, InteractionHand.MAIN_HAND, hit(panel, slot)));
    }

    private static FactoryPanelBlockEntity load(GameTestHelper helper, FactoryPanelBlockEntity panel, CompoundTag tag) {
        FactoryPanelBlockEntity copy = (FactoryPanelBlockEntity) BlockEntity.loadStatic(panel.getBlockPos(), panel.getBlockState(), tag);
        helper.assertTrue(copy != null, "Gauge NBT must reload");
        copy.setLevel(helper.getLevel());
        return copy;
    }

    private static List<ItemStack> drops(GameTestHelper helper, BlockPos pos) {
        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1.5)).stream()
                .map(ItemEntity::getItem).toList();
    }

    private static void assertStacks(GameTestHelper helper, List<ItemStack> stacks, int original, int expanded, int industrial) {
        int[] expected = {original, expanded, industrial};
        for (GaugeVariant variant : GaugeVariant.values()) {
            int count = stacks.stream().filter(stack -> stack.is(variant.item())).mapToInt(ItemStack::getCount).sum();
            helper.assertTrue(count == expected[variant.ordinal()], "Expected " + expected[variant.ordinal()]
                    + " " + variant + " gauges, got " + count + " in " + stacks);
        }
    }
}
