precision mediump float;

// Passed from shader by user.
varying vec3 myNorm;

// Passed form shader by system
uniform sampler2D textureUnit0;
varying vec2 TEX0;
varying vec3 lightVec[2];

// Passed from CPU
uniform vec3 mod1;
uniform float modTol;
uniform vec3 mod1NewColor;

bool isWithin(float check, float x, float tol) {
   if(  (check >= (x - tol)) &&
        (check <=(x + tol)))
        return true;
    return false;
}
void main () {
    vec4 printCol;
    vec4 base = texture2D(textureUnit0, TEX0);



    printCol = vec4(base.xyz, 1);// myAddColor;// * diffuse;// vDiffuse;

    //float distSqr = dot(lightVec[0], lightVec[0]);
	//vec3 lVec = lightVec[0]; //* inversesqrt(distSqr);

   // float diffuse = max(dot(lVec, normalize(myNorm)), 0.0);
   // vec4 vDiffuse = vec4(0.5, 0.5, 0.5, 0.0) * diffuse;

    gl_FragColor = printCol;
}