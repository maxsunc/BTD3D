package me.ChristopherW.core;

import me.ChristopherW.core.entity.Entity;

public interface IShader {
    public void start();
    public void prepare(Entity entity, Camera camera);
}
