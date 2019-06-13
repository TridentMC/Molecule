package com.tridevmc.molecule;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class MoleculeItemGroup extends ItemGroup {

    public MoleculeItemGroup(String name) {
        super(name);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.TRIDENT, 1);
    }
}
