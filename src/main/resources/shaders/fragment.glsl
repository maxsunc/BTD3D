#version 410 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;
in vec4 fragPosLightSpace;
in float visibility;

out vec4 fragColor;

struct Material {
    float specular;
    float reflectability;
    int hasTexture;
};

uniform int shadowFiltering;
uniform int showFog;
uniform Material material;
uniform sampler2D textureSampler;
uniform sampler2D shadowMap;
uniform vec4 skyColor; 
uniform vec3 sunPos;

float ShadowCalculation(vec4 fragPosLightSpace)
{
    // perform perspective divide
    vec3 lightCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    
    lightCoords = (lightCoords + 1.0) / 2.0;
    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowMap, lightCoords.xy).r;
    float currentDepth = lightCoords.z;
    // check whether current frag pos is in shadow
    float bias = 0.005;
    float shadow = 0.0;
    if(shadowFiltering == 1) {
        vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
        for(int x = -1; x <= 1; ++x)
        {
            for(int y = -1; y <= 1; ++y)
            {
                float pcfDepth = texture(shadowMap, lightCoords.xy + vec2(x, y) * texelSize).r; 
                shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.1;        
            }
        }
        shadow /= 9.0;
    } else {
        shadow = currentDepth - bias > closestDepth? 1.0 : 0.0; 
    }
    if(shadow == 0.0) {
        shadow = 1.0;
    }
    return shadow;
}

void main() {
    vec3 lightColor = vec3(1,1,1);
    vec3 lightCoords = sunPos;

    float ambientIntensity = 0.75;
    float specularIntensity = material.specular;
    float diffuseIntensity = 1;

    vec4 ambientColor = vec4(lightColor * ambientIntensity, 1.0);
    vec3 lightDirection = normalize(lightCoords - fragPos);

    vec3 normal = normalize(fragNormal);

    float diffuse = max(dot(normal, lightDirection), 0.0);

    vec4 textureColor = vec4(1,1,1,1);
    if(material.hasTexture == 1) {
        textureColor = texture(textureSampler, fragTextureCoord);
        if(textureColor.a < 0.5) {
            discard;
        }
    } 
    

    vec4 diffuseColor = vec4(0,0,0,0);
    vec4 specularColor = vec4(0,0,0,0);

    if(diffuse > 0) {
        diffuseColor = vec4(lightColor, 1.0) * diffuseIntensity * diffuse;
    }

    vec3 camera_direction = normalize(-fragPos);
    vec3 light_reflection = normalize(reflect(lightDirection, normal));
    float specular = dot(camera_direction, light_reflection);
    if(specular > 0) {
        specular = pow(specular, material.reflectability);
        specularColor = vec4(lightColor * specularIntensity * specular, 1.0);
    }

    float shadow = ShadowCalculation(fragPosLightSpace); 
    vec4 result = textureColor * (ambientColor + ((1.0 - shadow) * (diffuseColor + specularColor)));
    fragColor = result;
    if(showFog == 1) {
        fragColor = mix(skyColor, fragColor, visibility);
    }
}