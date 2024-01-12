package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Animations.AnimatedEntity;
import me.ChristopherW.core.custom.Animations.RiggedModel;

public class Monkey extends AnimatedEntity{
    private int value;
    private int range;
    private float rate;
    private float speed;
    private float tick;

    public Monkey(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale, MonkeyType type){
        super(name, model, position, rotation, scale);
        this.range = type.range;
        this.rate = type.rate;
        this.speed = type.speed;
        this.value = type.cost;
        this.tick = 0f;
        
    }
    public int getValue() {
        return value;
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
