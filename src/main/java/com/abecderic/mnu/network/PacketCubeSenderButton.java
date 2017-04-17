package com.abecderic.mnu.network;

import com.abecderic.mnu.block.TileEntityCubeSender;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCubeSenderButton implements IMessage
{
    private int dimension;
    private BlockPos pos;
    private int redstoneState;
    private boolean energyOnly;
    private int cubeHops;

    public PacketCubeSenderButton()
    {
    }

    public PacketCubeSenderButton(int dimension, BlockPos pos, int redstoneState, boolean energyOnly, int cubeHops)
    {
        this.dimension = dimension;
        this.pos = pos;
        this.redstoneState = redstoneState;
        this.energyOnly = energyOnly;
        this.cubeHops = cubeHops;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        dimension = buf.readInt();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        redstoneState = buf.readInt();
        energyOnly = buf.readBoolean();
        cubeHops = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(dimension);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(redstoneState);
        buf.writeBoolean(energyOnly);
        buf.writeByte(cubeHops);
    }

    public static class Handler implements IMessageHandler<PacketCubeSenderButton, IMessage>
    {
        @Override
        public IMessage onMessage(PacketCubeSenderButton message, MessageContext ctx)
        {
            TileEntity te = DimensionManager.getWorld(message.dimension).getTileEntity(message.pos);
            if (te != null && te instanceof TileEntityCubeSender)
            {
                TileEntityCubeSender cubeSender = (TileEntityCubeSender)te;
                cubeSender.setRedstoneMode(message.redstoneState);
                cubeSender.setEnergyOnly(message.energyOnly);
                cubeSender.setCubeHops(message.cubeHops);
            }
            return null;
        }
    }
}
