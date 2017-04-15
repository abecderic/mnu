package com.abecderic.mnu.fluid;

import com.abecderic.mnu.MNU;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidMNU extends Fluid
{
    public FluidMNU(String name)
    {
        super(name, new ResourceLocation(MNU.MODID, "blocks/" + name + "_still"), new ResourceLocation(MNU.MODID, "blocks/" + name + "_flowing"));
        setDensity(1000);
        setViscosity(1000);
    }
}
