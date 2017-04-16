package com.abecderic.mnu.util;

import net.minecraftforge.energy.EnergyStorage;

public class EnergyStorageInternal extends EnergyStorage
{
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

    public void addEnergy(int energy)
    {
        super.energy = Math.min(super.energy + energy, super.capacity);
    }

    public void removeEnergy(int energy)
    {
        super.energy = Math.max(super.energy - energy, 0);
    }
}
