package me.ChristopherW.core;

import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.process.Launcher;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class EngineManager {
    public static final long NANOSECOND = 1000000000L;

    private static int fps = (int) GlobalVariables.FRAMERATE;
    private static float frametime;

    private boolean isRunning;

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        // add a GLFW error callback for debugging
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        // get the window, game and input instances
        window = Launcher.getWindow();
        gameLogic = Launcher.getGame();
        mouseInput = new MouseInput();
        this.window.setInput(mouseInput);

        // initalize the window, game, and input manager
        window.init();
        gameLogic.init();
        mouseInput.init();

        // set the seconds per frame based on the current framerate preference
        frametime = 1.0f / GlobalVariables.FRAMERATE;
    }
    
    public void ForceUpdateFramerate() {
        // update the seconds per frame based on the current framerate preference
        frametime = 1.0f / GlobalVariables.FRAMERATE;
    }

    public void start() throws Exception {
        // initialize variables
        init();

        // make sure the game doesn't start twice
        if(isRunning)
            return;

        // start running the engine
        run();
    }
    public void run() throws Exception {
        // set variables to defaults
        this.isRunning = true;
        int totalFrames = 0;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        // run game loop as long as the game should be running
        while(isRunning) {
            boolean render = false;

            // get current time
            long startTime = System.nanoTime();

            // check how long it's been since the last frame
            long passedTime = startTime - lastTime;

            // replace last with current frame time
            lastTime = startTime;

            // add the passed time to a time counter
            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            // if the time counter reaches the seconds per frame required, render a frame
            while(unprocessedTime > frametime) {
                render = true;

                // remove that frametime from the time counter, this helps to prevent frame loss if they are backed up
                unprocessedTime -= frametime;

                // stop the engine if the window closes
                if(window.windowShouldClose())
                    stop();

                // if a second has passed, set the FPS and update the window title
                if(frameCounter >= NANOSECOND) {
                    setFps(frames);
                    window.setTitle(GlobalVariables.TITLE + " - FPS: " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if(render) {

                // pass the "delta time" into both the update and input methods
                update(getFps() == 0 ? 0 : 1f / getFps());

                // render game to the screen 
                render();

                input(getFps() == 0 ? 0 : 1f / getFps(), totalFrames);

                // count the frames for the FPS counter
                frames++;
                totalFrames++;
            }
        }

        // clean up memory after game stops
        cleanup();
    }

    public void stop() {
        // if the game isn't running, there's nothing to stop
        if(!isRunning)
            return;

        isRunning = false;
    }

    private void input(double deltaTime, int frame) {
        // pass the frame time data to the game instance and mouse input manager
        gameLogic.input(mouseInput, deltaTime, frame);
        mouseInput.input();
    }

    private void render() throws Exception {
        // render the game to the screen
        gameLogic.render();

        // refresh the window frame
        window.update();
    }

    private void update(float interval) {
        // pass the frame time data to the game instance
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        gameLogic.cleanup();
        window.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
