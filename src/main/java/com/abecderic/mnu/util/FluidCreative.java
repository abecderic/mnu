package com.abecderic.mnu.util;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class FluidCreative implements IFluidHandler
{
    private Fluid fluid;

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        if (fluid != null)
        {
            return new IFluidTankProperties[] {new FluidTankProperties(new FluidStack(fluid, Integer.MAX_VALUE), Integer.MAX_VALUE)};
        }
        return new IFluidTankProperties[] {new FluidTankProperties(null, Integer.MAX_VALUE)};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource != null)
        {
            return resource.amount;
        }
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return resource;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (fluid != null)
        {
            return new FluidStack(fluid, maxDrain);
        }
        return null;
    }

    public void setFluid(Fluid fluid)
    {
        this.fluid = fluid;
    }
}
