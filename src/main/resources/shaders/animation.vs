#version 460 core

#define MAX_BONE 150

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 tangent;
layout (location = 4) in ivec4 bone_id;
layout (location = 5) in vec4 weights;

out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 fragPosLightSpace;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lightProjection;
uniform mat4 lightSpaceMatrix;
uniform mat4 bones[MAX_BONE];

const float density = 0.01;
const float gradient = 10;

void main() {
    vec4 initPos = vec4(0, 0, 0, 0);
    vec4 initNormal = vec4(0, 0, 0, 0);
    vec4 initTangent = vec4(0, 0, 0, 0);

    int count = 0;
    for (int i = 0; i < 4; i++) {
        float weight = weights[i];
        if (weight > 0) {
            count++;
            int boneIndex = bone_id[i];
            vec4 tmpPos = bones[boneIndex] * vec4(position, 1.0);
            initPos += weight * tmpPos;

            vec4 tmpNormal = bones[boneIndex] * vec4(normal, 0.0);
            initNormal += weight * tmpNormal;

            vec4 tmpTangent = bones[boneIndex] * vec4(tangent, 0.0);
            initTangent += weight * tmpTangent;
        }
    }
    if (count == 0) {
        initPos = vec4(position, 1.0);
        initNormal = vec4(normal, 0.0);
        initTangent = vec4(tangent, 0.0);
    }


    //mat4 boneTransformation = bones[uint(bone_id.x)] * normalizedWeight.x + bones[uint(bone_id.y)] * normalizedWeight.y + bones[uint(bone_id.z)] * normalizedWeight.z + bones[uint(bone_id.w)] * normalizedWeight.w;
    //vec4 totalPosition = boneTransformation * vec4(position, 1.0);
    mat4 modelViewMatrix = viewMatrix * transformationMatrix;
    vec4 mvPosition = modelViewMatrix * initPos;

    fragNormal = normalize(modelViewMatrix * initNormal).xyz;
    fragPos = mvPosition.xyz;
    fragPosLightSpace = lightSpaceMatrix * mvPosition;
    fragTextureCoord = textureCoord;
    gl_Position = projectionMatrix * mvPosition;

    float distance = length(mvPosition.xyz);
    visibility = exp(-pow((distance*density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}