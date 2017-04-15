package com.abecderic.mnu;

import com.abecderic.mnu.fluid.MNUFluids;
import com.abecderic.mnu.proxy.CommonProxy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MNU.MODID, name = MNU.MODNAME, version = MNU.VERSION)
public class MNU
{
    public static final String MODNAME = "MNU";
    public static final String MODID = "mnu";
    public static final String VERSION = "${version}";

    @Mod.Instance
    public static MNU instance;

    @SidedProxy(serverSide = "com.abecderic.mnu.proxy.CommonProxy", clientSide = "com.abecderic.mnu.proxy.ClientProxy")
    public static CommonProxy proxy;

    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MNUFluids.registerFluids();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerModels();
    }
}
