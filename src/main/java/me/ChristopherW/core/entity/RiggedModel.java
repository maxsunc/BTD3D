package me.ChristopherW.core.entity;

public class RiggedModel extends Model {
    private int[] jointIds;
    private float[] weights;

    public RiggedModel(int id, int vertexCount) {
        super(id, vertexCount);
    }
    

    public int[] getJointIds() {
        return jointIds;
    }

    public void setJointIds(int[] jointIds) {
        this.jointIds = jointIds;
    }
    
    public float[] getWeights() {
        return weights;
    }

    public void setWeights(float[] weights) {
        this.weights = weights;
    }
}
