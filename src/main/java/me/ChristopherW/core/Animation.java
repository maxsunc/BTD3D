package me.ChristopherW.core;

public abstract class Animation {
    public boolean isPlaying = false;
    public float timeElapsed = 0.0f;
    public abstract void tick(float elapsed);
    public abstract void play();
}
