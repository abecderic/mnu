package com.abecderic.mnu.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSerializerList
{
    public static final DataSerializer<List<BlockPos>> POS_LIST = new DataSerializer<List<BlockPos>>()
    {
        public void write(PacketBuffer buf, List<BlockPos> value)
        {
            NBTTagCompound compound = new NBTTagCompound();
            if (value == null)
            {
                compound.setInteger("size", 0);
            }
            else
            {
                compound.setInteger("size", value.size());
                int[] array = new int[value.size() * 3];
                for (int i = 0; i < value.size() * 3; i += 3)
                {
                    array[i] = value.get(i / 3).getX();
                    array[i + 1] = value.get(i / 3).getY();
                    array[i + 2] = value.get(i / 3).getZ();
                }
                compound.setIntArray("list", array);
            }
            buf.writeCompoundTag(compound);
        }
        public List<BlockPos> read(PacketBuffer buf) throws IOException
        {
            NBTTagCompound compound = buf.readCompoundTag();
            int size = compound.getInteger("size");
            if (size == 0)
            {
                return new ArrayList<>();
            }
            else
            {
                int[] array = compound.getIntArray("list");
                List<BlockPos> list = new ArrayList<>();
                for (int i = 0; i < size * 3; i += 3)
                {
                    list.add(new BlockPos(array[i], array[i + 1], array[i + 2]));
                }
                return list;
            }
        }
        public DataParameter<List<BlockPos>> createKey(int id)
        {
            return new DataParameter(id, this);
        }
    };

    public static final DataSerializer<List<Byte>> BYTE_LIST = new DataSerializer<List<Byte>>()
    {
        public void write(PacketBuffer buf, List<Byte> value)
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("size", value.size());
            byte[] array = new byte[value.size()];
            for (int i = 0; i < value.size(); i++)
            {
                array[i] = value.get(i);
            }
            compound.setByteArray("list", array);
            buf.writeCompoundTag(compound);
        }
        public List<Byte> read(PacketBuffer buf) throws IOException
        {
            NBTTagCompound compound = buf.readCompoundTag();
            int size = compound.getInteger("size");
            byte[] array = compound.getByteArray("list");
            List<Byte> list = new ArrayList<>();
            for (int i = 0; i < size; i++)
            {
                list.add(array[i]);
            }
            return list;
        }
        public DataParameter<List<Byte>> createKey(int id)
        {
            return new DataParameter(id, this);
        }
    };

    static
    {
        DataSerializers.registerSerializer(POS_LIST);
        DataSerializers.registerSerializer(BYTE_LIST);
    }
}
