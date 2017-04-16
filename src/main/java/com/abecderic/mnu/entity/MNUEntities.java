package com.abecderic.mnu.entity;

import com.abecderic.mnu.MNU;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class MNUEntities
{
    public static final String CUBE = "cube";

    public static void registerEntities()
    {
        int i = 0;
        EntityRegistry.registerModEntity(new ResourceLocation(MNU.MODID, CUBE), EntityCube.class, CUBE, i++, MNU.instance, 64, 10, true);
    }
}
