package com.abecderic.mnu.item;

import com.abecderic.mnu.MNU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

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
}
