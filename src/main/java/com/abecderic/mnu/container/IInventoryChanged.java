package com.abecderic.mnu.container;

import net.minecraftforge.items.IItemHandler;

public interface IInventoryChanged
{
    void onContentsChanged(IItemHandler handler, int slot);
}
