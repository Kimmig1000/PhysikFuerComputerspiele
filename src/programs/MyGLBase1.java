package programs;
//  -------------   OpenGL Basis-Funktionen  -------------------
//                                                              E.Gutknecht, August 2016
import com.jogamp.opengl.*;
import com.jogamp.common.nio.*;
import java.util.*;
import java.nio.*;
import ch.fhnw.util.math.*;                                   // Vektor- und Matrix-Algebra

public class MyGLBase1
{

    //  --------------  Globale Daten  -------------------------------------

    private int maxVerts;                                     // max. Anzahl Vertices im Vertex-Array
    private int nVertices = 0;                                // momentane Anzahl Vertices

    private Mat4 M = Mat4.ID;                                 // ModelView-Matrix
    private Mat4 P = Mat4.ID;                                 // Projektions-Matrix

    private int shadingLevel = 0;                             // Beleuchtungs-Stufe 0=aus, 1=ambient u. diffus
    private float[] lightPosition = {0, 0, 10, 1};            // Lichtquelle
    private float ambient = 0.2f;                             // ambientes Licht
    private float diffuse = 0.8f;                             // diffuse Reflexion

    // ------ Identifiers fuer OpenGL-Objekte und Shader-Variablen  ------

    private int vaoId;                                        //  OpenGL VertexArray Object
    private int vertexBufId;                                  //  OpenGL Vertex Buffer
    private int vPositionId, vColorId, vNormalId;             //  Vertex Attribute S
    private int MId, PId;                                     //  Uniform Shader Variables
    private int shadingLevelId, lightPositionId, ambientId, diffuseId;   // Uniform Shader Variables


    //  --------  Vertex-Array (fuer die Attribute Position, Color, Normal)  ------------

    private FloatBuffer vertexBuf;                            // Vertex-Array
    final int vPositionSize = 4*Float.SIZE/8;                 // Anz. Bytes der x,y,z,w (homogene Koordinaten)
    final int vColorSize = 4*Float.SIZE/8;                    // Anz. Bytes der rgba Werte
    final int vNormalSize = 4*Float.SIZE/8;                   // Anz. Bytes der Normalen
    final int vertexSize = vPositionSize + vColorSize + vNormalSize; // Anz. Bytes eines Vertex
    private int bufSize;                                      // Anzahl Bytes des VertexArrays = maxVerts * vertexSize

    private float[] currentColor = { 1,1,1,1};                // aktuelle Farbe fuer Vertices
    private float[] currentNormal = { 1,0,0,0};               // aktuelle Normale Vertices


    //  ------------- Konstruktor  ---------------------------


    public MyGLBase1(GL3 gl,
                   int programId,                             // Program-Identifier
                   int maxVerts)                              // max. Anzahl Vertices im Vertex-Array
    {  this.maxVerts = maxVerts;
       setupVertexBuffer(programId, gl, maxVerts);            // Vertex-Buffer
        setupMatrices(programId, gl);                          // ModelView- und Projektions-Matrix
       setupLightingParms(programId, gl);                     // Beleuchtung
    };



    //  -------------  Methoden  ---------------------------

     private void setupVertexBuffer(int pgm, GL3 gl, int maxVerts)
     {
       bufSize = maxVerts * vertexSize;
       vertexBuf = Buffers.newDirectFloatBuffer(bufSize);
       // ------  OpenGl-Objekte -----------
       int[] tmp = new int[1];
       gl.glGenVertexArrays(1, tmp, 0);                        // VertexArrayObject
       vaoId = tmp[0];
       gl.glBindVertexArray(vaoId);
       gl.glGenBuffers(1, tmp, 0);                             // VertexBuffer
       vertexBufId = tmp[0];
       gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
       gl.glBufferData(GL3.GL_ARRAY_BUFFER, bufSize,           // Speicher allozieren
                            null, GL3.GL_STATIC_DRAW);

       // ----- get shader variable identifiers  -------------
       vPositionId = gl.glGetAttribLocation(pgm, "vPosition");
       vColorId = gl.glGetAttribLocation(pgm, "vColor");
       vNormalId = gl.glGetAttribLocation(pgm, "vNormal");

       //  ------  enable vertex attributes ---------------
       enableAttribute(gl, vPositionId, "vPosition", 4, GL3.GL_FLOAT, false, vertexSize, 0);
       enableAttribute(gl, vColorId, "vColor", 4, GL3.GL_FLOAT, false, vertexSize, vPositionSize);
       enableAttribute(gl, vNormalId, "vNormal", 4, GL3.GL_FLOAT, false, vertexSize, vPositionSize + vColorSize);
    };



  private void enableAttribute(GL3 gl, int attribId, String attribName,
                              int size, int type, boolean normalized, int stride, int offset  )    // Aktivierung eines Vertex-Attributes
  {  if (attribId >= 0)
     {  gl.glEnableVertexAttribArray(attribId);
        gl.glVertexAttribPointer(attribId, size, type, normalized, stride, offset);
        System.out.println("Attribute " + attribName + " enabled");
     }
     else
        System.out.println("Attribute " + attribName + " not enabled");    // Attribut-Var. im Vertex-Shader nicht definiert
  }



