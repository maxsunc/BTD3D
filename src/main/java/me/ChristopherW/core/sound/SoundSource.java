package me.ChristopherW.core.sound;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

public class SoundSource {
    private String name;
    private int id;

    public SoundSource(String name, boolean loop, boolean relative) {
        this.name = name;
        this.id = AL10.alGenSources();
        AL10.alSourcei(id, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
        AL10.alSourcei(id, AL10.AL_SOURCE_RELATIVE, relative ? AL10.AL_TRUE : AL10.AL_FALSE);
    }
    

    public void cleanup() {
        stop();
        AL10.alDeleteSources(id);
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(id, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void pause() {
        AL10.alSourcePause(id);
    }

    public void play() {
        AL10.alSourcePlay(id);
    }

    public void setBuffer(int bufferId) {
        stop();
        AL10.alSourcei(id, AL10.AL_BUFFER, bufferId);
    }

    public void setGain(float gain) {
        AL10.alSourcef(id, AL10.AL_GAIN, gain);
    }

    public float getGain() {
        return AL10.alGetSourcef(id, AL10.AL_GAIN);
    }

    public void setPosition(Vector3f position) {
        AL10.alSource3f(id, AL10.AL_POSITION, position.x, position.y, position.z);
    }

    public void stop() {
        AL10.alSourceStop(id);
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }
}
