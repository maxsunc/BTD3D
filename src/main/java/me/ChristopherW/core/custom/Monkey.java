package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Animations.AnimatedEntity;
import me.ChristopherW.core.custom.Animations.RiggedModel;

public class Monkey extends AnimatedEntity{
    private int range;
    private float rate;
    private float speed;
    private float tick;
    
    public Monkey(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale, int range, float rate, float speed, float tick){
        super(name, model, position, rotation, scale);
        this.range = range;
        this.rate = rate;
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

    public float getTick() {
        return tick;
    }

    public void addTick(float tick) {
        this.tick += tick;
    }
    public void setTick(float tick) {
        this.tick = tick;
    }

    public void attack(){

    }

}
