package me.ChristopherW.core.sound;

import org.lwjgl.openal.AL10;

import org.joml.Vector3f;

public class SoundListener {
    public SoundListener(Vector3f position) {
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }

    public void setOrientation(Vector3f at, Vector3f up) {
        float[] data = new float[6];
        data[0] = at.x;
        data[1] = at.y;
        data[2] = at.z;
        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;
        AL10.alListenerfv(AL10.AL_ORIENTATION, data);
    }

    public void setPosition(Vector3f position) {
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
    }

    public void setSpeed(Vector3f speed) {
        AL10.alListener3f(AL10.AL_VELOCITY, speed.x, speed.y, speed.z);
    }
}
