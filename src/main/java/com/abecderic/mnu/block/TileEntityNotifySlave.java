package com.abecderic.mnu.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityNotifySlave extends TileEntity
{
    private BlockPos pos;

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey("mx") && compound.hasKey("my") && compound.hasKey("mz"))
        {
            pos = new BlockPos(compound.getInteger("mx"), compound.getInteger("my"), compound.getInteger("mz"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        if (pos != null)
        {
            compound.setInteger("mx", pos.getX());
            compound.setInteger("my", pos.getY());
            compound.setInteger("mz", pos.getZ());
        }
        return compound;
    }

    public void setMaster(BlockPos pos)
    {
        this.pos = pos;
        markDirty();
    }

    public BlockPos getMaster()
    {
        return pos;
    }
}
