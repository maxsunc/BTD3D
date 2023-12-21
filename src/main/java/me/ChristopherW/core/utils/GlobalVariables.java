package me.ChristopherW.core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class GlobalVariables {
    public static final String TITLE = "Example Game";
    public static final String ICON_PATH = "assets/textures/icons/icon.png";
    public static final Vector4f BG_COLOR = new Vector4f(0.2f, 0.2f,1f, 1f);

    public static final float GRAVITY = -9.8f;
    public static boolean FULLSCREEN = false;
    public static int WIDTH = 1280, HEIGHT = 720;
    public static float MAX_FRAMERATE = 60;
    public static float FRAMERATE = 60;
    public static boolean SHOW_FPS = false;
    public static boolean VSYNC = false;
    public static int SHADOW_RES = 4098;
    public static boolean SHADOW_FILTERING = true;

    public static final float FOV = (float) Math.toRadians(50);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final float CAMERA_MOVE_SPEED = 0.05f;
    public static final float MOUSE_SENSITIVITY_X = 0.125f;
    public static final float MOUSE_SENSITIVITY_Y = 0.25f;

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.3f ,0.3f, 0.3f);
    public static final float SPECULAR_POWER = 10f;
    public static final long NANOSECOND = 1000000000L;
}
