#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 normal;

out vec4 theColor;

uniform mat4 camera;
uniform mat4 perspectiveMatrix;
uniform mat4 objectMatrix;

uniform vec3 color;

void main()
{
    vec4 obj = objectMatrix * position;
    vec4 cameraPos = camera * obj;
    gl_Position = perspectiveMatrix * cameraPos;

    //float cosNormal = abs( dot(normalize(normal), normalize(vec3(-obj.x, -obj.y, -obj.z))));
    //float cosNormal = clamp( (dot(normalize(normal), normalize(vec4(-obj.x, -obj.y, -obj.z, -obj.w)))), 0, 1);
    //float cosNormal = clamp( (dot(normalize(normal), normalize(obj))), 0, 1);
    //float cosNormal = clamp( (dot(normalize(normal), normalize(vec3(-position.x, -position.y, -position.z)))), 0, 1);

    theColor = vec4(color, 1.0);
    //theColor = vec4(color * cosNormal, 1.0);
    //theColor = vec4(normal.x, normal.y, normal.z, 1.0);
}