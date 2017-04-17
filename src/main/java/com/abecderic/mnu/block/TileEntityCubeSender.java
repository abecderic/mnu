package com.abecderic.mnu.block;

import com.abecderic.mnu.entity.EntityCube;
import com.abecderic.mnu.item.ItemUpgradeTransfer;
import com.abecderic.mnu.item.MNUItems;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
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
    private static final int UPGRADES_SIZE = 4;
    private ItemStackHandler inventory = new ItemStackHandler(BUFFER_SIZE);
    private ItemStackHandler upgrades = new ItemStackHandler(UPGRADES_SIZE);
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, MAX_TRANSFER, MAX_TRANSFER);
    private MultipleFluidTanks tanks = new MultipleFluidTanks(TANKS, TANK_CAPACITY);
    private int tickPart;
    private int cubeHops = 0;
    private static final int FLUID_MIN = 1000;
    private static final int ITEM_MIN = 8;
    private static final int ENERGY_MIN = 8192;
    private boolean energyOnly = false;
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
            /* handle upgrades */
            for (int i = 0; i < UPGRADES_SIZE; i++)
            {
                ItemStack s = upgrades.getStackInSlot(i);
                if (!s.isEmpty())
                {
                    if (s.getItem() == MNUItems.upgradeItemsTransfer)
                    {
                        if (((ItemUpgradeTransfer)MNUItems.upgradeItemsTransfer).isExtracting(s))
                        {
                            pullItems(((ItemUpgradeTransfer)MNUItems.upgradeItemsTransfer).getDirection(s));
                        }
                        else
                        {
                            pushItems(((ItemUpgradeTransfer)MNUItems.upgradeItemsTransfer).getDirection(s));
                        }
                    }
                    else if (s.getItem() == MNUItems.upgradeFluidsTransfer)
                    {
                        if (((ItemUpgradeTransfer)MNUItems.upgradeFluidsTransfer).isExtracting(s))
                        {
                            pullFluids(((ItemUpgradeTransfer)MNUItems.upgradeFluidsTransfer).getDirection(s));
                        }
                        else
                        {
                            pushFluids(((ItemUpgradeTransfer)MNUItems.upgradeFluidsTransfer).getDirection(s));
                        }
                    }
                }
            }
            /* get energy */
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
            /* send cube */
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
        upgrades.deserializeNBT(compound.getCompoundTag("upgrades"));
        tanks.deserializeNBT(compound.getCompoundTag("tanks"));
        redstoneMode = compound.getByte("r_mode");
        energyOnly = compound.getBoolean("energy_only");
        cubeHops = compound.getByte("cube_hops");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setInteger("storage", energyStorage.getEnergyStored());
        compound.setTag("buffer", inventory.serializeNBT());
        compound.setTag("upgrades", upgrades.serializeNBT());
        compound.setTag("tanks", tanks.serializeNBT());
        compound.setByte("r_mode", (byte) redstoneMode);
        compound.setBoolean("energy_only", energyOnly);
        compound.setByte("cube_hops", (byte) cubeHops);
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
    }

    protected boolean sendCube()
    {
        int energyNeeded = CUBE_ENERGY_PER_HOP * (cubeHops + 1) + (energyOnly ? ENERGY_MIN : 0);
        boolean hasEnergy = energyStorage.getEnergyStored() >= energyNeeded;
        boolean hasFluid = false;
        for (int i = 0; i < TANKS; i++)
        {
            if (tanks.getTank(i).getFluidAmount() >= FLUID_MIN)
            {
                hasFluid = true;
                break;
            }
        }
        boolean hasItem = false;
        for (int i = 0; i < BUFFER_SIZE; i++)
        {
            if (inventory.getStackInSlot(i).getCount() >= ITEM_MIN)
            {
                hasItem = true;
                break;
            }
        }
        if (hasEnergy && (energyOnly || hasItem || hasFluid))
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
                        if (inventory.getStackInSlot(i).getCount() >= ITEM_MIN)
                        {
                            ItemStack stack = inventory.extractItem(i, ITEM_MIN, false);
                            cube.setItem(stack);
                            break;
                        }
                    }
                }
                else if (hasFluid)
                {
                    for (int i = 0; i < TANKS; i++)
                    {
                        if (tanks.getTank(i).getFluidAmount() >= FLUID_MIN)
                        {
                            FluidStack fluid = tanks.getTank(i).drainInternal(FLUID_MIN, true);
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
            MNUNetwork.snw.sendTo(new PacketCubeSender(pos, energyStorage.getEnergyStored(), tanks, redstoneMode, energyOnly, cubeHops), (EntityPlayerMP) playerIn);
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

    public boolean isEnergyOnly()
    {
        return energyOnly;
    }

    public void setEnergyOnly(boolean energyOnly)
    {
        this.energyOnly = energyOnly;
    }

    public int getCubeHops()
    {
        return cubeHops;
    }

    public void setCubeHops(int cubeHops)
    {
        this.cubeHops = cubeHops;
    }

    public ItemStackHandler getUpgrades()
    {
        return upgrades;
    }

    private void pullItems(EnumFacing facing)
    {
        TileEntity te = world.getTileEntity(pos.offset(facing));
        if (te != null)
        {
            IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            if (itemHandler != null)
            {
                for (int slot = 0; slot < itemHandler.getSlots(); slot++)
                {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if (stack.isEmpty()) continue;
                    System.out.println("pulling from slot " + slot + " from " + facing.getName());
                    ItemStack returnStack = transferItems(stack, inventory, true);
                    if (stack.getCount() == returnStack.getCount()) return;
                    stack = itemHandler.extractItem(slot, stack.getCount() - returnStack.getCount(), false);
                    transferItems(stack, inventory, false);
                    break;
                }
            }
        }
    }

    private void pushItems(EnumFacing facing)
    {
        TileEntity te = world.getTileEntity(pos.offset(facing));
        if (te != null)
        {
            IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
            if (itemHandler != null)
            {
                for (int slot = 0; slot < BUFFER_SIZE; slot++)
                {
                    ItemStack stack = inventory.getStackInSlot(slot);
                    if (stack.isEmpty()) return;
                    ItemStack returnStack = transferItems(stack, itemHandler, true);
                    if (stack.getCount() == returnStack.getCount()) return;
                    stack = inventory.extractItem(slot, stack.getCount() - returnStack.getCount(), false);
                    transferItems(stack, itemHandler, false);
                }
            }
        }
    }

    private ItemStack transferItems(ItemStack stack, IItemHandler to, boolean simulate)
    {
        for (int i = 0; i < to.getSlots(); i++)
        {
            stack = to.insertItem(i, stack, simulate);
            if (stack.isEmpty())
            {
                break;
            }
        }
        return stack;
    }

    private void pullFluids(EnumFacing facing)
    {
        TileEntity te = world.getTileEntity(pos.offset(facing));
        if (te != null)
        {
            IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
            if (fluidHandler != null)
            {
                FluidStack stack = fluidHandler.drain(1000, false);
                if (stack != null)
                {
                    int amount = tanks.fill(stack, false);
                    if (amount > 0)
                    {
                        stack = fluidHandler.drain(amount, true);
                        tanks.fill(stack, true);
                    }
                }
            }
        }
    }

    private void pushFluids(EnumFacing facing)
    {
        TileEntity te = world.getTileEntity(pos.offset(facing));
        if (te != null)
        {
            IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
            if (fluidHandler != null)
            {
                for (int i = 0; i < TANKS; i++)
                {
                    int amount = fluidHandler.fill(tanks.getTank(i).getFluid(), true);
                    tanks.getTank(i).drain(amount, true);
                }
            }
        }
    }
}
