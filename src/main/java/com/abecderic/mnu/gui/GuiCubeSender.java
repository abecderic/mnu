package com.abecderic.mnu.gui;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.container.ContainerCubeSender;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class GuiCubeSender extends GuiContainer
{
    private static final int WIDTH = 180;
    private static final int HEIGHT = 152;

    private static final ResourceLocation background = new ResourceLocation(MNU.MODID, "textures/gui/cube_sender.png");

    public GuiCubeSender(TileEntityCubeSender tileEntity, ContainerCubeSender container)
    {
        super(container);
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
