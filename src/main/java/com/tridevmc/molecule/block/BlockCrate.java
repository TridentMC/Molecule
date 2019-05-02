package com.tridevmc.molecule.block;

import com.tridevmc.molecule.ui.ContainerCrate;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockCrate extends BlockContainer {
    public BlockCrate(Properties builder) {
        super(builder);
        new TileCrate();
        this.setRegistryName("molecule:crate");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileCrate();
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && player instanceof EntityPlayerMP && worldIn.getTileEntity(pos) instanceof TileCrate) {
            NetworkHooks.openGui((EntityPlayerMP) player, new IInteractionObject() {
                @Override
                public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
                    return new ContainerCrate(playerInventory, (TileCrate) worldIn.getTileEntity(pos));
                }

                @Override
                public String getGuiID() {
                    return "molecule:crate";
                }

                @Override
                public ITextComponent getName() {
                    return new TextComponentString("");
                }

                @Override
                public boolean hasCustomName() {
                    return false;
                }

                @Nullable
                @Override
                public ITextComponent getCustomName() {
                    return new TextComponentString("");
                }
            }, pos);
        }

        return true;

    }
}
