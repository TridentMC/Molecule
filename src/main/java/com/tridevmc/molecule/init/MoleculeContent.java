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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class MoleculeContent {

    public static BlockCrate CRATE = new BlockCrate(Block.Properties.of().mapColor(MapColor.WOOD));
    public static BlockEntityType<CrateBlockEntity> CRATE_TILE;
    public static MenuType<CrateMenu> CRATE_MENU;

    @SubscribeEvent
    public static void onRegisterEvent(final RegisterEvent e) {
        e.register(ForgeRegistries.Keys.BLOCKS, MoleculeContent::registerBlocks);
        e.register(ForgeRegistries.Keys.ITEMS, MoleculeContent::registerItemBlocks);
        e.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, MoleculeContent::registerBlockEntityTypes);
        e.register(ForgeRegistries.Keys.MENU_TYPES, MoleculeContent::registerMenus);
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
        registry.register(new ResourceLocation("molecule", "crate"), CRATE);
    }

    public static void registerItemBlocks(RegisterEvent.RegisterHelper<Item> registry) {
        registry.register(new ResourceLocation("molecule", "crateitem"), new BlockItem(CRATE, new Item.Properties()));
    }

    public static void registerMenus(RegisterEvent.RegisterHelper<MenuType<?>> registry) {
        CRATE_MENU = IForgeMenuType.create(CrateMenu::new);
        registry.register(new ResourceLocation("molecule", "crate"), CRATE_MENU);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> MoleculeContent::registerScreens);
    }

    public static void registerScreens() {
        MenuScreens.register(CRATE_MENU, CrateUI::new);
    }

    public static void registerBlockEntityTypes(RegisterEvent.RegisterHelper<BlockEntityType<?>> registry) {
        CRATE_TILE = registerTile(registry, new ResourceLocation("molecule", "crate"), CrateBlockEntity::new);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerTile(RegisterEvent.RegisterHelper<BlockEntityType<?>> registry, ResourceLocation name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier) {
        Type<?> fixer = null;
        try {
            fixer = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getProtocolVersion())).getChoiceType(References.BLOCK_ENTITY, name.toString());
        } catch (IllegalArgumentException e) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw e;
            }

            Molecule.LOG.warn("No data fixer registered for tile {}", name.toString());
        }
        BlockEntityType<T> type = BlockEntityType.Builder.of(blockEntitySupplier).build(fixer);
        registry.register(name, type);
        return type;
    }

}
