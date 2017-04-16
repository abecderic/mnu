package com.abecderic.mnu.block;

import com.abecderic.mnu.entity.EntityCube;
import com.abecderic.mnu.network.MNUNetwork;
import com.abecderic.mnu.network.PacketCubeSender;
import com.abecderic.mnu.util.EnergyStorageInternal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntityCubeSender extends TileEntity implements ITickable
{
    private static final int STORAGE = 512000;
    private static final int MAX_TRANSFER = 128;
    private static final int CUBE_ENERGY_PER_HOP = 2048;
    private static final int CUBE_HOPS = 3;
    public static final int BUFFER_SIZE = 9;
    private ItemStackHandler inventory = new ItemStackHandler(BUFFER_SIZE);
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

            // TODO uncomment
            //sendCube();
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
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    // TODO save and load all the capability things

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
            // TODO implement
            //return (T) fluidTank;
        }
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }

    private boolean sendCube()
    {
        // TODO enable cubes using energy again
        //if (energyStorage.getEnergyStored() >= CUBE_ENERGY_PER_HOP)
        {
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
            EnumFacing facing = world.getBlockState(getPos()).getValue(BlockCubeSender.FACING);
            BlockPos pos = this.pos.offset(facing);
            if (world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos))
            {
                energyStorage.removeEnergy(CUBE_ENERGY_PER_HOP);
                energyStorage.removeEnergy(energy);
                EntityCube cube = new EntityCube(world);
                cube.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                cube.setVelocity(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ());
                cube.setEnergy(energy);
                //cube.setFluid(new FluidStack(MNUFluids.fluidMNU, 1000));
                cube.setItem(new ItemStack(Items.GOLD_INGOT, 42));
                world.spawnEntity(cube);
                return true;
            }
        }
        return false;
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    public void sendUpdatePacket(EntityPlayer playerIn)
    {
        if (!world.isRemote)
        {
            MNUNetwork.snw.sendTo(new PacketCubeSender(pos, energyStorage.getEnergyStored()), (EntityPlayerMP) playerIn);
        }
    }
}
