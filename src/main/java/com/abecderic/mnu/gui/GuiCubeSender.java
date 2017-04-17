package com.abecderic.mnu.gui;

import com.abecderic.mnu.MNU;
import com.abecderic.mnu.block.TileEntityCubeSender;
import com.abecderic.mnu.container.ContainerCubeSender;
import com.abecderic.mnu.network.MNUNetwork;
import com.abecderic.mnu.network.PacketCubeSenderButton;
import com.abecderic.mnu.util.MultipleFluidTanks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiCubeSender extends GuiContainer
{
    private static final int WIDTH = 176;
    private static final int HEIGHT = 208;

    private DecimalFormat df = new DecimalFormat("#,##0");

    private TileEntityCubeSender te;

    private GuiButtonItem redstone;
    private GuiButtonIcon energyOnly;

    public static final ResourceLocation BACKGROUND = new ResourceLocation(MNU.MODID, "textures/gui/cube_sender.png");

    public GuiCubeSender(TileEntityCubeSender te, ContainerCubeSender container)
    {
        super(container);
        this.te = te;
        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        redstone = new GuiButtonItem(0, guiLeft + 124, guiTop + 6, 20, 20, "gui.cube_sender.redstone",
                new GuiButtonItem.State(Items.GUNPOWDER, "gui.cube_sender.redstone.always_on"),
                new GuiButtonItem.State(Items.REDSTONE, "gui.cube_sender.redstone.with_redstone_off"),
                new GuiButtonItem.State(Item.getItemFromBlock(Blocks.REDSTONE_TORCH), "gui.cube_sender.redstone.with_redstone_on"),
                new GuiButtonItem.State(Items.REPEATER, "gui.cube_sender.redstone.on_pulse"));
        addButton(redstone);
        energyOnly = new GuiButtonIcon(1, guiLeft + 100, guiTop + 6, 20, 20, "gui.cube_sender.energyonly", 16, 16,
                new GuiButtonIcon.State("gui.cube_sender.energyonly.disabled", 176, 70),
                new GuiButtonIcon.State("gui.cube_sender.energyonly.enabled", 176, 86));
        addButton(energyOnly);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        mc.getTextureManager().bindTexture(BACKGROUND);
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
        mc.renderEngine.bindTexture(BACKGROUND);
        for (int i = 0; i < TileEntityCubeSender.TANKS; i++)
        {
            int x = (i % 2) * 22 + 34;
            int y = (i / 2) * 38 + 8;
            drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 0, 16, 32);
        }

        /* buttons */
        redstone.setState(te.getRedstoneMode());
        energyOnly.setState(te.isEnergyOnly() ? 1 : 0);
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

        /* buttons */
        for (GuiButton button : buttonList)
        {
            if (button.isMouseOver() && button instanceof GuiButtonState)
            {
                drawHoveringText(((GuiButtonState)button).getTooltip(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);
        if (button.equals(redstone))
        {
            redstone.cycle();
            te.setRedstoneMode(redstone.getState());
        }
        else if (button.equals(energyOnly))
        {
            energyOnly.cycle();
            te.setEnergyOnly(energyOnly.getState() == 1);
        }
        MNUNetwork.snw.sendToServer(new PacketCubeSenderButton(te.getWorld().provider.getDimension(), te.getPos(), redstone.getState(), energyOnly.getState() == 1));
    }
}
