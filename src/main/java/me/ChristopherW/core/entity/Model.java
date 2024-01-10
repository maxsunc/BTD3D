package me.ChristopherW.core.entity;

import java.util.HashMap;

public class Model {
    private String name;
    private HashMap<String, Mesh> meshes = new HashMap<>();

    public void setAllMaterials(Material material) {
        for(Mesh mesh : meshes.values()) {
            mesh.setMaterial(material);
        }
    }

    public Mesh setMesh(String key, Mesh mesh) {
        return this.meshes.put(key, mesh);
    }
    public Mesh getMesh(String key) {
        return meshes.get(key);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public HashMap<String, Mesh> getMeshes() {
        return meshes;
    }
    public void setMeshs(HashMap<String, Mesh> meshes) {
        this.meshes = meshes;
    }

    public static Model copy(Model model) {
        Model newModel = new Model();
        newModel.setName(model.getName());
        for(String key : model.getMeshes().keySet()) {
            newModel.getMeshes().put(key, new Mesh(model.getMesh(key)));
        }
        return newModel;
    }
}
