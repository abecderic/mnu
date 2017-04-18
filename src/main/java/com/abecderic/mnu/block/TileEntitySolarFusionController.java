package com.abecderic.mnu.block;

import com.abecderic.mnu.fluid.MNUFluids;
import com.abecderic.mnu.util.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class TileEntitySolarFusionController extends TileEntity implements ITickable
{
    private static final int STORAGE = 81920;
    private static final int MAX_IN = 512;
    private static final int ENERGY_USAGE = 10240;
    private static final int TANK_STORAGE = 64000000;
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_IN, 0);
    private MicrobucketsFluidTank tankIn = new MicrobucketsFluidTank(TANK_STORAGE);
    private MicrobucketsFluidTank tankOut = new MicrobucketsFluidTank(TANK_STORAGE);
    private TwoMicrobucketsFluidTanksHandler handler = new TwoMicrobucketsFluidTanksHandler(MNUFluids.fluidDarkMatter, tankIn, tankOut);
    private int tickPart;

    public TileEntitySolarFusionController()
    {
        super();
        tickPart = (int)(Math.random() * 20D);
    }

    @Override
    public void update()
    {
        if (!world.isRemote && world.getTotalWorldTime() % 20 == tickPart)
        {

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
            return (T) handler;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        energyStorage = new EnergyStorageInternal(STORAGE, MAX_IN, 0, compound.getInteger("storage"));
        tankIn.deserializeNBT(compound.getCompoundTag("tankIn"));
        tankOut.deserializeNBT(compound.getCompoundTag("tankOut"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setInteger("storage", energyStorage.getEnergyStored());
        compound.setTag("tankIn", tankIn.serializeNBT());
        compound.setTag("tankOut", tankOut.serializeNBT());
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
    }

    public ITextComponent getEnergyString()
    {
        return new TextComponentTranslation("msg.solar_fusion.energy", energyStorage.getEnergyStoredText(), energyStorage.getMaxEnergyStoredText());
    }

    public ITextComponent getTankInString()
    {
        FluidStack fluidStack = tankIn.getFluid();
        if (fluidStack != null)
        {
            if (fluidStack.getFluid() != null)
            {
                return new TextComponentTranslation("msg.solar_fusion.tank", fluidStack.getFluid().getLocalizedName(fluidStack), tankIn.getFluidAmountText(), tankIn.getCapacityText());
            }
        }
        return new TextComponentTranslation("msg.solar_fusion.tank.empty");
    }

    public ITextComponent getTankOutString()
    {
        FluidStack fluidStack = tankOut.getFluid();
        if (fluidStack != null)
        {
            if (fluidStack.getFluid() != null)
            {
                return new TextComponentTranslation("msg.solar_fusion.tank", fluidStack.getFluid().getLocalizedName(fluidStack), tankOut.getFluidAmountText(), tankOut.getCapacityText());
            }
        }
        return new TextComponentTranslation("msg.solar_fusion.tank.empty");
    }
}
