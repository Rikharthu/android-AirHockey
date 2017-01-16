// default float precision is highdp
uniform mat4 u_Matrix; // 4x4 matrix

// attribute - variable that is different for each call (for each vertice)
attribute vec4 a_Position; // vertex position receiver from the renderer
attribute vec4 a_Color;

// varying blends the values given to it and sends these values to fragment shader
varying vec4 v_Color;


// called once for each vertex
void main()
{
    // send data to fragment shader
    v_Color=a_Color;
    // OpenGL will use the value stored in gl_Position as the final position of the current vertex
    gl_Position = u_Matrix*a_Position;
    gl_PointSize = 10.0;
}