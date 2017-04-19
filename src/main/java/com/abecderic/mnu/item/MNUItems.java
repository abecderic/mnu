package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class MNUItems
{
    public static final String DM_SCANNER = "dm_scanner";
    public static Item dmScanner;

    public static final String WRENCH = "wrench";
    public static Item wrench;

    public static final String UPGRADE_ITEMS_TRANSFER = "upgrade_items_transfer";
    public static Item upgradeItemsTransfer;

    public static final String UPGRADE_FLUIDS_TRANSFER = "upgrade_fluids_transfer";
    public static Item upgradeFluidsTransfer;

    public static final String UPGRADE_ENERGY = "upgrade_energy";
    public static Item upgradeEnergy;

    public static final String UPGRADE_SPEED = "upgrade_speed";
    public static Item upgradeSpeed;

    public static final String LOGIC_MODULE = "logic_module";
    public static Item logicModule;

    public static void registerItems()
    {
        dmScanner = new ItemDMScanner();
        registerItem(dmScanner, DM_SCANNER);

        wrench = new ItemWrench();
        registerItem(wrench, WRENCH);
        OreDictionary.registerOre("itemWrench", wrench);

        upgradeItemsTransfer = new ItemUpgradeTransfer(ItemUpgradeTransfer.Type.ITEMS);
        registerItem(upgradeItemsTransfer, UPGRADE_ITEMS_TRANSFER);

        upgradeFluidsTransfer = new ItemUpgradeTransfer(ItemUpgradeTransfer.Type.FLUIDS);
        registerItem(upgradeFluidsTransfer, UPGRADE_FLUIDS_TRANSFER);

        upgradeEnergy = new ItemUpgradeEnergy();
        registerItem(upgradeEnergy, UPGRADE_ENERGY);

        upgradeSpeed = new ItemUpgradeSpeed();
        registerItem(upgradeSpeed, UPGRADE_SPEED);

        logicModule = new ItemLogicModule();
        registerItem(logicModule, LOGIC_MODULE);
    }

    private static void registerItem(Item item, String name)
    {
        item.setRegistryName(name);
        GameRegistry.register(item);
    }

    public static void registerModels()
    {
        registerModelWithMeta(dmScanner, DM_SCANNER, 7);
        registerModel(wrench, WRENCH);
        registerModelWithSameMeta(upgradeItemsTransfer, UPGRADE_ITEMS_TRANSFER, 16);
        registerModelWithSameMeta(upgradeFluidsTransfer, UPGRADE_FLUIDS_TRANSFER, 16);
        registerModel(upgradeEnergy, UPGRADE_ENERGY);
        registerModel(upgradeSpeed, UPGRADE_SPEED);
        registerModel(logicModule, LOGIC_MODULE);
    }

    private static void registerModel(Item item, String name)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
        ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
    }

    private static void registerModelWithSameMeta(Item item, String name, int amount)
    {
        for (int i = 0; i < amount; i++)
        {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, i,
                    new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
            ModelLoader.setCustomModelResourceLocation(item, i,
                    new ModelResourceLocation(MNU.MODID + ":" + name, "inventory"));
        }
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

    public static void registerRecipes()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(dmScanner), " d ", "igi", "r r", 'd', "dustGlowstone", 'i', "ingotIron", 'g', "nuggetGold", 'r', "dustRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(wrench), "i i", " iy", " iy", 'i', "ingotIron", 'y', "dyeYellow"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(upgradeItemsTransfer), "rrr", "rpr", "sds", 'r', "dustRedstone", 'p', Blocks.PISTON, 's', "stone", 'd', "gemDiamond"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(upgradeFluidsTransfer), "b", "u", 'u', upgradeItemsTransfer, 'b', Items.BUCKET));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(upgradeEnergy), "rrr", "rbr", "sds", 'r', "dustRedstone", 'b', "blockRedstone", 's', "stone", 'd', "gemDiamond"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(upgradeSpeed), "uuu", "ubu", "sds", 'u', Items.SUGAR, 'b', "blockLapis", 's', "stone", 'd', "gemDiamond"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(logicModule), " l ", "gig", "r r", 'l', "dyeBlue", 'g', "nuggetGold", 'i', "ingotIron", 'r', "dustRedstone"));
    }
}
