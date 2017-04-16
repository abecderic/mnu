package com.abecderic.mnu.block;

import com.abecderic.mnu.entity.EntityCube;
import com.abecderic.mnu.util.EnergyStorageInternal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityCubeSender extends TileEntity implements ITickable
{
    private static final int STORAGE = 512000;
    private static final int MAX_TRANSFER = 128;
    private static final int CUBE_ENERGY_PER_HOP = 2048;
    private static final int CUBE_HOPS = 3;
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_TRANSFER, MAX_TRANSFER);
    private int tickPart;

    public TileEntityCubeSender()
    {
        super();
        tickPart = (int)(Math.random() * 20D);
    }

    @Override
    public void update()
    {
        if (!world.isRemote && world.getTotalWorldTime() % 20 == tickPart)
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
                        int extracted = energy.extractEnergy(Math.min(MAX_TRANSFER * 20, energy.getMaxEnergyStored() - energy.getEnergyStored()), false);
                        if (extracted > 0)
                        {
                            energyStorage.addEnergy(extracted);
                        }
                    }
                }
            }

            sendCube();
        }
    }

    private boolean sendCube()
    {
        if (energyStorage.getEnergyStored() >= CUBE_ENERGY_PER_HOP)
        {
            energyStorage.removeEnergy(CUBE_ENERGY_PER_HOP);
            int energy = 0;
            for (int i = 0; i < CUBE_HOPS; i++)
            {
                if (energyStorage.getEnergyStored() >= i * CUBE_ENERGY_PER_HOP)
                {
                    energy = i * CUBE_ENERGY_PER_HOP;
                }
                else
                {
                    break;
                }
            }
            energyStorage.removeEnergy(energy);
            EnumFacing facing = world.getBlockState(getPos()).getValue(BlockCubeSender.FACING);
            BlockPos pos = this.pos.offset(facing);
            if (world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos))
            {
                EntityCube cube = new EntityCube(world);
                cube.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                cube.setVelocity(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ());
                cube.setEnergy(energy);
                world.spawnEntity(cube);
                return true;
            }
        }
        return false;
    }

    public ITextComponent getEnergyString()
    {
        return new TextComponentTranslation("msg.fe_gen.energy", energyStorage.getEnergyStoredText(), energyStorage.getMaxEnergyStoredText());
    }
}
