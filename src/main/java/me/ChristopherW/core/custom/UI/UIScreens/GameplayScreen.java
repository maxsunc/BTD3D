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
import me.ChristopherW.core.custom.TowerType;
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
    Texture primarySlot;
    Texture militarySlot;
    Texture shop;
    boolean gameSpeedToggled = false;
    Texture[] tower_icons = new Texture[8];

    @Override
    public void start() {
        ObjectLoader loader = new ObjectLoader();
        panel = loader.createTexture("assets/textures/panel.png");
        coin = loader.createTexture("assets/textures/cash.png");
        heart = loader.createTexture("assets/textures/lives.png");
        shop = loader.createTexture("assets/textures/shop_large.png");
        primarySlot = loader.createTexture("assets/textures/primary_slot.png");
        militarySlot = loader.createTexture("assets/textures/military_slot.png");
        gameSpeedButton = loader.createTexture("assets/textures/ff_icon.png");
        gameSpeedButtonActive = loader.createTexture("assets/textures/ff_icon_active.png");

        tower_icons[0] = loader.createTexture("assets/textures/dart_monkey.png");
        tower_icons[1] = loader.createTexture("assets/textures/sniper_monkey.png");
        tower_icons[2] = loader.createTexture("assets/textures/bomb_shooter.png");
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
        Game game = Launcher.getGame();

        if(game.monkeyMode > 0) {
            ImGui.setNextWindowPos((int)gm.window.getInput().getCurrentPos().x,(int)gm.window.getInput().getCurrentPos().y, 0, 1,0);
            if (ImGui.begin("gameplay_mouse", p_open, gm.window_flags)) {
                ImGui.pushFont(gm.monkeyFontSmall);
                ImGui.image(panel.getId(), 71, 32);
                TowerType selectedMonkey = TowerType.values()[game.monkeyMode - 1];
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
            ImGui.image(heart.getId(),32, 28);
            ImGui.sameLine();
            ImGui.text(String.valueOf(game.player.getLives()));
            ImGui.image(coin.getId(),32, 30);
            ImGui.sameLine();
            ImGui.text(String.valueOf(game.player.getMoney()));
        }
        ImGui.end();

        ImGui.setNextWindowPos(gm.window.getWidth() + 5,gm.window.getHeight() + 5, 0, 1,1);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowMinSize, 0,0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0,0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0,0);
        if (ImGui.begin("gameplay_R", p_open, gm.window_flags)) {
            float panelWidth = gm.window.getHeight() * 0.3f;
            float panelHeight = gm.window.getHeight();
            ImGui.image(shop.getId(), panelWidth, panelHeight);



            ImGui.popFont();
            ImGui.pushFont(gm.monkeyFontSmall);
            String title = game.monkeyMode > 0 ? TowerType.values()[game.monkeyMode - 1].name : "Shop";
            ImVec2 textDim = ImGui.calcTextSize(title);
            ImGui.setCursorPos(panelWidth/2 - textDim.x/2, panelHeight/24);
            ImGui.text(title);

            int currentX = 0;
            int currentY = 0;
            float towerImageSize = panelHeight/10;
            for(int i = 0; i < TowerType.values().length; i++) {
                TowerType t = TowerType.values()[i];

                ImGui.setCursorPos(panelWidth/7 + (currentX * (towerImageSize + gm.window.getWidth()/256)), panelHeight/10 + (currentY * (towerImageSize + gm.window.getWidth()/64)));
                ImVec2 oldCursor = ImGui.getCursorPos();
                switch (t.towerClass) {
                    case PRIMARY:
                        ImGui.image(primarySlot.getId(), towerImageSize, towerImageSize);
                        break;
                
                    case MILITARY:
                        ImGui.image(militarySlot.getId(), towerImageSize, towerImageSize);
                        break;
                }
                ImGui.setCursorPos(oldCursor.x, oldCursor.y);

                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1, 1, 1, 0.25f);
                if(ImGui.imageButton(tower_icons[i].getId(), towerImageSize, towerImageSize)) {
                    game.monkeyMode = (Integer.valueOf(i) + 1);
                }
                ImGui.popStyleColor(3);

                if(game.player.getMoney() >= TowerType.values()[i].cost)
                    ImGui.pushStyleColor(ImGuiCol.Text, 1f,1f,1f,1f);
                else
                    ImGui.pushStyleColor(ImGuiCol.Text, 1f,0.25f,0.25f,1f);
                String cost = "$" + String.valueOf(TowerType.values()[i].cost);
                ImVec2 costSize = ImGui.calcTextSize(cost);
                ImGui.setCursorPos(oldCursor.x + towerImageSize/2 - costSize.x/2, oldCursor.y + towerImageSize);
                ImGui.text(cost);
                ImGui.popStyleColor();

                currentX++;
                if(currentX > 1) {
                    currentX = 0;
                    currentY++;
                }
            }

            float imageSize = panelHeight * 0.1388f;
            ImGui.setCursorPos(panelWidth/2 - imageSize/2, panelHeight - panelHeight/5);
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
            if (ImGui.imageButton((gameSpeedToggled) ? gameSpeedButtonActive.getId() : gameSpeedButton.getId(), imageSize, imageSize, 0, 0, 1, 1, 0, 0, 0, 0, 0)) {
                gameSpeedToggled = !gameSpeedToggled;
                game.gameSpeed = (gameSpeedToggled) ? 2f : 1f;
            }
            ImGui.popStyleColor(3);
        }
        ImGui.popStyleVar(3);
        
        ImGui.popFont();
        ImGui.end();
    }
}
