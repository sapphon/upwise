package org.sapphon.upwise.presentation;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import java.util.LinkedHashMap;

import static org.mockito.Mockito.verify;

public class ColorPickerTest {

    @Test
    public void setColorPaletteOnModel() {
        LinkedHashMap<String, String> expectedAttributes = new LinkedHashMap<>();
        expectedAttributes.put("color-bg1", "acolor");
        expectedAttributes.put("color-bg2", "anothercolor");
        expectedAttributes.put("color-bg3", "athirdcolor");
        expectedAttributes.put("color-1", "aquaternarycolor");
        expectedAttributes.put("color-2", "aquintarycolor");
        Model mockModel = Mockito.mock(Model.class);
        ColorPicker underTest = new ColorPicker("acolor", "anothercolor", "athirdcolor", "aquaternarycolor", "aquintarycolor");
        underTest.setColorPaletteOnModel(mockModel);
        verify(mockModel).addAllAttributes(expectedAttributes);
    }
}