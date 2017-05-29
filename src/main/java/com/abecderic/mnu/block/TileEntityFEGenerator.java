package com.abecderic.mnu.block;

import com.abecderic.mnu.util.EnergyStorageInternal;
import com.abecderic.mnu.util.ItemFuelHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileEntityFEGenerator extends TileEntity implements ITickable
{
    private static final int STORAGE = 512000;
    private static final int MAX_OUT = 2560;
    private EnergyStorageInternal energyStorage = new EnergyStorageInternal(STORAGE, 0, MAX_OUT);
    private ItemFuelHandler itemStack = new ItemFuelHandler(1);
    private int tickPart = 0;

    @Override
    public void update()
    {
        if (getWorld().isRemote) return;
        markDirty();
        if (tickPart > 0)
        {
            tickPart--;
            energyStorage.addEnergy(64);
        }
        else if (!itemStack.getStackInSlot(0).isEmpty())
        {
            if (energyStorage.getEnergyStored() + TileEntityFurnace.getItemBurnTime(itemStack.getStackInSlot(0)) <= energyStorage.getMaxEnergyStored())
            {
                ItemStack fuel = itemStack.getStackInSlot(0).splitStack(1);
                tickPart += TileEntityFurnace.getItemBurnTime(fuel) / 4;
            }
        }
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            BlockPos pos = getPos().offset(facing);
            TileEntity te = world.getTileEntity(pos);
            if (te != null)
            {
                IEnergyStorage energy = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (energy != null && energy.getEnergyStored() < energy.getMaxEnergyStored())
                {
                    int extracted = energyStorage.extractEnergy(Math.min(energy.getMaxEnergyStored() - energy.getEnergyStored(), MAX_OUT), false);
                    if (extracted > 0)
                    {
                        energy.receiveEnergy(extracted, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
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
        else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) itemStack;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        tickPart = compound.getInteger("tickPart");
        energyStorage = new EnergyStorageInternal(STORAGE, 0, MAX_OUT, compound.getInteger("storage"));
        itemStack.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        compound.setInteger("tickPart", tickPart);
        compound.setInteger("storage", energyStorage.getEnergyStored());
        compound.setTag("inventory", itemStack.serializeNBT());
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
    }

    public ITextComponent getEnergyString()
    {
        return new TextComponentTranslation("msg.fe_gen.energy", energyStorage.getEnergyStoredText(), energyStorage.getMaxEnergyStoredText());
    }

    public ItemStack getFuel()
    {
        return itemStack.getStackInSlot(0);
    }
}
