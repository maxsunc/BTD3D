package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;

public class Projectile extends Entity{
    private Vector3f destinationDirection;
    private Tower source;
    private Bloon target;
    private float speed;

    public Projectile(String name, Model model, Vector3f position, Vector3f rotation, Vector3f scale){
        super(name, model, position, rotation, scale);
    }

    public Vector3f getDestinationDirection() {
        return destinationDirection;
    }

    public void setDestinationDirection(Vector3f destination) {
        Vector3f difference = new Vector3f();
        destination.sub(this.getPosition(), difference);
        // move it towards the node pos
        difference.normalize();
        this.destinationDirection = new Vector3f(difference);
    }

    public Tower getSource() {
        return source;
    }

    public void setSource(Tower source) {
        this.source = source;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setTarget(Bloon target) {
        this.target = target;
    }

    public Bloon getTarget() {
        return this.target;
    }

    
}
