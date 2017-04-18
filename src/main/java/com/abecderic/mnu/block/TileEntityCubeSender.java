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
import net.minecraft.item.Item;
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
import net.minecraftforge.fluids.Fluid;
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
    private BlockPos receiverPos;
    private TileEntityCubeSender receiver;

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
            if (receiver == null)
            {
                if (receiverPos == null)
                {
                    sendCube(true);
                }
                else
                {
                    TileEntity te = world.getTileEntity(receiverPos);
                    if (te != null && te instanceof TileEntityCubeSender)
                    {
                        receiver = (TileEntityCubeSender) te;
                    }
                    else
                    {
                        receiverPos = null;
                    }
                }
            }
            else if (receiver.isInvalid())
            {
                receiver = null;
                receiverPos = null;
            }
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
                        int extracted = energy.extractEnergy(Math.min(MAX_TRANSFER * 20, energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()), false);
                        if (extracted > 0)
                        {
                            energyStorage.addEnergy(extracted);
                        }
                    }
                }
            }
            /* send cube */
            if (receiver != null)
            {
                if (redstoneMode == 0 || (redstoneMode == 1 && !world.isBlockPowered(pos)) || (redstoneMode == 2 && world.isBlockPowered(pos)))
                {
                    sendCube(false);
                }
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
        if (compound.hasKey("rec_x") && compound.hasKey("rec_y") && compound.hasKey("rec_z"))
        {
           receiverPos = new BlockPos(compound.getInteger("rec_x"), compound.getInteger("rec_y"), compound.getInteger("rec_z"));
        }
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
        if (receiver != null)
        {
            compound.setInteger("rec_x", receiver.getPos().getX());
            compound.setInteger("rec_y", receiver.getPos().getY());
            compound.setInteger("rec_z", receiver.getPos().getZ());
        }
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
    }

    protected boolean sendCube(boolean fake)
    {
        int itemSlot = -1;
        Fluid fluid = null;
        boolean extraEnergy = false;
        EnumFacing facing = world.getBlockState(getPos()).getValue(BlockCubeSender.FACING);
        BlockPos pos = this.pos.offset(facing);
        if (!world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos)) return false;
        int energyNeeded = CUBE_ENERGY_PER_HOP * (cubeHops + 1);
        if (!fake)
        {
            /* pre-flight checks */
            if (energyStorage.getEnergyStored() < energyNeeded) return false;
            for (int i = 0; i < BUFFER_SIZE; i++)
            {
                if (inventory.getStackInSlot(i).getCount() >= ITEM_MIN && receiver.canAccept(inventory.getStackInSlot(i).getItem(), ITEM_MIN))
                {
                    itemSlot = i;
                    break;
                }
            }
            for (int i = 0; i < TANKS; i++)
            {
                if (tanks.getTank(i).getFluidAmount() >= FLUID_MIN && tanks.getTank(i).getFluid() != null && receiver.canAccept(tanks.getTank(i).getFluid().getFluid(), FLUID_MIN))
                {
                    fluid = tanks.getTank(i).getFluid().getFluid();
                    break;
                }
            }
            if (itemSlot < 0 && fluid == null && energyOnly && energyStorage.getEnergyStored() >= energyNeeded + ENERGY_MIN)
            {
                extraEnergy = true;
            }
            if (itemSlot < 0 && fluid == null && !extraEnergy) return false;
        }
        /* fill the cube */
        EntityCube cube = new EntityCube(world, getPos());
        cube.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        cube.setVelocity(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ());
        if (!fake)
        {
            energyStorage.removeEnergy(energyNeeded);
            cube.setEnergy(energyNeeded - CUBE_ENERGY_PER_HOP);
            if (itemSlot >= 0)
            {
                ItemStack stack = inventory.extractItem(itemSlot, ITEM_MIN, false);
                cube.setItem(stack);
            }
            else if (fluid != null)
            {
                FluidStack stack = tanks.drain(new FluidStack(fluid, FLUID_MIN), true);
                cube.setFluid(stack);
            }
            else if (extraEnergy)
            {
                energyStorage.removeEnergy(ENERGY_MIN);
                cube.setEnergy(cube.getEnergy() + ENERGY_MIN);
            }
        }
        world.spawnEntity(cube);
        return true;
    }

    private boolean canAccept(Item item, int amount)
    {
        ItemStack stack = new ItemStack(item, amount);
        for (int i = 0; i < BUFFER_SIZE; i++)
        {
            stack = inventory.insertItem(i, stack, true);
            if (stack.isEmpty()) return true;
        }
        return false;
    }

    private boolean canAccept(Fluid fluid, int amount)
    {
        FluidStack stack = new FluidStack(fluid, amount);
        return tanks.fill(stack, false) >= amount;
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

    public void cubePingback(TileEntityCubeSender reciever)
    {
        this.receiver = reciever;
        this.receiverPos = reciever.getPos();
    }

    public void invalidateReciever()
    {
        this.receiver = null;
        this.receiverPos = null;
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
