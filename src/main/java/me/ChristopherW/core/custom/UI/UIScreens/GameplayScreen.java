package me.ChristopherW.core.custom.UI.UIScreens;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import java.awt.Color;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.custom.Assets;
import me.ChristopherW.core.custom.Tower;
import me.ChristopherW.core.custom.TowerType;
import me.ChristopherW.core.custom.Upgrade;
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
    Texture magicSlot;
    Texture upgrade;
    Texture upgradeDisabled;
    Texture upgradePanel;
    Texture cancel;
    Texture play;
    Texture music;
    Texture musicDisabled;
    Texture autoStart;
    Texture autoStartEnabled;
    Texture shop;
    Texture sell;
    Texture[] tower_icons = new Texture[8];

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

    @Override
    public void start() {
        ObjectLoader loader = new ObjectLoader();
        panel = loader.createTexture("assets/textures/panel.png");
        coin = loader.createTexture("assets/textures/cash.png");
        heart = loader.createTexture("assets/textures/lives.png");
        shop = loader.createTexture("assets/textures/shop_large.png");
        primarySlot = loader.createTexture("assets/textures/primary_slot.png");
        militarySlot = loader.createTexture("assets/textures/military_slot.png");
        magicSlot = loader.createTexture("assets/textures/magic_slot.png");
        gameSpeedButton = loader.createTexture("assets/textures/ff_icon.png");
        gameSpeedButtonActive = loader.createTexture("assets/textures/ff_icon_active.png");
        upgrade = loader.createTexture("assets/textures/upgrade.png");
        upgradeDisabled = loader.createTexture("assets/textures/upgrade_disabled.png");
        upgradePanel = loader.createTexture("assets/textures/upgrade_panel.png");
        autoStart = loader.createTexture("assets/textures/auto_start.png");
        autoStartEnabled = loader.createTexture("assets/textures/auto_start_active.png");

        cancel = loader.createTexture("assets/textures/cancel.png");
        play = loader.createTexture("assets/textures/play.png");
        music = loader.createTexture("assets/textures/music.png");
        musicDisabled = loader.createTexture("assets/textures/music_disabled.png");
        sell = loader.createTexture("assets/textures/sell.png");

        tower_icons[0] = loader.createTexture("assets/textures/dart_monkey.png");
        tower_icons[1] = loader.createTexture("assets/textures/sniper_monkey.png");
        tower_icons[2] = loader.createTexture("assets/textures/bomb_shooter.png");
        tower_icons[3] = loader.createTexture("assets/textures/super_monkey.png");
    }

    @Override
    public void render(ImBoolean p_open, GUIManager gm) {
        Game game = Launcher.getGame();
        float panelWidth = gm.window.getHeight() * 0.3f;
        float panelHeight = gm.window.getHeight();

        if(game.getMonkeyPlacingId() > 0) {
            ImGui.setNextWindowPos((int)gm.window.getInput().getCurrentPos().x - 15,(int)gm.window.getInput().getCurrentPos().y, 0, 1,0);
            if (ImGui.begin("gameplay_mouse", p_open, gm.window_flags)) {
                ImGui.pushFont(gm.monkeyFontMedium);
                //ImGui.image(panel.getId(), 71, 32);
                TowerType selectedMonkey = TowerType.values()[game.getMonkeyPlacingId() - 1];
                String cost = "$" + String.valueOf(selectedMonkey.cost);
                ImVec2 dimensions = ImGui.calcTextSize(cost);
                ImGui.setCursorPos(35-(dimensions.x/3), 14);
                
                Color costColor;
                if(game.getPlayer().getMoney() >= selectedMonkey.cost)
                    costColor = Color.white;
                else
                    costColor = Color.red;
                textOutline(cost, costColor, Color.black);
                ImGui.popFont();
            }
            ImGui.end();
        }

        ImGui.pushFont(gm.monkeyFontMedium);
        ImGui.setNextWindowBgAlpha(0f);
        ImGui.setNextWindowSize(0, 0);
        ImGui.setNextWindowPos(0,0, 0, 0,0);
        if (ImGui.begin("gameplay_L", p_open, gm.window_flags)) {
            ImGui.image(heart.getId(),32, 28);
            ImGui.sameLine();
            ImGui.setCursorPosY(ImGui.getCursorPosY() + 5);
            textOutline(String.valueOf(game.getPlayer().getLives()), Color.white, Color.black);
            ImGui.sameLine();
            ImGui.setCursorPosY(ImGui.getCursorPosY() - 5);
            ImGui.setCursorPosX(ImGui.getCursorPosX() + 10);
            ImGui.image(coin.getId(),32, 30);
            ImGui.sameLine();
            ImGui.setCursorPosY(ImGui.getCursorPosY() + 5);
            textOutline("$" + String.valueOf(game.getPlayer().getMoney()), Color.white, Color.black);
            ImGui.sameLine();
            ImGui.popFont();
            ImGui.pushFont(gm.monkeyFontSmall);
            ImVec2 roundDim = ImGui.calcTextSize("Round");
            ImGui.setCursorPosX(gm.window.getWidth() - panelWidth - roundDim.x - gm.window.getWidth()/128);
            ImVec2 pos = new ImVec2(ImGui.getCursorPos());
            String roundNumber = String.valueOf(game.getRoundNumber());
            ImVec2 roundNumDim = ImGui.calcTextSize(roundNumber);
            textOutline("Round", Color.white, Color.black);
            ImGui.setCursorPos(pos.x + roundDim.x - roundNumDim.x - gm.window.getWidth()/128, pos.y + 25);
            ImGui.popFont();
            ImGui.pushFont(gm.monkeyFont);
            textOutline(roundNumber, Color.white, Color.black);
        }
        ImGui.end();

        ImGui.setNextWindowBgAlpha(0f);
        ImGui.setNextWindowSize(0,0);
        ImGui.setNextWindowPos(5,gm.window.getHeight() - 5, 0, 0,1);
        if (ImGui.begin("gameplay_L_bottom", p_open, gm.window_flags)) {
            float imageSize = panelHeight * 0.07f;
            boolean musicStatus = game.music.getGain() > 0f;
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1, 1, 1, 0f);
            if(ImGui.imageButton(musicStatus ? music.getId() : musicDisabled.getId(), imageSize, imageSize)) {
                if(musicStatus)
                    game.music.setGain(0.0f);
                else
                    game.music.setGain(0.4f);;
            }
            ImGui.sameLine();
            if(ImGui.imageButton(game.isAutoStart() ? autoStartEnabled.getId() : autoStart.getId(), imageSize, imageSize)) {
                game.setAutoStart(!game.isAutoStart());
            }
            ImGui.popStyleColor(3);
        }
        ImGui.end();

        ImGui.setNextWindowPos(gm.window.getWidth() + 5,gm.window.getHeight() + 5, 0, 1,1);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowMinSize, 0,0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0,0);
        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0,0);
        if (ImGui.begin("gameplay_R", p_open, gm.window_flags)) {
            ImGui.image(shop.getId(), panelWidth, panelHeight);



            ImGui.popFont();
            ImGui.pushFont(gm.monkeyFontSmall);
            String title = game.getMonkeyPlacingId() > 0 ? TowerType.values()[game.getMonkeyPlacingId() - 1].name : "Shop";
            ImVec2 textDim = ImGui.calcTextSize(title);
            ImGui.setCursorPos(panelWidth/2 - textDim.x/2, panelHeight/24);
            textOutline(title, Color.white, Color.black);

            int currentX = 0;
            int currentY = 0;
            float towerImageSize = panelHeight/10;
            for(int i = 0; i < TowerType.values().length; i++) {
                TowerType t = TowerType.values()[i];

                ImGui.setCursorPos(panelWidth/7 + (currentX * (towerImageSize + gm.window.getWidth()/256)), panelHeight/10 + (currentY * (towerImageSize + gm.window.getHeight()/32)));
                ImVec2 oldCursor = ImGui.getCursorPos();
                switch (t.towerClass) {
                    case PRIMARY:
                        ImGui.image(primarySlot.getId(), towerImageSize, towerImageSize);
                        break;
                
                    case MILITARY:
                        ImGui.image(militarySlot.getId(), towerImageSize, towerImageSize);
                        break;

                    case MAGIC:
                        ImGui.image(magicSlot.getId(), towerImageSize, towerImageSize);
                        break;
                }
                ImGui.setCursorPos(oldCursor.x, oldCursor.y);

                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1, 1, 1, 0.25f);
                if(ImGui.imageButton(tower_icons[i].getId(), towerImageSize, towerImageSize)) {
                    game.setMonkeyPlacingId(Integer.valueOf(i) + 1);
                }
                ImGui.popStyleColor(3);
                Color costColor;
                if(game.getPlayer().getMoney() >= TowerType.values()[i].cost)
                    costColor = Color.white;
                else
                    costColor = Color.RED;
                String cost = "$" + String.valueOf(TowerType.values()[i].cost);
                ImVec2 costSize = ImGui.calcTextSize(cost);
                ImGui.setCursorPos(oldCursor.x + towerImageSize/2 - costSize.x/2, oldCursor.y + towerImageSize);
                textOutline(cost, costColor, Color.black);

                currentX++;
                if(currentX > 1) {
                    currentX = 0;
                    currentY++;
                }
            }

            ImGui.setCursorPosY(ImGui.getCursorPosY() + panelHeight/16);
            float upgradeSize = panelHeight * 0.1f;
            float upgradeIconSize = panelHeight * 0.075f;
            if(game.getCurrentTower() != null) {
                Tower selected = game.getCurrentTower();
                ImGui.setCursorPosX(ImGui.getCursorPosX() + panelWidth/2 - (upgradeSize * 1.25f));
                ImVec2 panelPos1 = ImGui.getCursorPos();
                ImGui.image(upgradePanel.getId(), upgradeSize * 1.25f, upgradeSize);

                Upgrade nextUpgrade1 = selected.getPath1().nextUpgrade;
                int costValue1 = Integer.MAX_VALUE;
                int ordinal1 = 4;
                String name1 = "";
                if(nextUpgrade1 != null) {
                    costValue1 = nextUpgrade1.cost;
                    ordinal1 = nextUpgrade1.ordinal();
                    name1 = nextUpgrade1.name;
                }

                boolean affordable = costValue1 <= game.getPlayer().getMoney();
                ImGui.sameLine();
                ImGui.setCursorPosX(ImGui.getCursorPosX() - 15);
                ImVec2 old = ImGui.getCursorPos();
                ImGui.image(affordable ? upgrade.getId() : upgradeDisabled.getId(), upgradeSize * 1.25f, upgradeSize);

                Upgrade currentUpgrade1 = selected.getPath1();
                String currentName1 = currentUpgrade1.name;

                if(!currentName1.equals("")) {
                    ImGui.setCursorPos(panelPos1.x + (upgradeSize/2 - upgradeIconSize/2), panelPos1.y + (upgradeSize/2 - upgradeIconSize/2));
                    ImGui.image(Assets.upgradeTextures[currentUpgrade1.ordinal()].getId(), upgradeIconSize, upgradeIconSize);
                    ImGui.setCursorPos(panelPos1.x, panelPos1.y);
                    ImGui.popFont();
                    ImGui.pushFont(gm.monkeyFontTiny);

                    String[] currentNameWords = currentName1.split(" ");
                    String top = "";
                    for(int i = 0; i < currentNameWords.length - 1; i++) {
                        top = top.concat(currentNameWords[i] + " ");
                    }
                    top = top.trim();
                    ImVec2 topSize = ImGui.calcTextSize(top);
                    ImGui.setCursorPos(panelPos1.x + upgradeSize/2 - topSize.x/2, old.y);
                    textOutline(top, Color.white, Color.black);
                    String bottom = currentNameWords[currentNameWords.length - 1];
                    ImVec2 bottomSize = ImGui.calcTextSize(bottom);
                    ImGui.setCursorPos(panelPos1.x + upgradeSize/2 - bottomSize.x/2, old.y + gm.window.getHeight()/54);
                    textOutline(bottom, Color.white, Color.black);

                    ImGui.popFont();
                    ImGui.pushFont(gm.monkeyFontSmall);
                    ImVec2 ownerDim = ImGui.calcTextSize("OWNED");
                    ImGui.setCursorPos(panelPos1.x + upgradeSize/2 - ownerDim.x/2, panelPos1.y + upgradeSize);
                    textOutline("OWNED", Color.GREEN, Color.black);
                    ImGui.popFont();
                    ImGui.pushFont(gm.monkeyFontTiny);
                } else {
                    ImGui.popFont();
                    ImGui.pushFont(gm.monkeyFontTiny);
                    String not = "Not";
                    String upgraded = "Upgraded";
                    ImVec2 notDim = ImGui.calcTextSize(not);
                    ImVec2 upgradedDim = ImGui.calcTextSize(upgraded);
                    ImGui.setCursorPos(panelPos1.x + upgradeSize/2 - notDim.x/2, old.y + upgradeSize/2 - gm.window.getHeight()/48);
                    textOutline(not, Color.decode("#c1995f"), Color.decode("#7a5c3b"));
                    ImGui.setCursorPos(panelPos1.x + upgradeSize/2 - upgradedDim.x/2, old.y + upgradeSize/2);
                    textOutline(upgraded, Color.decode("#c1995f"), Color.decode("#7a5c3b"));
                }

                ImGui.setCursorPos(old.x + (upgradeSize * 0.25f) + (upgradeSize/2 - upgradeIconSize/2), old.y + (upgradeSize/2 - upgradeIconSize/2));

                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1, 1, 1, 0f);
                if(ImGui.imageButton(Assets.upgradeTextures[ordinal1].getId(), upgradeIconSize, upgradeIconSize)) {
                    if(nextUpgrade1 != null && affordable) {
                        game.getPlayer().removeMoney(selected.getPath1().nextUpgrade.cost);
                        selected.upgradePath(1);
                        Game.audioSources.get("upgrade").play();
                    }
                }
                ImGui.popStyleColor(3);

                ImGui.popFont();
                ImGui.pushFont(gm.monkeyFontTiny);
                String upgradeName = name1;

                String[] upgradeNameWords = upgradeName.split(" ");
                String top = "";
                for(int i = 0; i < upgradeNameWords.length - 1; i++) {
                    top = top.concat(upgradeNameWords[i] + " ");
                }
                top = top.trim();
                ImVec2 topSize = ImGui.calcTextSize(top);
                ImGui.setCursorPos(old.x + upgradeSize/2 - topSize.x/2 + (upgradeSize * 0.25f), old.y);
                textOutline(top, Color.white, Color.black);
                String bottom = upgradeNameWords[upgradeNameWords.length - 1];
                ImVec2 bottomSize = ImGui.calcTextSize(bottom);
                ImGui.setCursorPos(old.x + upgradeSize/2 - bottomSize.x/2 + (upgradeSize * 0.25f), old.y + gm.window.getHeight()/54);
                textOutline(bottom, Color.white, Color.black);

                if(costValue1 == Integer.MAX_VALUE) {
                    String max = "Max";
                    String upgrade = "Upgrades";
                    ImVec2 maxDim = ImGui.calcTextSize(max);
                    ImVec2 upgradeDim = ImGui.calcTextSize(upgrade);
                    ImGui.setCursorPos(old.x + upgradeSize/2 - maxDim.x/2 + (upgradeSize * 0.2f), old.y + upgradeSize/2 - gm.window.getHeight()/48);
                    textOutline(max, Color.white, Color.black);
                    ImGui.setCursorPos(old.x + upgradeSize/2 - upgradeDim.x/2 + (upgradeSize * 0.2f), old.y + upgradeSize/2);
                    textOutline(upgrade, Color.white, Color.black);
                }
                ImGui.popFont();
                ImGui.pushFont(gm.monkeyFontSmall);

                String cost = costValue1 == Integer.MAX_VALUE ? "" : "$" + String.valueOf(costValue1);
                ImVec2 costDim = ImGui.calcTextSize(cost);
                ImGui.setCursorPos(old.x + upgradeSize/2 - costDim.x/2 + (upgradeSize * 0.25f), old.y + upgradeSize);
                textOutline(cost, affordable ? Color.white : Color.red, Color.black);

                ImGui.setCursorPosY(ImGui.getCursorPosY() + gm.window.getHeight()/64);

                Upgrade nextUpgrade2 = selected.getPath2().nextUpgrade;
                int costValue2 = Integer.MAX_VALUE;
                int ordinal2 = 4;
                String name2 = "";
                if(nextUpgrade2 != null) {
                    costValue2 = nextUpgrade2.cost;
                    ordinal2 = nextUpgrade2.ordinal();
                    name2 = nextUpgrade2.name;
                }
                boolean affordable2 = costValue2 <= game.getPlayer().getMoney();
                ImGui.popFont();
                ImGui.pushFont(gm.monkeyFontTiny);
                ImGui.setCursorPosX(ImGui.getCursorPosX() + panelWidth/2 - (upgradeSize * 1.25f));
                ImVec2 panelPos2 = ImGui.getCursorPos();
                ImGui.image(upgradePanel.getId(), upgradeSize * 1.25f, upgradeSize);
                ImGui.sameLine();
                ImGui.setCursorPosX(ImGui.getCursorPosX() - 15);
                old = ImGui.getCursorPos();
                ImGui.image(affordable2 ? upgrade.getId() : upgradeDisabled.getId(), upgradeSize * 1.25f, upgradeSize);

                Upgrade currentUpgrade2 = selected.getPath2();
                String currentName2 = currentUpgrade2.name;

                if(!currentName2.equals("")) {
                    ImGui.setCursorPos(panelPos2.x + (upgradeSize/2 - upgradeIconSize/2), panelPos2.y + (upgradeSize/2 - upgradeIconSize/2));
                    ImGui.image(Assets.upgradeTextures[currentUpgrade2.ordinal()].getId(), upgradeIconSize, upgradeIconSize);
                    ImGui.setCursorPos(panelPos2.x, panelPos2.y);
                    ImGui.popFont();
                    ImGui.pushFont(gm.monkeyFontTiny);

                    String[] currentName3Words = currentName2.split(" ");
                    String top3 = "";
                    for(int i = 0; i < currentName3Words.length - 1; i++) {
                        top3 = top3.concat(currentName3Words[i] + " ");
                    }
                    top3 = top3.trim();
                    ImVec2 top3Size = ImGui.calcTextSize(top3);
                    ImGui.setCursorPos(panelPos2.x + upgradeSize/2 - top3Size.x/2, old.y);
                    textOutline(top3, Color.white, Color.black);
                    String bottom3 = currentName3Words[currentName3Words.length - 1];
                    ImVec2 bottom3Size = ImGui.calcTextSize(bottom3);
                    ImGui.setCursorPos(panelPos2.x + upgradeSize/2 - bottom3Size.x/2, old.y + gm.window.getHeight()/54);
                    textOutline(bottom3, Color.white, Color.black);

                    ImGui.popFont();
                    ImGui.pushFont(gm.monkeyFontSmall);
                    ImVec2 ownerDim = ImGui.calcTextSize("OWNED");
                    ImGui.setCursorPos(panelPos2.x + upgradeSize/2 - ownerDim.x/2, panelPos2.y + upgradeSize);
                    textOutline("OWNED", Color.green, Color.black);
                    ImGui.popFont();
                    ImGui.pushFont(gm.monkeyFontTiny);
                } else {
                    String not = "Not";
                    String upgraded = "Upgraded";
                    ImVec2 notDim = ImGui.calcTextSize(not);
                    ImVec2 upgradedDim = ImGui.calcTextSize(upgraded);
                    ImGui.setCursorPos(panelPos2.x + upgradeSize/2 - notDim.x/2, old.y + upgradeSize/2 - gm.window.getHeight()/48);
                    textOutline(not, Color.decode("#c1995f"), Color.decode("#7a5c3b"));
                    ImGui.setCursorPos(panelPos2.x + upgradeSize/2 - upgradedDim.x/2, old.y + upgradeSize/2);
                    textOutline(upgraded, Color.decode("#c1995f"), Color.decode("#7a5c3b"));
                }

                ImGui.setCursorPos(old.x + (upgradeSize * 0.25f) + (upgradeSize/2 - upgradeIconSize/2), old.y + (upgradeSize/2 - upgradeIconSize/2));

                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 1, 1, 1, 0f);
                if(ImGui.imageButton(Assets.upgradeTextures[ordinal2].getId(), upgradeIconSize, upgradeIconSize)) {
                    if(nextUpgrade2 != null && affordable2) {
                        game.getPlayer().removeMoney(selected.getPath2().nextUpgrade.cost);
                        selected.upgradePath(2);
                        Game.audioSources.get("upgrade").play();
                    }
                }
                ImGui.popStyleColor(3);

                String upgradeName2 = name2;
                String[] upgradeName2Words = upgradeName2.split(" ");
                String top2 = "";
                for(int i = 0; i < upgradeName2Words.length - 1; i++) {
                    top2 = top2.concat(upgradeName2Words[i] + " ");
                }
                top2 = top2.trim();
                ImVec2 top2Size = ImGui.calcTextSize(top2);
                ImGui.setCursorPos(old.x + upgradeSize/2 - top2Size.x/2 + (upgradeSize * 0.25f), old.y);
                textOutline(top2, Color.white, Color.black);
                String bottom2 = upgradeName2Words[upgradeName2Words.length - 1];
                ImVec2 bottom2Size = ImGui.calcTextSize(bottom2);
                ImGui.setCursorPos(old.x + upgradeSize/2 - bottom2Size.x/2 + (upgradeSize * 0.25f), old.y + gm.window.getHeight()/54);
                textOutline(bottom2, Color.white, Color.black);


                if(costValue2 == Integer.MAX_VALUE) {
                    String max = "Max";
                    String upgrade = "Upgrades";
                    ImVec2 maxDim = ImGui.calcTextSize(max);
                    ImVec2 upgradeDim = ImGui.calcTextSize(upgrade);
                    ImGui.setCursorPos(old.x + upgradeSize/2 - maxDim.x/2 + (upgradeSize * 0.2f), old.y + upgradeSize/2 - gm.window.getHeight()/48);
                    textOutline(max, Color.white, Color.black);
                    ImGui.setCursorPos(old.x + upgradeSize/2 - upgradeDim.x/2 + (upgradeSize * 0.2f), old.y + upgradeSize/2);
                    textOutline(upgrade, Color.white, Color.black);
                }

                ImGui.popFont();
                ImGui.pushFont(gm.monkeyFontSmall);
                String cost2 = costValue2 == Integer.MAX_VALUE ? "" : "$" + String.valueOf(costValue2);
                ImVec2 costDim2 = ImGui.calcTextSize(cost2);
                ImGui.setCursorPos(old.x + upgradeSize/2 - costDim2.x/2 + (upgradeSize * 0.25f), old.y + upgradeSize);
                textOutline(cost2, affordable2 ? Color.white : Color.red, Color.black);

                float sellSize = upgradeSize * 2.25f;
                ImGui.setCursorPos(old.x - sellSize/2, old.y + panelHeight/7);
                ImVec2 oldSellPos = ImGui.getCursorPos();
                ImGui.image(panel.getId(), sellSize, upgradeSize/2);
                ImGui.setCursorPos(oldSellPos.x + sellSize/32, oldSellPos.y + upgradeSize/2 - upgradeSize/4 - upgradeSize/3/2);
                ImGui.image(coin.getId(), upgradeSize/3, upgradeSize/3);
                ImGui.setCursorPos(oldSellPos.x + sellSize/16 + upgradeSize/3, oldSellPos.y + upgradeSize/2 - upgradeSize/3);
                float towerValue = selected.getValue() * 0.7f;
                textOutline("$" + String.valueOf((int)towerValue), Color.white, Color.BLACK);
                ImGui.setCursorPos(oldSellPos.x + sellSize/4 + upgradeSize/1.25f, oldSellPos.y);
                ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
                if(ImGui.imageButton(sell.getId(), sellSize * 0.5f, sellSize/4)) {
                    game.getPlayer().addMoney((int)towerValue);
                    Game.audioSources.get("sell").play();
                    game.entities.remove(selected.getName());
                    game.getTowers().remove(selected);
                    game.setCurrentTower(null);
                    game.getRange().setEnabled(false);
                }
                ImGui.popStyleColor(3);
                ImGui.setCursorPos(oldSellPos.x + sellSize/4 + upgradeSize/1.25f + sellSize * 0.125f, oldSellPos.y + upgradeSize/2 - upgradeSize/3);
                textOutline("Sell", Color.white, Color.BLACK);
            }

            float imageSize = panelHeight * 0.1388f;
            ImGui.setCursorPos(panelWidth/2 - imageSize/2, panelHeight - panelHeight/5);
            ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
            if(game.isRoundRunning()) {
                if (ImGui.imageButton((Game.gameSpeedToggled) ? gameSpeedButtonActive.getId() : gameSpeedButton.getId(), imageSize, imageSize, 0, 0, 1, 1, 0, 0, 0, 0, 0)) {
                    Game.gameSpeedToggled = !Game.gameSpeedToggled;
                    game.setGameSpeed((Game.gameSpeedToggled) ? 3f : 1f);
                }
            } else {
                if (ImGui.imageButton(play.getId(), imageSize, imageSize, 0, 0, 1, 1, 0, 0, 0, 0, 0)) {
                    game.setRoundShouldRun(true);;
                    game.setRoundRunning(true);
                }
            }

            if(game.getMonkeyPlacingId() > 0) {
                ImGui.setCursorPos(panelWidth/2 - imageSize/2, panelHeight/2);
                if (ImGui.imageButton(cancel.getId(), imageSize, imageSize, 0, 0, 1, 1, 0, 0, 0, 0, 0)) {
                    game.setMonkeyPlacingId(0);
                    game.getRange().setEnabled(false);
                }
            }
            
            ImGui.popStyleColor(3);
        }
        ImGui.popStyleVar(3);
        
        ImGui.popFont();
        ImGui.end();
    }
}
