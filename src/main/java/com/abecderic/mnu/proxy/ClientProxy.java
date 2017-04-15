package com.abecderic.mnu.proxy;

import com.abecderic.mnu.fluid.MNUFluids;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerModels()
    {
        MNUFluids.registerModels();
    }
}
