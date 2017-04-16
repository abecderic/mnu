package com.abecderic.mnu.gui;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.container.ContainerCubeSender;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiCubeSender extends GuiContainer
{
    private static final int WIDTH = 176;
    private static final int HEIGHT = 208;

    private DecimalFormat df = new DecimalFormat("#,##0");

    private TileEntityCubeSender te;

    private static final ResourceLocation background = new ResourceLocation(MNU.MODID, "textures/gui/cube_sender.png");

    public GuiCubeSender(TileEntityCubeSender te, ContainerCubeSender container)
    {
        super(container);
        this.te = te;
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        /* energy bar */
        IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, null);
        if (storage != null)
        {
            int energyHeight = (int) (((storage.getEnergyStored() + 0.0D) / storage.getMaxEnergyStored()) * 70);
            drawTexturedModalRect(guiLeft + 8, guiTop + 78 - energyHeight, 176, 70 - energyHeight, 16, energyHeight);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        mouseX -= getGuiLeft();
        mouseY -= getGuiTop();

        /* energy bar tooltip */
        if (8 <= mouseX && mouseX < 24 && 8 <= mouseY && mouseY < 78)
        {
            IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null)
            {
                List<String> list = new ArrayList<>();
                list.add(new TextComponentTranslation("gui.cube_sender.energy").getFormattedText());
                list.add(new TextComponentTranslation("gui.cube_sender.energy.value", df.format(storage.getEnergyStored()), df.format(storage.getMaxEnergyStored())).getFormattedText());
                drawHoveringText(list, mouseX, mouseY);
            }
        }
    }
}
