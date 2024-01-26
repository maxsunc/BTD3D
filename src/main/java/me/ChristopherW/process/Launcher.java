package me.ChristopherW.process;

// import com.jme3.bullet.PhysicsSpace;
// import com.jme3.bullet.objects.PhysicsRigidBody;
// import com.jme3.bullet.util.NativeLibrary;
import me.ChristopherW.core.EngineManager;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.utils.Config;

// import com.jme3.system.NativeLibraryLoader;

import java.io.File;
import java.util.logging.Level;

import com.jme3.bullet.util.NativeLibrary;
import com.jme3.system.NativeLibraryLoader;

public class Launcher {
    private static WindowManager window;
    private static Game game;
    private static EngineManager engine;

    public static void main(String[] args) throws Exception {
        // Load the physics engine natives
        NativeLibraryLoader.loadLibbulletjme(true, new File("natives/"), "Release", "Sp");
        NativeLibrary.setStartupMessageEnabled(false);
        NativeLibrary.logger.setLevel(Level.OFF);

        // Change graphics settings based on the configs graphics mode
        switch (Config.GRAPHICS_MODE) {
            case LOW:
                Config.SHADOW_RES = 0;
                Config.SHADOW_FILTERING = false;
                break;
            case MEDIUM:
                Config.SHADOW_RES = 2048;
                Config.SHADOW_FILTERING = false;
                break;
            case HIGH:
                Config.SHADOW_RES = 4096;
                Config.SHADOW_FILTERING = true;
                break;
            default:
                break;
        }

        // create new window manager, game instance and engine instance
        window = new WindowManager(Config.TITLE, Config.WIDTH, Config.HEIGHT, Config.VSYNC);
        game = new Game();
        engine = new EngineManager();


        // attempt to start the engine but catch any errors
        try {
            engine.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }
    public static Game getGame() {
        return game;
    }
    public static EngineManager getEngine() {
        return engine;
    }
}