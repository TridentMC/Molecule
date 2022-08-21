package com.tridevmc.molecule.block;

import com.tridevmc.molecule.init.MoleculeContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrateBlockEntity extends BlockEntity {

    public ItemStackHandler inventory = new ItemStackHandler(27);

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(MoleculeContent.CRATE_TILE, pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.inventory.deserializeNBT(compound.getCompound("inv"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("inv", this.inventory.serializeNBT());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return LazyOptional.of(() -> (T) this.inventory);
        }

        return super.getCapability(cap, side);
    }
}
