package me.ChristopherW.core;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.ChristopherW.core.custom.UI.GUIManager;
import me.ChristopherW.core.custom.UI.UIScreens.Resolution;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.process.Launcher;

import java.nio.IntBuffer;
import java.util.prefs.Preferences;

import javax.swing.event.MouseInputListener;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {
    public final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    public final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    public GUIManager guiManager;
    private String glslVersion = null;
    private String title;

    private int width, height;
    private long window;

    public Resolution monitorResolution = null;
    public int monitorRefreshRate = -1;
    public Vector2i winPos;
    public Vector2i winSize;

    private boolean resize, vSync;

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;

        // Create a new projection matrix for converting 3D space to 2D space  
        projectionMatrix = new Matrix4f();

        // create a new GUI manager attached to this window
        guiManager = new GUIManager(this);

        // set the fullscreen variable based on the saved user preference     }
    }

    public boolean isResize() {
        return resize;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    private void initWindow() {
        // set GLFW error callback to print
        GLFWErrorCallback.createPrint(System.err).set();

        // attempt to start GLFW
        if(!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // set the shader language version to 4.1
        glslVersion = "#version 410";

        // set window parameters
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);

        // get default monitor
        long monitor = GLFW.glfwGetPrimaryMonitor();
        // get display specs of said monitor
        GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);

        // store the resolution and refresh rate of th primary monitor
        monitorResolution = new Resolution(mode.width(), mode.height());
        monitorRefreshRate = mode.refreshRate();

        // if the game should be fullscreen, make it fill the monitor
        if(GlobalVariables.FULLSCREEN) {
            // set fullscreen-specific window parameters
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, mode.redBits());
            GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, mode.greenBits());
            GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, mode.blueBits());
            GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, mode.refreshRate());

            // create a non-maximized window to save if the user decides to change to windowed mode
            window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
            // center the window
            GLFW.glfwSetWindowPos(window, (mode.width() - width) / 2, (mode.height() - height) / 2);
            // save the window dimensions and position
            winSize = new Vector2i(GlobalVariables.WIDTH, GlobalVariables.HEIGHT);
            IntBuffer xbuf = BufferUtils.createIntBuffer(1);
            IntBuffer ybuf = BufferUtils.createIntBuffer(1);
            GLFW.glfwGetWindowPos(window, xbuf, ybuf);
            winPos = new Vector2i(xbuf.get(0), ybuf.get(0));

            // create the new maximized window
            window = GLFW.glfwCreateWindow(mode.width(), mode.height(), title, monitor, MemoryUtil.NULL);
            width = mode.width();
            height = mode.height();
            GLFW.glfwMaximizeWindow(window);
        } else {
            // create the windowed mode window
            window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
            GLFW.glfwSetWindowPos(window, (mode.width() - width) / 2, (mode.height() - height) / 2);
        }

        // catch null windows
        if(window == MemoryUtil.NULL)
            throw new IllegalStateException("Failed to create GLFW window");

        // set resize window callback
        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResize(true);
        });

        // create a new object loader instance
        ObjectLoader loader = new ObjectLoader();
        // allocate memory for the image icon buffer
        GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
        // load the image icon into the buffer
        imagebf.put(0, loader.loadtextureBuffer(GlobalVariables.ICON_PATH));
        // set the window icon
        GLFW.glfwSetWindowIcon(window, imagebf);

        // set OpenGL to use this window
        GLFW.glfwMakeContextCurrent(window);

        // if vSync is enabled set the frame swap interval to 1 and cap the framerate to the monitor's refresh rate
        if(vSync) {
            GLFW.glfwSwapInterval(1);
            GlobalVariables.FRAMERATE = mode.refreshRate();
        }

        // set a keyboard press callback
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            Launcher.getGame().keyDown(window, key, scancode, action, mods);
        });

        GLFW.glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override public void invoke (long win, double dx, double dy) {
                Launcher.getGame().onScroll(dy);
            }
        });

        GLFW.glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Launcher.getGame().depthBuffer = BufferUtils.createFloatBuffer(width * height);
            }
            
        });
        
        // make the window visible
        GLFW.glfwShowWindow(window);

        // create OpenGL capabilites instance
        GL.createCapabilities();

        // set the clear screen color to be just an empty pixel
        GL11.glClearColor(0f,0f,0f,0f);
        
        // enable OpenGL parameters
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
    }

    private void initImGui() {
        ImGui.createContext();
        guiManager.init();
    }

    public void init() {
        // create window
        initWindow();

        // initialize ImGui for this window
        initImGui();
        imGuiGlfw.init(window, true);
        imGuiGl3.init(glslVersion);
    }

    public void update() {
        // swap frame buffers
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public void cleanup() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        GLFW.glfwDestroyWindow(window);
    }

    public void setClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r, g, b, a);
    }
    public boolean isKeyReleased(int keycode) {
        return GLFW.glfwGetKey(window, keycode) == GLFW.GLFW_RELEASE;
    }
    public boolean isKeyPressed(int keycode) {
        return GLFW.glfwGetKey(window, keycode) == GLFW.GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(window, title);
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindow() {
        return window;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getOrthoMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.ortho(-10, 10, -10, 10, 1.0f, 7.5f);
        return matrix;
    }

    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / height;
        return projectionMatrix.setPerspective(GlobalVariables.FOV, aspectRatio, GlobalVariables.Z_NEAR, GlobalVariables.Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / height;
        return matrix.setPerspective(GlobalVariables.FOV, aspectRatio, GlobalVariables.Z_NEAR, GlobalVariables.Z_FAR);
    }

    private MouseInput mouseInput;
    public void setInput(MouseInput mouseInput) {
        this.mouseInput = mouseInput;
    }

    public MouseInput getInput() {
        return mouseInput;
    }
}
