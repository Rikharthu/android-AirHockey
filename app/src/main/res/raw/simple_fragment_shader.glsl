precision mediump float; // precision for the floating point values
// uniform - constant
uniform vec4 u_Color;
/* when shaders are linked to a program, each shader's uniform is associated with an id */

// called once for each fragment (mapped pixels)
void main()
{
    // set the final color for the current fragment
    gl_FragColor = u_Color;
}