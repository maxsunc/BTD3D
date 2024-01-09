package me.ChristopherW.core.custom;

import org.joml.Vector3f;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Model;

public class Dart extends Entity{
    public Dart(String name, Model model, Vector3f position, Vector3f rotation, Vector3f scale){
        super(name, model, position, rotation, scale);
    }
}
