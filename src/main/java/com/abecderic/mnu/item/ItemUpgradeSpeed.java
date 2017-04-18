package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class ItemUpgradeSpeed extends Item
{
    public ItemUpgradeSpeed()
    {
        setCreativeTab(MNU.TAB);
        setMaxStackSize(4);
        setUnlocalizedName(MNUItems.UPGRADE_SPEED);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + MNU.MODID + ":" + MNUItems.UPGRADE_SPEED;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(new TextComponentTranslation("item.mnu:upgrade_speed.tip").getFormattedText());
    }
}
