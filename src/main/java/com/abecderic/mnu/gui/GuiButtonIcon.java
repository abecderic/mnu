package com.abecderic.mnu.gui;

import net.minecraft.client.Minecraft;

public class GuiButtonIcon extends GuiButtonState
{
    private int iconWidth;
    private int iconHeight;

    public GuiButtonIcon(int buttonId, int x, int y, int widthIn, int heightIn, String text, int iconWidth, int iconHeight, GuiButtonIcon.State... states)
    {
        super(buttonId, x, y, widthIn, heightIn, text, states);
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        super.drawButton(mc, mouseX, mouseY);
        mc.renderEngine.bindTexture(GuiCubeSender.BACKGROUND);
        State state = (GuiButtonIcon.State)states[currentState];
        drawTexturedModalRect(xPosition + 2, yPosition + 2, state.getTextureX(), state.getTextureY(), iconWidth, iconHeight);
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY)
    {
        super.drawButtonForegroundLayer(mouseX, mouseY);
    }

    public static class State extends GuiButtonState.State
    {
        private int textureX;
        private int textureY;

        public State(String text, int textureX, int textureY)
        {
            super(text);
            this.textureX = textureX;
            this.textureY = textureY;
        }

        public int getTextureX()
        {
            return textureX;
        }

        public int getTextureY()
        {
            return textureY;
        }
    }
}
