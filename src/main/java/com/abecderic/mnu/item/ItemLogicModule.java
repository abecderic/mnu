package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class ItemLogicModule extends Item
{
    public ItemLogicModule()
    {
        setCreativeTab(MNU.TAB);
        setMaxStackSize(64);
        setUnlocalizedName(MNUItems.LOGIC_MODULE);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + MNU.MODID + ":" + MNUItems.LOGIC_MODULE;
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
        tooltip.add(new TextComponentTranslation("item.mnu:logic_module.tip").getFormattedText());
    }
}
