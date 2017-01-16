precision mediump float; // precision for the floating point values
// uniform - constant
// uniform vec4 u_Color; use varying color instead
/* when shaders are linked to a program, each shader's uniform is associated with an id */

varying vec4 v_Color;


// called once for each fragment (mapped pixels)
void main()
{
    // set the final color for the current fragment
    gl_FragColor = v_Color;
}