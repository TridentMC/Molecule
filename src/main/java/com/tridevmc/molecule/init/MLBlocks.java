package com.tridevmc.molecule.init;

import com.tridevmc.molecule.block.BlockCrate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;

public class MLBlocks {

    static BlockCrate crate = new BlockCrate(Block.Properties.create(Material.WOOD));

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(crate);
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        ItemBlock itemBlock = new ItemBlock(crate, new Item.Properties());
        itemBlock.setRegistryName("molecule:crateitem");
        registry.register(itemBlock);
    }
}
