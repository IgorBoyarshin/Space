#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 normal;

out vec4 theColor;

uniform mat4 camera;
uniform mat4 perspectiveMatrix;
uniform mat4 objectMatrix;

uniform vec3 color;
uniform float applyLighting;

void main()
{
    vec4 obj = objectMatrix * position;
    vec4 cameraPos = camera * obj;
    gl_Position = perspectiveMatrix * cameraPos;

    //float cosNormal = clamp( (dot(normalize(normal), normalize(obj))), 0, 1);
    //vec4 l = vec4(vec3(300.0, 0.0, 0.0), 0.0);
    //float cosNormal = clamp( (dot((normal), normalize(-l))), 0.0, 1.0);

    if (applyLighting == -1.0) {
        vec4 l = vec4(vec3(obj.x, obj.y, obj.z), 0.0);
        float cosNormal = clamp( (dot(normal, normalize(-l))), 0.0, 1.0);
        theColor = vec4(color * cosNormal, 1.0);
    } else {
        theColor = vec4(color * applyLighting, 1.0);
    }

    //theColor = vec4(color, 1.0);

    //theColor = vec4(color, 1.0);
    //theColor = vec4(color * cosNormal, 1.0);
    //theColor = vec4(normal.x, normal.y, normal.z, 1.0);
}