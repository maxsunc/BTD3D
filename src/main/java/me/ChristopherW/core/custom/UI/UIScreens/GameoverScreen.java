package me.ChristopherW.core.custom.UI.UIScreens;

import imgui.ImGui;
import imgui.ImVec2;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.custom.Bloon;
import me.ChristopherW.core.custom.Projectile;
import me.ChristopherW.core.custom.Tower;
import me.ChristopherW.core.custom.UI.GUIManager;
import me.ChristopherW.core.custom.UI.IGUIScreen;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.process.Game;
import me.ChristopherW.process.Launcher;

public class GameoverScreen implements IGUIScreen {


    private void textOutline(String text, Color fill, Color outline) {
        ImVec2 currentCursor = new ImVec2(ImGui.getCursorPos());
        int size = 2;
        ImGui.setCursorPos(currentCursor.x - size,  currentCursor.y - size);
        ImGui.textColored(outline.getRed(), outline.getGreen(), outline.getBlue(), outline.getAlpha(), text);
        ImGui.setCursorPos(currentCursor.x + size,  currentCursor.y - size);
        ImGui.textColored(outline.getRed(), outline.getGreen(), outline.getBlue(), outline.getAlpha(), text);
        ImGui.setCursorPos(currentCursor.x - size,  currentCursor.y + size);
        ImGui.textColored(outline.getRed(), outline.getGreen(), outline.getBlue(), outline.getAlpha(), text);
        ImGui.setCursorPos(currentCursor.x + size,  currentCursor.y + size);
        ImGui.textColored(outline.getRed(), outline.getGreen(), outline.getBlue(), outline.getAlpha(), text);
        ImGui.setCursorPos(currentCursor.x,  currentCursor.y - size);
        ImGui.textColored(outline.getRed(), outline.getGreen(), outline.getBlue(), outline.getAlpha(), text);
        ImGui.setCursorPos(currentCursor.x,  currentCursor.y + size);
        ImGui.textColored(outline.getRed(), outline.getGreen(), outline.getBlue(), outline.getAlpha(), text);
        ImGui.setCursorPos(currentCursor.x - size,  currentCursor.y);
        ImGui.textColored(outline.getRed(), outline.getGreen(), outline.getBlue(), outline.getAlpha(), text);
        ImGui.setCursorPos(currentCursor.x + size,  currentCursor.y);
        ImGui.textColored(outline.getRed(), outline.getGreen(), outline.getBlue(), outline.getAlpha(), text);

        ImGui.setCursorPos(currentCursor.x, currentCursor.y);
        ImGui.textColored(fill.getRed(), fill.getGreen(), fill.getBlue(), fill.getAlpha(), text);
    }

    Texture panel;
    Texture defeat;
    Texture defeatMonkey;
    Texture restartButton;

    @Override
    public void start() {
        ObjectLoader loader = new ObjectLoader();
        panel = loader.createTexture("assets/textures/DefeatPanel.png");
        defeat = loader.createTexture("assets/textures/Defeat.png");
        defeatMonkey = loader.createTexture("assets/textures/DefeatMonkey.png");
        restartButton = loader.createTexture("assets/textures/restart.png");
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
        Game game = Launcher.getGame();

        ImGui.pushFont(gm.monkeyFontMedium);
        ImGui.setNextWindowBgAlpha(0.5f);
        ImGui.setNextWindowSize(gm.window.getWidth(), gm.window.getHeight());
        ImGui.setNextWindowPos(0,0, 0, 0,0);
        int custom_window_flags = ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoSavedSettings | ImGuiWindowFlags.NoFocusOnAppearing | ImGuiWindowFlags.NoNav;
        if (ImGui.begin("gameover", p_open, custom_window_flags)) {
            float panelWidth = gm.window.getWidth()/3;
            ImGui.setCursorPos(gm.window.getWidth() / 2 - panelWidth/2, gm.window.getHeight() / 2 - (panelWidth / 3) / 2);
            ImGui.image(panel.getId(), panelWidth, panelWidth/3);

            ImGui.setCursorPos(gm.window.getWidth() / 2 - panelWidth/4, gm.window.getHeight() / 2 - (panelWidth / 2.2f));
            ImGui.image(defeatMonkey.getId(), panelWidth/2, Math.round(panelWidth/2 * 0.566f));

            ImGui.setCursorPos(gm.window.getWidth() / 2 - panelWidth/4, gm.window.getHeight() / 2 - (panelWidth / 3/1.5f));
            ImGui.image(defeat.getId(), panelWidth/2, panelWidth/2 * 0.2666f);

            String roundNumber = "Round " + String.valueOf(game.getRoundNumber());
            ImVec2 roundNumDim = ImGui.calcTextSize(roundNumber);
            ImGui.setCursorPos(gm.window.getWidth()/2 - roundNumDim.x/2, gm.window.getHeight()/2 - panelWidth/16);
            textOutline("Round " + String.valueOf(game.getRoundNumber()), Color.white, Color.black);

            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
            float imageSize = gm.window.getHeight() * 0.1388f / 1.25f;
            ImGui.setCursorPos(gm.window.getWidth()/2 - imageSize/2, gm.window.getHeight()/2 + panelWidth/12 - imageSize/3);
            if(ImGui.imageButton(restartButton.getId(), imageSize, imageSize)) {
                for(Tower t : game.getTowers()) {
                    game.entities.remove(t.getName());
                }
                game.getTowers().clear();
                for(Bloon b : game.getBloons()) {
                    game.entities.remove(b.getName());
                }
                game.getBloons().clear();
                for(Projectile p : game.getProjectiles()) {
                    game.entities.remove(p.getName());
                }
                game.getProjectiles().clear();

                game.getPlayer().setLives(200);
                game.getPlayer().setMoney(650);
                game.setRoundNumber(1);
                game.setRoundRunning(false);
                game.setRoundShouldRun(true);
                game.setGameSpeed((game.isFastForward()) ? 3f : 1f);
                game.setCurrentTower(null);
                game.getRange().setEnabled(false);
                try {
                    game.setRoundScanner(new Scanner(new File("assets/rounds.txt")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            ImGui.popStyleColor(3);
            ImGui.popFont();
            ImGui.pushFont(gm.monkeyFontSmall);
            String restart = "Restart";
            ImVec2 restartDim = ImGui.calcTextSize(restart);
            ImGui.setCursorPos(gm.window.getWidth()/2 - restartDim.x/2, gm.window.getHeight()/2 + panelWidth/4.5f);
            textOutline(restart, Color.white, Color.black);

        }
        
        ImGui.popFont();
        ImGui.end();
    }
}
