package com.abecderic.mnu.network;

import com.abecderic.mnu.MNU;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MNUNetwork
{
    public static SimpleNetworkWrapper snw;

    public static void init()
    {
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(MNU.MODID);
        int i = 0;
        snw.registerMessage(PacketCubeSender.Handler.class, PacketCubeSender.class, i++, Side.CLIENT);
        snw.registerMessage(PacketCubeSenderButton.Handler.class, PacketCubeSenderButton.class, i++, Side.SERVER);
    }
}
