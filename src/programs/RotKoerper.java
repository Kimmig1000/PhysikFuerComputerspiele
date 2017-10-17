package programs;
//  -------------   Rotations-Koerper  (Kugel, Torus, Zylinder)  ------------
//                                                            E.Gutknecht, Juli 2015
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class RotKoerper
{
   //  --------------  globale Daten  -----------------
   MyGLBase1 vb;                                 // OpenGL Kontext fuer PPP


   //  ------------------  Methoden  ------------------

   public RotKoerper(MyGLBase1 vb)
   {  this.vb = vb;
   }



   // ----  n1 x n2 Punkte-Gitternetz einer Rotationsflaeche berechnen  ---------
   //
   //       Die gegebene Kurve in der xy-Ebene wird um die y-Achse gedreht

   private void berechnePunkte(float[] x, float[] y,          // Kurve in xy-Ebene
                               float[] nx, float[] ny,        // Normalen in xy-Ebene
                               float[][] xa, float[][] ya, float[][] za,     // Gitternetz
                               float[][] nxa, float[][] nya, float[][] nza)  // gedrehte Normalen
  {
     int n1 = xa.length;                            // Anzahl Zeilen
     int n2 = xa[0].length;                         // Anzahl Spalten
     float todeg = (float)(180/Math.PI);
     float dtheta = (float)(2*Math.PI / n2);        // Drehwinkel
     float c = (float)Math.cos(dtheta);             // Koeff. der Drehmatrix
     float s = (float)Math.sin(dtheta);

     for (int i=0; i < n1; i++)                     // erste Nord-Sued Linie
     {  xa[i][0] = x[i];
        ya[i][0] = y[i];
        nxa[i][0] = nx[i];
        nya[i][0] = ny[i];
     }
     // ------  alle Vertices der Rotationsflaeche berechnen -----
     int j2;
     for (int j=0; j < n2-1; j++)                     // n2-1 Nord-Sued Linien
       for (int i=0; i < n1; i++)
        {  j2 = j+1;
           xa[i][j2] = c*xa[i][j]+s*za[i][j];         // Drehung um y-Achse
           ya[i][j2] = ya[i][j];
           za[i][j2] = -s*xa[i][j]+c*za[i][j];
           nxa[i][j2] = c*nxa[i][j]+s*nza[i][j];      // gedrehter Normalenvektor
           nya[i][j2] = nya[i][j];
           nza[i][j2] = -s*nxa[i][j]+c*nza[i][j];
        }
 }




   public void zeichneRotFlaeche(GL3 gl,            // Rotationsflaeche (Rotation um y-Achse)
               float[] x, float[] y,                // Kurve in xy-Ebene
               float[] nx, float[] ny,              // Normalenvektoren
               int n2)                              // Anzahl Drehungen um y-Achse
    {

     int n1 = x.length;                             // Anzahl Breitenlinien
     float[][] xa = new float[n1][n2];              // Vertex-Koordinaten
     float[][] ya = new float[n1][n2];
     float[][] za = new float[n1][n2];
     float[][] nxa = new float[n1][n2];             // Normalen
     float[][] nya = new float[n1][n2];
     float[][] nza = new float[n1][n2];

     berechnePunkte(x,y,nx,ny,
                    xa,ya,za,nxa,nya,nza);

     // ------  Streifen zeichnen   ------
     int j2;
     vb.rewindBuffer(gl);
     for (int j=0; j < n2; j++)                     // n2 Streifen von Norden nach Sueden
       for (int i=0; i < n1; i++)
        {  vb.setNormal(nxa[i][j],nya[i][j],nza[i][j]);
           vb.putVertex(xa[i][j],ya[i][j],za[i][j]);
           j2 = (j+1) % n2;
           vb.setNormal(nxa[i][j2],nya[i][j2],nza[i][j2]);
           vb.putVertex(xa[i][j2],ya[i][j2],za[i][j2]);
        }

     vb.copyBuffer(gl);
     int nVerticesStreifen = 2*n1;                  // Anzahl Vertices eines Streifens
     for (int j=0; j < n2; j++)                     // die Streifen muessen einzeln gezeichnet werden
          gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP,j*nVerticesStreifen,nVerticesStreifen);  // Streifen von Norden nach Sueden
  }



   public void zeichneRotGitternetz(GL3 gl,         // Rotationsflaeche (Rotation um y-Achse)
               float[] x, float[] y,                // Kurve in xy-Ebene
               float[] nx, float[] ny,              // Normalenvektoren
               int n2)                              // Anzahl Drehungen um y-Achse
    {
      int n1 = x.length;                            // Anzahl Breitenlinien
      float[][] xa = new float[n1][n2];                    // Vertex-Koordinaten
      float[][] ya = new float[n1][n2];
      float[][] za = new float[n1][n2];
      float[][] nxa = new float[n1][n2];                   // Normalen
      float[][] nya = new float[n1][n2];
      float[][] nza = new float[n1][n2];

      berechnePunkte(x,y,nx,ny,
                    xa,ya,za,nxa,nya,nza);


     vb.rewindBuffer(gl);
     for (int i=0; i < n1; i++)                     // n1 Breitenlinien (Kresie um y-Achse)
       for (int j=0; j < n2; j++)
        {  vb.setNormal(nxa[i][j],nya[i][j],nza[i][j]);
           vb.putVertex(xa[i][j],ya[i][j],za[i][j]);
        }
     vb.copyBuffer(gl);
     int nVerticesOffset = n2;                  // Anzahl Vertices einer Breitenlinie
     for (int i=0; i < n1; i++)                     // die Linien muessen einzeln gezeichnet werden
       gl.glDrawArrays(GL3.GL_LINE_LOOP,i*nVerticesOffset,n2);  // Breitenlinie

     vb.rewindBuffer(gl);
     for (int j=0; j < n2; j++)                     // n2 Laengslinien
       for (int i=0; i < n1; i++)
        {  vb.setNormal(nxa[i][j],nya[i][j],nza[i][j]);
           vb.putVertex(xa[i][j],ya[i][j],za[i][j]);
        }
     vb.copyBuffer(gl);
     nVerticesOffset = n1;                  // Anzahl Vertices einer Laengslinie
     for (int j=0; j < n2; j++)                     // die Linien muessen einzeln gezeichnet werden
       gl.glDrawArrays(GL3.GL_LINE_LOOP,j*nVerticesOffset,n1);  // Laengslinie

   }


  public void zeichneKugel(GL3 gl, float r, int n1, int n2, boolean solid)
  {  float[] x = new float[n1];                     // Halbkreis in xy-Ebene von Nord- zum Suedpol
     float[] y = new float[n1];
     float[] nx = new float[n1];                    // Normalenvektoren
     float[] ny = new float[n1];
     float dphi = (float)(Math.PI / (n1-1)), phi;
     for (int i = 0; i < n1; i++)
     {  phi  = (float)(0.5*Math.PI) - i*dphi;
        x[i] = r*(float)Math.cos(phi);
        y[i] = r*(float)Math.sin(phi);
        nx[i] = x[i];
        ny[i] = y[i];
     }
     if (solid)
        zeichneRotFlaeche(gl,x,y,nx,ny,n2);
     else
        zeichneRotGitternetz(gl,x,y,nx,ny,n2);
   }



   public void zeichneTorus(GL3 gl, float r, float R, int n1, int n2, boolean solid)
   {  int nn1 = n1+1;
      float[] x = new float[nn1];                    // Kreis in xy-Ebene
      float[] y = new float[nn1];
      float[] nx = new float[nn1];                   // Normalenvektoren
      float[] ny = new float[nn1];
      float dphi = 2*(float)(Math.PI / n1), phi;
      for (int i = 0; i <= n1; i++)
      {  phi  =  i*dphi;
         x[i] = r*(float)Math.cos(phi);
         y[i] = r*(float)Math.sin(phi);
         nx[i] = x[i];
         ny[i] = y[i];
         x[i] += R;
      }
      if (solid)
        zeichneRotFlaeche(gl,x,y,nx,ny,n2);
      else
        zeichneRotGitternetz(gl,x,y,nx,ny,n2);
   }


   public void zeichneZylinder(GL3 gl, float r, float s, int n1, int n2, boolean solid)
   {
      float[] x = new float[n1];                     // Mantellinie in xy-Ebene
      float[] y = new float[n1];
      float[] nx = new float[n1];                    // Normalenvektoren
      float[] ny = new float[n1];
      float dy = s / (n1-1);
      for (int i = 0; i < n1; i++)
      {  x[i] = r;
         y[i] = i*dy;
         nx[i] = 1;
         ny[i] = 0;
      }
      if (solid)
        zeichneRotFlaeche(gl,x,y,nx,ny,n2);
      else
        zeichneRotGitternetz(gl,x,y,nx,ny,n2);

      //  ------  Grund-Kreis (y=0) -------
      int nPkte = n2;
      float[] xx = new float[nPkte+1];
      float[] zz = new float[nPkte+1];
      float phi = 2*(float)Math.PI/nPkte;
      for (int i=0; i<=nPkte; i++)
      {  zz[i] =  r*(float)Math.cos(i*phi);
         xx[i] =  r*(float)Math.sin(i*phi);
      }
      vb.rewindBuffer(gl);
      vb.setNormal(0,-1,0);
      vb.putVertex(0,0,0);
      for (int i=0; i<=nPkte; i++)
        vb.putVertex(xx[i],0,zz[i]);
      vb.copyBuffer(gl);
      vb.drawArrays(gl,GL3.GL_TRIANGLE_FAN);

      //  ------  Deck-Kreis  (y=s) -------
      vb.rewindBuffer(gl);
      vb.setNormal(0,1,0);
      vb.putVertex(0,s,0);
      for (int i=0; i<=nPkte; i++)
        vb.putVertex(xx[i],s,zz[i]);
      vb.copyBuffer(gl);
      vb.drawArrays(gl,GL3.GL_TRIANGLE_FAN);
   }

}