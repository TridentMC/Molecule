package com.tridevmc.molecule.proxy;


import com.tridevmc.molecule.ui.UIGallery;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

public class ClientProxy extends CommonProxy {

    public void setup() {
        super.setup();
        NeoForge.EVENT_BUS.addListener(this::onKey);
    }

    private void onKey(InputEvent.Key event) {
        if (event.getKey() != GLFW.GLFW_KEY_F8 || event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            if (!(minecraft.screen instanceof UIGallery)) {
                minecraft.setScreen(new UIGallery(minecraft.screen));
            }
        });
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        //ModelLoader.setCustomModelResourceLocation(item, meta,
        //    new ModelResourceLocation(Molecule.MOD_ID + ":" + id, "inventory"));
    }

}
