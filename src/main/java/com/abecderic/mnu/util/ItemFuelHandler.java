package com.abecderic.mnu.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ItemFuelHandler extends ItemStackHandler
{
    public ItemFuelHandler(int size)
    {
        super(size);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (TileEntityFurnace.isItemFuel(stack))
        {
            return super.insertItem(slot, stack, simulate);
        }
        else
        {
            return stack;
        }
    }
}
