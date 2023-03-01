package com.tridevmc.molecule;

import com.tridevmc.compound.config.CompoundConfig;
import com.tridevmc.compound.network.core.CompoundNetwork;
import com.tridevmc.molecule.config.MoleculeConfig;
import com.tridevmc.molecule.init.MoleculeContent;
import com.tridevmc.molecule.network.ClientTestMessage;
import com.tridevmc.molecule.network.ServerTestMessage;
import com.tridevmc.molecule.proxy.ClientProxy;
import com.tridevmc.molecule.proxy.CommonProxy;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
    public static Molecule INSTANCE;
    public static MoleculeConfig CONFIG;

    public Molecule() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

        FMLJavaModLoadingContext loadingContext = FMLJavaModLoadingContext.get();
        loadingContext.getModEventBus().addListener(this::onSetup);
        loadingContext.getModEventBus().register(MoleculeContent.class);

        CONFIG = CompoundConfig.of(MoleculeConfig.class, ModLoadingContext.get().getActiveContainer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        var player = e.getEntity();
        if (player instanceof ServerPlayer) {
            new ServerTestMessage(true).sendTo((ServerPlayer) player);
        } else if (player instanceof AbstractClientPlayer) {
            new ClientTestMessage(true).sendToServer();
        }
    }

    private void onSetup(FMLCommonSetupEvent e) {
        PROXY.setup();
        MinecraftForge.EVENT_BUS.register(Molecule.class);
        CompoundNetwork.createNetwork(ModLoadingContext.get().getActiveContainer(), "molecule");
    }

}
