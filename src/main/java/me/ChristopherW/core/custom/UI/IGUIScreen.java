package me.ChristopherW.core.custom.UI;

import imgui.type.ImBoolean;

public interface IGUIScreen {
    public void start();
    public void render(ImBoolean p_open, GUIManager gm);
}
