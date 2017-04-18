package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemWrench extends Item
{
    public ItemWrench()
    {
        setCreativeTab(MNU.TAB);
        setMaxStackSize(1);
        setUnlocalizedName(MNUItems.WRENCH);
        setHarvestLevel("wrench", 1);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + MNU.MODID + ":" + MNUItems.WRENCH;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }
}
