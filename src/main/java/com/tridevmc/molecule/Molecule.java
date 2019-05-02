package com.tridevmc.molecule;


import com.tridevmc.compound.config.CompoundConfig;
import com.tridevmc.compound.network.core.CompoundNetwork;
import com.tridevmc.molecule.config.MoleculeConfig;
import com.tridevmc.molecule.init.MLBlocks;
import com.tridevmc.molecule.network.ClientTestMessage;
import com.tridevmc.molecule.network.ServerTestMessage;
import com.tridevmc.molecule.proxy.ClientProxy;
import com.tridevmc.molecule.proxy.CommonProxy;
import com.tridevmc.molecule.ui.UIHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
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
        loadingContext.getModEventBus().addListener(Molecule::onRegister);

        CONFIG = CompoundConfig.of(MoleculeConfig.class, ModLoadingContext.get().getActiveContainer());
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> UIHandler::openGui);
    }

    public static void onRegister(final RegistryEvent.Register event) {
        if (event.getRegistry().getRegistryName().getPath().equals("blocks")) {
            MLBlocks.registerBlocks(event.getRegistry());
            return;
        }
        if (event.getRegistry().getRegistryName().getPath().equals("items")) {
            MLBlocks.registerItemBlocks(event.getRegistry());
            return;
        }
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

    private void onSetup(FMLCommonSetupEvent e) {
        PROXY.setup();
        MinecraftForge.EVENT_BUS.register(Molecule.class);
        CompoundNetwork.createNetwork(ModLoadingContext.get().getActiveContainer(), "molecule");
    }

}
