package com.abecderic.mnu.util;

import com.abecderic.mnu.container.IInventoryChanged;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerUpdate extends ItemStackHandler
{
    private IInventoryChanged callback;

    public ItemStackHandlerUpdate(int size, IInventoryChanged callback)
    {
        super(size);
        this.callback = callback;
    }

    public void setCallback(IInventoryChanged callback)
    {
        this.callback = callback;
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);
        callback.onContentsChanged(this, slot);
    }
}
