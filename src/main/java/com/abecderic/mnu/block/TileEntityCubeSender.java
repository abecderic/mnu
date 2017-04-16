package com.abecderic.mnu.block;

import com.abecderic.mnu.entity.EntityCube;
import com.abecderic.mnu.util.EnergyStorageInternal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
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
        if (!world.isRemote && world.getTotalWorldTime() % 20 == 0)
        {
            EnumFacing facing = world.getBlockState(getPos()).getValue(BlockCubeSender.FACING);
            BlockPos pos = this.pos.offset(facing);
            EntityCube cube = new EntityCube(world);
            cube.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            cube.setVelocity(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ());
            world.spawnEntity(cube);
        }
    }

    public void receiveCube(EntityCube cube)
    {

    }

    public ITextComponent getEnergyString()
    {
        return new TextComponentTranslation("msg.fe_gen.energy", energyStorage.getEnergyStoredText(), energyStorage.getMaxEnergyStoredText());
    }
}
