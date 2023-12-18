package me.ChristopherW.process;

// import com.jme3.bullet.PhysicsSpace;
// import com.jme3.bullet.objects.PhysicsRigidBody;
// import com.jme3.bullet.util.NativeLibrary;
import me.ChristopherW.core.EngineManager;
import me.ChristopherW.core.WindowManager;
import me.ChristopherW.core.utils.GlobalVariables;

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
        // // Load the physics engine natives
        NativeLibraryLoader.loadLibbulletjme(true, new File("natives/"), "Release", "Sp");
        NativeLibrary.setStartupMessageEnabled(false);
        NativeLibrary.logger.setLevel(Level.OFF);

        // create new window manager, game instance and engine instance
        window = new WindowManager(GlobalVariables.TITLE, GlobalVariables.WIDTH, GlobalVariables.HEIGHT, GlobalVariables.VSYNC);
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