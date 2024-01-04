package me.ChristopherW.core;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHININESS;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SPECULAR_FACTOR;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiGetMaterialFloatArray;
import static org.lwjgl.assimp.Assimp.aiGetMaterialProperty;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_LimitBoneWeights;
import static org.lwjgl.assimp.Assimp.aiProcess_PreTransformVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;
import static org.lwjgl.assimp.Assimp.aiTextureType_SHININESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_SPECULAR;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMaterialProperty;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import com.jme3.bullet.collision.shapes.infos.IndexedMesh;

import me.ChristopherW.core.custom.Animations.Bone;
import me.ChristopherW.core.custom.Animations.RiggedMesh;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.Utils;
import me.ChristopherW.process.Game;

public class ObjectLoader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

     public IndexedMesh loadIndexedMesh(Mesh mesh, Vector3f scale) {
         // create a new array of Vector3s the size of the amount of verticies the model has
         com.jme3.math.Vector3f[] verticiesArr = new com.jme3.math.Vector3f[mesh.getVertices().length / 3];

         // move the data from the array of floats to the array of Vector3s
         for(int i = 0; i < mesh.getVertices().length; i += 3) {
             Vector3f vertex = new Vector3f(mesh.getVertices()[i], mesh.getVertices()[i + 1], mesh.getVertices()[i + 2]);
             verticiesArr[i / 3] = new com.jme3.math.Vector3f(vertex.x * scale.x, vertex.y * scale.y, vertex.z * scale.z);
         }

         // pass the data into an IndexedMesh for the physics engine to register
         return new IndexedMesh(verticiesArr, mesh.getIndices());
     }

    public int loadMesh(float[] vertices, float[] textureCoords) {
        // create a new VAO and store it's id
        int id = createVAO();

        // store the vertices and texCoords in its VBOs
        storeDataInAttribList(0, 2, vertices);
        storeDataInAttribList(1, 2, textureCoords);

        // unbind the VAO
        unbind();
        return id;
    }
    public Mesh loadMesh(float[] vertices, float[] textureCoords, float[] normals, int[] indices, Texture texture, String path) {
        // create a new VAO and store its id
        int id = createVAO();

        // store the vertices, texCoords, and normals in VBOs
        storeInticiesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        storeDataInAttribList(2, 3, normals);

        // unbind the VAO
        unbind();

        // create a new model and store the VAO (with its VBOs) in it
        Mesh mesh = new Mesh(id, indices.length, path);
        mesh.setVertices(vertices);
        mesh.setTextureCoords(textureCoords);
        mesh.setNormals(normals);
        mesh.setIndices(indices);
        mesh.getMaterial().setTexture(texture);
        return mesh;
    }

    public RiggedMesh loadRiggedModel(String fileName, Texture texture) {
        AIScene scene = Assimp.aiImportFile(fileName,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_GenSmoothNormals |
                        Assimp.aiProcess_FlipUVs |
                        Assimp.aiProcess_CalcTangentSpace |
                        Assimp.aiProcess_JoinIdenticalVertices
        );

        assert scene != null;
        assert scene.mNumMeshes() == 1;
        assert scene.mNumAnimations() > 0;
        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        /*
            position    3
            tex         2
            normal      3
            tangent     3
            bone_id     3
            weights     3
                        17
        */
        final int vertexSize = 17;
        final int floatSize = 4;

        float[] vertices = new float[mesh.mNumVertices() * vertexSize];

        int i = 0;
        for (int v = 0; v < mesh.mNumVertices(); v++)
        {
            AIVector3D position = mesh.mVertices().get(v);
            AIVector3D tex = mesh.mTextureCoords(0).get(v);
            AIVector3D normal = mesh.mNormals().get(v);
            AIVector3D tangent = mesh.mTangents().get(v);

            vertices[i++] = position.x();
            vertices[i++] = position.y();
            vertices[i++] = position.z();

            vertices[i++] = tex.x();
            vertices[i++] = tex.y();

            vertices[i++] = normal.x();
            vertices[i++] = normal.y();
            vertices[i++] = normal.z();

            vertices[i++] = tangent.x();
            vertices[i++] = tangent.y();
            vertices[i++] = tangent.z();

            i += 6;
        }

        int[] indices = new int[mesh.mNumFaces() * 3];

        i = 0;
        for (int f = 0; f < mesh.mNumFaces(); f++)
        {
            AIFace face = mesh.mFaces().get(f);

            indices[i++] = (face.mIndices().get(0));
            indices[i++] = (face.mIndices().get(1));
            indices[i++] = (face.mIndices().get(2));
        }

        final int offset = 11;

        for (int b = 0; b < mesh.mNumBones(); b++)
        {
            AIBone bone = AIBone.create(mesh.mBones().get(b));

            for (int w = 0; w < bone.mNumWeights(); w++)
            {
                AIVertexWeight vw = bone.mWeights().get(w);

                int access = vw.mVertexId() * vertexSize + offset;

                for (int j = 0; j < 3; j++)
                {
                    if (vertices[access] == 0 && vertices[access + 3] == 0)
                    {
                        vertices[access] = b;
                        vertices[access + 3] = vw.mWeight();
                        break;
                    } else
                    {
                        access++;
                    }
                }
            }
        }

        Bone[] bones = new Bone[mesh.mNumBones()];

        for (int b = 0; b < mesh.mNumBones(); b++)
        {
            AIBone bone = AIBone.create(mesh.mBones().get(b));
            bones[b] = new Bone(bone.mName().dataString(), Utils.convertMatrix(bone.mOffsetMatrix()));
        }

        AIAnimation[] animations = new AIAnimation[scene.mNumAnimations()];
        for (int a = 0; a < animations.length; a++)
            animations[a] = AIAnimation.create(scene.mAnimations().get(a));

        
        int vao = createVAO();

        int vbo = GL30.glGenBuffers();
        vbos.add(vbo);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        GL30.glEnableVertexAttribArray(4);
        GL30.glEnableVertexAttribArray(5);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, vertexSize * floatSize, 0);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, vertexSize * floatSize, 12);
        GL30.glVertexAttribPointer(2, 3, GL30.GL_FLOAT, false, vertexSize * floatSize, 20);
        GL30.glVertexAttribPointer(3, 3, GL30.GL_FLOAT, false, vertexSize * floatSize, 32);
        GL30.glVertexAttribPointer(4, 3, GL30.GL_FLOAT, false, vertexSize * floatSize, 44);
        GL30.glVertexAttribPointer(5, 3, GL30.GL_FLOAT, false, vertexSize * floatSize, 56);

        int ibo = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);

        GL30.glBindVertexArray(0);

        RiggedMesh model = new RiggedMesh(vao, indices.length, texture);
        model.setBones(bones);
        model.setAnimations(animations);
        model.setRoot(scene.mRootNode());

        return model;
    }

    public Model loadModel(String modelPath) {
        // using LWJGL's ASSIMP module, we can extract the vertices, normals, texCoords, and indiies 
        File file = new File(modelPath);
        if (!file.exists()) {
            throw new RuntimeException("Model path does not exist [" + modelPath + "]");
        }
        String modelDir = file.getParent();

        AIScene aiScene = aiImportFile(modelPath, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_PreTransformVertices);
        if (aiScene == null) {
            throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");
        }
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            try {
                processMaterial(aiMaterial, materials, "assets/textures/materials/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        HashMap<String, Mesh> meshes = new HashMap<>();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            float[] vertices = processVertices(aiMesh);
            float[] normals = processNormals(aiMesh);
            float[] textCoords = processTextCoords(aiMesh);
            int[] indices = processIndices(aiMesh);

            // Texture coordinates may not have been populated. We need at least the empty slots
            if (textCoords.length == 0) {
                int numElements = (vertices.length / 3) * 2;
                textCoords = new float[numElements];
            }

            Material material = materials.get(aiMesh.mMaterialIndex());
            Mesh mesh = loadMesh(vertices, textCoords, normals, indices, material.getTexture(), modelPath);
            meshes.put(aiMesh.mName().dataString(), mesh);
        }

        Model model = new Model();
        model.setMeshs(meshes);
        return model;
    }
    private static float[] processNormals(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mNormals();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D normal = buffer.get();
            data[pos++] = normal.x();
            data[pos++] = normal.y();
            data[pos++] = normal.z();
        }
        return data;
    }

    private void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDir) throws Exception {
        AIString path = AIString.calloc();
        aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String textPath = path.dataString();
        Texture texture = null;
        if (textPath != null && textPath.length() > 0) {
            texture = this.createTexture(texturesDir + "/" + textPath);
        }

        Material material = new Material(texture);
        materials.add(material);
    }

    private static int[] processIndices(AIMesh aiMesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    private static float[] processTextCoords(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();
        }
        return data;
    }
    private static float[] processVertices(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D vertice = buffer.get();
            data[pos++] = vertice.x();
            data[pos++] = vertice.y();
            data[pos++] = vertice.z();
        }
        return data;
    }

    public int loadTextureColor(Color color) {
        // create a new solid color image of the color provided
        int width = 16, height = 16;
        try {
            BufferedImage img = ImageIO.read(new File("assets/textures/DefaultTexture.png"));
            // get the Graphics context from the BufferedImage
            Graphics2D g = img.createGraphics();

            g.setColor(color);
            g.fillRect(0, 0, width, height);

            // dispose the graphics context that was created
            ImageIO.write(img, "png", new File("assets/textures/temp.png"));
            g.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load the BufferedImage into a readable texture for GLFW
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load("assets/textures/temp.png", w, h, c, 4);
            if(buffer == null)
                throw new Exception("Texture file" + "assets/textures/temp.png" + " not loaded. " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        return id;
    }

    public GLFWImage loadtextureBuffer(String iconPath) {
        // load an icon texture from a file using STBI for GLFW
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(iconPath, w, h, c, 4);
            if(buffer == null)
                throw new Exception("Texture file " + iconPath + " not loaded. " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        GLFWImage image = GLFWImage.malloc();
        image.set(width, height, buffer);
        return image;
    }
    public int loadTexture(String file) throws Exception {
        // load a texture from a file using STBI for GLFW
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(file, w, h, c, 4);
            if(buffer == null)
                throw new Exception("Texture file" + file + " not loaded. " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    private int createVAO() {
        // generate a VAO
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }
    private void storeInticiesBuffer(int[] data) {
        // store data into the VBO of the bound VAO
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }
    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data) {
        // store data into the VBO of the bound VAO
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
    private void storeDataInAttribList(int attribNo, int vertexCount, int[] data) {
        // store data into the VBO of the bound VAO
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL30.glVertexAttribIPointer(attribNo, vertexCount, GL11.GL_INT, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbind() {
        // unbind VAO
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        // delete all textures, VAOs, and VBOs
        for(int vao: vaos)
            GL30.glDeleteVertexArrays(vao);
        for(int vbo: vbos)
            GL30.glDeleteBuffers(vbo);
        for(int texture: textures)
            GL11.glDeleteTextures(texture);
    }

    private static Matrix4f toMatrix(AIMatrix4x4 aiMatrix4x4) {
        Matrix4f result = new Matrix4f();
        result.m00(aiMatrix4x4.a1());
        result.m10(aiMatrix4x4.a2());
        result.m20(aiMatrix4x4.a3());
        result.m30(aiMatrix4x4.a4());
        result.m01(aiMatrix4x4.b1());
        result.m11(aiMatrix4x4.b2());
        result.m21(aiMatrix4x4.b3());
        result.m31(aiMatrix4x4.b4());
        result.m02(aiMatrix4x4.c1());
        result.m12(aiMatrix4x4.c2());
        result.m22(aiMatrix4x4.c3());
        result.m32(aiMatrix4x4.c4());
        result.m03(aiMatrix4x4.d1());
        result.m13(aiMatrix4x4.d2());
        result.m23(aiMatrix4x4.d3());
        result.m33(aiMatrix4x4.d4());

        return result;
    }

    public Texture createTexture(String file) {
        try {
            return new Texture(loadTexture(file));
        } catch (Exception e) {
        }
        return null;
    }
}