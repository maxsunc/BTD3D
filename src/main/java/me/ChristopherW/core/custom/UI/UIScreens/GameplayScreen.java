package me.ChristopherW.core.custom.UI.UIScreens;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.custom.MonkeyType;
import me.ChristopherW.core.custom.UI.GUIManager;
import me.ChristopherW.core.custom.UI.IGUIScreen;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.process.Game;
import me.ChristopherW.process.Launcher;

public class GameplayScreen implements IGUIScreen {

    Texture gameSpeedButton;
    Texture gameSpeedButtonActive;
    Texture panel;
    Texture coin;
    Texture heart;
    boolean gameSpeedToggled = false;

    @Override
    public void start() {
        ObjectLoader loader = new ObjectLoader();
        panel = loader.createTexture("assets/textures/panel.png");
        coin = loader.createTexture("assets/textures/cash.png");
        heart = loader.createTexture("assets/textures/lives.png");
        gameSpeedButton = loader.createTexture("assets/textures/ff_icon.png");
        gameSpeedButtonActive = loader.createTexture("assets/textures/ff_icon_active.png");
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
        Game game = Launcher.getGame();

        if(game.monkeyMode > 0) {
            ImGui.setNextWindowPos((int)gm.window.getInput().getCurrentPos().x,(int)gm.window.getInput().getCurrentPos().y, 0, 1,0);
            if (ImGui.begin("gameplay_mouse", p_open, gm.window_flags)) {
                ImGui.pushFont(gm.monkeyFontSmall);
                ImGui.image(panel.getId(), 71, 32);
                MonkeyType selectedMonkey = MonkeyType.values()[game.monkeyMode - 1];
                String cost = "$" + String.valueOf(selectedMonkey.cost);
                ImVec2 dimensions = ImGui.calcTextSize(cost);
                ImGui.setCursorPos(35-(dimensions.x/3), 14);
                
                if(game.player.getMoney() >= selectedMonkey.cost)
                    ImGui.pushStyleColor(ImGuiCol.Text, 1f,1f,1f,1f);
                else
                    ImGui.pushStyleColor(ImGuiCol.Text, 1f,0.25f,0.25f,1f);
                ImGui.text(cost);
                ImGui.popStyleColor();
                ImGui.popFont();
            }
            ImGui.end();
        }

        ImGui.pushFont(gm.monkeyFont);
        ImGui.setNextWindowBgAlpha(0f);
        ImGui.setNextWindowSize(0, 0);
        ImGui.setNextWindowPos(0,0, 0, 0,0);
        if (ImGui.begin("gameplay_L", p_open, gm.window_flags)) {
            ImGui.text("Round 0");
            ImGui.image(heart.getId(),32, 32);
            ImGui.sameLine();
            ImGui.text(String.valueOf(game.player.getLives()));
            ImGui.image(coin.getId(),32, 32);
            ImGui.sameLine();
            ImGui.text(String.valueOf(game.player.getMoney()));
        }
        ImGui.end();

        ImGui.setNextWindowPos(gm.window.getWidth(),0, 0, 1,0);
        if (ImGui.begin("gameplay_R", p_open, gm.window_flags)) {
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
            if (ImGui.imageButton((gameSpeedToggled) ? gameSpeedButtonActive.getId() : gameSpeedButton.getId(), 50, 50, 0, 0, 1, 1, 0, 0, 0, 0, 0)) {
                gameSpeedToggled = !gameSpeedToggled;
                game.gameSpeed = (gameSpeedToggled) ? 2f : 1f;
            }
            ImGui.popStyleColor(3);
        }
        
        ImGui.popFont();
        ImGui.end();
    }
}
