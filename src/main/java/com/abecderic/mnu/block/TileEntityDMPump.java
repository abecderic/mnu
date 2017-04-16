package com.abecderic.mnu.block;

import com.abecderic.mnu.fluid.MNUFluids;
import com.abecderic.mnu.util.DarkMatterDeposits;
import com.abecderic.mnu.util.EnergyStorageInternal;
import com.abecderic.mnu.util.MicrobucketsFluidTank;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntityDMPump extends TileEntity implements ITickable
{
    private static final int STORAGE = 81920;
    private static final int MAX_IN = 512;
    private static final int ENERGY_USAGE = 10240;
    private static final int TANK_STORAGE = 8000000;
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_IN, 0);
    private MicrobucketsFluidTank fluidTank = new MicrobucketsFluidTank(TANK_STORAGE);
    private int tickPart;
    private int dmSize = -1;

    public TileEntityDMPump()
    {
        super();
        tickPart = (int)(Math.random() * 20D);
    }

    @Override
    public void update()
    {
        if (dmSize < 0)
        {
            dmSize = DarkMatterDeposits.getVolumeForCoords(getPos().getX(), getPos().getZ());
        }
        if (!world.isRemote && world.getTotalWorldTime() % 20 == tickPart)
        {
            if (energyStorage.getEnergyStored() >= ENERGY_USAGE)
            {
                if (fluidTank.getMicrobucketsCapacity() - fluidTank.getMicrobucketsVolume() >= dmSize)
                {
                    if (world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK)
                    {
                        energyStorage.removeEnergy(ENERGY_USAGE);
                        fluidTank.fillMicrobuckets(MNUFluids.fluidDarkMatter, dmSize);
                    }
                }
            }
            else
            {
                for (EnumFacing facing : EnumFacing.VALUES)
                {
                    BlockPos pos = getPos().offset(facing);
                    TileEntity te = world.getTileEntity(pos);
                    if (te != null)
                    {
                        IEnergyStorage energy = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                        if (energy != null)
                        {
                            int extracted = energy.extractEnergy(Math.min(MAX_IN * 20, energy.getMaxEnergyStored() - energy.getEnergyStored()), false);
                            if (extracted > 0)
                            {
                                energyStorage.addEnergy(extracted);
                            }
                        }
                    }
                }
            }
            markDirty();
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
        return new TextComponentTranslation("msg.dm_pump.energy", energyStorage.getEnergyStoredText(), energyStorage.getMaxEnergyStoredText());
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

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        energyStorage = new EnergyStorageInternal(STORAGE, MAX_IN, 0, compound.getInteger("storage"));
        fluidTank.deserializeNBT(compound.getCompoundTag("tank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setInteger("storage", energyStorage.getEnergyStored());
        compound.setTag("tank", fluidTank.serializeNBT());
        return compound;
    }
}
