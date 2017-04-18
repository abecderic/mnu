package com.abecderic.mnu.block;

import com.abecderic.mnu.fluid.MNUFluids;
import com.abecderic.mnu.util.EnergyStorageInternal;
import com.abecderic.mnu.util.MicrobucketsFluidTank;
import com.abecderic.mnu.util.TwoMicrobucketsFluidTanksHandler;
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
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class TileEntitySolarFusionController extends TileEntity implements ITickable, INotifyMaster
{
    private static final int STORAGE = 81920;
    private static final int MAX_IN = 512;
    private static final int TANK_STORAGE = 64000000;
    private static final int ENERGY_USAGE = 4;
    private static final int DM_USAGE = 8;
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_IN, 0);
    private MicrobucketsFluidTank tankIn = new MicrobucketsFluidTank(TANK_STORAGE);
    private MicrobucketsFluidTank tankOut = new MicrobucketsFluidTank(TANK_STORAGE);
    private TwoMicrobucketsFluidTanksHandler handler = new TwoMicrobucketsFluidTanksHandler(MNUFluids.fluidDarkMatter, tankIn, tankOut);
    private int tickPart;
    private boolean isComplete;
    private Set<BlockPos> mirrors = new HashSet<>();
    private int mirrorsY = -1;

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
            if (!isComplete) return;
            if (world.isRaining() || !world.isDaytime()) return;
            if (energyStorage.getEnergyStored() >= ENERGY_USAGE * mirrors.size())
            {
                if (tankOut.getMicrobucketsCapacity() - tankOut.getMicrobucketsVolume() >= mirrors.size())
                {
                    if (tankIn.getMicrobucketsVolume() >= mirrors.size() * DM_USAGE)
                    {
                        energyStorage.removeEnergy(ENERGY_USAGE * mirrors.size());
                        tankIn.drainMicrobuckets(MNUFluids.fluidDarkMatter, DM_USAGE * mirrors.size());
                        tankOut.fillMicrobuckets(MNUFluids.fluidMNU, mirrors.size());
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
        mirrorsY = compound.getInteger("mirrorsY");
        int[] mirrorsArray = compound.getIntArray("mirrors");
        mirrors = new HashSet<>();
        for (int i = 0; i < mirrorsArray.length; i += 2)
        {
            mirrors.add(new BlockPos(mirrorsArray[i], mirrorsY, mirrorsArray[i+1]));
        }
        isComplete = compound.getBoolean("complete");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setInteger("storage", energyStorage.getEnergyStored());
        compound.setTag("tankIn", tankIn.serializeNBT());
        compound.setTag("tankOut", tankOut.serializeNBT());
        int[] mirrorsArray = new int[mirrors.size() * 2];
        int i = 0;
        for (BlockPos mirror : mirrors)
        {
            mirrorsArray[i++] = mirror.getX();
            mirrorsArray[i++] = mirror.getZ();
        }
        compound.setInteger("mirrorsY", mirrorsY);
        compound.setIntArray("mirrors", mirrorsArray);
        compound.setBoolean("complete", isComplete);
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

    public ITextComponent getMirrorsString()
    {
        if (mirrors != null && !mirrors.isEmpty())
        {
            return new TextComponentTranslation("msg.solar_fusion.mirrors", mirrors.size(), getMirrorsYLevel());
        }
        return new TextComponentTranslation("msg.solar_fusion.mirrors.empty");
    }

    public boolean isComplete()
    {
        return isComplete;
    }

    public void setComplete(boolean complete)
    {
        isComplete = complete;
    }

    public int getMirrorsYLevel()
    {
        return mirrorsY;
    }

    @Override
    public void addBlock(BlockPos pos)
    {
        System.out.println("add block " + pos);
        if (mirrorsY < 0 || pos.getY() == mirrorsY)
        {
            mirrors.add(pos);
            if (mirrorsY < 0)
            {
                mirrorsY = pos.getY();
            }
        }
    }

    @Override
    public void removeBlock(BlockPos pos)
    {
        System.out.println("remove block " + pos);
        if (pos.getY() > this.pos.getY())
        {
            setComplete(false);
        }
        else
        {
            mirrors.remove(pos);
            if (mirrors.isEmpty())
            {
                mirrorsY = -1;
            }
        }
    }
}
