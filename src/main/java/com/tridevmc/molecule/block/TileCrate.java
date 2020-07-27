package com.tridevmc.molecule.block;

import com.tridevmc.molecule.init.MoleculeContent;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileCrate extends TileEntity {

    public ItemStackHandler inventory = new ItemStackHandler(27);

    public TileCrate() {
        super(MoleculeContent.CRATE_TILE);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.inventory.deserializeNBT(compound.getCompound("inv"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("inv", this.inventory.serializeNBT());
        return super.write(compound);
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
