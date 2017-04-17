package com.abecderic.mnu.gui;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.container.ContainerCubeSender;
import com.abecderic.mnu.util.MultipleFluidTanks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

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
            int energyHeight = (int) ((storage.getEnergyStored() + 0.0D) / storage.getMaxEnergyStored() * 70);
            drawTexturedModalRect(guiLeft + 8, guiTop + 78 - energyHeight, 176, 70 - energyHeight, 16, energyHeight);
        }

        /* tanks */
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        for (int i = 0; i < TileEntityCubeSender.TANKS; i++)
        {
            int x = (i % 2) * 22 + 34;
            int y = (i / 2) * 38 + 8;
            IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (fluidHandler != null && fluidHandler instanceof MultipleFluidTanks)
            {
                MultipleFluidTanks tanks = (MultipleFluidTanks)fluidHandler;
                FluidTank tank = tanks.getTank(i);
                if (tank != null && tank.getFluid() != null)
                {
                    int fluidHeight = (int) ((tank.getFluidAmount() + 0.0D) / tank.getCapacity() * 32);
                    TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
                    TextureAtlasSprite sprite = map.getTextureExtry(tank.getFluid().getFluid().getStill().toString());
                    if (sprite != null)
                    {
                        if (fluidHeight > 16)
                        {
                            drawTexturedModalRect(guiLeft + x, guiTop + y + 32 - fluidHeight, sprite, 16, fluidHeight - 16);
                            fluidHeight = 16;
                        }
                        drawTexturedModalRect(guiLeft + x, guiTop + y + 32 - fluidHeight, sprite, 16, fluidHeight);
                    }
                }
            }
        }
        mc.renderEngine.bindTexture(background);
        for (int i = 0; i < TileEntityCubeSender.TANKS; i++)
        {
            int x = (i % 2) * 22 + 34;
            int y = (i / 2) * 38 + 8;
            drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 0, 16, 32);
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

        /* tanks */
        for (int i = 0; i < TileEntityCubeSender.TANKS; i++)
        {
            int x = (i % 2) * 22 + 34;
            int y = (i / 2) * 38 + 8;

            if (x <= mouseX && mouseX < x + 16 && y <= mouseY && mouseY < y + 32)
            {
                IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (fluidHandler != null && fluidHandler instanceof MultipleFluidTanks)
                {
                    MultipleFluidTanks tanks = (MultipleFluidTanks)fluidHandler;
                    FluidTank tank = tanks.getTank(i);
                    if (tank == null || tank.getFluid() == null || tank.getFluidAmount() <= 0)
                    {
                        List<String> list = new ArrayList<>();
                        list.add(new TextComponentTranslation("gui.cube_sender.tank.empty").getFormattedText());
                        drawHoveringText(list, mouseX, mouseY);
                    }
                    else
                    {
                        List<String> list = new ArrayList<>();
                        list.add(new TextComponentTranslation("gui.cube_sender.fluid", tank.getFluid().getLocalizedName()).getFormattedText());
                        list.add(new TextComponentTranslation("gui.cube_sender.tank.value", df.format(tank.getFluidAmount()), df.format(tank.getCapacity())).getFormattedText());
                        drawHoveringText(list, mouseX, mouseY);
                    }
                }
            }
        }
    }
}
