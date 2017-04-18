package com.abecderic.mnu.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityReactorController extends TileEntity implements ITickable, INotifyMaster
{
    private int tickPart;
    private boolean isComplete;

    public TileEntityReactorController()
    {
        super();
        tickPart = (int)(Math.random() * 20D);
    }

    @Override
    public void update()
    {
        if (!world.isRemote && world.getTotalWorldTime() % 20 == tickPart)
        {
            if (!isComplete) return;

            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        return super.writeToNBT(compound);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
    }

    public boolean isComplete()
    {
        return isComplete;
    }

    public void setComplete(boolean complete)
    {
        isComplete = complete;
    }

    @Override
    public void addBlock(BlockPos pos)
    {
        /* NO-OP */
    }

    @Override
    public void removeBlock(BlockPos pos)
    {
        setComplete(false);
    }
}
