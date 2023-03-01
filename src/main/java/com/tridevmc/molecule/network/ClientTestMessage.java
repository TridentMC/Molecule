package com.tridevmc.molecule.network;


import com.tridevmc.compound.network.message.RegisteredMessage;
import net.minecraftforge.fml.LogicalSide;

@RegisteredMessage(destination = LogicalSide.CLIENT, channel = "molecule")
public class ClientTestMessage extends TestMessage {

    public ClientTestMessage() {
    }

    public ClientTestMessage(boolean genValues) {
        super(genValues);
    }

}
