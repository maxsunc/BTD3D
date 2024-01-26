package me.ChristopherW.process;

import java.io.File;
import java.util.Scanner;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.jme3.bullet.PhysicsSpace;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import me.ChristopherW.core.Camera;
import me.ChristopherW.core.ILogic;
import me.ChristopherW.core.MouseInput;
import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.RenderManager;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.custom.Assets;
import me.ChristopherW.core.custom.Bloon;
import me.ChristopherW.core.custom.BloonType;
import me.ChristopherW.core.custom.Bomb;
import me.ChristopherW.core.custom.Player;
import me.ChristopherW.core.custom.Projectile;
import me.ChristopherW.core.custom.Spawner;
import me.ChristopherW.core.custom.Tower;
import me.ChristopherW.core.custom.TowerType;
import me.ChristopherW.core.custom.Animations.AnimatedEntity;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.custom.Towers.BombTower;
import me.ChristopherW.core.custom.Towers.DartMonkey;
import me.ChristopherW.core.custom.Towers.ITower;
import me.ChristopherW.core.custom.Towers.SniperMonkey;
import me.ChristopherW.core.custom.Towers.SuperMonkey;
import me.ChristopherW.core.custom.UI.GUIManager;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.sound.SoundListener;
import me.ChristopherW.core.sound.SoundManager;
import me.ChristopherW.core.sound.SoundSource;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.core.utils.Transformation;
import me.ChristopherW.core.utils.Utils;

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
    public SoundSource music;
    private Vector3f mouseWorldPos = new Vector3f(0, 0, 0);

    /*
     * INSTANCE VARIABLES
     */
    private ArrayList<Bloon> bloons = new ArrayList<Bloon>();
    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    private ArrayList<Tower> towers = new ArrayList<Tower>();
    private Vector3f[] bloonNodes;
    private String[] previewTowerKeys = {"preview_monkey", "preview_sniper_monkey", "preview_bomb_tower", "preview_super_monkey"};
    private ArrayList<Spawner> spawners = new ArrayList<Spawner>();
    private float sphereRadius = 1.2f;
    private Tower currentTowerInspecting;
    private float gameSpeed = 1.0f;
    private Player player;
    private int bloonCounter = 0;    
    private int placingMonkeyId = 0;
    private boolean autoStart = false;
    private boolean isInvalid = false;
    private Spawner currentSpawnerTimer;
    private Scanner roundScanner;
    private boolean roundIsRunning;
    private boolean roundShouldRun = true;
    private Entity range;
    private int roundNumber = 1;
    private boolean fastForwardToggled = false;
    private float defaultCameraOrbitRadius = 20f;
    private Random random = new Random();
    private Entity cameraPos = new Entity(null, new Vector3f(), new Vector3f(), new Vector3f());
    private Vector2d dMouse = new Vector2d();
    private int spawnNewBloonOnNextTick = -1;
    private boolean secondBloonRow = false;
    private float cameraRotationX = 45;
    private float cameraRotationY = 45;
    private float cameraZoom = 2;

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
        Assets.loadSounds(audioSources, soundManager);
    }


    @Override
    public void init() throws Exception {

        renderer.init();
        Assets.init();

        player = new Player();

        // initialize entities map
        entities = new HashMap<>();

        // init static objects
        Model mapModel = loader.loadModel("assets/models/map.dae");
        Entity map = new Entity("map", mapModel, 
            new Vector3f(), 
            new Vector3f(), 
            new Vector3f(1f,1f,1f)
        );
        entities.put("map", map);


        // load can map.txt node positions
        Scanner scanner = new Scanner(new File("assets/map.txt"));
        bloonNodes = new Vector3f[scanner.nextInt()];
        // clear the enter key
        scanner.nextLine();
        for(int i = 0; i < bloonNodes.length; i++){
            String[] splitLine = scanner.nextLine().split(" ");
            bloonNodes[i] = new Vector3f(Float.parseFloat(splitLine[0]), 1, -Float.parseFloat(splitLine[1]));
        }
        // scans the round file
        roundScanner = new Scanner(new File("assets/rounds.txt"));


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

        AnimatedEntity super_monkey = new AnimatedEntity(RiggedModel.copy(Assets.monkeyModels[3]), 
            new Vector3f(), new Vector3f(), new Vector3f(0.1f));
            super_monkey.setAnimationId(1);
            super_monkey.setEnabled(false);
        entities.put("preview_super_monkey", super_monkey);


        AnimatedEntity bomb_tower = new AnimatedEntity(RiggedModel.copy(Assets.monkeyModels[2]), 
            new Vector3f(), new Vector3f(), new Vector3f(0.1f));
        bomb_tower.setAnimationId(1);
        bomb_tower.setEnabled(false);
        entities.put("preview_bomb_tower", bomb_tower);

        AnimatedEntity monkey = new AnimatedEntity(RiggedModel.copy(Assets.monkeyModels[0]), 
            new Vector3f(), new Vector3f(), new Vector3f(0.1f));
        monkey.setEnabled(false);
        monkey.setAnimationId(2);
        entities.put("preview_monkey", monkey);
        AnimatedEntity sniper_monkey = new AnimatedEntity(RiggedModel.copy(Assets.monkeyModels[1]), 
            new Vector3f(), new Vector3f(), new Vector3f(0.1f));
        sniper_monkey.setEnabled(false);
        sniper_monkey.setAnimationId(2);
        entities.put("preview_sniper_monkey", sniper_monkey);
        
        Model circle = loader.loadModel("assets/models/circle.fbx");
        range = new Entity("range", circle,
            new Vector3f(0, 0.1f, 0),
            new Vector3f(0, 0, 0),
            new Vector3f(1, 1, 1)
        );
        range.getModel().setAllColorFilter(new Vector4f(0.2f, 0.2f, 0.2f, 0.5f));
        range.getModel().setAllColorBlending(1f);
        range.setEnabled(false);

        music = playRandom(new String[]{"upbeat1", "upbeat2", "upbeat3", "musicBMC", "sailsAgain", "jazzHD"});

        if(GlobalVariables.USE_CUSTOM_CURSOR)
            GLFW.glfwSetCursor(window.getWindow(), window.getInput().getCursor());
    }

    public void mouseDown(long window, int button, int action, int mods, MouseInput input) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if(action == GLFW.GLFW_PRESS) {
                dMouse = new Vector2d(input.getCurrentPos());
            } else if (action == GLFW.GLFW_RELEASE) {
                if(placingMonkeyId == 0){
                    if(towers.size() < 1){
                        return;
                    }

                    if(player.getLives() < 1)
                        return;

                    if(dMouse.distance(input.getCurrentPos()) > 2)
                        return;

                    if(input.getCurrentPos().x > (this.window.getWidth() - this.window.getHeight() * 0.3f) && input.getCurrentPos().y > this.window.getHeight()*0.4)
                        return;

                    // check for towers to inspect
                    currentTowerInspecting = null;
                    range.setEnabled(false);
                    float shortestDistance = Float.MAX_VALUE;
                    Tower closestTower = towers.get(0);
                    for(int i = 0; i < towers.size(); i++){
                        towers.get(i).getModel().setAllColorBlending(0f);
                        float distance = mouseWorldPos.distance(towers.get(i).getPosition());
                        if(shortestDistance > distance){
                            closestTower = towers.get(i);
                            shortestDistance = distance;
                        }
                    }
                    if(closestTower.getPosition().distance(mouseWorldPos) < 2f) {
                        currentTowerInspecting = closestTower;
                        closestTower.getModel().setAllColorBlending(0.5f);
                        range.setEnabled(true);
                        range.setPosition(closestTower.getPosition().x, 0.05f, closestTower.getPosition().z);
                    }
                }
                else if(isInvalid){
                    return;
                }
                else if(dMouse.distance(input.getCurrentPos()) < 2f) {
                    if(input.getCurrentPos().x > (this.window.getWidth() - this.window.getHeight() * 0.3f))
                        return;

                    Tower monkey;
                    TowerType type = TowerType.values()[placingMonkeyId - 1];

                    switch (type) {
                        case BOMB_TOWER:
                            monkey = new BombTower("monkey" + Utils.generateHash(8), RiggedModel.copy(Assets.monkeyModels[placingMonkeyId - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                        case DART_MONKEY:
                            monkey = new DartMonkey("monkey" + Utils.generateHash(8), RiggedModel.copy(Assets.monkeyModels[placingMonkeyId - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                        case SNIPER_MONKEY:
                            monkey = new SniperMonkey("monkey" + Utils.generateHash(8), RiggedModel.copy(Assets.monkeyModels[placingMonkeyId - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                        case SUPER_MONKEY:
                            monkey = new SuperMonkey("monkey" + Utils.generateHash(8), RiggedModel.copy(Assets.monkeyModels[placingMonkeyId - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                        default:
                            monkey = new Tower("monkey" + Utils.generateHash(8), RiggedModel.copy(Assets.monkeyModels[placingMonkeyId - 1]), 
                                mouseWorldPos, 
                                new Vector3f(), 
                                new Vector3f(0.1f,0.1f,0.1f),
                                type
                            );
                            break;
                    }


                    towers.add(monkey);
                    monkey.setAnimationId(monkey.getSpawnAnimationId());
                    entities.put(monkey.getName(), monkey);
                    playRandom(new String[]{"tower_place_1", "tower_place_2"});
                    player.removeMoney(monkey.getValue());
                    placingMonkeyId = 0;
                }
            }
        }
    }

    public void keyDown(long window, int key, int scancode, int action, int mods) {
        if(action == GLFW.GLFW_PRESS) {
            if(key ==GLFW.GLFW_KEY_LEFT_SHIFT)
                secondBloonRow = true;
            // sets the spawnNewBloonOnNextTick to a number which refers to the type of bloon to spawn.
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
            if(key ==GLFW.GLFW_KEY_0)
                spawnNewBloonOnNextTick = 9;
            if(key == GLFW.GLFW_KEY_RIGHT){
                music.stop();
            }
            if(key ==GLFW.GLFW_KEY_ESCAPE) {
                // sets monkey mode to 0 which gets rid of selected monkeys
                placingMonkeyId = 0;
            }
            // check for key presses Q and E, checking for E uses basically the same code but for the other path 
            if(key == GLFW.GLFW_KEY_Q){
                // only attempts an upgrade if the current inspecting tower is not null
                if(currentTowerInspecting != null){
                    // check if the next upgrade we want to buy is not null
                    if(currentTowerInspecting.getPath1().nextUpgrade != null)
                    // check if you can actually buy it
                    if(currentTowerInspecting.getPath1().nextUpgrade.cost <= player.getMoney()){
                        // remove the money, upgrade the monkey and play the sound
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
                    currentTowerInspecting.upgradePath(2);
                    audioSources.get("upgrade").play();
                    }
            }
        }
        if(action == GLFW.GLFW_RELEASE) {
            if(key == GLFW.GLFW_KEY_LEFT_SHIFT)
                secondBloonRow = false;
        }
    }

    private boolean checkPlacementValidity(Entity previewMonkey) {

        if(player.getMoney() < TowerType.values()[placingMonkeyId - 1].cost)
            return false;

        if(previewMonkey.getPosition().distance(0,0,0) > 50)
            return false;


        for(Tower monkey : towers) {
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

        if(placingMonkeyId > 0) {
            currentTowerInspecting = null;
            Entity previewMonkey = entities.get(previewTowerKeys[placingMonkeyId - 1]);
            previewMonkey.setPosition(new Vector3f(mouseWorldPos));
            isInvalid = !checkPlacementValidity(previewMonkey);
            if(isInvalid) {
                previewMonkey.getModel().setAllMaterials(Assets.previewRed);
            } else {
                previewMonkey.getModel().setAllMaterials(Assets.previewWhite);
            }
        }

        if(input.isLeftButtonPress() && player.getLives() > 0) {
            if(GUIManager.currentScreen == "MainMenu")
                return;
            cameraRotationX += input.getDisplVec().y * GlobalVariables.MOUSE_SENSITIVITY_X;
            cameraRotationY += input.getDisplVec().x * GlobalVariables.MOUSE_SENSITIVITY_X;
            cameraRotationY = Utils.clamp(cameraRotationY, 10, 90);
        }
        cameraZoom = Utils.clamp(cameraZoom, 0.75f, 1.75f);

        float moveSpeed = 10;
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
        if(window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            if(bloons.size() < 1){
                roundIsRunning = true;
                roundShouldRun = true;
            }
        }
        
        Vector3f normalized = panVec.normalize().mul((float) (moveSpeed * deltaTime));
        
        if(player.getLives() > 0)
            cameraPos.translate(normalized.isFinite() ? panVec : new Vector3f());
    }

    public void onScroll(double dy) {
        if(player.getLives() > 0)
            cameraZoom -= dy/4;
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        
        if(spawnNewBloonOnNextTick >= 0) {
            // spawns the type of bloon based on spawnNewBloonOnNextTick. adds extra 10 if shift key is pressed (secondBloonRow) 
            BloonType type = BloonType.values()[Math.min(spawnNewBloonOnNextTick + (secondBloonRow ? 10 : 0), BloonType.values().length - 1)];
            // create new bloon based on the type selected
            Bloon b = new Bloon("bloon", type,
                (type == BloonType.MOAB) ? Model.copy(Assets.moabModel) : Model.copy(Assets.bloonModel), 
                new Vector3f(bloonNodes[0]), 
                new Vector3f(),
                new Vector3f(type.size)
            );
            // add the bloon to the arrayList of bloons and increment bloonCounter
            bloons.add(b);
            bloonCounter++;
            // add it to the hashmap of entities so it can be renderered and exist.
            entities.put("bloon" + bloonCounter, b);
            b.setName("bloon" + bloonCounter);
            // reset spawnNewBloon on next tick so it wont be spawned again
            spawnNewBloonOnNextTick = -1;
        }
        // if we are inspecting a tower make the dark circle that reveals the tower's range
        if(currentTowerInspecting != null) {
            if(currentTowerInspecting instanceof SniperMonkey)
            // sniper monkey range is everything but we set it to 1 to show that.
                range.setScale(1);
            else
                range.setScale(currentTowerInspecting.getRange());
        }

        float radius = defaultCameraOrbitRadius * cameraZoom;

        int minPan = -20, maxPan = 20;
        cameraPos.setPosition(Utils.clamp(cameraPos.getPosition().x,minPan, maxPan),0,Utils.clamp(cameraPos.getPosition().z, minPan, maxPan));

        // orbit the camera around the active ball
        Vector3f orbitVec = new Vector3f();
        orbitVec.x = (float) (Math.abs(radius * Math.cos(Math.toRadians(cameraRotationY))) * Math.cos(Math.toRadians(cameraRotationX))) + cameraPos.getPosition().x;
        orbitVec.y = (float) (radius * Math.sin(Math.toRadians(cameraRotationY))) + 1;
        orbitVec.z = (float) (Math.abs(radius * Math.cos(Math.toRadians(cameraRotationY))) * Math.sin(Math.toRadians(cameraRotationX))) + cameraPos.getPosition().z;

        camera.setPosition(orbitVec);
        camera.setRotation(cameraRotationY, cameraRotationX-90, camera.getRotation().z);
        cameraPos.setRotation(0, -camera.getRotation().y,0);  

        // contains all of the bloons targetted by monkeys 
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
                // animate the tower
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

                // save the position of the next node the bloon should get to
                Vector3f nodePos = bloonNodes[bloon.getNodeIndex()];
                // compare the positions check if the bloon met the nodePosition (at least distance of 0.25f)
                if(bloon.getPosition().distance(nodePos) <= 0.25f){
                    // look at the next node
                    bloon.incremenNodeIndex();
                    // set the new nodePosition to follow
                    nodePos = bloonNodes[bloon.getNodeIndex()];
                }
                // bloon.isEnabled is only false if the bloon makes it to the end
                if(bloon.isEnabled() == false) {
                    // remove the bloon based on the name from entities and bloon array
                    entities.remove(bloon.getName());
                    bloons.remove(bloon);
                    // remove the amount of lives based on how many red bloons the bloon is worth
                    player.removeLives(bloon.getType().RBE);

                    // check if we're dead
                    if(player.getLives() <= 0) {
                        audioSources.get("defeat").play();
                        gameSpeed = 0;
                    }
                    // we know the bloon made it to the end so there is no need to do any more with it
                    continue;
                }
                // finally make the bloon look at the node Position (on the y rotation only)
                Vector3f difference = new Vector3f();
                    nodePos.sub(bloon.getPosition(), difference);
                    difference.normalize();
                    // sets the direction of motion to go
                    bloon.setCurrentHeading(difference);
                    bloon.lookAtY(nodePos);
                    // translate it forward
                bloon.translate(new Vector3f(bloon.getCurrentHeading()).mul(gameSpeed * 2 * bloon.getSpeed() * interval));
            }

            if(entity instanceof Tower) {
                Tower monkey = (Tower) entity;
                //System.out.printf("%.2f/%.2f\n", monkey.getTick(), monkey.getRate());
                if(monkey.getTick() >= monkey.getRate()/gameSpeed) {
                    ((ITower)monkey).shoot(bloons, projectiles, targeted);
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
                        } else if(dart.getPosition().distance(bloon.getPosition()) < 1.25f) {

                            if(bloon.getType() == BloonType.LEAD) {
                                if(dart instanceof Bomb) {
                                    int result = bloon.damage(dart.getDamage());
                                    if(result >= 0) {
                                        playRandom(new String[]{"pop_1", "pop_2", "pop_3", "pop_4"});
                                        
                                        player.addMoney(1);
                                        if(result > 0) {
                                            entities.remove(bloon.getName());
                                            bloons.remove(bloon);
                                            bloon.setPopped(true);
                                        }
                                    }
                                } else {
                                    playRandom(new String[]{"metal_hit_1", "metal_hit_2", "metal_hit_3", "metal_hit_4"});
                                }
                            } else {
                                int result = bloon.damage(dart.getDamage());
                                if(result >= 0) {
                                    playRandom(new String[]{"pop_1", "pop_2", "pop_3", "pop_4"});
                                    
                                    player.addMoney(1);
                                    if(result > 0) {
                                        entities.remove(bloon.getName());
                                        bloons.remove(bloon);
                                        bloon.setPopped(true);
                                    }
                                }
                            }
                            
                            
                            dart.setEnabled(false);
                            projectiles.remove(dart);
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
                    projectiles.remove(dart);
                    entities.remove(dart.getName());
                    continue;
                }
                dart.translate(new Vector3f(dart.getDestinationDirection()).mul(dart.getSpeed() * interval));
            }
        }

        bloons.addAll(targeted);
        targeted.clear();
        // run rounds
        // if there is no spawner or time left for next Spawner is done
        if(currentSpawnerTimer == null || currentSpawnerTimer.canSpawnNext(interval * gameSpeed)){
            // go the next line (spawner or new round)
            if (roundScanner.hasNext() && roundIsRunning && roundShouldRun) {
                String line = roundScanner.nextLine();
                // 2nd char and 3rd char are constructors for either client or waitClient
                String[] elements = line.split(" ");
                // determine indicator
                switch (elements[0]) {
                  case "R":
                    // end round
                    roundShouldRun = false;
                    break;
                  case "C":
                    int bloonQuantity = Integer.parseInt(elements[2]);
                    float spawnTime = Integer.parseInt(elements[3]);
                    float timeToNextSpawn = elements.length > 4 ? Integer.parseInt(elements[4]) : Integer.parseInt(elements[3]);
                    Spawner spawner = new Spawner(determineBloonType(elements[1]), bloonQuantity, spawnTime, timeToNextSpawn);
                    spawners.add(spawner);
                    // set the 
                    currentSpawnerTimer = spawner;
                    break;
                }
              }
        }

        if (!roundShouldRun) {
            //check if all bloons are gone
            if (bloons.size() <= 0 && roundIsRunning) {
                roundIsRunning = false;
                roundShouldRun = true;
                // add round money
                player.addMoney(99 + roundNumber);
                // onlychangein the next round when all bloons are gone
                roundNumber++;

                if(autoStart) {
                    roundIsRunning = true;
                }
            }
        }

        // spawn the bloons from the spawners
        for(int i = 0; i < spawners.size(); i++){
            Spawner spawner = spawners.get(i);
            if (spawner.checkSpawn(interval  * gameSpeed)) {
                BloonType type = spawner.getType();
                Bloon bloon  = new Bloon("bloon", type,
                    (type == BloonType.MOAB) ? Model.copy(Assets.moabModel) : Model.copy(Assets.bloonModel), 
                    new Vector3f(bloonNodes[0]), 
                    new Vector3f(),
                    new Vector3f(type.size)
                );
                bloons.add(bloon);
                bloonCounter++;
                entities.put("bloon" + bloonCounter, bloon);
                bloon.setName("bloon" + bloonCounter);
            }
            if (spawner.checkDone()) {
                spawners.remove(spawner);
            }
        }
        if(!music.isPlaying()){
            music.stop();
            float gain = music.getGain();
            SoundSource soundSource = music;
            do {
                soundSource = getRandomSound(new String[]{"upbeat1", "upbeat2", "upbeat3", "musicBMC", "sailsAgain", "jazzHD"});
            }  while(soundSource.getName().equals(music.getName()));
            music = soundSource;
            music.play();
            music.setGain(gain);
        }
       
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

    BloonType determineBloonType(String givenType) {
    BloonType bloonType;
    // determines bloon type based on String
    switch (givenType) {
    case "RED":
        bloonType = BloonType.RED;
        break;
    case "BLUE":
        bloonType = BloonType.BLUE;
        break;
    case "GREEN":
        bloonType = BloonType.GREEN;
        break;
    case "YELLOW":
        bloonType = BloonType.YELLOW;
        break;
    case "PINK":
        bloonType = BloonType.PINK;
        break;
    case "BLACK":
        bloonType = BloonType.BLACK;
        break;
    case "LEAD":
        bloonType = BloonType.LEAD;
        break;
    case "ZEBRA":
        bloonType = BloonType.ZEBRA;
        break;
    case "RAINBOW":
        bloonType = BloonType.RAINBOW;
        break;
    case "CERAMIC":
        bloonType = BloonType.CERAMIC;
        break;
    case "MOAB":
        bloonType = BloonType.MOAB;
        break;
    default:
        bloonType = BloonType.RED;
        break;
    }
    return bloonType;
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

        if(placingMonkeyId > 0) {
            Entity preview = entities.get(previewTowerKeys[placingMonkeyId - 1]);
            preview.setEnabled(true);
            renderer.forceRender(preview, camera);
            preview.setEnabled(false);

            range.setEnabled(true);
            float scale = (TowerType.values()[placingMonkeyId - 1] == TowerType.SNIPER_MONKEY) ? 1.0f : TowerType.values()[placingMonkeyId - 1].range;
            range.setScale(scale);
            range.setPosition(preview.getPosition().x, 0.05f, preview.getPosition().z);
        }

        renderer.forceRender(range, camera);
 
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


    //Plays a random sound given an array of String keys and returns it.
    public SoundSource playRandom(String[] keys) {
        int randomNumber = random.nextInt(keys.length);
        audioSources.get(keys[randomNumber]).play();
        return audioSources.get(keys[randomNumber]);
    }
    // gets and returns a random sound in a hashmap using keys
    public SoundSource getRandomSound(String[] keys) {
        int randomNumber = random.nextInt(keys.length);
        return audioSources.get(keys[randomNumber]);
    }

    /*
     * GETTERS AND SETTERS
     */

    public int getRoundNumber() {
        return roundNumber;
    }

    public ArrayList<Bloon> getBloons() {
        return bloons;
    }

    public boolean isRoundRunning() {
        return roundIsRunning;
    }

    public void setRoundRunning(boolean value) {
        this.roundIsRunning = value;
    }

    public Player getPlayer() {
        return player;
    }

    public int getBloonCount() {
        return bloonCounter;
    }

    public void addBloonCount(int amount) {
        this.bloonCounter += amount;
    }

    public Vector3f[] getMapNodes() {
        return bloonNodes;
    }

    public int getMonkeyPlacingId() {
        return placingMonkeyId;
    }

    public void setMonkeyPlacingId(int value) {
        this.placingMonkeyId = value;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean value) {
        this.autoStart = value;
    }

    public Tower getCurrentTower() {
        return currentTowerInspecting;
    }

    public void setCurrentTower(Tower tower) {
        this.currentTowerInspecting = tower;
    }

    public ArrayList<Tower> getTowers() {
        return towers;
    }

    public Entity getRange() {
        return range;
    }

    public void setRoundShouldRun(boolean value) {
        this.roundShouldRun = value;
    }

    public boolean getRoundShouldRun() {
        return roundShouldRun;
    }

    public void setGameSpeed(float value) {
        this.gameSpeed = value;
    }

    public float getGameSpeed() {
        return gameSpeed;
    }

    public void toggleFastForward() {
        this.fastForwardToggled = !fastForwardToggled;
    }

    public boolean isFastForward() {
        return fastForwardToggled;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void setRoundNumber(int value) {
        this.roundNumber = value;
    }

    public void setRoundScanner(Scanner scanner) {
        this.roundScanner = scanner;
    }
}
