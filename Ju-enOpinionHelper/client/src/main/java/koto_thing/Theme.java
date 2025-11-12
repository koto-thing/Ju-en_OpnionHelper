package koto_thing;

import java.awt.*;

public class Theme {
    private Color backgroundColor;
    private Color foregroundColor;
    private Color panelColor;
    private Color borderColor;
    private Color buttonColor;
    private Color buttonTextColor;

    public static Theme getLightTheme() {
        Theme theme = new Theme();
        theme.backgroundColor = Color.WHITE;
        theme.foregroundColor = Color.BLACK;
        theme.panelColor = new Color(240, 240, 240);
        theme.borderColor = new Color(200, 200, 200);
        theme.buttonColor = new Color(230, 230, 230);
        theme.buttonTextColor = Color.BLACK;
        return theme;
    }

    public static Theme getDarkTheme() {
        Theme theme = new Theme();
        theme.backgroundColor = new Color(43, 43, 43);
        theme.foregroundColor = new Color(220, 220, 220);
        theme.panelColor = new Color(60, 63, 65);
        theme.borderColor = new Color(80, 80, 80);
        theme.buttonColor = new Color(75, 110, 175);
        theme.buttonTextColor = Color.WHITE;
        return theme;
    }

    public Color getBackgroundColor() { return backgroundColor; }
    public Color getForegroundColor() { return foregroundColor; }
    public Color getPanelColor() { return panelColor; }
    public Color getBorderColor() { return borderColor; }
    public Color getButtonColor() { return buttonColor; }
    public Color getButtonTextColor() { return buttonTextColor; }
}
