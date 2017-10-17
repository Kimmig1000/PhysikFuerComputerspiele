package programs;
//  -------------   Quader mit Randlinien ------------
//                                                            E.Gutknecht, Juli 2015
import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;

public class Quader
{
    //  ----------------  globale Daten  -------------------------
    MyGLBase1 vb;

    Vec3 e1 = new Vec3(1,0,0);               // Normalenvektoren
    Vec3 e2 = new Vec3(0,1,0);
    Vec3 e3 = new Vec3(0,0,1);
    Vec3 e1n = new Vec3(-1,0,0);             // negative Richtung
    Vec3 e2n = new Vec3(0,-1,0);
    Vec3 e3n = new Vec3(0,0,-1);


    //  ---------------------  Methoden  --------------------------

    public Quader(MyGLBase1 vb)
    {  this.vb = vb;
    }



    public void Viereck(GL3 gl, Vec3 A, Vec3 B, Vec3 C, Vec3 D,
                                Vec3 n)      // Normale
    {  vb.setNormal(n.x, n.y, n.z);
       vb.putVertex(A.x, A.y, A.z);          // Dreieck 1
       vb.putVertex(B.x, B.y, B.z);
       vb.putVertex(C.x, C.y, C.z);
       vb.putVertex(C.x, C.y, C.z);          // Dreieck 2
       vb.putVertex(D.x, D.y, D.z);
       vb.putVertex(A.x, A.y, A.z);
    }


    public void kante(Vec3  a, Vec3 b)
    {  vb.putVertex(a.x, a.y, a.z);
       vb.putVertex(b.x, b.y, b.z);
    }


    public void zeichne(GL3 gl,
                        float a, float b, float c,   // Kantenlaengen
                        boolean gefuellt)
    {  a *= 0.5f;
       b *= 0.5f;
       c *= 0.5f;
       Vec3 A = new Vec3( a,-b, c);           // Bodenpunkte
       Vec3 B = new Vec3( a,-b,-c);
       Vec3 C = new Vec3(-a,-b,-c);
       Vec3 D = new Vec3(-a,-b, c);
       Vec3 E = new Vec3( a, b, c);           // Deckpunkte
       Vec3 F = new Vec3( a, b,-c);
       Vec3 G = new Vec3(-a, b,-c);
       Vec3 H = new Vec3(-a, b, c);
       vb.rewindBuffer(gl);
       int nVertices;
       if ( gefuellt )
       {  Viereck(gl,D,C,B,A,e2n);            // Boden
          Viereck(gl,E,F,G,H,e2);             // Deckflaeche
          Viereck(gl,A,B,F,E,e1);             // Seitenflaechen
          Viereck(gl,B,C,G,F,e3n);
          Viereck(gl,D,H,G,C,e1n);
          Viereck(gl,A,E,H,D,e3);
          nVertices = 36;
          vb.copyBuffer(gl);
          vb.drawArrays(gl,GL3.GL_TRIANGLES);
       }
       else
       {  kante(A,B);                         // Boden
          kante(B,C);
          kante(C,D);
          kante(D,A);
          kante(E,F);                         // Decke
          kante(F,G);
          kante(G,H);
          kante(H,E);
          kante(A,E);                         // Kanten
          kante(B,F);
          kante(C,G);
          kante(D,H);
          nVertices = 24;
          vb.copyBuffer(gl);
          vb.drawArrays(gl,GL3.GL_LINES);
        }
    }

}