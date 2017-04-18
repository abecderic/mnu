package com.abecderic.mnu.util;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageCreative implements IEnergyStorage
{
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        return maxReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return maxExtract;
    }

    @Override
    public int getEnergyStored()
    {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtract()
    {
        return true;
    }

    @Override
    public boolean canReceive()
    {
        return true;
    }
}
