package me.ChristopherW.core.entity.primatives;

import org.joml.Vector3f;

import me.ChristopherW.core.custom.Assets;
import me.ChristopherW.core.entity.Entity;

public class Cube extends Entity {
    public Cube(String name, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(name, Assets.cube, position, rotation, scale);
    }
    public Cube(Vector3f position, Vector3f rotation, Vector3f scale) {
        super("New Cube", Assets.cube, position, rotation, scale);
    }
    public Cube(Vector3f position) {
        super("New Cube", Assets.cube, position, new Vector3f(0,0,0), new Vector3f(1,1,1));
    }
    public Cube(float scale) {
        super("New Cube", Assets.cube, new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(scale,scale,scale));
    }
}
