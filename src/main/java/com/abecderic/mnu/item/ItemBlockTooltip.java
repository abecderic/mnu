package com.abecderic.mnu.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class ItemBlockTooltip extends ItemBlock
{
    private String[] tooltip;

    public ItemBlockTooltip(Block block, String... tooltip)
    {
        super(block);
        this.tooltip = tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, playerIn, tooltip, advanced);
        for (String tip : this.tooltip)
        {
            tooltip.add(new TextComponentTranslation(tip).getFormattedText());
        }
    }
}
