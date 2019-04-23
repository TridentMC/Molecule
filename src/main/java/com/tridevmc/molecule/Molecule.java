package com.tridevmc.molecule;


import com.tridevmc.compound.config.CompoundConfig;
import com.tridevmc.compound.network.core.CompoundNetwork;
import com.tridevmc.molecule.config.MoleculeConfig;
import com.tridevmc.molecule.init.MLBlocks;
import com.tridevmc.molecule.network.ClientTestMessage;
import com.tridevmc.molecule.network.ServerTestMessage;
import com.tridevmc.molecule.proxy.ClientProxy;
import com.tridevmc.molecule.proxy.CommonProxy;
import com.tridevmc.molecule.ui.MoleculeUI;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("molecule")
public final class Molecule {

    public static final String MOD_ID = "molecule";
    public static final String NAME = "Molecule";
    public static final String VERSION = "1.12.2-0.1.0";

    public static final Logger LOG = LogManager.getLogger(Molecule.NAME);
    public static CommonProxy PROXY;
    public static MoleculeItemGroup CREATIVE_TAB = new MoleculeItemGroup(Molecule.NAME);
    public static Molecule INSTANCE;
    public static MoleculeConfig CONFIG;

    public Molecule() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

        FMLJavaModLoadingContext loadingContext = FMLJavaModLoadingContext.get();
        loadingContext.getModEventBus().addListener(this::onSetup);

        CONFIG = CompoundConfig.of(MoleculeConfig.class, ModLoadingContext.get().getActiveContainer());
    }

    private void onSetup(FMLCommonSetupEvent e) {
        PROXY.setup();
        CompoundNetwork.createNetwork(ModLoadingContext.get().getActiveContainer(), "molecule");
        MinecraftForge.EVENT_BUS.register(Molecule.class);
    }

    static MoleculeUI ui;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        MLBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        MLBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        MLBlocks.registerBlockModels();
    }

    @SubscribeEvent
    public static void renderEvent(RenderGameOverlayEvent.Text event) {
        if (ui == null)
            ui = new MoleculeUI();
        Minecraft mc = Minecraft.getInstance();
        int x = (int) (mc.mouseHelper.getMouseX() * (double) mc.mainWindow.getScaledWidth() / (double) mc.mainWindow.getWidth());
        int y = (int) (mc.mouseHelper.getMouseY() * (double) mc.mainWindow.getScaledHeight() / (double) mc.mainWindow.getHeight());
        ui.render(x, y, mc.getTickLength());
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityPlayer) {
            if (e.getEntity() instanceof EntityPlayerMP) {
                new ServerTestMessage(true).sendTo((EntityPlayerMP) e.getEntity());
            } else if (e.getEntity() instanceof EntityPlayerSP) {
                new ClientTestMessage(true).sendToServer();
            }
        }
    }

}
