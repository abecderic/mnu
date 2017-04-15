package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MNUBlocks
{
    public static final String FE_GEN = "fe_generator";
    public static Block feGen;

    public static void registerBlocks()
    {
        feGen = new BlockFEGenerator();
        registerTileEntity(feGen, FE_GEN, TileEntityFEGenerator.class);
    }

    private static void registerBlock(Block block, String name)
    {
        /* block */
        block.setRegistryName(name);
        GameRegistry.register(block);

        /* item */
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(name);
        GameRegistry.register(itemBlock);
    }

    private static void registerTileEntity(Block block, String name, Class<? extends TileEntity> clazz)
    {
        registerBlock(block, name);
        GameRegistry.registerTileEntity(clazz, name);
    }

    public static void registerModels()
    {
        registerModel(feGen, FE_GEN);
    }

    private static void registerModel(Block block, String name)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
    }
}
