package com.abecderic.mnu.block;

import com.abecderic.mnu.fluid.MNUFluids;
import com.abecderic.mnu.util.DarkMatterDeposits;
import com.abecderic.mnu.util.EnergyStorageInternal;
import com.abecderic.mnu.util.MicrobucketsFluidTank;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntityDMPump extends TileEntity implements ITickable
{
    private static final int STORAGE = 64000;
    private static final int MAX_IN = 512;
    private static final int ENERGY_USAGE = 10240;
    private static final int TANK_STORAGE = 8000;
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_IN, 0);
    private MicrobucketsFluidTank fluidTank = new MicrobucketsFluidTank(TANK_STORAGE);
    private int tickPart;
    private int dmSize;

    public TileEntityDMPump()
    {
        super();
        tickPart = (int)(Math.random() * 20D);
        dmSize = DarkMatterDeposits.getVolumeForCoords(getPos().getX(), getPos().getZ());
    }

    @Override
    public void update()
    {
        if (world.getWorldTime() % 20 == tickPart)
        {
            if (energyStorage.getEnergyStored() >= ENERGY_USAGE)
            {
                energyStorage.removeEnergy(ENERGY_USAGE);
                fluidTank.fillMicrobuckets(MNUFluids.fluidDarkMatter, dmSize);
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            return true;
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            return (T) energyStorage;
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return (T) fluidTank;
        }
        return super.getCapability(capability, facing);
    }

    public ITextComponent getEnergyString()
    {
        return new TextComponentTranslation("msg.dm_pump.energy", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored());
    }

    public ITextComponent getTankString()
    {
        FluidStack fluidStack = fluidTank.getFluid();
        if (fluidStack != null)
        {
            if (fluidStack.getFluid() != null)
            {
                return new TextComponentTranslation("msg.dm_pump.tank", fluidStack.getFluid().getLocalizedName(fluidStack), fluidTank.getFluidAmountText(), fluidTank.getCapacityText());
            }
        }
        return new TextComponentTranslation("msg.dm_pump.tank.empty");
    }
}
