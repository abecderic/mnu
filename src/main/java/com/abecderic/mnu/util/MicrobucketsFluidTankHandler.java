package com.abecderic.mnu.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class MicrobucketsFluidTankHandler implements IFluidHandler
{
    private MicrobucketsFluidTank tank;

    public MicrobucketsFluidTankHandler(MicrobucketsFluidTank tank)
    {
        this.tank = tank;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new IFluidTankProperties[] {tank};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return tank.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return tank.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return tank.drain(maxDrain, doDrain);
    }
}
