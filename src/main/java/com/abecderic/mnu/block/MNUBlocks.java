package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.item.ItemBlockTooltip;
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

    public static final String DM_PUMP = "dm_pump";
    public static Block dmPump;

    public static final String CUBE_SENDER = "cube_sender";
    public static Block cubeSender;

    public static final String MIRROR = "mirror";
    public static Block mirror;

    public static void registerBlocks()
    {
        feGen = new BlockFEGenerator();
        registerBlockWithItemBlock(feGen, FE_GEN, new ItemBlockTooltip(feGen, "msg.fe_gen.tip", "msg.fe_gen.tip2", "msg.fe_gen.tip3"));
        registerTileEntity(feGen, FE_GEN, TileEntityFEGenerator.class);

        dmPump = new BlockDMPump();
        registerBlockWithItemBlock(dmPump, DM_PUMP, new ItemBlockTooltip(dmPump, "msg.dm_pump.tip", "msg.dm_pump.tip2"));
        registerTileEntity(dmPump, DM_PUMP, TileEntityDMPump.class);

        cubeSender = new BlockCubeSender();
        registerBlockWithItemBlock(cubeSender, CUBE_SENDER, new ItemBlockTooltip(cubeSender, "msg.cube_sender.catchphrase"));
        registerTileEntity(cubeSender, CUBE_SENDER, TileEntityCubeSender.class);

        mirror = new BlockMirror();
        registerBlock(mirror, MIRROR);
        registerTileEntity(mirror, MIRROR, TileEntityMirror.class);
    }

    private static void registerBlock(Block block, String name)
    {
        ItemBlock itemBlock = new ItemBlock(block);
        registerBlockWithItemBlock(block, name, itemBlock);
    }

    private static void registerBlockWithItemBlock(Block block, String name, ItemBlock itemBlock)
    {
        /* block */
        block.setRegistryName(name);
        GameRegistry.register(block);

        /* item */
        itemBlock.setRegistryName(name);
        GameRegistry.register(itemBlock);
    }

    private static void registerTileEntity(Block block, String name, Class<? extends TileEntity> clazz)
    {
        GameRegistry.registerTileEntity(clazz, name);
    }

    public static void registerModels()
    {
        registerModel(feGen, FE_GEN);
        registerModel(dmPump, DM_PUMP);
        registerModel(cubeSender, CUBE_SENDER);
        registerModel(mirror, MIRROR);
    }

    private static void registerModel(Block block, String name)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
    }
}
