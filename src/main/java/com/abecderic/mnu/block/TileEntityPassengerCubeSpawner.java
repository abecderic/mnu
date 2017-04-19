package com.abecderic.mnu.block;

import com.abecderic.mnu.entity.EntityPassengerCube;
import com.abecderic.mnu.util.EnergyStorageInternal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileEntityPassengerCubeSpawner extends TileEntity implements ITickable, INotifyMaster
{
    private static final int STORAGE = 81920;
    private static final int MAX_IN = 512;
    private static final int ENERGY_USAGE = 10240;
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_IN, 0);
    private int tickPart;
    private List<BlockPos> list = new ArrayList<>();

    public TileEntityPassengerCubeSpawner()
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
                        int extracted = energy.extractEnergy(Math.min(MAX_IN * 20, energy.getMaxEnergyStored() - energy.getEnergyStored()), false);
                        if (extracted > 0)
                        {
                            energyStorage.addEnergy(extracted);
                        }
                    }
                }
            }
            List<EntityPassengerCube> list = world.getEntitiesWithinAABB(EntityPassengerCube.class, new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() -1, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2));
            if (list.isEmpty())
            {
                spawnPassengerCube();
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
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        energyStorage = new EnergyStorageInternal(STORAGE, MAX_IN, 0, compound.getInteger("storage"));
        list.clear();
        int[] pos = compound.getIntArray("points");
        for (int i = 0; i < pos.length; i += 3)
        {
            list.add(new BlockPos(pos[i], pos[i+1], pos[i+2]));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setInteger("storage", energyStorage.getEnergyStored());
        int[] pos = new int[list.size() * 3];
        int i = 0;
        for (BlockPos p : list)
        {
            pos[i++] = p.getX();
            pos[i++] = p.getY();
            pos[i++] = p.getZ();
        }
        compound.setIntArray("points", pos);
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
    }

    public ITextComponent getEnergyString()
    {
        return new TextComponentTranslation("msg.passenger_cube_spawner.energy", energyStorage.getEnergyStoredText(), energyStorage.getMaxEnergyStoredText());
    }

    public void spawnPassengerCube()
    {
        if (energyStorage.getEnergyStored() >= ENERGY_USAGE)
        {
            energyStorage.removeEnergy(ENERGY_USAGE);
            EntityPassengerCube cube = new EntityPassengerCube(world, pos);
            cube.setPosition(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
            cube.setVelocity(0, 0, 0);
            cube.setList(list);
            world.spawnEntity(cube);
        }
    }

    public void reset()
    {
        list.clear();
    }

    @Override
    public void addBlock(BlockPos pos, EntityPlayer player)
    {
        list.add(pos);
        player.sendMessage(new TextComponentTranslation("msg.passenger_cube.add"));
    }

    @Override
    public void removeBlock(BlockPos pos, EntityPlayer player)
    {
        /* NO-OP */
    }
}
