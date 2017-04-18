package com.abecderic.mnu.util;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class TwoMicrobucketsFluidTanksHandler implements IFluidHandler
{
    private Fluid fluid;
    private MicrobucketsFluidTank tankIn;
    private MicrobucketsFluidTank tankOut;

    public TwoMicrobucketsFluidTanksHandler(Fluid fluid, MicrobucketsFluidTank tankIn, MicrobucketsFluidTank tankOut)
    {
        this.fluid = fluid;
        this.tankIn = tankIn;
        this.tankOut = tankOut;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new IFluidTankProperties[] {tankIn, tankOut};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource != null && resource.getFluid() != null && resource.getFluid().equals(fluid))
        {
            return tankIn.fill(resource, doFill);
        }
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return tankOut.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return tankOut.drain(maxDrain, doDrain);
    }
}
