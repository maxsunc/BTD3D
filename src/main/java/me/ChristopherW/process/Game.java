package me.ChristopherW.process;

import static org.lwjgl.assimp.Assimp.aiComponent_ANIMATIONS;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.Box2dShape;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import me.ChristopherW.core.Camera;
import me.ChristopherW.core.EngineManager;
import me.ChristopherW.core.ILogic;
import me.ChristopherW.core.IShader;
import me.ChristopherW.core.MouseInput;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.RenderManager;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.custom.Bloon;
import me.ChristopherW.core.custom.BloonType;
import me.ChristopherW.core.custom.Bomb;
import me.ChristopherW.core.custom.Projectile;
import me.ChristopherW.core.custom.Tower;
import me.ChristopherW.core.custom.TowerType;
import me.ChristopherW.core.custom.Upgrade;
import me.ChristopherW.core.custom.Player;
import me.ChristopherW.core.custom.Animations.AnimatedEntity;
import me.ChristopherW.core.custom.Animations.Bone;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.custom.Animations.RiggedMesh;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.custom.Shaders.AnimatedShader;
import me.ChristopherW.core.custom.Towers.BombTower;
import me.ChristopherW.core.custom.Towers.DartMonkey;
import me.ChristopherW.core.custom.Towers.ITower;
import me.ChristopherW.core.custom.Towers.SniperMonkey;
import me.ChristopherW.core.custom.UI.GUIManager;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.entity.primatives.Cube;
import me.ChristopherW.core.entity.primatives.Plane;
import me.ChristopherW.core.entity.primatives.Sphere;
import me.ChristopherW.core.sound.SoundListener;
import me.ChristopherW.core.sound.SoundManager;
import me.ChristopherW.core.sound.SoundSource;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.core.utils.Transformation;
import me.ChristopherW.core.utils.Utils;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Game implements ILogic {
    private final RenderManager renderer;
    public static ObjectLoader loader;
    private final WindowManager window;
    private final SoundManager soundManager;
    public static HashMap<String, SoundSource> audioSources = new HashMap<>();
    public Map<String, Entity> entities;
    
    public FloatBuffer depthBuffer;

    public static PhysicsSpace physicsSpace;
    private Camera camera;
    public static Texture defaultTexture;
    private Material previewRed;
    private Material previewWhite;
    private RiggedModel[] monkeyModels = new RiggedModel[8];
    public static Model bloonModel;
    public static Model bombModel;
    public static Model dartModel;
    private Model moabModel;

    public static Texture RED;
    public static Texture BLUE;
    public static Texture GREEN;
    public static Texture YELLOW;
    public static Texture PINK;
    public static Texture BLACK;
    public static Texture WHITE;
    public static Texture CERAMIC;
    public static Texture MOAB;
    public static Texture MOAB_1;
    public static Texture MOAB_2;
    public static Texture MOAB_3;
    public static Texture MOAB_4;

    public static Texture[] upgradeTextures;

    private Vector3f mouseWorldPos = new Vector3f(0, 0, 0);
    public ArrayList<Bloon> bloons = new ArrayList<Bloon>();
    private ArrayList<Projectile> darts = new ArrayList<Projectile>();
    private ArrayList<Tower> monkeys = new ArrayList<Tower>();
    public Vector3f[] bloonNodes;
    private String[] previewKeys = {"preview_monkey", "preview_sniper_monkey", "preview_bomb_tower"};

    // temp
    //private Entity[] nodes = new Entity[5];
    private float sphereRadius = 1.2f;
    public Tower currentTowerInspecting;
    public float gameSpeed = 1.0f;
    public Player player;
    public int bloonCounter = 0;    
    public int monkeyMode = 0;
    public boolean isInvalid = false;

    public Game() throws Exception {
        // create new instances for these things
        renderer = new RenderManager();
        window = Launcher.getWindow();
        depthBuffer = BufferUtils.createFloatBuffer(window.getWidth() * window.getHeight());
        loader = new ObjectLoader();

        // setup sound system
        soundManager = new SoundManager();
        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);

        // create new camera
        camera = new Camera();

        physicsSpace = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        physicsSpace.getSolverInfo().setSplitImpulseEnabled(true);
        physicsSpace.setGravity(new com.jme3.math.Vector3f(0, GlobalVariables.GRAVITY, 0));
        
        // set setup the sound listener to be at the world origin and load the audio sounds
        soundManager.setListener(new SoundListener(new Vector3f(0, 0, 0)));
        loadSounds();
    }

    public void playRandom(String[] keys) {
        int randomNumber = random.nextInt(keys.length);
        audioSources.get(keys[randomNumber]).play();
    }

    void loadSounds() {
        try {
            // load the sound file to a buffer, then create a new audio source at the world origin with the buffer attached
            // store that sound source to a map of sounds
            // repeat this for each sound file
            SoundSource jazz = soundManager.createSound("jazz", "assets/sounds/jazz.ogg", new Vector3f(0,0,0), true, false, 0.4f);
            audioSources.put("jazz", jazz);
            SoundSource jazzHD = soundManager.createSound("jazzHD", "assets/sounds/MusicBTD5JazzA.ogg", new Vector3f(0,0,0), true, false, 0.4f);
            audioSources.put("jazzHD", jazzHD);
            SoundSource upbeat = soundManager.createSound("upbeat", "assets/sounds/MusicUpbeat1A.ogg", new Vector3f(0,0,0), true, false, 0.4f);
            audioSources.put("upbeat", upbeat);

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

            SoundSource upgrade = soundManager.createSound("upgrade", "assets/sounds/upgrade_hd.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("upgrade", upgrade);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Entity cameraPos = new Entity(null, new Vector3f(), new Vector3f(), new Vector3f());
    @Override
    public void init() throws Exception {

        renderer.init();

        // load all the textures
        defaultTexture = new Texture(loader.loadTexture("assets/textures/DefaultTexture.png"));

        RED = loader.createTextureColor(Color.decode("#ed1f1f"));
        BLUE = loader.createTextureColor(Color.decode("#2F9CE4"));
        GREEN = loader.createTextureColor(Color.decode("#70A204"));
        YELLOW = loader.createTextureColor(Color.decode("#FFD514"));
        PINK = loader.createTextureColor(Color.PINK);
        BLACK = loader.createTextureColor(Color.decode("#262626"));
        WHITE = loader.createTextureColor(Color.decode("#E3E3E3"));
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

        player = new Player();

        // initialize entities map
        entities = new HashMap<>();

        // init static objects
        Model mapModel = loader.loadModel("assets/models/map.dae");
        Entity map = new Entity(mapModel, 
            new Vector3f(), 
            new Vector3f(), 
            new Vector3f(1f,1f,1f)
        );
        entities.put("map", map);

        bombModel = loader.loadModel("assets/models/bomb.fbx");
        dartModel = loader.loadModel("assets/models/dart.fbx");
        bloonModel = loader.loadModel("assets/models/bloon.dae");


        // load can map.txt node positions
        Scanner scanner = new Scanner(new File("assets/map.txt"));
        bloonNodes = new Vector3f[scanner.nextInt()];
        // clear the enter key
        scanner.nextLine();
        for(int i = 0; i < bloonNodes.length; i++){
            String[] splitLine = scanner.nextLine().split(" ");
            bloonNodes[i] = new Vector3f(Float.parseFloat(splitLine[0]), 1, -Float.parseFloat(splitLine[1]));
        }

        for(Bloon b : bloons) {
            bloonCounter++;
            b.setPosition(bloonNodes[0]);
            Vector3f difference = new Vector3f();
            bloonNodes[1].sub(b.getPosition(), difference);
            difference.normalize();
            b.setCurrentHeading(difference);
            entities.put("bloon" + bloonCounter, b);
            b.setName("bloon" + bloonCounter);
        }

        monkeyModels[0] = loader.loadRiggedModel("assets/models/monkey.fbx");
        monkeyModels[1] = loader.loadRiggedModel("assets/models/sniper_monkey.fbx");
        monkeyModels[2] = loader.loadRiggedModel("assets/models/bomb_tower.fbx");

        monkeyModels[0].getMeshes().remove("Scene.003");

        AnimatedEntity bomb_tower = new AnimatedEntity(RiggedModel.copy(monkeyModels[2]), 
            new Vector3f(), new Vector3f(), new Vector3f(0.1f));
        bomb_tower.setAnimationId(1);
        bomb_tower.setEnabled(false);
        entities.put("preview_bomb_tower", bomb_tower);

        AnimatedEntity monkey = new AnimatedEntity(RiggedModel.copy(monkeyModels[0]), 
            new Vector3f(), new Vector3f(), new Vector3f(0.1f));
        monkey.setEnabled(false);
        monkey.setAnimationId(2);
        entities.put("preview_monkey", monkey);
        AnimatedEntity sniper_monkey = new AnimatedEntity(RiggedModel.copy(monkeyModels[1]), 
            new Vector3f(), new Vector3f(), new Vector3f(0.1f));
        sniper_monkey.setEnabled(false);
        sniper_monkey.setAnimationId(2);
        entities.put("preview_sniper_monkey", sniper_monkey);

        moabModel = loader.loadModel("assets/models/moab.fbx");
        // Entity moab = new Entity(Model.copy(moabModel), 
        //     new Vector3f(0,5,0), new Vector3f(), new Vector3f(0.075f));
        // //moab.setAnimationId(0);
        // moab.setName("moab");

        previewRed = new Material(loader.createTextureColor(Color.RED));
        previewWhite = new Material(loader.createTextureColor(Color.WHITE));
        

        playRandom(new String[]{"upbeat", "jazzHD"});
    }

    int i = 0;
    Vector2d dMouse = new Vector2d();
    public void mouseDown(long window, int button, int action, int mods, MouseInput input) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if(action == GLFW.GLFW_PRESS) {
                dMouse = new Vector2d(input.getCurrentPos());
            } else if (action == GLFW.GLFW_RELEASE) {
                if(monkeyMode == 0){
                    if(monkeys.size() < 1){
                        return;
                    }

                    if(dMouse.distance(input.getCurrentPos()) > 2)
                        return;

                    if(input.getCurrentPos().x > (this.window.getWidth() - this.window.getHeight() * 0.3f) && input.getCurrentPos().y > this.window.getHeight()*0.4)
                        return;

                    // check for towers to inspect
                    currentTowerInspecting = null;
                    float shortestDistance = Float.MAX_VALUE;
                    Tower closestTower = monkeys.get(0);
                    for(int i = 0; i < monkeys.size(); i++){
                        monkeys.get(i).getModel().setAllColorBlending(0f);
                        float distance = mouseWorldPos.distance(monkeys.get(i).getPosition());
                        if(shortestDistance > distance){
                            closestTower = monkeys.get(i);
                            shortestDistance = distance;
                        }
                    }
                    if(closestTower.getPosition().distance(mouseWorldPos) < 2f) {
                        currentTowerInspecting = closestTower;
                        closestTower.getModel().setAllColorBlending(0.5f);
                    }
                }
                else if(isInvalid){
                    return;
                }
                else if(dMouse.distance(input.getCurrentPos()) < 2f) {
                    if(input.getCurrentPos().x > (this.window.getWidth() - this.window.getHeight() * 0.3f))
                        return;

                    Tower monkey;
                    TowerType type = TowerType.values()[monkeyMode - 1];

                    switch (type) {
                        case BOMB_TOWER:
                            monkey = new BombTower("monkey" + (monkeys.size() + 1), RiggedModel.copy(monkeyModels[monkeyMode - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                        case DART_MONKEY:
                            monkey = new DartMonkey("monkey" + (monkeys.size() + 1), RiggedModel.copy(monkeyModels[monkeyMode - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                        case SNIPER_MONKEY:
                            monkey = new SniperMonkey("monkey" + (monkeys.size() + 1), RiggedModel.copy(monkeyModels[monkeyMode - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                        default:
                            monkey = new Tower("monkey" + (monkeys.size() + 1), RiggedModel.copy(monkeyModels[monkeyMode - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                    }


                    monkeys.add(monkey);
                    monkey.setAnimationId(monkey.getSpawnAnimationId());
                    entities.put("monkey" + monkeys.size(), monkey);
                    playRandom(new String[]{"tower_place_1", "tower_place_2"});
                    player.removeMoney(monkey.getValue());
                    monkeyMode = 0;
                }
            }
        }
    }

    int spawnNewBloonOnNextTick = -1;

    public void keyDown(long window, int key, int scancode, int action, int mods) {
        if(action == GLFW.GLFW_PRESS) {
            if(key ==GLFW.GLFW_KEY_1)
                spawnNewBloonOnNextTick = 0;
            if(key ==GLFW.GLFW_KEY_2)
                spawnNewBloonOnNextTick = 1;
            if(key ==GLFW.GLFW_KEY_3)
                spawnNewBloonOnNextTick = 2;
            if(key ==GLFW.GLFW_KEY_4)
                spawnNewBloonOnNextTick = 3;
            if(key ==GLFW.GLFW_KEY_5)
                spawnNewBloonOnNextTick = 4;
            if(key ==GLFW.GLFW_KEY_6)
                spawnNewBloonOnNextTick = 5;
            if(key ==GLFW.GLFW_KEY_7)
                spawnNewBloonOnNextTick = 6;
            if(key ==GLFW.GLFW_KEY_8)
                spawnNewBloonOnNextTick = 7;
            if(key ==GLFW.GLFW_KEY_9)
                spawnNewBloonOnNextTick = 8;

            if(key ==GLFW.GLFW_KEY_ESCAPE) {
                monkeyMode = 0;
            }

            if(key == GLFW.GLFW_KEY_Q){
                if(currentTowerInspecting != null){
                    // check if it's not null
                    if(currentTowerInspecting.getPath1().nextUpgrade != null)
                    // check if you can buy it
                    if(currentTowerInspecting.getPath1().nextUpgrade.cost <= player.getMoney()){
                        player.removeMoney(currentTowerInspecting.getPath1().nextUpgrade.cost);
                        currentTowerInspecting.upgradePath(1);
                        audioSources.get("upgrade").play();
                    }
                }
            }
            if(key == GLFW.GLFW_KEY_E){
                
                if(currentTowerInspecting != null)
                if(currentTowerInspecting.getPath2().nextUpgrade != null)
                    if(currentTowerInspecting.getPath2().nextUpgrade.cost <= player.getMoney()){
                    player.removeMoney(currentTowerInspecting.getPath2().nextUpgrade.cost);
                    System.out.println("bought " + currentTowerInspecting.getPath2().nextUpgrade.name);
                    currentTowerInspecting.upgradePath(2);
                    audioSources.get("upgrade").play();
                    }
            }
        }
    }

    float rotX = 45;
    float rotY = 45;
    float zoom = 2;
    float panX = 0;
    float panZ = 0;

    private boolean checkPlacementValidity(Entity previewMonkey) {

        if(player.getMoney() < TowerType.values()[monkeyMode - 1].cost)
            return false;

        for(Tower monkey : monkeys) {
            Vector3f monkeyPos = monkey.getPosition();
            if(monkeyPos.distance(previewMonkey.getPosition().x, monkeyPos.y, previewMonkey.getPosition().z) < 0.75f) {
                return false;
            }
        }

        // also check if it's place on a path

        int nodeIndex = 0;
        float leastDistance = Float.MAX_VALUE;
        
        // get the 2 closest nodes
        for(int i = 0; i < bloonNodes.length; i++){
            if(mouseWorldPos.distance(bloonNodes[i]) < leastDistance){
                nodeIndex = i;
                leastDistance = mouseWorldPos.distance(bloonNodes[i]);
            }
        }
        Vector3f closestNode = bloonNodes[nodeIndex];
        Vector3f backNode = bloonNodes[Math.max(nodeIndex-1,0)];
        Vector3f frontNode = bloonNodes[Math.min(nodeIndex+1, bloonNodes.length - 1)];

        Vector3f averageVector1 = new Vector3f();
        closestNode.add(backNode, averageVector1);
        averageVector1.div(2);
        Vector3f averageVector2 = new Vector3f();
        closestNode.add(frontNode, averageVector2);
        averageVector2.div(2);
        
            Vector3f[] vectorsToCompare = {closestNode, backNode, frontNode, averageVector1, averageVector2};
            // check all vectors

            float smallestDist = Float.MAX_VALUE;
            for(Vector3f v : vectorsToCompare){
                v = new Vector3f(v.x, mouseWorldPos.y, v.z);
                float dist = v.distance(mouseWorldPos);
                smallestDist = Math.min(dist, smallestDist);
                if(dist < sphereRadius){
                    return false;
                }
            }

            return true;
    }

    @Override
    public void input(MouseInput input, double deltaTime, int frame) {

        if(monkeyMode > 0) {
            Entity previewMonkey = entities.get(previewKeys[monkeyMode - 1]);
            previewMonkey.setPosition(new Vector3f(mouseWorldPos));
            isInvalid = !checkPlacementValidity(previewMonkey);
            if(isInvalid) {
                previewMonkey.getModel().setAllMaterials(previewRed);
            } else {
                previewMonkey.getModel().setAllMaterials(previewWhite);
            }
        }

        if(input.isLeftButtonPress()) {
            if(GUIManager.currentScreen == "MainMenu")
                return;
            rotX += input.getDisplVec().y * GlobalVariables.MOUSE_SENSITIVITY_X;
            rotY += input.getDisplVec().x * GlobalVariables.MOUSE_SENSITIVITY_X;
            rotY = Utils.clamp(rotY, 10, 90);
        }
        zoom = Utils.clamp(zoom, 0.75f, 1.75f);

        float moveSpeed = 0.125f/2;
        Vector3f panVec = new Vector3f(0);
        if(window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            panVec.add(cameraPos.getFoward().mul(-moveSpeed));
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            panVec.add(cameraPos.getRight().mul(-moveSpeed));
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            panVec.add(cameraPos.getFoward().mul(moveSpeed));
        }
        if(window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            panVec.add(cameraPos.getRight().mul(moveSpeed));
        }
        Vector3f normalized = panVec.normalize().mul(moveSpeed * (180/EngineManager.getFps()));
        cameraPos.translate(normalized.isFinite() ? panVec : new Vector3f());
    }

    public void onScroll(double dy) {
        if(GUIManager.currentScreen != "MainMenu")
            zoom -= dy/4;
    }

    float defaultRadius = 20f;
    Random random = new Random();
    @Override
    public void update(float interval, MouseInput mouseInput) {
        if(spawnNewBloonOnNextTick >= 0) {
            BloonType type = BloonType.values()[spawnNewBloonOnNextTick];
            Bloon b = new Bloon("bloon", type,
                (type == BloonType.MOAB) ? Model.copy(moabModel) : Model.copy(bloonModel), 
                new Vector3f(bloonNodes[0]), 
                new Vector3f(),
                new Vector3f((type == BloonType.MOAB) ? 0.075f : 0.5f)
            );
            bloons.add(b);
            bloonCounter++;
            entities.put("bloon" + bloonCounter, b);
            b.setName("bloon" + bloonCounter);
            spawnNewBloonOnNextTick = -1;
        }

        float radius = defaultRadius * zoom;

        int minPan = -20, maxPan = 20;
        cameraPos.setPosition(Utils.clamp(cameraPos.getPosition().x,minPan, maxPan),0,Utils.clamp(cameraPos.getPosition().z, minPan, maxPan));

        // orbit the camera around the active ball
        Vector3f orbitVec = new Vector3f();
        orbitVec.x = (float) (Math.abs(radius * Math.cos(Math.toRadians(rotY))) * Math.cos(Math.toRadians(rotX))) + cameraPos.getPosition().x;
        orbitVec.y = (float) (radius * Math.sin(Math.toRadians(rotY))) + 1;
        orbitVec.z = (float) (Math.abs(radius * Math.cos(Math.toRadians(rotY))) * Math.sin(Math.toRadians(rotX))) + cameraPos.getPosition().z;

        camera.setPosition(orbitVec);
        camera.setRotation(rotY, rotX-90, camera.getRotation().z);
        cameraPos.setRotation(0, -camera.getRotation().y,0);  


        ArrayList<Bloon> targeted = new ArrayList<>();
        // for each entity in the world
        // sync the visual rotation and positions with the physics rotations and positions
        for(int entityId = 0; entityId < entities.values().size(); entityId++) {
            Entity entity = entities.values().toArray(new Entity[]{})[entityId];
            if(entity.getRigidBody() != null) {
                entity.setPosition(Utils.convert(entity.getRigidBody().getPhysicsLocation(null)));
                entity.setRotation(Utils.ToEulerAngles(Utils.convert(entity.getRigidBody().getPhysicsRotation(null))));
            }
            if(entity instanceof AnimatedEntity) {
                AnimatedEntity animatedEntity = (AnimatedEntity)entity;

                if(animatedEntity.getAnimationTick() >= (1f/(gameSpeed * 60f))) {
                    boolean loopComplete = animatedEntity.nextFrame();
                    animatedEntity.setAnimationTick(0);

                    if(loopComplete) {
                        if(animatedEntity instanceof Tower) {
                            Tower tower = (Tower)animatedEntity;
                            if(animatedEntity.getAnimationId() == tower.getAttackAnimationId()) {
                                tower.setAnimationId(tower.getPostAttackAnimationId());
                            }
                            int randomNumber = random.nextInt(6) + 1;
                            if(randomNumber == 6)
                                tower.setAnimationId(tower.getIdle2AnimationId());
                            else if(randomNumber == 5)
                                tower.setAnimationId(tower.getIdle1AnimationId());
                            else
                                tower.setAnimationId(tower.getIdleAnimationId());
                        }
                    }
                    
                }

                animatedEntity.tick(interval);
            }

            if(entity instanceof Bloon) {
                Bloon bloon = (Bloon) entity;

                // check if the bloon met the nodePosition
                
                Vector3f nodePos = bloonNodes[bloon.getNodeIndex()];
                // compare the positions
                if(bloon.getPosition().distance(nodePos) <= 0.25f){
                    // look at the next node
                    bloon.incremenNodeIndex();
                    nodePos = bloonNodes[bloon.getNodeIndex()];
                }
                
                if(bloon.isEnabled() == false) {
                    entities.remove(bloon.getName());
                    bloons.remove(bloon);
                    player.removeLives(bloon.getType().RBE);

                    // Stop the game
                    //if(player.getLives() <= 0)
                    //    Launcher.getEngine().stop();
                    continue;
                }
                Vector3f difference = new Vector3f();
                    nodePos.sub(bloon.getPosition(), difference);
                    difference.normalize();
                    bloon.setCurrentHeading(difference);
                    bloon.lookAtY(nodePos);
                bloon.translate(new Vector3f(bloon.getCurrentHeading()).mul(gameSpeed * 2 * bloon.getSpeed() * interval));
            }

            if(entity instanceof Tower) {
                Tower monkey = (Tower) entity;
                //System.out.printf("%.2f/%.2f\n", monkey.getTick(), monkey.getRate());
                if(monkey.getTick() >= monkey.getRate()/gameSpeed) {
                    ((ITower)monkey).shoot(bloons, darts, targeted);
                }

                monkey.addTick(interval);
            }

            if(entity instanceof Projectile) {
                Projectile dart = (Projectile) entity;
                
                for(int b = 0; b < bloons.size(); b++) {
                    Bloon bloon = bloons.get(b);
                    if(bloon != null) {
                        if(bloon.isPopped()) {
                            dart.setTarget(null);
                        } else if(dart.getPosition().distance(bloon.getPosition()) < 1f) {
                            int result = bloon.damage(dart.getDamage());
                                if(result >= 0) {
                                    playRandom(new String[]{"pop_1", "pop_2", "pop_3", "pop_4"});
                                    
                                    if(result > 0) {
                                        player.addMoney(1);
                                        entities.remove(bloon.getName());
                                        bloons.remove(bloon);
                                        bloon.setPopped(true);
                                    }
                                }
                            
                            dart.setEnabled(false);
                            darts.remove(dart);
                            entities.remove(dart.getName());

                            if(dart instanceof Bomb) {
                                playRandom(new String[]{"explosion_1", "explosion_2", "explosion_3", "explosion_4", "explosion_5"});
                                for(int bloonId = 0; bloonId < bloons.size(); bloonId++) {
                                    Bloon bl = bloons.get(bloonId);
                                    if(bl.getPosition().distance(dart.getPosition()) < 2) {
                                        int r = bl.damage(1);
                                        if(r >= 0) {
                                            playRandom(new String[]{"pop_1", "pop_2", "pop_3", "pop_4"});
                                            player.addMoney(1);
                                            
                                            if(r > 0) {
                                                entities.remove(bl.getName());
                                                bloons.remove(bl);
                                                bl.setPopped(true);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                if(!dart.isEnabled())
                    continue;

                if(dart.getPosition().distance(dart.getSource().getPosition()) > 75f) {
                    darts.remove(dart);
                    entities.remove(dart.getName());
                    continue;
                }
                dart.translate(new Vector3f(dart.getDestinationDirection()).mul(gameSpeed * dart.getSpeed() * interval));
            }
        }

        bloons.addAll(targeted);
        targeted.clear();
        
        // update the physics world
        physicsSpace.update(1/GlobalVariables.FRAMERATE, 2, false, true, false);

        // for each visible entity in the world, process its data before rendered
        for(Entity entity : entities.values()) {
            if(entity.isVisible())
                renderer.processEntity(entity);
        }

        if(GUIManager.currentScreen == "MainMenu")
            return;
    } 

    
    @Override
    public void render() throws Exception {
        // if the window was resized, update the OpenGL viewport to match
        if(window.isResize()) {
            GL11.glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        // set the clear color to the sky color
        GL11.glClearColor(GlobalVariables.BG_COLOR.x, GlobalVariables.BG_COLOR.y, GlobalVariables.BG_COLOR.z, GlobalVariables.BG_COLOR.w);
        
        // render to the OpenGL viewport from the perspective of the camera
        renderer.render(camera);

        
        try {
            double[] x = new double[1];
            double[] y = new double[1];
            GLFW.glfwGetCursorPos(window.getWindow(), x, y);

            Matrix4f projMat = window.getProjectionMatrix();
            Matrix4f viewMat = Transformation.createViewMatrix(camera);
            Matrix4f combinedMat = projMat.mul(viewMat);
            combinedMat = combinedMat.invert();

            // depth (world mouse pos, shadows)
            Vector4f vec = new Vector4f();
            vec.x = (2.0f*((float)(x[0])/(window.getWidth())))-1.0f;
            vec.y = 1.0f-(2.0f*((float)(y[0])/(window.getHeight())));

            
            int framebufferStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (framebufferStatus != GL30.GL_FRAMEBUFFER_COMPLETE) {
                System.err.println("Framebuffer is not complete: " + framebufferStatus);
            }
            int xPos = (int)x[0];
            int yPos = window.getHeight() - (int)y[0];
            GL11.glReadPixels(xPos, yPos, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, depthBuffer);
            float depth = Float.MAX_VALUE;
            try {
                depth = depthBuffer.get();
            } catch (Exception e) {
                // TODO: handle exception
            }
        
            vec.z = 2.0f * depth - 1.0f;
            vec.w = 1.0f;
            
            Vector4f pos = vec.mul(combinedMat);
            pos.w = 1.0f/pos.w;

            pos.x *= pos.w;
            pos.y *= pos.w;
            pos.z *= pos.w;

            mouseWorldPos = new Vector3f(pos.x, pos.y, pos.z);
        } catch(OutOfMemoryError e) {
            System.out.println("Error. Out of Memory. Skipping depth check");
        }

        if(monkeyMode > 0)
            renderer.forceRender(entities.get(previewKeys[monkeyMode - 1]), camera);
 
        // update the render of the ImGui frame
        window.imGuiGlfw.newFrame();
        ImGui.newFrame();
        window.guiManager.render();
        ImGui.render();
        window.imGuiGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
        GLFW.glfwPollEvents();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
