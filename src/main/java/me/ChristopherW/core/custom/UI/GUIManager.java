package me.ChristopherW.core.custom.UI;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import imgui.*;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.custom.UI.UIScreens.MainMenu;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.process.Launcher;

public class GUIManager {

    public HashMap<String, IGUIScreen> screens = new HashMap<String, IGUIScreen>();
    public static String currentScreen = "";
    ImFontAtlas fontAtlas;
    ImFontConfig fontConfig;
    public ImFont font;
    public ImFont fontSmall;
    ImGuiIO io;
    public int window_flags;
    int location = 0;
    public WindowManager window;
    public static String resultText = "Example Title";

    public void render() {
        ImBoolean p_open = new ImBoolean();
        if(currentScreen != "")
            screens.get(currentScreen).render(p_open, this);
    }

    public GUIManager(WindowManager window) {
        this.window = window;
        window_flags = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav;
    }
    public void init() {
        // bunch of font loading stuff
        fontConfig = new ImFontConfig();
        io = ImGui.getIO();
        fontAtlas = io.getFonts();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontDefault();
        font = fontAtlas.addFontFromFileTTF("assets/fonts/mont-heavy.ttf", 34f, fontConfig);
        fontSmall = fontAtlas.addFontFromFileTTF("assets/fonts/mont-heavy.ttf", 17f, fontConfig);
        fontAtlas.build();
        fontConfig.destroy();

        // initialize screens
        screens.put("MainMenu", new MainMenu());
        currentScreen = "MainMenu";

        // call the start method for each screen
        for(IGUIScreen screen : screens.values()) {
            screen.start();
        }
    }
}
