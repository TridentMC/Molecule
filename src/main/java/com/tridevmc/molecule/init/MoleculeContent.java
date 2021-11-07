package com.tridevmc.molecule.init;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.tridevmc.molecule.Molecule;
import com.tridevmc.molecule.block.BlockCrate;
import com.tridevmc.molecule.block.CrateBlockEntity;
import com.tridevmc.molecule.ui.CrateMenu;
import com.tridevmc.molecule.ui.CrateUI;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;

public class MoleculeContent {

    public static BlockCrate CRATE = new BlockCrate(Block.Properties.of(Material.WOOD));
    public static BlockEntityType<CrateBlockEntity> CRATE_TILE;
    public static MenuType<CrateMenu> CRATE_MENU;

    @SubscribeEvent
    public static void onBlockRegister(final RegistryEvent.Register<Block> e) {
        registerBlocks(e.getRegistry());
    }

    @SubscribeEvent
    public static void onItemRegister(final RegistryEvent.Register<Item> e) {
        registerItemBlocks(e.getRegistry());
    }

    @SubscribeEvent
    public static void onBlockEntityRegister(final RegistryEvent.Register<BlockEntityType<?>> e) {
        registerTiles(e.getRegistry());
    }

    @SubscribeEvent
    public static void onMenuRegister(final RegistryEvent.Register<MenuType<?>> e) {
        registerMenus(e.getRegistry());
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(CRATE);
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        BlockItem itemBlock = new BlockItem(CRATE, new Item.Properties());
        itemBlock.setRegistryName("molecule:crateitem");
        registry.register(itemBlock);
    }

    public static void registerMenus(IForgeRegistry<MenuType<?>> registry) {
        CRATE_MENU = IForgeContainerType.create(CrateMenu::new);
        registry.register(CRATE_MENU.setRegistryName("molecule", "crate"));

        DistExecutor.runWhenOn(Dist.CLIENT, () -> MoleculeContent::registerScreens);
    }

    public static void registerScreens() {
        MenuScreens.register(CRATE_MENU, CrateUI::new);
    }

    public static void registerTiles(IForgeRegistry<BlockEntityType<?>> registry) {
        CRATE_TILE = registerTile(registry, new ResourceLocation("molecule", "crate"), CrateBlockEntity::new);
    }

    private static MenuType<?> registerContainer(IForgeRegistry<MenuType<?>> registry, ResourceLocation name, IContainerFactory<?> containerFactory) {
        MenuType<?> containerType = IForgeContainerType.create(containerFactory);
        registry.register(containerType.setRegistryName(name));
        return containerType;
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerTile(IForgeRegistry<BlockEntityType<?>> registry, ResourceLocation name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier) {
        Type<?> fixer = null;
        try {
            fixer = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).getChoiceType(References.BLOCK_ENTITY, name.toString());
        } catch (IllegalArgumentException e) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw e;
            }

            Molecule.LOG.warn("No data fixer registered for tile {}", name.toString());
        }
        BlockEntityType<T> type = BlockEntityType.Builder.of(blockEntitySupplier).build(fixer);
        registry.register(type.setRegistryName(name));
        return type;
    }
}
