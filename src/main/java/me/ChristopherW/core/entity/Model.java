package me.ChristopherW.core.entity;

import java.util.HashMap;

public class Model {
    private String name;
    private HashMap<String, Mesh> meshs = new HashMap<>();


    public Mesh getMesh(String key) {
        return meshs.get(key);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public HashMap<String, Mesh> getMeshs() {
        return meshs;
    }
    public void setMeshs(HashMap<String, Mesh> meshs) {
        this.meshs = meshs;
    }

    
}
