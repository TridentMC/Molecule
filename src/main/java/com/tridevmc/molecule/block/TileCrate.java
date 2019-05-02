package com.tridevmc.molecule.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileCrate extends TileEntity {

    public static final TileEntityType<TileCrate> TYPE = TileEntityType.register("molecule:crate", TileEntityType.Builder.create(TileCrate::new));

    public ItemStackHandler inventory = new ItemStackHandler(27);

    public TileCrate() {
        super(TYPE);
    }

    @Override
    public void read(NBTTagCompound compound) {
        super.read(compound);
        this.inventory.deserializeNBT(compound.getCompound("inv"));
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.put("inv", this.inventory.serializeNBT());
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return LazyOptional.of(() -> (T) this.inventory);
        }

        return super.getCapability(cap, side);
    }
}
