package com.abecderic.mnu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;

public class GuiButtonItem extends GuiButton
{
    private String text;
    private State[] states;
    private int currentState;

    public GuiButtonItem(int buttonId, int x, int y, int widthIn, int heightIn, String text, State... states)
    {
        super(buttonId, x, y, widthIn, heightIn, "");
        this.text = text;
        this.states = states;
        this.currentState = 0;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        super.drawButton(mc, mouseX, mouseY);
        mc.getRenderItem().renderItemIntoGUI(new ItemStack(states[currentState].getItem()), xPosition + 2, yPosition + 2);
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY)
    {
        super.drawButtonForegroundLayer(mouseX, mouseY);
    }

    public List<String> getTooltip()
    {
        List<String> list = new ArrayList<>();
        list.add(new TextComponentTranslation(text).getFormattedText());
        list.add(new TextComponentTranslation(states[currentState].getText()).getFormattedText());
        list.add(new TextComponentTranslation("gui.click_to_change").getFormattedText());
        return list;
    }

    public void cycle()
    {
        currentState++;
        if (currentState >= states.length)
        {
            currentState = 0;
        }
    }

    public static class State
    {
        private Item item;
        private String text;

        public State(Item item, String text)
        {
            this.item = item;
            this.text = text;
        }

        public Item getItem()
        {
            return item;
        }

        public String getText()
        {
            return text;
        }
    }
}
