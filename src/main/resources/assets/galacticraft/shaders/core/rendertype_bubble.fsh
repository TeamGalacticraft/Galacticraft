#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec2 ScreenSize;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec4 vertexColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;
in vec3 normal2;
in vec3 viewDir;

out vec4 fragColor;

void main() {
    // - Rim lighting -
    vec3 viewAngle = normalize(-viewDir);

    // The more orthogonal the camera is to the fragment, the stronger the rim light.
    // abs() so that the back faces get treated the same as the front, giving a rim effect.
    float rimStrength = 1 - abs(dot(viewAngle, normal2)); // The more orthogonal, the stronger

    float rimFactor = pow(rimStrength, 6); // higher power = sharper rim light
    vec4 rim = vec4(rimFactor);

    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }
    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    fragColor = color + rim * linear_fog_fade(vertexDistance, FogStart, FogEnd);
}
