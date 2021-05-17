package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.container.CompoundContainer;
import com.tridevmc.molecule.block.TileCrate;
import com.tridevmc.molecule.init.MoleculeContent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

public class ContainerCrate extends CompoundContainer {
    private final TileCrate crate;
    private final IItemHandler inventory;

    public ContainerCrate(int window, PlayerInventory playerInv, TileEntity tile) {
        super(MoleculeContent.CRATE_CONTAINER, window);
        this.crate = tile instanceof TileCrate ? (TileCrate) tile : null;
        this.inventory = crate.inventory;

        // Actual inventory
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlotFor(this.inventory, l + k * 9);
            }
        }

        // Player inventory
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.addSlotFor(playerInv, k1 + i1 * 9 + 9);
            }
        }

        // Hotbar
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlotFor(playerInv, j1);
        }
    }

    public ContainerCrate(int window, PlayerInventory playerInventory, PacketBuffer data) {
        this(window, playerInventory, playerInventory.player.level.getBlockEntity(data.readBlockPos()));
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (this.crate.getLevel().getBlockEntity(this.crate.getBlockPos()) != this.crate) {
            return false;
        } else {
            return player.distanceToSqr((double) this.crate.getBlockPos().getX() + 0.5D,
                    (double) this.crate.getBlockPos().getY() + 0.5D,
                    (double) this.crate.getBlockPos().getZ() + 0.5D) <= 64D;
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack out = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackSlot = slot.getItem();
            out = stackSlot.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.moveItemStackTo(stackSlot, this.inventory.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackSlot, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (stackSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return out;
    }
}
