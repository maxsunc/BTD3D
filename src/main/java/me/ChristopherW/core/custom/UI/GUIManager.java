package me.ChristopherW.core.custom.UI;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import imgui.*;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDataType;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.custom.UI.UIScreens.GameoverScreen;
import me.ChristopherW.core.custom.UI.UIScreens.GameplayScreen;
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
    public ImFont monkeyFont;
    public ImFont monkeyFontMedium;
    public ImFont monkeyFontSmall;
    public ImFont monkeyFontTiny;
    ImGuiIO io;
    public int window_flags;
    int location = 0;
    public WindowManager window;
    public static String resultText = "Example Title";

    public void render() {
        ImBoolean p_open = new ImBoolean();
        if(currentScreen != "")
            screens.get(currentScreen).render(p_open, this);

        if(Launcher.getGame().getPlayer().getLives() <= 0) {
            screens.get("Gameover").render(p_open, this);
            ImGui.setWindowFocus("gameover");
        }
    }

    public GUIManager(WindowManager window) {
        this.window = window;
        window_flags = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav;
    }
    public void init() {
        // bunch of font loading stuff
        fontConfig = new ImFontConfig();
        io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
        fontAtlas = io.getFonts();
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontDefault();
        monkeyFont = fontAtlas.addFontFromFileTTF("assets/fonts/lucky_font.ttf", 34f, fontConfig);
        monkeyFontMedium = fontAtlas.addFontFromFileTTF("assets/fonts/lucky_font.ttf", 27f, fontConfig);
        monkeyFontSmall = fontAtlas.addFontFromFileTTF("assets/fonts/lucky_font.ttf", 20f, fontConfig);
        monkeyFontTiny = fontAtlas.addFontFromFileTTF("assets/fonts/lucky_font.ttf", 12f, fontConfig);
        font = fontAtlas.addFontFromFileTTF("assets/fonts/mont-heavy.ttf", 34f, fontConfig);
        fontSmall = fontAtlas.addFontFromFileTTF("assets/fonts/mont-heavy.ttf", 17f, fontConfig);
        fontAtlas.build();
        fontConfig.destroy();

        // initialize screens
        screens.put("Gameplay", new GameplayScreen());
        screens.put("Gameover", new GameoverScreen());
        currentScreen = "Gameplay";

        // call the start method for each screen
        for(IGUIScreen screen : screens.values()) {
            screen.start();
        }
    }
}
