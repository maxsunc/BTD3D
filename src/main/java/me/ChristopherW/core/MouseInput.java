package me.ChristopherW.core;

import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.process.Launcher;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

public class MouseInput {
    private final Vector2d previousPos, currentPos;
    public Vector2d getCurrentPos() {
        return currentPos;
    }

    private final Vector2f displVec;

    private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false;

    private long cursor;
    private long cursorDown;
    public long getCursor() {
        return cursor;
    }

    public long getCursorDown() {
        return cursorDown;
    }

    public MouseInput() {
        // init variables
        this.previousPos = new Vector2d(GlobalVariables.WIDTH/2, GlobalVariables.HEIGHT/2);
        this.currentPos = new Vector2d(GlobalVariables.WIDTH/2,GlobalVariables.HEIGHT/2);
        this.displVec = new Vector2f();
    }

    public void init() {
        // set the mouse to the center of the window
        GLFW.glfwSetCursorPos(Launcher.getWindow().getWindow(), GlobalVariables.WIDTH/2, GlobalVariables.HEIGHT/2);

        // set the callback for mouse movement to store the position
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xpos, ypos) -> {
            this.currentPos.x = xpos;
            this.currentPos.y = ypos;
        });

        // set the callback for whent he mouse enters the window
        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(), (window, entered) -> {
            this.inWindow = entered;
        });

        ObjectLoader loader = new ObjectLoader();
        GLFWImage.Buffer mousebf = GLFWImage.malloc(1);
        mousebf.put(0, loader.loadtextureBuffer("assets/textures/HandCursor.png"));
        GLFWImage.Buffer mouseDownbf = GLFWImage.malloc(1);
        mouseDownbf.put(0, loader.loadtextureBuffer("assets/textures/HandCursorDown.png"));
        cursor = GLFW.glfwCreateCursor(mousebf.get(), 20, 8);
        cursorDown = GLFW.glfwCreateCursor(mouseDownbf.get(), 20, 8);
        // set the mouse click callbacks to store the result
        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(), (window, button, action, mods) -> {
            Launcher.getGame().mouseDown(window, button, action, mods, this);
            this.leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            this.rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;

            if(GlobalVariables.USE_CUSTOM_CURSOR) {
                if(isLeftButtonPress()) {
                    GLFW.glfwSetCursor(window, cursorDown);
                } else {
                    GLFW.glfwSetCursor(window, cursor);
                }
            }
            
        });
    }

    public void input() {
        // to summarize
        // capture the distance moved from the center of the screen when the mouse is disabled
        this.displVec.x = 0;
        this.displVec.y = 0;
        double x = this.currentPos.x - this.previousPos.x;
        double y = this.currentPos.y - this.previousPos.y;
        boolean rotateX = x != 0;
        boolean rotateY = y != 0;
        if (rotateX)
            this.displVec.y = (float) x;
        if (rotateY)
            this.displVec.x = (float) y;
        this.previousPos.x = this.currentPos.x;
        this.previousPos.y = this.currentPos.y;
    }
    public Vector2f getDisplVec() {
        return displVec;
    }

    public boolean isLeftButtonPress() {
        return leftButtonPress;
    }

    public boolean isRightButtonPress() {
        return rightButtonPress;
    }
}
