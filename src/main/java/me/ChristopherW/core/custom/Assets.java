package me.ChristopherW.core.custom;

import java.awt.Color;
import java.util.HashMap;

import org.joml.Vector3f;

import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.sound.SoundManager;
import me.ChristopherW.core.sound.SoundSource;

public class Assets {
    public static Texture defaultTexture;
    public static Model cube;
    public static Model sphere;
    public static Model plane;
    public static Material previewRed;
    public static Material previewWhite;
    public static RiggedModel[] monkeyModels = new RiggedModel[8];
    public static Model bloonModel;
    public static Model bombModel;
    public static Model dartModel;
    public static Model moabModel;
    public static Texture RED;
    public static Texture BLUE;
    public static Texture GREEN;
    public static Texture YELLOW;
    public static Texture PINK;
    public static Texture BLACK;
    public static Texture WHITE;
    public static Texture LEAD;
    public static Texture ZEBRA;
    public static Texture RAINBOW;
    public static Texture CERAMIC;
    public static Texture MOAB;
    public static Texture MOAB_1;
    public static Texture MOAB_2;
    public static Texture MOAB_3;
    public static Texture MOAB_4;
    public static Texture[] upgradeTextures;

    public static void init() throws Exception {
        ObjectLoader loader = new ObjectLoader();

        // load all the textures
        defaultTexture = new Texture(loader.loadTexture("assets/textures/DefaultTexture.png"));
        RED = loader.createTextureColor(Color.decode("#ed1f1f"));
        BLUE = loader.createTextureColor(Color.decode("#2F9CE4"));
        GREEN = loader.createTextureColor(Color.decode("#70A204"));
        YELLOW = loader.createTextureColor(Color.decode("#FFD514"));
        PINK = loader.createTextureColor(Color.PINK);
        BLACK = loader.createTextureColor(Color.decode("#262626"));
        WHITE = loader.createTextureColor(Color.decode("#E3E3E3"));
        LEAD = loader.createTexture("assets/textures/materials/Lead.png");
        ZEBRA = loader.createTexture("assets/textures/materials/Zebra.png");
        RAINBOW = loader.createTexture("assets/textures/materials/Rainbow.png");
        CERAMIC = loader.createTexture("assets/textures/materials/Ceramic.png");
        MOAB = loader.createTexture("assets/textures/materials/MoabStandardDiffuse.png");
        MOAB_1 = loader.createTexture("assets/textures/materials/MoabDamage1Diffuse.png");
        MOAB_2 = loader.createTexture("assets/textures/materials/MoabDamage2Diffuse.png");
        MOAB_3 = loader.createTexture("assets/textures/materials/MoabDamage3Diffuse.png");
        MOAB_4 = loader.createTexture("assets/textures/materials/MoabDamage4Diffuse.png");
        upgradeTextures = new Texture[32];
        for(int i = 0; i < Upgrade.values().length; i++) {
            upgradeTextures[i] = loader.createTexture("assets/textures/Upgrades/" + (i) + ".png");
        }

        sphere = loader.loadModel("assets/models/primatives/sphere.obj");
        cube = loader.loadModel("assets/models/primatives/cube.obj");
        plane = loader.loadModel("assets/models/primatives/plane.obj");

        bombModel = loader.loadModel("assets/models/bomb.fbx");
        dartModel = loader.loadModel("assets/models/dart.fbx");
        bloonModel = loader.loadModel("assets/models/bloon.dae");
        monkeyModels[0] = loader.loadRiggedModel("assets/models/monkey.fbx");
        monkeyModels[1] = loader.loadRiggedModel("assets/models/sniper_monkey.fbx");
        monkeyModels[2] = loader.loadRiggedModel("assets/models/bomb_tower.fbx");
        monkeyModels[3] = loader.loadRiggedModel("assets/models/super_monkey.fbx");
        monkeyModels[0].getMeshes().remove("Scene.003"); // remove random floating dart bug

        moabModel = loader.loadModel("assets/models/moab.fbx");
        previewRed = new Material(loader.createTextureColor(Color.RED));
        previewWhite = new Material(loader.createTextureColor(Color.WHITE));
    }

