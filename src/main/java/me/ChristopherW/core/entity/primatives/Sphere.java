package me.ChristopherW.core.entity.primatives;

import org.joml.Vector3f;

import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.entity.Entity;

public class Sphere extends Entity {
    public Sphere(String name, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(name, new ObjectLoader().loadModel("assets/models/primatives/sphere.obj"), position, rotation, scale);
    }
    public Sphere(Vector3f position, Vector3f rotation, Vector3f scale) {
        super("New Sphere", new ObjectLoader().loadModel("assets/models/primatives/sphere.obj"), position, rotation, scale);
    }
    public Sphere(Vector3f position) {
        super("New Sphere", new ObjectLoader().loadModel("assets/models/primatives/sphere.obj"), position, new Vector3f(0,0,0), new Vector3f(1,1,1));
    }
    public Sphere(float radius) {
        super("New Sphere", new ObjectLoader().loadModel("assets/models/primatives/sphere.obj"), new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(2*radius,2*radius,2*radius));
    }
}
