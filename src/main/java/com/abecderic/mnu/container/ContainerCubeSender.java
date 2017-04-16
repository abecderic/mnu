package com.abecderic.mnu.container;

import com.abecderic.mnu.block.TileEntityCubeSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCubeSender extends Container
{
    private TileEntityCubeSender te;

    public ContainerCubeSender(InventoryPlayer inv, TileEntityCubeSender te)
    {
        this.te = te;

        /* player hotbar */
        for (int row = 0; row < 9; ++row)
        {
            int x = 9 + row * 18;
            int y = 58 + 70;
            this.addSlotToContainer(new Slot(inv, row, x, y));
        }

        /* player inventory */
        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                int x = 9 + col * 18;
                int y = row * 18 + 70;
                this.addSlotToContainer(new Slot(inv, col + row * 9 + 9, x, y));
            }
        }

		/* buffer */
        IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        int slotIndex = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            int x = 9 + 18 * i;
            int y = 6;
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
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return te.canInteractWith(playerIn);
    }
}
