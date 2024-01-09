package me.ChristopherW.core;

import me.ChristopherW.core.entity.Entity;
import me.ChristopherW.core.entity.Mesh;

public interface IShader {
    public void start();
    public void prepare(Entity entity, Mesh mesh, Camera camera);
}
