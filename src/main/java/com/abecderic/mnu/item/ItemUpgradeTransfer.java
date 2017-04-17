package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

public class ItemUpgradeTransfer extends Item
{
    private Type type;

    public ItemUpgradeTransfer(Type type)
    {
        this.type = type;
        setCreativeTab(MNU.TAB);
        setMaxStackSize(1);
        setUnlocalizedName(type.name);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + MNU.MODID + ":" + type.name;
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
        if (isExtracting(stack))
        {
            tooltip.add(new TextComponentTranslation("item.mnu:upgrade.extract", new TextComponentTranslation("item.mnu:upgrade.side." + getDirection(stack).getName()).getFormattedText()).getFormattedText());
        }
        else
        {
            tooltip.add(new TextComponentTranslation("item.mnu:upgrade.insert", new TextComponentTranslation("item.mnu:upgrade.side." + getDirection(stack).getName()).getFormattedText()).getFormattedText());
        }
        tooltip.add(new TextComponentTranslation("item.mnu:upgrade.tip").getFormattedText());
        tooltip.add(new TextComponentTranslation("item.mnu:upgrade.tip2").getFormattedText());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack stack = player.getHeldItem(hand);
            if (stack.isEmpty())
            {
                return EnumActionResult.PASS;
            }
            if (player.isSneaking())
            {
                player.getHeldItem(hand).setItemDamage((stack.getMetadata() & 0x07) + (isExtracting(stack) ? 0 : 8));
            }
            else
            {
                player.getHeldItem(hand).setItemDamage((stack.getMetadata() & 0x08) + facing.getIndex());
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    public EnumFacing getDirection(ItemStack stack)
    {
        return EnumFacing.getFront(stack.getMetadata() & 0x07);
    }

    public boolean isExtracting(ItemStack stack)
    {
        return (stack.getMetadata() & 0x08) != 0;
    }

    public enum Type
    {
        ITEMS(MNUItems.UPGRADE_ITEMS_TRANSFER),
        FLUIDS(MNUItems.UPGRADE_FLUIDS_TRANSFER);

        private String name;

        Type(String name)
        {
            this.name = name;
        }
    }
}
