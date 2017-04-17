package com.abecderic.mnu.container;

import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.util.MultipleFluidTanks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCubeSender extends Container
{
    private TileEntityCubeSender te;

    private int oldEnergy = -1;
    private NBTTagCompound oldTanks = new NBTTagCompound();
    private int oldRedstone = -1;
    private boolean oldEnergyOnly = false;
    private int oldCubeHops = -1;

    public ContainerCubeSender(InventoryPlayer inv, TileEntityCubeSender te)
    {
        this.te = te;

        /* player hotbar */
        for (int row = 0; row < 9; ++row)
        {
            int x = row * 18 + 8;
            int y = 184;
            this.addSlotToContainer(new Slot(inv, row, x, y));
        }

        /* player inventory */
        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                int x = col * 18 + 8;
                int y = row * 18 + 126;
                this.addSlotToContainer(new Slot(inv, col + row * 9 + 9, x, y));
            }
        }

		/* buffer */
        IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        int slotIndex = 0;
        for (int row = 0; row < itemHandler.getSlots(); row++)
        {
            int x = row * 18 + 8;
            int y = 94;
            addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, x, y));
            slotIndex++;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < TileEntityCubeSender.BUFFER_SIZE)
            {
                if (!this.mergeItemStack(itemstack1, TileEntityCubeSender.BUFFER_SIZE, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, TileEntityCubeSender.BUFFER_SIZE, false))
            {
                return ItemStack.EMPTY;
            }
            slot.onSlotChanged();
        }
        return itemstack;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        boolean shouldUpdate = false;
        IEnergyStorage energy = te.getCapability(CapabilityEnergy.ENERGY, null);
        if (energy != null && energy.getEnergyStored() != oldEnergy)
        {
            oldEnergy = energy.getEnergyStored();
            shouldUpdate = true;
        }
        IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (fluidHandler != null && fluidHandler instanceof MultipleFluidTanks)
        {
            NBTTagCompound newTanks = ((MultipleFluidTanks)fluidHandler).serializeNBT();
            if (!newTanks.equals(oldTanks))
            {
                oldTanks = newTanks;
                shouldUpdate = true;
            }
        }
        if (te.getRedstoneMode() != oldRedstone)
        {
            oldRedstone = te.getRedstoneMode();
            shouldUpdate = true;
        }
        if (te.isEnergyOnly() != oldEnergyOnly)
        {
            oldEnergyOnly = te.isEnergyOnly();
            shouldUpdate = true;
        }
        if (te.getCubeHops() != oldCubeHops)
        {
            oldCubeHops = te.getCubeHops();
            shouldUpdate = true;
        }
        if (shouldUpdate)
        {
            System.out.println("sending cube sender update packet to " + listeners.size() + " players");
            for (IContainerListener player : listeners)
            {
                if (player instanceof EntityPlayerMP)
                {
                    te.sendUpdatePacket((EntityPlayer) player);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return te.canInteractWith(playerIn);
    }
}
