package com.abecderic.mnu.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public interface INotifyMaster
{
    void addBlock(BlockPos pos, EntityPlayer player);
    void removeBlock(BlockPos pos, EntityPlayer player);
}
