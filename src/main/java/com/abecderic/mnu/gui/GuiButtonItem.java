package com.abecderic.mnu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GuiButtonItem extends GuiButtonState
{
    public GuiButtonItem(int buttonId, int x, int y, int widthIn, int heightIn, String text, State... states)
    {
        super(buttonId, x, y, widthIn, heightIn, text, states);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        super.drawButton(mc, mouseX, mouseY);
        mc.getRenderItem().renderItemIntoGUI(new ItemStack(((State)states[currentState]).getItem()), xPosition + 2, yPosition + 2);
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY)
    {
        super.drawButtonForegroundLayer(mouseX, mouseY);
    }

    public static class State extends GuiButtonState.State
    {
        private Item item;

        public State(Item item, String text)
        {
            super(text);
            this.item = item;
        }

        public Item getItem()
        {
            return item;
        }
    }
}
