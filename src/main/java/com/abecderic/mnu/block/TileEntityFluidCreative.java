package com.abecderic.mnu.block;

import com.abecderic.mnu.util.FluidCreative;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntityFluidCreative extends TileEntity
{
    private FluidCreative fluid = new FluidCreative();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return (T) fluid;
        }
        return super.getCapability(capability, facing);
    }

    public void setFluid(Fluid fluid)
    {
        this.fluid.setFluid(fluid);
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagCompound fluid = compound.getCompoundTag("fluid");
        if (fluid.getSize() > 0)
        {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(fluid);
            setFluid(stack.getFluid());
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        FluidStack stack = fluid.drain(Fluid.BUCKET_VOLUME, false);
        if (stack != null)
        {
            NBTTagCompound fluidCompound = new NBTTagCompound();
            stack.writeToNBT(fluidCompound);
            compound.setTag("fluid", fluidCompound);
        }
        return compound;
    }
}
