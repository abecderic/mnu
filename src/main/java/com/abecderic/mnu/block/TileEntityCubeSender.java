package com.abecderic.mnu.block;

import com.abecderic.mnu.entity.EntityCube;
import com.abecderic.mnu.util.EnergyStorageInternal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityCubeSender extends TileEntity implements ITickable
{
    private static final int STORAGE = 512000;
    private static final int MAX_TRANSFER = 128;
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_TRANSFER, MAX_TRANSFER);

    @Override
    public void update()
    {

    }

    public void receiveCube(EntityCube cube)
    {

    }

    public ITextComponent getEnergyString()
    {
        return new TextComponentTranslation("msg.fe_gen.energy", energyStorage.getEnergyStoredText(), energyStorage.getMaxEnergyStoredText());
    }
}
