package com.abecderic.mnu.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;

public class GuiButtonState extends GuiButton
{
    protected String text;
    protected State[] states;
    protected int currentState;

    public GuiButtonState(int buttonId, int x, int y, int widthIn, int heightIn, String text, GuiButtonState.State[] states)
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

    public int getState()
    {
        return currentState;
    }

    public void setState(int state)
    {
        this.currentState = state;
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
        private String text;

        public State(String text)
        {
            this.text = text;
        }

        public String getText()
        {
            return text;
        }
    }
}
