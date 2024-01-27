package me.ChristopherW.core.entity.primatives;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Assets;
import me.ChristopherW.core.entity.Entity;

public class Sphere extends Entity {
    public Sphere(String name, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(name, Assets.sphere, position, rotation, scale);
    }
    public Sphere(Vector3f position, Vector3f rotation, Vector3f scale) {
        super("New Sphere", Assets.sphere, position, rotation, scale);
    }
    public Sphere(Vector3f position) {
        super("New Sphere", Assets.sphere, position, new Vector3f(0,0,0), new Vector3f(1,1,1));
    }
    public Sphere(float radius) {
        super("New Sphere", Assets.sphere, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(2*radius,2*radius,2*radius));
    }
    public Sphere(Vector3f position, float radius){
        super("New Sphere", Assets.sphere, position, new Vector3f(0,0,0), new Vector3f(2*radius,2*radius,2*radius));
    }
}
