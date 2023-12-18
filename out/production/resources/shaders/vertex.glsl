#version 430 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;

out vec3 lightVec;
out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 fragPosLight;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lightProjection;
uniform mat3 m3x3InvTrans;

void main() {
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    vec4 normalPos =  vec4(normal, 1.0);
    vec3 lightPos = vec3(-500, 500, 500);
    lightVec = normalize((vec4(lightPos, 1.0)).xyz - worldPos.xyz);
    fragNormal = normalize(m3x3InvTrans * normalPos.xyz);
    fragPos = worldPos.xyz;
    fragTextureCoord = textureCoord;
    gl_Position = projectionMatrix * viewMatrix * worldPos;
}