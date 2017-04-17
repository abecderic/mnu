package com.abecderic.mnu.util;

import net.minecraftforge.energy.EnergyStorage;

import java.text.DecimalFormat;

public class EnergyStorageInternal extends EnergyStorage
{
    private DecimalFormat df = new DecimalFormat("#,##0");

    public EnergyStorageInternal(int capacity)
    {
        super(capacity);
    }

    public EnergyStorageInternal(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer);
    }

    public EnergyStorageInternal(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract);
    }

    public EnergyStorageInternal(int capacity, int maxReceive, int maxExtract, int energy)
    {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public int addEnergy(int energy)
    {
        int toInsert = Math.min(energy, super.capacity - super.energy);
        super.energy += toInsert;
        return energy - toInsert;
    }

    public void removeEnergy(int energy)
    {
        super.energy = Math.max(super.energy - energy, 0);
    }

    public void setEnergy(int energy)
    {
        super.energy = energy;
    }

    public String getEnergyStoredText()
    {
        return df.format(super.getEnergyStored());
    }

    public String getMaxEnergyStoredText()
    {
        return df.format(super.getMaxEnergyStored());
    }
}
