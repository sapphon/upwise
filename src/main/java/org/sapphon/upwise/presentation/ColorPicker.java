package org.sapphon.upwise.presentation;

import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ColorPicker {

    List<String> currentColors;
    private static final String[] COLOR_NAMES = {"color-bg1", "color-bg2", "color-bg3", "color-1", "color-2"};
    public ColorPicker(String... colors){
        currentColors = Arrays.asList(colors);
    }

    public void setColorPaletteOnModel(Model model){
        model.addAllAttributes(createPaletteMap());
    }

    private LinkedHashMap<String, String> createPaletteMap() {
        LinkedHashMap<String, String> colorPalette = new LinkedHashMap<>();
        for (int i = 0; i < COLOR_NAMES.length; i++) {
            colorPalette.put(COLOR_NAMES[i], currentColors.get(i) == null ? "#eeeeee" : currentColors.get(i));
        }
        return colorPalette;
    }
}
