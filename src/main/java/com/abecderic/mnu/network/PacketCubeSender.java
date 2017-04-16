package com.abecderic.mnu.network;

import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.util.EnergyStorageInternal;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCubeSender implements IMessage
{
    private BlockPos pos;
    private int energy;

    public PacketCubeSender()
    {
    }

    public PacketCubeSender(BlockPos pos, int energy)
    {
        this.pos = pos;
        this.energy = energy;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        energy = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(energy);
    }

    public static class Handler implements IMessageHandler<PacketCubeSender, IMessage>
    {

        @Override
        public IMessage onMessage(PacketCubeSender message, MessageContext ctx)
        {
            TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos);
            if (te != null && te instanceof TileEntityCubeSender)
            {
                TileEntityCubeSender cubeSender = (TileEntityCubeSender)te;
                IEnergyStorage energyStorage = cubeSender.getCapability(CapabilityEnergy.ENERGY, null);
                if (energyStorage != null && energyStorage instanceof EnergyStorageInternal)
                {
                    ((EnergyStorageInternal)energyStorage).setEnergy(message.energy);
                }
            }
            return null;
        }
    }
}
