package com.tridevmc.molecule.init;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.tridevmc.molecule.Molecule;
import com.tridevmc.molecule.block.BlockCrate;
import com.tridevmc.molecule.block.TileCrate;
import com.tridevmc.molecule.ui.ContainerCrate;
import com.tridevmc.molecule.ui.UICrate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class MoleculeContent {

    public static BlockCrate CRATE = new BlockCrate(Block.Properties.create(Material.WOOD));
    public static TileEntityType<TileCrate> CRATE_TILE;
    public static ContainerType<ContainerCrate> CRATE_CONTAINER;

    @SubscribeEvent
    public static void onBlockRegister(final RegistryEvent.Register<Block> e) {
        registerBlocks(e.getRegistry());
    }

    @SubscribeEvent
    public static void onItemRegister(final RegistryEvent.Register<Item> e) {
        registerItemBlocks(e.getRegistry());
    }

    @SubscribeEvent
    public static void onTileRegister(final RegistryEvent.Register<TileEntityType<?>> e) {
        registerTiles(e.getRegistry());
    }

    @SubscribeEvent
    public static void onContainerRegister(final RegistryEvent.Register<ContainerType<?>> e) {
        registerContainers(e.getRegistry());
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(CRATE);
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        BlockItem itemBlock = new BlockItem(CRATE, new Item.Properties());
        itemBlock.setRegistryName("molecule:crateitem");
        registry.register(itemBlock);
    }

    public static void registerContainers(IForgeRegistry<ContainerType<?>> registry) {
        CRATE_CONTAINER = IForgeContainerType.create(ContainerCrate::new);
        registry.register(CRATE_CONTAINER.setRegistryName("molecule", "crate"));

        DistExecutor.runWhenOn(Dist.CLIENT, () -> MoleculeContent::registerScreens);
    }

    public static void registerScreens() {
        ScreenManager.registerFactory(CRATE_CONTAINER, UICrate::new);
    }

    public static void registerTiles(IForgeRegistry<TileEntityType<?>> registry) {
        CRATE_TILE = registerTile(registry, new ResourceLocation("molecule", "crate"), TileCrate::new);
    }

    private static ContainerType<?> registerContainer(IForgeRegistry<ContainerType<?>> registry, ResourceLocation name, IContainerFactory<?> containerFactory) {
        ContainerType<?> containerType = IForgeContainerType.create(containerFactory);
        registry.register(containerType.setRegistryName(name));
        return containerType;
    }

    private static <T extends TileEntity> TileEntityType<T> registerTile(IForgeRegistry<TileEntityType<?>> registry, ResourceLocation name, Supplier<T> tileSupplier) {
        Type<?> fixer = null;
        try {
            fixer = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getVersion().getWorldVersion())).getChoiceType(TypeReferences.BLOCK_ENTITY, name.toString());
        } catch (IllegalArgumentException e) {
            if (SharedConstants.developmentMode) {
                throw e;
            }

            Molecule.LOG.warn("No data fixer registered for tile {}", name.toString());
        }
        TileEntityType<T> type = TileEntityType.Builder.create(tileSupplier).build(fixer);
        registry.register(type.setRegistryName(name));
        return type;
    }
}
