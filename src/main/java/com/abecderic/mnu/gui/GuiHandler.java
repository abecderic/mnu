package com.abecderic.mnu.gui;

import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.container.ContainerCubeSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler
{
    public enum GUIs
    {
        CUBE_SENDER;
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(GUIs.values()[ID])
        {
            case CUBE_SENDER:
                return new ContainerCubeSender(player.inventory, (TileEntityCubeSender) world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(GUIs.values()[ID])
        {
            case CUBE_SENDER:
                TileEntityCubeSender te = (TileEntityCubeSender)world.getTileEntity(new BlockPos(x, y, z));
                return new GuiCubeSender(te, new ContainerCubeSender(player.inventory, te));
            default:
                return null;
        }
    }
}