    public static void loadSounds(HashMap<String, SoundSource> audioSources, SoundManager soundManager) {
        try {
            // load the sound file to a buffer, then create a new audio source at the world origin with the buffer attached
            // store that sound source to a map of sounds
            // repeat this for each sound file
            SoundSource jazz = soundManager.createSound("jazz", "assets/sounds/jazz.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("jazz", jazz);
            SoundSource jazzHD = soundManager.createSound("jazzHD", "assets/sounds/MusicBTD5JazzA.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("jazzHD", jazzHD);
            SoundSource upbeat1 = soundManager.createSound("upbeat1", "assets/sounds/MusicUpbeat1A.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("upbeat1", upbeat1);
            SoundSource upbeat2 = soundManager.createSound("upbeat2", "assets/sounds/MusicUpbeat2A.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("upbeat2", upbeat2);
            SoundSource upbeat3 = soundManager.createSound("upbeat3", "assets/sounds/MusicUpbeat3A.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("upbeat3", upbeat3);
            SoundSource musicBMC = soundManager.createSound("musicBMC", "assets/sounds/MusicBMCStreetParty.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("musicBMC", musicBMC);
            SoundSource sailsAgain = soundManager.createSound("sailsAgain", "assets/sounds/MusicSailsAgain.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("sailsAgain", sailsAgain);

            SoundSource tower_place_1 = soundManager.createSound("tower_place_1", "assets/sounds/PlaceTowerMonkey01.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("tower_place_1", tower_place_1);
            SoundSource tower_place_2 = soundManager.createSound("tower_place_2", "assets/sounds/PlaceTowerMonkey02.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("tower_place_2", tower_place_2);
            
            SoundSource pop1 = soundManager.createSound("pop1", "assets/sounds/Pop01.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("pop_1", pop1);
            SoundSource pop2 = soundManager.createSound("pop2", "assets/sounds/Pop02.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("pop_2", pop2);
            SoundSource pop3 = soundManager.createSound("pop3", "assets/sounds/Pop03.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("pop_3", pop3);
            SoundSource pop4 = soundManager.createSound("pop4", "assets/sounds/Pop04.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("pop_4", pop4);

            SoundSource explosion_1 = soundManager.createSound("explosion_1", "assets/sounds/Explosion01.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("explosion_1", explosion_1);
            SoundSource explosion_2 = soundManager.createSound("explosion_2", "assets/sounds/Explosion02.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("explosion_2", explosion_2);
            SoundSource explosion_3 = soundManager.createSound("explosion_3", "assets/sounds/Explosion03.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("explosion_3", explosion_3);
            SoundSource explosion_4 = soundManager.createSound("explosion_4", "assets/sounds/Explosion04.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("explosion_4", explosion_4);
            SoundSource explosion_5 = soundManager.createSound("explosion_5", "assets/sounds/Explosion05.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("explosion_5", explosion_5);

            SoundSource moab_damage_1 = soundManager.createSound("moab_damage_1", "assets/sounds/HitMoab01.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("moab_damage_1", moab_damage_1);
            SoundSource moab_damage_2 = soundManager.createSound("moab_damage_2", "assets/sounds/HitMoab02.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("moab_damage_2", moab_damage_2);
            SoundSource moab_damage_3 = soundManager.createSound("moab_damage_3", "assets/sounds/HitMoab03.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("moab_damage_3", moab_damage_3);
            SoundSource moab_damage_4 = soundManager.createSound("moab_damage_4", "assets/sounds/HitMoab04.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("moab_damage_4", moab_damage_4);


            SoundSource moab_destroyed = soundManager.createSound("moab_destroyed", "assets/sounds/MoabDestroyed03.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("moab_destroyed", moab_destroyed);

            SoundSource ceramic_destroy_1 = soundManager.createSound("ceramic_destroy_1", "assets/sounds/CeramicDestroyed01.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(ceramic_destroy_1.getName(), ceramic_destroy_1);
            SoundSource ceramic_destroy_2 = soundManager.createSound("ceramic_destroy_2", "assets/sounds/CeramicDestroyed02.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(ceramic_destroy_2.getName(), ceramic_destroy_2);
            SoundSource ceramic_destroy_3 = soundManager.createSound("ceramic_destroy_3", "assets/sounds/CeramicDestroyed04.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(ceramic_destroy_3.getName(), ceramic_destroy_3);

            SoundSource ceramic_hit_1 = soundManager.createSound("ceramic_hit_1", "assets/sounds/HitCeramic01.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(ceramic_hit_1.getName(), ceramic_hit_1);
            SoundSource ceramic_hit_2 = soundManager.createSound("ceramic_hit_2", "assets/sounds/HitCeramic02.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(ceramic_hit_2.getName(), ceramic_hit_2);
            SoundSource ceramic_hit_3 = soundManager.createSound("ceramic_hit_3", "assets/sounds/HitCeramic03.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(ceramic_hit_3.getName(), ceramic_hit_3);
            SoundSource ceramic_hit_4 = soundManager.createSound("ceramic_hit_4", "assets/sounds/HitCeramic04.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(ceramic_hit_4.getName(), ceramic_hit_4);

            SoundSource metal_hit_1 = soundManager.createSound("metal_hit_1", "assets/sounds/HitMetal01.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(metal_hit_1.getName(), metal_hit_1);
            SoundSource metal_hit_2 = soundManager.createSound("metal_hit_2", "assets/sounds/HitMetal02.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(metal_hit_2.getName(), metal_hit_2);
            SoundSource metal_hit_3 = soundManager.createSound("metal_hit_3", "assets/sounds/HitMetal03.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(metal_hit_3.getName(), metal_hit_3);
            SoundSource metal_hit_4 = soundManager.createSound("metal_hit_4", "assets/sounds/HitMetal04.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put(metal_hit_4.getName(), metal_hit_4);

            SoundSource upgrade = soundManager.createSound("upgrade", "assets/sounds/upgrade_hd.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("upgrade", upgrade);
            SoundSource sell = soundManager.createSound("sell", "assets/sounds/UIGetGold.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("sell", sell);
            SoundSource defeat = soundManager.createSound("defeat", "assets/sounds/UIDefeat.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("defeat", defeat);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
