package com.abecderic.mnu.network;

import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.util.EnergyStorageInternal;
import com.abecderic.mnu.util.MultipleFluidTanks;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

public class PacketCubeSender implements IMessage
{
    private BlockPos pos;
    private int energy;
    private NBTTagCompound tanks;
    private byte redstoneMode;

    public PacketCubeSender()
    {
    }

    public PacketCubeSender(BlockPos pos, int energy, MultipleFluidTanks tanks, int redstoneMode)
    {
        this.pos = pos;
        this.energy = energy;
        this.tanks = tanks.serializeNBT();
        this.redstoneMode = (byte) redstoneMode;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        energy = buf.readInt();
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try
        {
            tanks = packetBuffer.readCompoundTag();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        redstoneMode = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(energy);
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeCompoundTag(tanks);
        buf.writeByte(redstoneMode);
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
                IFluidHandler fluidHandler = cubeSender.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (fluidHandler != null && fluidHandler instanceof MultipleFluidTanks)
                {
                    ((MultipleFluidTanks)fluidHandler).deserializeNBT(message.tanks);
                }
                cubeSender.setRedstoneMode(message.redstoneMode);
            }
            return null;
        }
    }
}
