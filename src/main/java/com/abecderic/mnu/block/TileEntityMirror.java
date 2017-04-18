package com.abecderic.mnu.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityMirror extends TileEntity
{
    private BlockPos pos;

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey("ox") && compound.hasKey("oy") && compound.hasKey("oz"))
        {
            pos = new BlockPos(compound.getInteger("ox"), compound.getInteger("oy"), compound.getInteger("oz"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        if (pos != null)
        {
            compound.setInteger("ox", pos.getX());
            compound.setInteger("oy", pos.getY());
            compound.setInteger("oz", pos.getZ());
        }
        return compound;
    }
}
