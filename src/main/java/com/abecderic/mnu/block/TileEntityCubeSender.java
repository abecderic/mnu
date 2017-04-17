package com.abecderic.mnu.block;

import com.abecderic.mnu.entity.EntityCube;
import com.abecderic.mnu.network.MNUNetwork;
import com.abecderic.mnu.network.PacketCubeSender;
import com.abecderic.mnu.util.EnergyStorageInternal;
import com.abecderic.mnu.util.MultipleFluidTanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntityCubeSender extends TileEntity implements ITickable
{
    private static final int STORAGE = 512000;
    private static final int MAX_TRANSFER = 128;
    private static final int CUBE_ENERGY_PER_HOP = 2048;
    public static final int BUFFER_SIZE = 9;
    public static final int TANKS = 4;
    private static final int TANK_CAPACITY = 8000;
    private ItemStackHandler inventory = new ItemStackHandler(BUFFER_SIZE);
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_TRANSFER, MAX_TRANSFER);
    private MultipleFluidTanks tanks = new MultipleFluidTanks(TANKS, TANK_CAPACITY);
    private int tickPart;
    private int cubeHops = 0;
    private int fluidMin = 0;
    private int itemMin = 0;
    private int energyMin = 0;
    private int redstoneMode = 0;

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
                    if (energy != null && energy.getEnergyStored() < energy.getMaxEnergyStored())
                    {
                        int extracted = energy.extractEnergy(Math.min(MAX_TRANSFER * 20, energy.getMaxEnergyStored() - energy.getEnergyStored()), false);
                        if (extracted > 0)
                        {
                            energyStorage.addEnergy(extracted);
                        }
                    }
                }
            }
            if (redstoneMode == 0 || (redstoneMode == 1 && !world.isBlockPowered(pos)) || (redstoneMode == 2 && world.isBlockPowered(pos)))
            {
                sendCube();
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
            return (T) tanks;
        }
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        energyStorage = new EnergyStorageInternal(STORAGE, MAX_TRANSFER, MAX_TRANSFER, compound.getInteger("storage"));
        inventory.deserializeNBT(compound.getCompoundTag("buffer"));
        tanks.deserializeNBT(compound.getCompoundTag("tanks"));
        redstoneMode = compound.getByte("r_mode");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setInteger("storage", energyStorage.getEnergyStored());
        compound.setTag("buffer", inventory.serializeNBT());
        compound.setTag("tanks", tanks.serializeNBT());
        compound.setByte("r_mode", (byte) redstoneMode);
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
    }

    protected boolean sendCube()
    {
        int energyNeeded = CUBE_ENERGY_PER_HOP * (cubeHops + 1) + energyMin;
        boolean hasEnergy = energyStorage.getEnergyStored() >= energyNeeded;
        boolean hasFluid = false;
        for (int i = 0; i < TANKS; i++)
        {
            if (tanks.getTank(i).getFluidAmount() >= fluidMin)
            {
                hasFluid = true;
                break;
            }
        }
        boolean hasItem = false;
        for (int i = 0; i < BUFFER_SIZE; i++)
        {
            if (inventory.getStackInSlot(i).getCount() >= itemMin)
            {
                hasItem = true;
                break;
            }
        }
        if (hasEnergy /*&& hasFluid*/ /*&& hasItem*/)
        {
            EnumFacing facing = world.getBlockState(getPos()).getValue(BlockCubeSender.FACING);
            BlockPos pos = this.pos.offset(facing);
            if (world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos))
            {
                energyStorage.removeEnergy(energyNeeded);
                EntityCube cube = new EntityCube(world);
                cube.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                cube.setVelocity(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ());
                cube.setEnergy(energyNeeded - CUBE_ENERGY_PER_HOP);
                if (hasItem)
                {
                    for (int i = 0; i < BUFFER_SIZE; i++)
                    {
                        if (inventory.getStackInSlot(i).getCount() >= itemMin)
                        {
                            ItemStack stack = inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false);
                            cube.setItem(stack);
                            break;
                        }
                    }
                }
                else if (hasFluid)
                {
                    for (int i = 0; i < TANKS; i++)
                    {
                        if (tanks.getTank(i).getFluidAmount() >= fluidMin)
                        {
                            FluidStack fluid = tanks.getTank(i).drainInternal(tanks.getTank(i).getFluidAmount(), true);
                            cube.setFluid(fluid);
                            break;
                        }
                    }
                }
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
            MNUNetwork.snw.sendTo(new PacketCubeSender(pos, energyStorage.getEnergyStored(), tanks, redstoneMode), (EntityPlayerMP) playerIn);
        }
    }

    public int getRedstoneMode()
    {
        return redstoneMode;
    }

    public void setRedstoneMode(int redstoneMode)
    {
        this.redstoneMode = redstoneMode;
        markDirty();
    }
}
