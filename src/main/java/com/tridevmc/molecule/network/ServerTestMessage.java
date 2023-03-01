package com.tridevmc.molecule.network;

import com.tridevmc.compound.network.message.RegisteredMessage;
import net.minecraftforge.fml.LogicalSide;

@RegisteredMessage(destination = LogicalSide.SERVER, channel = "molecule")
public class ServerTestMessage extends TestMessage {

    public ServerTestMessage() {
    }

    public ServerTestMessage(boolean genValues) {
        super(genValues);
    }

}
