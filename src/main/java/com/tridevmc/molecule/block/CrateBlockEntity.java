package com.tridevmc.molecule.block;

import com.tridevmc.molecule.init.MoleculeContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CrateBlockEntity extends BlockEntity {

    public ItemStackHandler inventory = new ItemStackHandler(27);

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(MoleculeContent.CRATE_TILE, pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("inv").ifPresent(inventoryInput -> this.inventory.deserialize(inventoryInput));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.inventory.serialize(output.child("inv"));
    }

}
