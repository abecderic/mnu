package com.abecderic.mnu.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MultipleFluidTanks implements IFluidHandler
{
    private int amount;
    private FluidTank[] tanks;

    public MultipleFluidTanks(int amount)
    {
        this.amount = amount;
        this.tanks = new FluidTank[amount];
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        List<IFluidTankProperties> properties = new ArrayList<>();
        for (int i = 0; i < amount; i++)
        {
            for (IFluidTankProperties property : tanks[i].getTankProperties())
            {
                properties.add(property);
            }
        }
        return properties.toArray(new IFluidTankProperties[properties.size()]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        for (int i = 0; i < amount; i++)
        {
            if (tanks[i].getFluid().isFluidEqual(resource))
            {
                return tanks[i].fill(resource, doFill);
            }
        }
        for (int i = 0; i < amount; i++)
        {
            if (tanks[i].getFluid() == null || tanks[i].getFluidAmount() <= 0)
            {
                return tanks[i].fill(resource, doFill);
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        for (int i = 0; i < amount; i++)
        {
            FluidStack drain = tanks[i].drain(resource, doDrain);
            if (drain != null)
            {
                return drain;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return null;
    }

    public void deserializeNBT(NBTTagCompound compound)
    {
        amount = compound.getInteger("size");
        for (int i = 0; i < amount; i++)
        {
            tanks[i].readFromNBT(compound.getCompoundTag("tank_" + i));
        }
    }

    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("size", amount);
        for (int i = 0; i < amount; i++)
        {
            NBTTagCompound tank = new NBTTagCompound();
            tanks[i].writeToNBT(tank);
            compound.setTag("tank_" + i, tank);
        }
        return compound;
    }
}
