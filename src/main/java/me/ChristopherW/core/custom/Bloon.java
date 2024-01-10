package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;

public class Bloon extends Entity{
    private float speed;
    private BloonType type;
    private int health;
    private int nodeIndex;
    private Vector3f currentHeading;
    


    public Bloon(String name, Model model, Vector3f position, Vector3f rotation, Vector3f scale){
        super(name, model, position, rotation, scale);
        nodeIndex = 0;
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
    
    public boolean damage(int amount){
        this.health -= amount;

        if(this.health <= 0) {
            return true;
        }
        return false;
    }


    public int getNodeIndex() {
        return nodeIndex;
    }


    public void incremenNodeIndex(){
        if(nodeIndex >= 45){
            this.setEnabled(false);
            return;
        }
        nodeIndex++;
    }




    public void setNodeIndex(int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }




    public Vector3f getCurrentHeading() {
        return currentHeading;
    }




    public void setCurrentHeading(Vector3f currentHeading) {
        this.currentHeading = currentHeading;
    }

}
