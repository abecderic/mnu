package com.abecderic.mnu.block;

import net.minecraft.util.math.BlockPos;

public interface INotifyMaster
{
    void addBlock(BlockPos pos);
    void removeBlock(BlockPos pos);
}
