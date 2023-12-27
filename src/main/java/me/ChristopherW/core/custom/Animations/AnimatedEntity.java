package me.ChristopherW.core.custom.Animations;

import java.util.Arrays;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.linux.Flock;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;

import java.util.HashMap;

public class AnimatedEntity extends Entity {

    public AnimatedEntity(RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(model, position, rotation, scale);
    }
    

    public AnimatedEntity(String name, RiggedModel model, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(name, model, position, rotation, scale);
    }

    private HashMap<Integer, Float> animationTicks = new HashMap<Integer,Float>();
    public float getTick(int id) {
        if(animationTicks.get(id) == null)
            return 0;
        return animationTicks.get(id);
    }

    public void tick(int id, float amount) {
        animationTicks.put(id, getTick(id) + amount);
    }

    public void reset(int id) {
        animationTicks.put(id, 0.0f);
    }
}