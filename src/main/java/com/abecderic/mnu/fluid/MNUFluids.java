package com.abecderic.mnu.fluid;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.block.BlockFluid;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MNUFluids
{
    public static final String FLUID_DARK_MATTER = "fluid_darkmatter";
    public static Fluid fluidDarkMatter;

    public static final String FLUID_MNU = "fluid_mnu";
    public static Fluid fluidMNU;

    public static void registerFluids()
    {
        fluidDarkMatter = new FluidDarkMatter(FLUID_DARK_MATTER);
        fluidMNU = new FluidMNU(FLUID_MNU);

        registerFluid(fluidDarkMatter, FLUID_DARK_MATTER);
        registerFluid(fluidMNU, FLUID_MNU);
    }

    private static void registerFluid(Fluid fluid, String name)
    {
        /* register fluid */
        fluid.setUnlocalizedName(name);
        FluidRegistry.registerFluid(fluid);

        /* block */
        Block block = new BlockFluid(fluid, MNU.MODID + ":" + name);
        block.setRegistryName(name);
        fluid.setBlock(block);
        GameRegistry.register(block);
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(name);
        GameRegistry.register(itemBlock);

        /* bucket */
        FluidRegistry.addBucketForFluid(fluid);
    }

    public static void registerModels()
    {
        registerModel(fluidDarkMatter.getBlock(), FLUID_DARK_MATTER);
        registerModel(fluidMNU.getBlock(), FLUID_MNU);
    }

    private static void registerModel(Block block, String name)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
    }
}
