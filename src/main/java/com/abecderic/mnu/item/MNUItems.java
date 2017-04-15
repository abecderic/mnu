package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MNUItems
{
    public static final String DM_SCANNER = "dm_scanner";
    public static Item dmScanner;

    public static void registerItems()
    {
        dmScanner = new ItemDMScanner();
        registerItem(dmScanner, DM_SCANNER);
    }

    private static void registerItem(Item item, String name)
    {
        item.setRegistryName(name);
        GameRegistry.register(item);
    }

    public static void registerModels()
    {
        registerModelWithMeta(dmScanner, DM_SCANNER, 7);
    }

    private static void registerModelWithMeta(Item item, String name, int amount)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
        ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));

        for (int i = 0; i < amount; i++)
        {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, i,
                    new ModelResourceLocation(MNU.MODID + ":" + name + "_" + i, "inventory"));
            ModelLoader.setCustomModelResourceLocation(item, i,
                    new ModelResourceLocation(MNU.MODID + ":" + name + "_" + i, "inventory"));
        }
    }
}
