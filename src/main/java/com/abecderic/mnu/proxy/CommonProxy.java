package com.abecderic.mnu.proxy;

import com.abecderic.mnu.block.MNUBlocks;
import com.abecderic.mnu.item.MNUItems;

public class CommonProxy
{
    public void registerRecipes()
    {
        MNUBlocks.registerRecipes();
        MNUItems.registerRecipes();
    }

    public void registerRenderers() {}

    public void registerModels() {}
}
