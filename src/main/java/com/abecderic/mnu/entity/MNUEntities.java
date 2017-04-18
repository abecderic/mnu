package com.abecderic.mnu.entity;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.render.RenderCube;
import com.abecderic.mnu.render.RenderPassengerCube;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class MNUEntities
{
    public static final String CUBE = "cube";
    public static final String PASSENGER_CUBE = "passenger_cube";

    public static void registerEntities()
    {
        int i = 0;
        EntityRegistry.registerModEntity(new ResourceLocation(MNU.MODID, CUBE), EntityCube.class, CUBE, i++, MNU.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MNU.MODID, PASSENGER_CUBE), EntityPassengerCube.class, PASSENGER_CUBE, i++, MNU.instance, 64, 10, true);
    }

    public static void registerRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityCube.class, RenderCube::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityPassengerCube.class, RenderPassengerCube::new);
    }
}
