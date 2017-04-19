package com.abecderic.mnu;

import com.abecderic.mnu.block.MNUBlocks;
import com.abecderic.mnu.entity.MNUEntities;
import com.abecderic.mnu.fluid.MNUFluids;
import com.abecderic.mnu.gui.GuiHandler;
import com.abecderic.mnu.item.MNUItems;
import com.abecderic.mnu.network.MNUNetwork;
import com.abecderic.mnu.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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

    public static CreativeTabs TAB = new CreativeTabs(MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(MNUFluids.fluidMNU.getBlock());
        }
    };

    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MNUBlocks.registerBlocks();
        MNUItems.registerItems();
        MNUFluids.registerFluids();
        MNUEntities.registerEntities();
        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        MNUNetwork.init();
        proxy.registerModels();
        proxy.registerRecipes();
    }
}
