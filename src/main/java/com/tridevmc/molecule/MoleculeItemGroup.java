package com.tridevmc.molecule;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MoleculeItemGroup extends CreativeModeTab {

    public MoleculeItemGroup(String name) {
        super(name);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(Items.TRIDENT, 1);
    }
}
