package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;

public class Monkey extends Entity{
    private int range;
    private float rate;
    private float speed;
    private int tick;
    
    public Monkey(String name, Model model, Vector3f position, Vector3f rotation, Vector3f scale, int range, float rate, float speed, int tick){
        super(name, model, position, rotation, scale);
        this.range = range;
        this.speed = speed;
        this.tick = tick;
        this.speed = speed;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void attack(){

    }

}
