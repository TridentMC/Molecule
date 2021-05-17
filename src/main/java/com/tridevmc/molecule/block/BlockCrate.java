package com.tridevmc.molecule.block;

import com.tridevmc.molecule.ui.ContainerCrate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockCrate extends ContainerBlock {
    public BlockCrate(Properties builder) {
        super(builder);
        new TileCrate();
        this.setRegistryName("molecule:crate");
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new TileCrate();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide && player instanceof ServerPlayerEntity && world.getBlockEntity(pos) instanceof TileCrate) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Nullable
                @Override
                public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                    return new ContainerCrate(id, inventory, world.getBlockEntity(pos));
                }

                @Override
                public ITextComponent getDisplayName() {
                    return new StringTextComponent("");
                }
            }, pos);
        }

        return ActionResultType.SUCCESS;
    }
}
