package me.ChristopherW.core.custom.Animations;

import java.util.HashMap;

import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.entity.Model;


public class RiggedModel extends Model {

    public RiggedMesh getRiggedMesh(String key) {
        return (RiggedMesh) this.getMesh(key);
    }

    public void setRiggedMeshes(HashMap<String, RiggedMesh> riggedMeshes) {
        for(String key : riggedMeshes.keySet()) {
            this.setMesh(key, riggedMeshes.get(key));
        }
    }

    public HashMap<String, RiggedMesh> getRiggedMeshes() {
        HashMap<String, RiggedMesh> m = new HashMap<>();
        for(String key : this.getMeshes().keySet()) {
            RiggedMesh rm = (RiggedMesh) this.getMesh(key);
            m.put(key, rm);
        }
        return m;
    }

    public static RiggedModel copy(RiggedModel model) {
        RiggedModel newModel = new RiggedModel();
        newModel.setName(model.getName());
        for(String key : model.getRiggedMeshes().keySet()) {
            newModel.getMeshes().put(key, new RiggedMesh(model.getRiggedMesh(key)));
        }
        return newModel;
    }
}
