package me.ChristopherW.core.utils;

import me.ChristopherW.core.custom.UI.UIScreens.Resolution;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.system.MemoryUtil;

import com.jme3.math.Quaternion;

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

    public static Vector3f ToEulerAngles(Quaternionf q1) {
        double sqw = q1.w*q1.w;
        double sqx = q1.x*q1.x;
        double sqy = q1.y*q1.y;
        double sqz = q1.z*q1.z;
        double heading = Math.atan2(2.0 * (q1.x*q1.y + q1.z*q1.w),(sqx - sqy - sqz + sqw));
        double bank = Math.atan2(2.0 * (q1.y*q1.z + q1.x*q1.w),(-sqx - sqy + sqz + sqw));
        double attitude = Math.asin(-2.0 * (q1.x*q1.z - q1.y*q1.w));
        return new Vector3f((float)Math.toDegrees(bank), (float)Math.toDegrees(heading), (float)Math.toDegrees(attitude));
    }

    // algorithm to convert a Euler angle to a Quaternion angle
    public static Quaternion ToQuaternion(org.joml.Vector3f vector) {
        return new Quaternion().fromAngles((float)Math.toRadians(vector.x), (float)Math.toRadians(vector.y), (float)Math.toRadians(vector.z));
    }

    public static com.jme3.math.Quaternion ToQuaternion(com.jme3.math.Vector3f vector) {
        return new Quaternion().fromAngles((float)Math.toRadians(vector.x), (float)Math.toRadians(vector.y), (float)Math.toRadians(vector.z));
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

    public static com.jme3.math.Vector3f convert(Vector3f in) {
        return new com.jme3.math.Vector3f(in.x, in.y, in.z);
    }
    public static Vector3f convert(com.jme3.math.Vector3f in) {
        return new Vector3f(in.x, in.y, in.z);
    }
    public static com.jme3.math.Quaternion convert(Quaternionf in) {
        return new com.jme3.math.Quaternion(in.x, in.y, in.z, in.w);
    }
    public static Quaternionf convert(com.jme3.math.Quaternion in) {
        return new Quaternionf(in.getX(), in.getY(), in.getZ(), in.getW());
    }
    public static Matrix4f convertMatrix(AIMatrix4x4 assimp)
    {
        return new Matrix4f(
                assimp.a1(), assimp.b1(), assimp.c1(), assimp.d1(),
                assimp.a2(), assimp.b2(), assimp.c2(), assimp.d2(),
                assimp.a3(), assimp.b3(), assimp.c3(), assimp.d3(),
                assimp.a4(), assimp.b4(), assimp.c4(), assimp.d4()
        );
    }

    public static Matrix4f multiplyMatrices(Matrix4f... sequence)
    {
        Matrix4f res = new Matrix4f();

        for (Matrix4f m : sequence)
            res.mul(m);


        return res;
    }

    public static Quaternionf slerp(Quaternionf start, Quaternionf end, float alpha)
    {
        Quaternionf a = new Quaternionf(start);
        Quaternionf b = new Quaternionf(end);

        a.slerp(b, alpha);

        return a;
    }
}
