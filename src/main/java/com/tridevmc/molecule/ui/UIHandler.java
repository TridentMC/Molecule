package com.tridevmc.molecule.ui;

import com.tridevmc.molecule.block.TileCrate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class UIHandler {

    public static GuiScreen openGui(FMLPlayMessages.OpenContainer openContainer) {
        BlockPos pos = openContainer.getAdditionalData().readBlockPos();
        Minecraft instance = Minecraft.getInstance();
        WorldClient world = instance.world;
        if (world.getTileEntity(pos) instanceof TileCrate) {
            return new UICrate(new ContainerCrate(instance.player.inventory, (TileCrate) world.getTileEntity(pos)));
        }

        return null;
    }

}
