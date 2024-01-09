package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;

public class Bloon extends Entity{
    private float speed;
    private BloonType type;
    private int health;


    public Bloon(String name, Model model, Vector3f position, Vector3f rotation, Vector3f scale){
        super(name, model, position, rotation, scale);
    }


    public float getSpeed() {
        return speed;
    }


    public void setSpeed(float speed) {
        this.speed = speed;
    }


    public BloonType getType() {
        return type;
    }


    public void setType(BloonType type) {
        this.type = type;
    }


    public int getHealth() {
        return health;
    }


    public void setHealth(int health) {
        this.health = health;
    }
    
    public void damage(int amount){
        
    }

}
