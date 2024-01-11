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
import me.ChristopherW.core.custom.Dart;
import me.ChristopherW.core.custom.Monkey;
import me.ChristopherW.core.custom.Animations.AnimatedEntity;
import me.ChristopherW.core.custom.Animations.Bone;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.custom.Animations.RiggedMesh;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.custom.Shaders.AnimatedShader;
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
    public HashMap<String, SoundSource> audioSources = new HashMap<>();
    public Map<String, Entity> entities;
    

    public static PhysicsSpace physicsSpace;
    private Camera camera;
    public static Texture defaultTexture;
    private Vector3f mouseWorldPos = new Vector3f(0, 0, 0);
    private ArrayList<Bloon> bloons = new ArrayList<Bloon>();
    private ArrayList<Dart> darts = new ArrayList<Dart>();
    private ArrayList<Monkey> monkeys = new ArrayList<Monkey>();
    private Model bloonModel;
    private Model dartModel;
    private Vector3f[] bloonNodes;

    // temp
    //private Entity[] nodes = new Entity[5];
    private float sphereRadius = 1.2f;

    public Game() throws Exception {
        // create new instances for these things
        renderer = new RenderManager();
        window = Launcher.getWindow();
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

    void loadSounds() {
        try {
            // load the sound file to a buffer, then create a new audio source at the world origin with the buffer attached
            // store that sound source to a map of sounds
            // repeat this for each sound file
            SoundSource jazz = soundManager.createSound("jazz", "assets/sounds/jazz.ogg", new Vector3f(0,0,0), true, false, 0.4f);
            audioSources.put("jazz", jazz);
            SoundSource tower_place = soundManager.createSound("tower_place", "assets/sounds/tower_place.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("tower_place", tower_place);
            
            SoundSource pop1 = soundManager.createSound("pop1", "assets/sounds/pop1.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("pop1", pop1);
            SoundSource pop2 = soundManager.createSound("pop2", "assets/sounds/pop2.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("pop2", pop2);
            SoundSource pop3 = soundManager.createSound("pop3", "assets/sounds/pop3.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("pop3", pop3);
            SoundSource pop4 = soundManager.createSound("pop4", "assets/sounds/pop4.ogg", new Vector3f(0,0,0), false, false, 0.4f);
            audioSources.put("pop4", pop4);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Material previewRed;
    Material previewWhite;
    RiggedModel monkeyModel;
    Entity cameraPos = new Entity(null, new Vector3f(), new Vector3f(), new Vector3f());
    @Override
    public void init() throws Exception {

        renderer.init();

        // load all the textures
        defaultTexture = new Texture(loader.loadTexture("assets/textures/DefaultTexture.png"));

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

        dartModel = loader.loadModel("assets/models/dart.fbx");
        bloonModel = loader.loadModel("assets/models/bloon.dae");
        bloonModel.setAllMaterials(new Material(0f, 2, loader.createTextureColor(Color.RED)));


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
        //TEMP
        // nodes[0] = new Sphere(new Vector3f(0,2f,0), sphereRadius);
        // nodes[1] = new Sphere(new Vector3f(0,2f,0), sphereRadius);
        // nodes[2] = new Sphere(new Vector3f(0,2f,0), sphereRadius);
        // nodes[3] = new Sphere(new Vector3f(0,2f,0), sphereRadius);
        // nodes[4] = new Sphere(new Vector3f(0,2f,0), sphereRadius);
        // entities.put("node5", nodes[3]);
        // entities.put("node4", nodes[4]);
        // entities.put("node1", nodes[0]);
        // entities.put("node2", nodes[1]);
        // entities.put("node3", nodes[2]);

        monkeyModel = loader.loadRiggedModel("assets/models/monkey.fbx");

        AnimatedEntity monkey = new AnimatedEntity(RiggedModel.copy(monkeyModel), 
            new Vector3f(), new Vector3f(), new Vector3f(0.1f));
        monkey.setEnabled(false);
        monkey.setAnimationId(2);
        entities.put("preview_monkey", monkey);

        previewRed = new Material(loader.createTextureColor(Color.RED));
        previewWhite = new Material(loader.createTextureColor(Color.WHITE));
        

        audioSources.get("jazz").play();
    }

    int i = 0;
    Vector2d dMouse = new Vector2d();
    public void mouseDown(long window, int button, int action, int mods, MouseInput input) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if(action == GLFW.GLFW_PRESS) {
                dMouse = new Vector2d(input.getCurrentPos());
            } else if (action == GLFW.GLFW_RELEASE) {
                if(!monkeyMode || isInvalid)
                    return;
                if(dMouse.distance(input.getCurrentPos()) < 2f) {
                    Monkey monkey = new Monkey("monkey" + (monkeys.size() + 1), RiggedModel.copy(monkeyModel), 
                        mouseWorldPos, 
                        new Vector3f(), 
                        new Vector3f(0.1f,0.1f,0.1f),
                        5,0.5f,50,0f
                    );
                    monkeys.add(monkey);
                    monkey.setAnimationId(2);
                    entities.put("monkey" + monkeys.size(), monkey);
                    audioSources.get("tower_place").play();
                }

            }
        }
    }

    int frameCounter = 0;
    int bloonCounter = 0;
    int spawnNewBloonOnNextTick = -1;
    boolean monkeyMode = false;
    boolean isInvalid = false;
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

            if(key ==GLFW.GLFW_KEY_SPACE) {
                monkeyMode = true;
            }
        }
        if(action ==GLFW.GLFW_RELEASE) {
            if(key ==GLFW.GLFW_KEY_SPACE) {
                monkeyMode = false;
            }
        }
    }

    float rotX = 45;
    float rotY = 45;
    float zoom = 2;
    float panX = 0;
    float panZ = 0;

    @Override
    public void input(MouseInput input, double deltaTime, int frame) {

        if(monkeyMode) {
            Entity previewMonkey = entities.get("preview_monkey");
            previewMonkey.setPosition(new Vector3f(mouseWorldPos));
            
            isInvalid = false;
            if(previewMonkey.getPosition().y > 0.01f)
                isInvalid = true;

            for(Monkey monkey : monkeys) {
                Vector3f monkeyPos = monkey.getPosition();
                if(monkeyPos.distance(previewMonkey.getPosition()) < 0.5f) {
                    isInvalid = true;
                    break;
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

            // nodes[0].setPosition(closestNode);
            // nodes[1].setPosition(backNode);
            // nodes[2].setPosition(frontNode);
            // get the slopes (m values)
            Vector3f averageVector1 = new Vector3f();
            closestNode.add(backNode, averageVector1);
            averageVector1.div(2);
            Vector3f averageVector2 = new Vector3f();
            closestNode.add(frontNode, averageVector2);
            averageVector2.div(2);
            // nodes[4].setPosition(averageVector1);
            // nodes[3].setPosition(averageVector2);
            
            Vector3f[] vectorsToCompare = {closestNode, backNode, frontNode, averageVector1, averageVector2};
            // check all vectors

            float smallestDist = Float.MAX_VALUE;
            for(Vector3f v : vectorsToCompare){
                v = new Vector3f(v.x, mouseWorldPos.y, v.z);
                float dist = v.distance(mouseWorldPos);
                smallestDist = Math.min(dist, smallestDist);
                if(dist < sphereRadius){
                    isInvalid = true;
                    break;
                }
            }
            System.out.println(smallestDist);
            

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
            Bloon b = new Bloon("bloon", BloonType.values()[spawnNewBloonOnNextTick],
                Model.copy(bloonModel), 
                new Vector3f(bloonNodes[0]), 
                new Vector3f(),
                new Vector3f(0.5f)
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


        // for each entity in the world
        // sync the visual rotation and positions with the physics rotations and positions
        for(int i = 0; i < entities.values().size(); i++) {
            Entity entity = entities.values().toArray(new Entity[]{})[i];
            if(entity.getRigidBody() != null) {
                entity.setPosition(Utils.convert(entity.getRigidBody().getPhysicsLocation(null)));
                entity.setRotation(Utils.ToEulerAngles(Utils.convert(entity.getRigidBody().getPhysicsRotation(null))));
            }
            if(entity instanceof AnimatedEntity) {
                AnimatedEntity animatedEntity = (AnimatedEntity)entity;

                if(animatedEntity.getAnimationTick() >= (1f/60f)) {
                    boolean loopComplete = animatedEntity.nextFrame();
                    animatedEntity.setAnimationTick(0);

                    if(loopComplete) {
                        if(animatedEntity instanceof Monkey) {
                            int randomNumber = random.nextInt(6) + 1;
                            if(randomNumber == 6)
                                ((Monkey)animatedEntity).setAnimationId(3);
                            else if(randomNumber == 5)
                                ((Monkey)animatedEntity).setAnimationId(4);
                            else
                                ((Monkey)animatedEntity).setAnimationId(2);
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
                if(bloon.getPosition().distance(nodePos) <= 0.1f){
                    // look at the next node
                    bloon.incremenNodeIndex();
                    nodePos = bloonNodes[bloon.getNodeIndex()];
                    

                    Vector3f difference = new Vector3f();
                    nodePos.sub(bloon.getPosition(), difference);
                    difference.normalize();
                    bloon.setCurrentHeading(difference);
                }
                
                if(bloon.isEnabled() == false) {
                    entities.remove(bloon.getName());
                    bloons.remove(bloon);
                }
                bloon.translate(new Vector3f(bloon.getCurrentHeading()).mul(2 * bloon.getSpeed() * interval));
            }

            if(entity instanceof Monkey) {
                Monkey monkey = (Monkey) entity;
                //System.out.printf("%.2f/%.2f\n", monkey.getTick(), monkey.getRate());
                if(monkey.getTick() >= monkey.getRate()) {
                    if(bloons.size() < 1)
                        continue;
                    Bloon target = null;
                    for(int b = 0; b < bloons.size(); b++) {
                        Bloon bloon = bloons.get(b);
                        if(bloon.getPosition().distance(monkey.getPosition()) <= monkey.getRange()) {
                            target = bloon;
                            break;
                        }
                    }
                    if(target == null)
                        continue;
                    monkey.setAnimationId(1);
                    monkey.lookAtY(new Vector3f(target.getPosition()));
                    
                    Dart d = new Dart("dart", Model.copy(dartModel), 
                        new Vector3f(monkey.getPosition().x, monkey.getPosition().y + 0.6f, monkey.getPosition().z).add(monkey.getRight().div(-3)), 
                        new Vector3f(), 
                        new Vector3f(0.1f)
                    );
                    d.setDestinationDirection(new Vector3f(target.getPosition()));
                    d.lookAtY(new Vector3f(target.getPosition()));
                    d.setSource(monkey);
                    d.setSpeed(monkey.getSpeed());
                    darts.add(d);
                    d.setName("dart" + darts.size());
                    entities.put("dart" + darts.size(), d);
                    monkey.setTick(0);
                }

                monkey.addTick(interval);
            }

            if(entity instanceof Dart) {
                Dart dart = (Dart) entity;

                boolean popped = false;
                for(int b = 0; b < bloons.size(); b++) {
                    Bloon bloon = bloons.get(b);
                    if(dart.getPosition().distance(bloon.getPosition()) < 0.5f) {
                        int result = bloon.damage(1);
                        if(result >= 0) {
                            int randomNumber = random.nextInt(4) + 1;
                            audioSources.get("pop" + randomNumber).play();
                            
                            if(result > 0) {
                                bloons.remove(b);
                                entities.remove(bloon.getName());
                            }
                        }
                        darts.remove(dart);
                        entities.remove(dart.getName());
                        popped = true;
                        break;
                    }
                }

                if(popped)
                    continue;

                if(dart.getPosition().distance(0,0,0) > 15f) {
                    darts.remove(dart);
                    entities.remove(dart.getName());
                    continue;
                }
                dart.translate(new Vector3f(dart.getDestinationDirection()).mul(dart.getSpeed() * interval));
            }
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

    
    FloatBuffer depthBuffer = BufferUtils.createFloatBuffer(1920*1080);
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
            float depth = depthBuffer.get();
        
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

        if(monkeyMode)
            renderer.forceRender(entities.get("preview_monkey"), camera);
 
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
