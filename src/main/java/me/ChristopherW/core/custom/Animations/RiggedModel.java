package me.ChristopherW.core.custom.Animations;

import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.entity.Model;


public class RiggedModel extends Model {

    public RiggedMesh getRiggedMesh(String key) {
        return (RiggedMesh) this.getMesh(key);
    }

    public static RiggedModel copy(RiggedModel model) {
        RiggedModel newModel = new RiggedModel();
        newModel.setName(model.getName());
        for(String key : model.getMeshes().keySet()) {
            if(model.getMesh(key) instanceof RiggedMesh)
                newModel.getMeshes().put(key, new RiggedMesh(model.getRiggedMesh(key)));
            else
                newModel.getMeshes().put(key, new Mesh(model.getMesh(key)));
        }
        return newModel;
    }
}
