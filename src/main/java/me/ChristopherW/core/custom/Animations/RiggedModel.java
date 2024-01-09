package me.ChristopherW.core.custom.Animations;

import java.util.HashMap;

import me.ChristopherW.core.entity.Model;


public class RiggedModel extends Model {
    public void setRiggedMeshes(HashMap<String, RiggedMesh> riggedMeshes) {
        for(String key : riggedMeshes.keySet()) {
            this.setMesh(key, riggedMeshes.get(key));
        }
    }
}
