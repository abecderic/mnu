package com.abecderic.mnu.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockMulti extends ItemBlock
{
    public ItemBlockMulti(Block block)
    {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (success && !world.isRemote)
        {
            world.setBlockState(pos, block.getStateFromMeta(stack.getItemDamage()));
        }
        return success;
    }
}
