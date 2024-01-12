package me.ChristopherW.core.entity;

import org.joml.Vector3f;

import me.ChristopherW.core.IShader;
import me.ChristopherW.core.ShaderManager;
import me.ChristopherW.core.custom.Shaders.DefaultShader;

public class Mesh {
    public static int MAX_WEIGHTS = 4;
    private String name;
    private String path = "generated";
    private int id;
    private int vertexCount;
    private Material material;
    private ShaderManager shader;
    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private int[] indices;

    public void init() {
        try {
            this.shader = new DefaultShader("/shaders/vertex.glsl", "/shaders/fragment.glsl");
            ((IShader) shader).start();
        
        
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public Mesh(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
        init();
    }

    public Mesh(int id, int vertexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
        init();
    }
    public Mesh(int id, int vertexCount, Texture texture, String path) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
        this.path = path;
        init();
    }
    public Mesh(int id, int vertexCount, String path) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
        this.path = path;
        init();
    }

    public Mesh(Mesh mesh, Material material) {
        this.id = mesh.getId();
        this.vertexCount = mesh.getVertexCount();
        this.material = material;
        init();
    }
    public Mesh(Mesh mesh) {
        this.id = mesh.getId();
        this.vertexCount = mesh.getVertexCount();
        this.material = new Material(mesh.getMaterial());
        this.vertices = mesh.vertices;
        this.normals = mesh.normals;
        this.indices = mesh.indices;
        this.textureCoords = mesh.textureCoords;
        this.name = mesh.name;
        init();
    }

    public Mesh(Mesh mesh, Texture texture) {
        this.id = mesh.getId();
        this.vertexCount = mesh.getVertexCount();
        this.material = mesh.getMaterial();
        this.material.setTexture(texture);
        init();
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public float[] getVertices() {
        return vertices;
    }
    public Vector3f[] getVerticesVectors() {
        if (vertices.length % 3 != 0) {
            throw new IllegalArgumentException("Invalid number of floats for vertices");
        }

        int numVertices = vertices.length / 3;
        Vector3f[] vectors = new Vector3f[numVertices];

        for (int i = 0; i < numVertices; i++) {
            int index = i * 3; // Start index for each set of three floats
            float x = vertices[index];
            float y = vertices[index + 1];
            float z = vertices[index + 2];
            vectors[i] = new Vector3f(x, y, z);
        }

        return vectors;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public void setTextureCoords(float[] textureCoords) {
        this.textureCoords = textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setShader(ShaderManager shader) {
        this.shader = shader;
    }
    public ShaderManager getShader() {
        return shader;
    }

    public String getName() {
        if(name == null)
            return "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
