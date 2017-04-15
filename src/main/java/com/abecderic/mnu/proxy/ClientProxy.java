package com.abecderic.mnu.proxy;

import com.abecderic.mnu.block.MNUBlocks;
import com.abecderic.mnu.fluid.MNUFluids;
import com.abecderic.mnu.item.MNUItems;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerModels()
    {
        MNUBlocks.registerModels();
        MNUItems.registerModels();
        MNUFluids.registerModels();
    }
}
