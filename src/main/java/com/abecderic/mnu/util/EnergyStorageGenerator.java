package com.abecderic.mnu.util;

import net.minecraftforge.energy.EnergyStorage;

public class EnergyStorageGenerator extends EnergyStorage
{
    public EnergyStorageGenerator(int capacity)
    {
        super(capacity);
    }

    public EnergyStorageGenerator(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer);
    }

    public EnergyStorageGenerator(int capacity, int maxReceive, int maxExtract)
    {
        super(capacity, maxReceive, maxExtract);
    }

    public EnergyStorageGenerator(int capacity, int maxReceive, int maxExtract, int energy)
    {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void addEnergy(int energy)
    {
        super.energy = Math.min(super.energy + energy, super.capacity);
    }
}
