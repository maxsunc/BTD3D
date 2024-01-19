package me.ChristopherW.core.entity.primatives;

import org.joml.Vector3f;

import me.ChristopherW.core.ObjectLoader;
import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.process.Game;

public class Plane extends Entity {
    public Plane(String name, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(name, Game.plane, position, rotation, scale);
    }
    public Plane(Vector3f position, Vector3f rotation, Vector3f scale) {
        super("New Plane", Game.plane, position, rotation, scale);
    }
    public Plane(Vector3f position) {
        super("New Plane", Game.plane, position, new Vector3f(0,0,0), new Vector3f(1,1,1));
    }
    public Plane(float scale) {
        super("New Plane", Game.plane, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(scale,scale,scale));
    }
}
