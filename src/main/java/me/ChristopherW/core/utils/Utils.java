package me.ChristopherW.core.utils;

import me.ChristopherW.core.custom.UI.UIScreens.Resolution;

import org.joml.Quaternionf;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }
    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static String loadResource(String filename) throws Exception {
        String result;
        try(InputStream in = Utils.class.getResourceAsStream(filename);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> readAllLines(String fileName) {
        List<String> list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static float GetColorDistance(Color a, Color b) {
        return (float) Math.sqrt(Math.pow(b.getRed() - a.getRed(),2) + Math.pow(b.getGreen() - a.getGreen(),2) + Math.pow(b.getBlue() - a.getBlue(),2));
    }
    public static org.joml.Vector3f ToEulerAngles(org.joml.Quaternionf q) {
        org.joml.Vector3f angles = new org.joml.Vector3f(0,0,0);

        // roll (x-axis rotation)
        double sinr_cosp = 2 * (q.w * q.x + q.y * q.z);
        double cosr_cosp = 1 - 2 * (q.x * q.x + q.y * q.y);
        angles.x = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // pitch (y-axis rotation)
        double sinp = Math.sqrt(1 + 2 * (q.w * q.y - q.x * q.z));
        double cosp = Math.sqrt(1 - 2 * (q.w * q.y - q.x * q.z));
        angles.y = (float) (2 * Math.atan2(sinp, cosp) - Math.PI / 2);

        // yaw (z-axis rotation)
        double siny_cosp = 2 * (q.w * q.z + q.x * q.y);
        double cosy_cosp = 1 - 2 * (q.y * q.y + q.z * q.z);
        angles.z = (float) Math.atan2(siny_cosp, cosy_cosp);

        return angles;
    }

    // algorithm to convert a Euler angle to a Quaternion angle
    public static org.joml.Quaternionf ToQuaternion(org.joml.Vector3f vector) {
        double cr = Math.cos(vector.x * 0.5);
        double sr = Math.sin(vector.x * 0.5);
        double cp = Math.cos(vector.y * 0.5);
        double sp = Math.sin(vector.y * 0.5);
        double cy = Math.cos(vector.z * 0.5);
        double sy = Math.sin(vector.z * 0.5);

        org.joml.Quaternionf q = new Quaternionf();
        q.w = (float) (cr * cp * cy + sr * sp * sy);
        q.x = (float) (sr * cp * cy - cr * sp * sy);
        q.y = (float) (cr * sp * cy + sr * cp * sy);
        q.z = (float) (cr * cp * sy - sr * sp * cy);

        return q;
    }


    public static org.joml.Vector3f localPosition(float x, float y, float z, org.joml.Vector3f rot) {
        org.joml.Vector3f position = new org.joml.Vector3f(x, y, z);
        if(z != 0) {
            position.x = (float) Math.sin(Math.toRadians(rot.y)) * -1.0f * z;
            position.z = (float) Math.cos(Math.toRadians(rot.y)) * z;
        }
        if(x != 0) {
            position.x = (float) Math.sin(Math.toRadians(rot.y - 90)) * -1.0f * x;
            position.z = (float) Math.cos(Math.toRadians(rot.y - 90)) * x;
        }
        position.y = y;
        return position;
    }

    public static int indexOf(int[] array, int check) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] == check)
                return i;
        }
        return -1;
    }

    public static int getResolutionIndex(Resolution[] array, Resolution check) {
        for(int i = 0; i < array.length; i++) {
            if(array[i].height == check.height)
                if(array[i].width == check.width)
                    return i;
        }
        return -1;
    }

    <T> int indexOf(T[] array, T check) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] == check)
                return i;
        }
        return -1;
    }

    public static ByteBuffer allocBytes(int howmany) {
        return ByteBuffer.allocateDirect(howmany).order(ByteOrder.nativeOrder());
     }

}
