package com.abecderic.mnu.util;

import java.text.DecimalFormat;

public class DarkMatterDeposits
{
    private static DecimalFormat df = new DecimalFormat("0.0");

    public static int getVolumeForCoords(int x, int z)
    {
        double d = Math.sin(x / (2*Math.PI)) + Math.cos(1 / 3d * x + z / (2 * Math.PI)) + 1;
        if (d <= 1)
        {
            d = Math.pow(d, 2);
        }
        else if (d > 1.90)
        {
            d = Math.pow(2, d) * 5;
        }
        else
        {
            d = Math.pow(d, 3);
        }
        return (int)(d * 1000);
    }

    public static String getEstimateText(int volume)
    {
        String text = df.format(volume / 1000D);
        switch (getAmountCategory(volume))
        {
            case 1:
                text = "\u00A74" + text;
                break;
            case 2:
                text = "\u00A7c" + text;
                break;
            case 3:
                text = "\u00A76" + text;
                break;
            case 4:
                text = "\u00A7e" + text;
                break;
            case 5:
                text = "\u00A7a" + text;
                break;
            case 6:
                text = "\u00A72" + text;
                break;
        }
        return text;
    }

    public static int getAmountCategory(int volume)
    {
        if (volume <= 250)
        {
            return 1;
        }
        else if (volume <= 1000)
        {
            return 2;
        }
        else if (volume <= 5000)
        {
            return 3;
        }
        else if (volume <= 10000)
        {
            return 4;
        }
        else if (volume <= 30000)
        {
            return 5;
        }
        else
        {
            return 6;
        }
    }
}