    private void setupMatrices(int pgm, GL3 gl)
    {
       // ----- get shader variable identifiers  -------------
       MId = gl.glGetUniformLocation(pgm, "M");
       PId = gl.glGetUniformLocation(pgm, "P");

       // -----  set uniform variables  -----------------------
       gl.glUniformMatrix4fv(MId, 1, false, Mat4.ID.toArray(), 0);
       gl.glUniformMatrix4fv(PId, 1, false, Mat4.ID.toArray(), 0);
    };


    private void setupLightingParms(int pgm, GL3 gl)
    {  float[] lightPosition = {0,0,10,1};               // Default-Koordinaten der Lichtquelle

       // ----- get shader variable identifiers  -------------
       shadingLevelId = gl.glGetUniformLocation(pgm, "shadingLevel");
       lightPositionId = gl.glGetUniformLocation(pgm, "lightPosition");
       ambientId =  gl.glGetUniformLocation(pgm, "ambient");
       diffuseId =  gl.glGetUniformLocation(pgm, "diffuse");

       // -----  set uniform variables  -----------------------
       gl.glUniform1i(shadingLevelId, shadingLevel);
       gl.glUniform1f(ambientId, ambient);
       gl.glUniform1f(diffuseId, diffuse);
       gl.glUniformMatrix4fv(lightPositionId, 1, false, lightPosition, 0);
    };


    //  ----------  oeffentliche Methoden   -------------


    public void setColor(float r, float g, float b)             // aktuelle Vertexfarbe setzen
    {  currentColor[0] = r;
       currentColor[1] = g;
       currentColor[2] = b;
       currentColor[3] = 1;
    }

    public void setNormal(float x, float y, float z)             // aktuelle Vertexfarbe setzen
    {  currentNormal[0] = x;
       currentNormal[1] = y;
       currentNormal[2] = z;
       currentNormal[3] = 0;
    }


    public void putVertex(float x, float y, float z)            // Vertex-Daten in Buffer speichern
    {  vertexBuf.put(x);
       vertexBuf.put(y);
       vertexBuf.put(z);
       vertexBuf.put(1);
       vertexBuf.put(currentColor);                              // Farbe
       vertexBuf.put(currentNormal);                             // Normale
       nVertices++;
    }

    public void copyBuffer(GL3 gl)                              // Vertex-Array in OpenGL-Buffer kopieren
    {  vertexBuf.rewind();
       if ( nVertices > maxVerts )
         throw new IndexOutOfBoundsException();
       gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufId);
       gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, nVertices*vertexSize, vertexBuf);
    }

    public void rewindBuffer(GL3 gl)                            // Bufferposition zuruecksetzen
    {  vertexBuf.rewind();
       nVertices = 0;
    }

    public void drawArrays(GL3 gl, int figureType)
    {   gl.glDrawArrays(figureType, 0, nVertices);
    }


    public void setM(GL3 gl, Mat4 M)                            // ModelView-Matrix
    {   this.M = M;
        gl.glUniformMatrix4fv(MId, 1, false, M.toArray(), 0);
    }


    public void setP(GL3 gl, Mat4 P)                             // Projektions-Matrix
    {   this.P = P;
        gl.glUniformMatrix4fv(PId, 1, false, P.toArray(), 0);
    }


    public void setShadingLevel(GL3 gl, int level)            // 0: ohne Beleuchtung
    {   gl.glUniform1i(shadingLevelId, level);
    }


    public void setShadingParam(GL3 gl, float ambient, float diffuse)  // Lichtparameter
    {   this.ambient = ambient;
        this.diffuse = diffuse;
        gl.glUniform1f(ambientId, ambient);
        gl.glUniform1f(diffuseId, diffuse);
    }


   public void setLightPosition(GL3 gl, float x, float y, float z)
   { lightPosition = new float[]{ x, y, z };
     Vec4 tmp = new Vec4(x,y,z,1);
      tmp =  M.transform(tmp);                               // ModelView-Transformation
      gl.glUniform4fv(lightPositionId, 1, tmp.toArray(),0);
   }


    //  ---------  Abfrage-Methoden ----------

    public float[] getCurrentColor()
    {  float[] c = { currentColor[0],
                     currentColor[1], currentColor[2] };
       return c;
    }

    public float[] getCurrentNormal()
    {  float[] n = { currentNormal[0],
                     currentNormal[1], currentNormal[2]};
       return n;
    }

    public Mat4 getM()                                         // ModelView-Matrix
    {  return M;
    }

    public Mat4 getP()                                         // Projektions-Matrix
    {  return P;
    }

    public int getShadingLevel()                                         // Projektions-Matrix
    {  return shadingLevel;
    }

    public float getAmbient()                                         // Projektions-Matrix
    {  return ambient;
    }

    public float getDiffuse()                                         // Projektions-Matrix
    {  return diffuse;
    }

    public float[] getLightPosition()
    {  return lightPosition;
    }


    //  ---------  Zeichenmethoden  ------------------------------

    public void drawAxis(GL3 gl, float a, float b, float c)                   // Koordinatenachsen zeichnen
    {  rewindBuffer(gl);
       putVertex(0,0,0);           // Eckpunkte in VertexArray speichern
       putVertex(a,0,0);
       putVertex(0,0,0);
       putVertex(0,b,0);
       putVertex(0,0,0);
       putVertex(0,0,c);
       copyBuffer(gl);
       drawArrays(gl, GL3.GL_LINES);
    }

}