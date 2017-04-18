package com.abecderic.mnu.block;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.item.ItemBlockMulti;
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

    public static final String FE_CREATIVE = "fe_creative";
    public static Block feCreative;

    public static final String DM_PUMP = "dm_pump";
    public static Block dmPump;

    public static final String CUBE_SENDER = "cube_sender";
    public static Block cubeSender;

    public static final String MIRROR = "mirror";
    public static Block mirror;

    public static final String SOLAR_FUSION_CONTROLLER = "solar_fusion_controller";
    public static Block solarFusionController;

    public static final String SOLAR_FUSION_CASING = "solar_fusion_casing";
    public static Block solarFusionCasing;

    public static final String REACTOR_CONTROLLER = "reactor_controller";
    public static Block reactorController;

    public static final String REACTOR_CASING = "reactor_casing";
    public static Block reactorCasing;

    public static void registerBlocks()
    {
        feGen = new BlockFEGenerator();
        registerBlockWithItemBlock(feGen, FE_GEN, new ItemBlockTooltip(feGen, "msg.fe_gen.tip", "msg.fe_gen.tip2", "msg.fe_gen.tip3"));
        registerTileEntity(feGen, FE_GEN, TileEntityFEGenerator.class);

        feCreative = new BlockFECreative();
        registerBlock(feCreative, FE_CREATIVE);
        registerTileEntity(feCreative, FE_CREATIVE, TileEntityFECreative.class);

        dmPump = new BlockDMPump();
        registerBlockWithItemBlock(dmPump, DM_PUMP, new ItemBlockTooltip(dmPump, "msg.dm_pump.tip", "msg.dm_pump.tip2"));
        registerTileEntity(dmPump, DM_PUMP, TileEntityDMPump.class);

        cubeSender = new BlockCubeSender();
        registerBlockWithItemBlock(cubeSender, CUBE_SENDER, new ItemBlockTooltip(cubeSender, "msg.cube_sender.catchphrase"));
        registerTileEntity(cubeSender, CUBE_SENDER, TileEntityCubeSender.class);

        mirror = new BlockMirror();
        registerBlock(mirror, MIRROR);
        registerTileEntity(mirror, MIRROR, TileEntityMirror.class);

        solarFusionController = new BlockSolarFusionController();
        registerBlockWithItemBlock(solarFusionController, SOLAR_FUSION_CONTROLLER, new ItemBlockTooltip(solarFusionController, "msg.solar_fusion.tip", "msg.solar_fusion.tip2", "msg.solar_fusion.tip3"));
        registerTileEntity(solarFusionController, SOLAR_FUSION_CONTROLLER, TileEntitySolarFusionController.class);

        solarFusionCasing = new BlockSolarFusionCasing();
        registerBlock(solarFusionCasing, SOLAR_FUSION_CASING);
        registerTileEntity(solarFusionCasing, SOLAR_FUSION_CASING, TileEntitySolarFusionCasing.class);

        reactorController = new BlockReactorController();
        registerBlock(reactorController, REACTOR_CONTROLLER);
        registerTileEntity(reactorController, REACTOR_CONTROLLER, TileEntityReactorController.class);

        reactorCasing = new BlockReactorCasing();
        registerBlockWithItemBlock(reactorCasing, REACTOR_CASING, new ItemBlockMulti(reactorCasing));
        registerTileEntity(reactorCasing, REACTOR_CASING, TileEntityReactorCasing.class);
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
        registerModel(feCreative, FE_CREATIVE);
        registerModel(dmPump, DM_PUMP);
        registerModel(cubeSender, CUBE_SENDER);
        registerModel(mirror, MIRROR);
        registerModel(solarFusionController, SOLAR_FUSION_CONTROLLER);
        registerModel(solarFusionCasing, SOLAR_FUSION_CASING);
        registerModel(reactorController, REACTOR_CONTROLLER);
        registerMultipleModels(reactorCasing, REACTOR_CASING, 2);
    }

    private static void registerModel(Block block, String name)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
    }

    private static void registerMultipleModels(Block block, String name, int amount)
    {
        registerModel(block, name);
        for (int i = 0; i < amount; i++)
        {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), i,
                    new ModelResourceLocation(MNU.MODID + ":" + name + "_" + i, "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i,
                    new ModelResourceLocation(MNU.MODID + ":" + name + "_" + i, "inventory"));
        }
    }
}
