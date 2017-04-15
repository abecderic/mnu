package com.abecderic.mnu.proxy;

import com.abecderic.mnu.block.MNUBlocks;
import com.abecderic.mnu.fluid.MNUFluids;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerModels()
    {
        MNUBlocks.registerModels();
        MNUFluids.registerModels();
    }
}
