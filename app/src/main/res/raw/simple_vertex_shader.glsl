// default float precision is highdp
// attribute - variable that is different for each call (for each vertice)
attribute vec4 a_Position; // vertex position receiver from the renderer

// called once for each vertex
void main()
{
    // OpenGL will use the value stored in gl_Position as the final position of the current vertex
    gl_Position = a_Position;
    gl_PointSize = 10.0;
}