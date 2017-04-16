package com.abecderic.mnu.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.*;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class MicrobucketsFluidTank implements IFluidTank, INBTSerializable<NBTTagCompound>
{
    private DecimalFormat df = new DecimalFormat("#,##0.000");

    private TileEntity tile;
    private Fluid fluid;
    private int microbucketsCapacity;
    private int microbucketsVolume;
    private boolean canFill = true;
    private boolean canDrain = true;

    public MicrobucketsFluidTank(int capacity)
    {
        this(null, capacity, 0);
    }

    public MicrobucketsFluidTank(Fluid fluid, int microbucketsCapacity, int microbucketsVolume)
    {
        this.fluid = fluid;
        this.microbucketsCapacity = microbucketsCapacity;
        this.microbucketsVolume = microbucketsVolume;
    }

    @Nullable
    @Override
    public FluidStack getFluid()
    {
        if (fluid != null)
        {
            return new FluidStack(fluid, microbucketsVolume / 1000);
        }
        else
        {
            return null;
        }
    }

    @Override
    public int getFluidAmount()
    {
        return microbucketsVolume / 1000;
    }

    @Override
    public int getCapacity()
    {
        return microbucketsCapacity / 1000;
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return new FluidTankInfo(getFluid(), getCapacity());
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (!canFill || resource == null || resource.amount <= 0)
        {
            return 0;
        }

        if (!doFill)
        {
            if (fluid == null)
            {
                return Math.min(getCapacity(), resource.amount);
            }

            if (fluid != resource.getFluid())
            {
                return 0;
            }

            return Math.min(getCapacity() - getFluidAmount(), resource.amount);
        }

        if (fluid == null)
        {
            fluid = resource.getFluid();
            microbucketsVolume = Math.min(getCapacity(), resource.amount) * 1000;

            if (tile != null)
            {
                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(getFluid(), tile.getWorld(), tile.getPos(), this, getFluidAmount()));
            }
            return getFluidAmount();
        }

        if (fluid != resource.getFluid())
        {
            return 0;
        }
        int filled = getCapacity() - getFluidAmount();

        if (resource.amount < filled)
        {
            microbucketsVolume += resource.amount * 1000;
            filled = resource.amount;
        }
        else
        {
            microbucketsVolume = microbucketsCapacity;
        }

        if (tile != null)
        {
            FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(getFluid(), tile.getWorld(), tile.getPos(), this, filled));
        }
        return filled;
    }

    /**
     * @return amount in microbuckets that was accepted by the tank
     */
    public int fillMicrobuckets(Fluid fluid, int microbuckets)
    {
        if (fluid == null || microbuckets <= 0)
        {
            return 0;
        }

        if (this.fluid == null)
        {
            this.fluid = fluid;
            this.microbucketsVolume = Math.min(microbuckets, this.microbucketsCapacity);

            if (tile != null)
            {
                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(getFluid(), tile.getWorld(), tile.getPos(), this, getFluidAmount()));
            }

            return this.microbucketsVolume;
        }
        if (this.fluid != fluid)
        {
            return 0;
        }
        int filled = microbucketsCapacity - microbucketsVolume;

        if (microbuckets < filled)
        {
            microbucketsVolume += microbuckets;
            filled = microbuckets;
        }
        else
        {
            microbucketsVolume = microbucketsCapacity;
        }

        if (tile != null)
        {
            FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(getFluid(), tile.getWorld(), tile.getPos(), this, filled / 1000));
        }
        return filled;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (!canDrain || fluid == null || maxDrain <= 0)
        {
            return null;
        }

        int drained = maxDrain;
        if (getFluidAmount() < drained)
        {
            drained = getFluidAmount();
        }

        FluidStack stack = new FluidStack(fluid, drained);
        if (doDrain)
        {
            microbucketsVolume -= drained * 1000;
            if (microbucketsVolume <= 0)
            {
                fluid = null;
            }

            if (tile != null)
            {
                FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(getFluid(), tile.getWorld(), tile.getPos(), this, drained));
            }
        }
        return stack;
    }

    public int drainMicrobuckets(Fluid fluid, int maxDrain)
    {
        if (this.fluid == null || maxDrain <= 0 || this.fluid != fluid)
        {
            return 0;
        }

        int drained = maxDrain;
        if (microbucketsVolume < drained)
        {
            drained = microbucketsVolume;
        }

        microbucketsVolume -= drained;
        if (microbucketsVolume <= 0)
        {
            this.fluid = null;
        }

        if (tile != null)
        {
            FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(getFluid(), tile.getWorld(), tile.getPos(), this, drained / 1000));
        }
        return drained;
    }

    public void setTile(TileEntity tile)
    {
        this.tile = tile;
    }

    public boolean canFill()
    {
        return canFill;
    }

    public void setCanFill(boolean canFill)
    {
        this.canFill = canFill;
    }

    public boolean canDrain()
    {
        return canDrain;
    }

    public void setCanDrain(boolean canDrain)
    {
        this.canDrain = canDrain;
    }

    public String getFluidAmountText()
    {
        return df.format(microbucketsVolume / 1000D);
    }

    public String getCapacityText()
    {
        return df.format(microbucketsCapacity / 1000D);
    }

    public int getMicrobucketsCapacity()
    {
        return microbucketsCapacity;
    }

    public int getMicrobucketsVolume()
    {
        return microbucketsVolume;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
        if (fluid != null)
        {
            compound.setString("fluid", fluid.getName());
        }
        compound.setInteger("capacity", microbucketsCapacity);
        compound.setInteger("volume", microbucketsVolume);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("fluid"))
        {
            fluid = FluidRegistry.getFluid(compound.getString("fluid"));
        }
        microbucketsCapacity = compound.getInteger("capacity");
        microbucketsVolume = compound.getInteger("volume");
    }
}
