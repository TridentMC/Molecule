package com.tridevmc.molecule.init;

import com.tridevmc.molecule.block.BlockCrate;
import com.tridevmc.molecule.block.CrateBlockEntity;
import com.tridevmc.molecule.ui.CrateMenu;
import com.tridevmc.molecule.ui.CrateUI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegisterEvent;

public class MoleculeContent {

    public static BlockCrate CRATE = new BlockCrate(Block.Properties.of().mapColor(MapColor.WOOD));
    public static BlockEntityType<CrateBlockEntity> CRATE_TILE;
    public static MenuType<CrateMenu> CRATE_MENU;

    @SubscribeEvent
    public static void onRegisterEvent(final RegisterEvent e) {
        e.register(BuiltInRegistries.BLOCK.key(), MoleculeContent::registerBlocks);
        e.register(BuiltInRegistries.ITEM.key(), MoleculeContent::registerItemBlocks);
        e.register(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), MoleculeContent::registerBlockEntityTypes);
        e.register(BuiltInRegistries.MENU.key(), MoleculeContent::registerMenus);
    }

    //@SubscribeEvent
    //public static void onCreativeTabRegisterEvent(CreativeModeTabEvent.Register e) {
    //    //e.registerCreativeModeTab(new ResourceLocation(Molecule.MOD_ID, "molecule"),
    //    //                          b -> b.title(Component.literal("Molecule"))
    //    //                                  .icon(() -> new ItemStack(CRATE))
    //    //                                  .displayItems((itemDisplayParameters, output) -> output.accept(new ItemStack(CRATE)))
    //    //);
    //}

    public static void registerBlocks(RegisterEvent.RegisterHelper<Block> registry) {
        registry.register(ResourceLocation.fromNamespaceAndPath("molecule", "crate"), CRATE);
    }

    public static void registerItemBlocks(RegisterEvent.RegisterHelper<Item> registry) {
        registry.register(ResourceLocation.fromNamespaceAndPath("molecule", "crateitem"), new BlockItem(CRATE, new Item.Properties()));
    }

    public static void registerMenus(RegisterEvent.RegisterHelper<MenuType<?>> registry) {
        CRATE_MENU = IMenuTypeExtension.create(CrateMenu::new);
        registry.register(ResourceLocation.fromNamespaceAndPath("molecule", "crate"), CRATE_MENU);
    }

    @SubscribeEvent
    public static void onRegisterMenuScreensEvent(RegisterMenuScreensEvent e) {
        e.register(CRATE_MENU, CrateUI::new);
    }

    public static void registerBlockEntityTypes(RegisterEvent.RegisterHelper<BlockEntityType<?>> registry) {
        CRATE_TILE = registerTile(registry, ResourceLocation.fromNamespaceAndPath("molecule", "crate"), CrateBlockEntity::new, CRATE);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerTile(RegisterEvent.RegisterHelper<BlockEntityType<?>> registry, ResourceLocation name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier, Block... validBlocks) {
        BlockEntityType<T> type = BlockEntityType.Builder.of(blockEntitySupplier, validBlocks).build(null);
        registry.register(name, type);
        return type;
    }

}
