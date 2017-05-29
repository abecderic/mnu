package com.abecderic.mnu.block;

import com.abecderic.mnu.util.EnergyStorageCreative;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class TileEntityFECreative extends TileEntity implements ITickable
{
    private static final int MAX_TRANSFER = 1024000;

    private EnergyStorageCreative energyStorage = new EnergyStorageCreative();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
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
        return super.getCapability(capability, facing);
    }

    @Override
    public void update()
    {
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            BlockPos pos = getPos().offset(facing);
            TileEntity te = world.getTileEntity(pos);
            if (te != null)
            {
                IEnergyStorage energy = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (energy != null && energy.getEnergyStored() < energy.getMaxEnergyStored())
                {
                    energy.receiveEnergy(MAX_TRANSFER, false);
                }
            }
        }
    }
}
