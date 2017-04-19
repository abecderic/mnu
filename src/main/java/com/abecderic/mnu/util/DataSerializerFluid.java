package com.abecderic.mnu.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;

public class DataSerializerFluid
{
    public static final DataSerializer<FluidStack> OPTIONAL_FLUID_STACK = new DataSerializer<FluidStack>()
    {
        public void write(PacketBuffer buf, FluidStack value)
        {
            NBTTagCompound compound = new NBTTagCompound();
            if (value != null)
            {
                value.writeToNBT(compound);
            }
            buf.writeCompoundTag(compound);
        }
        public FluidStack read(PacketBuffer buf) throws IOException
        {
            NBTTagCompound compound = buf.readCompoundTag();
            if (compound.getSize() == 0)
            {
                return null;
            }
            return FluidStack.loadFluidStackFromNBT(compound);
        }
        public DataParameter<FluidStack> createKey(int id)
        {
            return new DataParameter(id, this);
        }
    };

    public static void init()
    {
        DataSerializers.registerSerializer(OPTIONAL_FLUID_STACK);
    }
}
