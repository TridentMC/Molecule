package com.tridevmc.molecule.ui;

import com.tridevmc.compound.ui.container.CompoundContainer;
import com.tridevmc.molecule.block.TileCrate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCrate extends CompoundContainer {
    private final TileCrate crate;
    private final IItemHandler inventory;

    public ContainerCrate(InventoryPlayer playerInv, TileCrate crate) {
        this.crate = crate;
        this.inventory = crate.inventory;

        // Actual inventory
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new SlotItemHandler(this.inventory, l + k * 9, this.inventorySlots.size(), 0));
            }
        }

        // Player inventory
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(playerInv, k1 + i1 * 9 + 9, this.inventorySlots.size(), 0));
            }
        }

        // Hotbar
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInv, j1, this.inventorySlots.size(), 0));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (this.crate.getWorld().getTileEntity(this.crate.getPos()) != this.crate) {
            return false;
        } else {
            return player.getDistanceSq((double) this.crate.getPos().getX() + 0.5D,
                    (double) this.crate.getPos().getY() + 0.5D,
                    (double) this.crate.getPos().getZ() + 0.5D) <= 64D;
        }
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack out = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stackSlot = slot.getStack();
            out = stackSlot.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.mergeItemStack(stackSlot, this.inventory.getSlots(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stackSlot, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (stackSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return out;
    }

}
