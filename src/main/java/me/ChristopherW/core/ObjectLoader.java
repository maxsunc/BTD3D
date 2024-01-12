package me.ChristopherW.core;

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
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
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
import me.ChristopherW.core.custom.Animations.Node;
import me.ChristopherW.core.custom.Animations.RiggedMesh;
import me.ChristopherW.core.custom.Animations.RiggedModel;
import me.ChristopherW.core.entity.Material;
import me.ChristopherW.core.entity.Mesh;
import me.ChristopherW.core.entity.Model;
import me.ChristopherW.core.entity.Texture;
import me.ChristopherW.core.utils.GlobalVariables;
import me.ChristopherW.core.utils.Utils;
import me.ChristopherW.process.Game;

import java.util.Arrays;

public class ObjectLoader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

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
    
    public RiggedMesh loadRiggedMesh(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, float[] weights, int[] boneIds, Bone[] bones, Texture texture, String path) {
        // create a new VAO and store its id
        int id = createVAO();

        // store the vertices, texCoords, and normals in VBOs
        storeInticiesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        storeDataInAttribList(2, 3, normals);
        storeDataInAttribList(3, 3, tangents);
        storeDataInAttribList(4, 4, boneIds);
        storeDataInAttribList(5, 4, weights);

        // unbind the VAO
        unbind();

        // create a new model and store the VAO (with its VBOs) in it
        RiggedMesh mesh = new RiggedMesh(id, indices.length, path);
        mesh.setVertices(vertices);
        mesh.setTextureCoords(textureCoords);
        mesh.setNormals(normals);
        mesh.setIndices(indices);
        mesh.setBones(bones);
        mesh.getMaterial().setTexture(texture);
        return mesh;
    }

    public RiggedModel loadRiggedModel(String fileName) {
        AIScene aiScene = aiImportFile(fileName, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights);
        File file = new File(fileName);
        if (!file.exists()) {
            throw new RuntimeException("Model path does not exist [" + fileName + "]");
        }

        if (aiScene == null) {
            throw new RuntimeException("Error loading model [fileName: " + fileName + "]");
        }
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            try {
                processMaterial(aiMaterial, materials, "assets/textures/materials");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<Bone> boneList = new ArrayList<>();
        HashMap<String, Mesh> meshes = new HashMap<>();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));

            System.out.println("[ObjectLoader] Loading Mesh: " + aiMesh.mName().dataString() + " (" +fileName+")");

            float[] vertices = processVertices(aiMesh);
            float[] textCoords = processTextCoords(aiMesh);
            float[] normals = processNormals(aiMesh);
            float[] tangents = processTangents(aiMesh, normals);
            int[] indices = processIndices(aiMesh);
            AnimMeshData animMeshData = processBones(aiMesh, boneList);

            // Texture coordinates may not have been populated. We need at least the empty slots
            if (textCoords.length == 0) {
                int numElements = (vertices.length / 3) * 2;
                textCoords = new float[numElements];
            }

            int numberOfBones = aiMesh.mNumBones();
            if(numberOfBones > 0) {
                List<RiggedMesh.Animation> animations = new ArrayList<>();
                int numAnimations = aiScene.mNumAnimations();
                if (numAnimations > 0) {
                    Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
                    Matrix4f globalInverseTransformation = toMatrix(aiScene.mRootNode().mTransformation()).invert();
                    animations = processAnimations(aiScene, boneList, rootNode, globalInverseTransformation);

                    Material material = materials.get(aiMesh.mMaterialIndex());
                    RiggedMesh mesh = loadRiggedMesh(vertices, textCoords, normals, tangents, indices, animMeshData.weights, animMeshData.boneIds, boneList.toArray(new Bone[]{}), material.getTexture(), fileName);
                    mesh.setAnimations(animations);
                    mesh.setName(aiMesh.mName().dataString());
                    String suffix = "";
                    while(meshes.containsKey(aiMesh.mName().dataString() + suffix)) {
                        suffix += "(Clone)";
                    }
                    meshes.put(aiMesh.mName().dataString() + suffix, mesh);
                }
            } else {
                Material material = materials.get(aiMesh.mMaterialIndex());
                Mesh mesh = loadMesh(vertices, textCoords, normals, indices, material.getTexture(), fileName);
                mesh.setName(aiMesh.mName().dataString());

                String suffix = "";
                while(meshes.containsKey(aiMesh.mName().dataString() + suffix)) {
                    suffix += "(Clone)";
                }
                meshes.put(aiMesh.mName().dataString() + suffix, mesh);
            }
        }

        
        RiggedModel model = new RiggedModel();
        model.setMeshes(meshes);
        return model;
    }

    public Model loadModel(String modelPath) {
        // using LWJGL's ASSIMP module, we can extract the vertices, normals, texCoords, and indiies 
        File file = new File(modelPath);
        if (!file.exists()) {
            throw new RuntimeException("Model path does not exist [" + modelPath + "]");
        }

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
                processMaterial(aiMaterial, materials, "assets/textures/materials");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        HashMap<String, Mesh> meshes = new HashMap<>();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            
            System.out.println("[ObjectLoader] Loading Mesh: " + aiMesh.mName().dataString() + " (" +modelPath+")");

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
            mesh.setName(aiMesh.mName().dataString());

            String suffix = "";
            while(meshes.containsKey(aiMesh.mName().dataString() + suffix)) {
                suffix += "(Clone)";
            }
            meshes.put(aiMesh.mName().dataString() + suffix, mesh);
        }

        Model model = new Model();
        model.setMeshes(meshes);
        return model;
    }

    private static List<RiggedMesh.Animation> processAnimations(AIScene aiScene, List<Bone> boneList,
                                                           Node rootNode, Matrix4f globalInverseTransformation) {
        List<RiggedMesh.Animation> animations = new ArrayList<>();

        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
            int maxFrames = calcAnimationMaxFrames(aiAnimation);

            List<RiggedMesh.AnimatedFrame> frames = new ArrayList<>();
            RiggedMesh.Animation animation = new RiggedMesh.Animation(aiAnimation.mName().dataString(), aiAnimation.mDuration(), frames);
            animations.add(animation);

            for (int j = 0; j < maxFrames; j++) {
                Matrix4f[] boneMatrices = new Matrix4f[GlobalVariables.MAX_BONES];
                Arrays.fill(boneMatrices, IDENTITY_MATRIX);
                RiggedMesh.AnimatedFrame animatedFrame = new RiggedMesh.AnimatedFrame(boneMatrices);
                buildFrameMatrices(aiAnimation, boneList, animatedFrame, j, rootNode,
                        rootNode.getNodeTransformation(), globalInverseTransformation);
                frames.add(animatedFrame);
            }
        }
        return animations;
    }
    
    private static int calcAnimationMaxFrames(AIAnimation aiAnimation) {
        int maxFrames = 0;
        int numNodeAnims = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i = 0; i < numNodeAnims; i++) {
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            int numFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()),
                    aiNodeAnim.mNumRotationKeys());
            maxFrames = Math.max(maxFrames, numFrames);
        }

        return maxFrames;
    }

    private static Matrix4f buildNodeTransformationMatrix(AINodeAnim aiNodeAnim, int frame) {
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        AIVectorKey aiVecKey;
        AIVector3D vec;

        Matrix4f nodeTransform = new Matrix4f();
        int numPositions = aiNodeAnim.mNumPositionKeys();
        if (numPositions > 0) {
            aiVecKey = positionKeys.get(Math.min(numPositions - 1, frame));
            vec = aiVecKey.mValue();
            nodeTransform.translate(vec.x(), vec.y(), vec.z());
        }
        int numRotations = aiNodeAnim.mNumRotationKeys();
        if (numRotations > 0) {
            AIQuatKey quatKey = rotationKeys.get(Math.min(numRotations - 1, frame));
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            nodeTransform.rotate(quat);
        }
        int numScalingKeys = aiNodeAnim.mNumScalingKeys();
        if (numScalingKeys > 0) {
            aiVecKey = scalingKeys.get(Math.min(numScalingKeys - 1, frame));
            vec = aiVecKey.mValue();
            nodeTransform.scale(vec.x(), vec.y(), vec.z());
        }

        return nodeTransform;
    }

    private static void buildFrameMatrices(AIAnimation aiAnimation, List<Bone> boneList, RiggedMesh.AnimatedFrame animatedFrame,
                                           int frame, Node node, Matrix4f parentTransformation, Matrix4f globalInverseTransform) {
        String nodeName = node.getName();
        AINodeAnim aiNodeAnim = findAIAnimNode(aiAnimation, nodeName);
        Matrix4f nodeTransform = node.getNodeTransformation();
        if (aiNodeAnim != null) {
            nodeTransform = buildNodeTransformationMatrix(aiNodeAnim, frame);
        }
        Matrix4f nodeGlobalTransform = new Matrix4f(parentTransformation).mul(nodeTransform);

        List<Bone> affectedBones = boneList.stream().filter(b -> b.getName().equals(nodeName)).toList();
        for (Bone bone : affectedBones) {
            Matrix4f boneTransform = new Matrix4f(globalInverseTransform).mul(nodeGlobalTransform).
                    mul(bone.getOffsetMatrix());
            animatedFrame.boneMatrices()[bone.boneId()] = boneTransform;
        }

        for (Node childNode : node.getChildren()) {
            buildFrameMatrices(aiAnimation, boneList, animatedFrame, frame, childNode, nodeGlobalTransform,
                    globalInverseTransform);
        }
    }
    
    private static AINodeAnim findAIAnimNode(AIAnimation aiAnimation, String nodeName) {
        AINodeAnim result = null;
        int numAnimNodes = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i = 0; i < numAnimNodes; i++) {
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            if (nodeName.equals(aiNodeAnim.mNodeName().dataString())) {
                result = aiNodeAnim;
                break;
            }
        }
        return result;
    }

    private static Node buildNodesTree(AINode aiNode, Node parentNode) {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parentNode, toMatrix(aiNode.mTransformation()));

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = buildNodesTree(aiChildNode, node);
            node.addChild(childNode);
        }
        return node;
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

    private static float[] processTangents(AIMesh aiMesh, float[] normals) {

        AIVector3D.Buffer buffer = aiMesh.mTangents();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D aiTangent = buffer.get();
            data[pos++] = aiTangent.x();
            data[pos++] = aiTangent.y();
            data[pos++] = aiTangent.z();
        }

        // Assimp may not calculate tangents with models that do not have texture coordinates. Just create empty values
        if (data.length == 0) {
            data = new float[normals.length];
        }
        return data;
    }

    private static AnimMeshData processBones(AIMesh aiMesh, List<Bone> boneList) {
        List<Integer> boneIds = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        HashMap<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
            boneList.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                VertexWeight vw = new VertexWeight(bone.boneId(), aiWeight.mVertexId(),
                        aiWeight.mWeight());
                List<VertexWeight> vertexWeightList = weightSet.get(vw.vertexId());
                if (vertexWeightList == null) {
                    vertexWeightList = new ArrayList<>();
                    weightSet.put(vw.vertexId(), vertexWeightList);
                }
                vertexWeightList.add(vw);
            }
        }

        int numVertices = aiMesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for (int j = 0; j < Mesh.MAX_WEIGHTS; j++) {
                if (j < size) {
                    VertexWeight vw = vertexWeightList.get(j);
                    weights.add(vw.weight());
                    boneIds.add(vw.boneId());
                } else {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }

        return new AnimMeshData(Utils.listFloatToArray(weights), Utils.listIntToArray(boneIds));
    }

    private void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDir) throws Exception {
        AIString path = AIString.calloc();
        aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String textPath = path.dataString();
        if(textPath.contains("\\")) {
            String[] parts = textPath.split(Pattern.quote(File.separator));
            textPath = parts[parts.length - 1];
        }
        System.out.println("[ObjectLoader] Loading Texture: " + textPath);
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

    public Texture createTextureColor(Color color) {
        return new Texture(loadTextureColor(color));
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

    public record AnimMeshData(float[] weights, int[] boneIds) {
    }

    private record VertexWeight(int boneId, int vertexId, float weight) {
    }
}