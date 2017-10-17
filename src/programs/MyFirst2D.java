package programs;
//  -------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class MyFirst2D
       implements WindowListener, GLEventListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 800;
    String vShader = MyShaders.vShader1;                 // Vertex-Shader
    String fShader = MyShaders.fShader0;                 // Fragment-Shader
    int maxVerts = 2048;                                 // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas;                                     // OpenGL Window
    MyGLBase1 mygl;                                      // eigene OpenGL-Basisfunktionen
    Mat4 M = Mat4.ID;									 // Transformationsmatrix
    float drehwinkel = 30;
    


    //  ---------  Methoden  ----------------------------------

    public MyFirst2D()                                   // Konstruktor
    { createFrame();
    }


    void createFrame()                                    // Fenster erzeugen
    {  Frame f = new Frame(windowTitle);
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       GLProfile glp = GLProfile.get(GLProfile.GL3);
       GLCapabilities glCaps = new GLCapabilities(glp);
       canvas = new GLCanvas(glCaps);
       canvas.addGLEventListener(this);
       f.add(canvas);
       f.setVisible(true);
    };



    public void zeichneDreieck(GL3 gl, float x1, float y1,
                             float x2, float y2, float x3, float y3)
    {  mygl.rewindBuffer(gl);             // Vertex-Buffer zuruecksetzen
       mygl.putVertex(x1,y1,0);           // Eckpunkte in VertexArray speichern
       mygl.putVertex(x2,y2,0);
       mygl.putVertex(x3,y3,0);
       mygl.copyBuffer(gl);
       mygl.drawArrays(gl,GL3.GL_TRIANGLES);
    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)             //  Initialisierung
    {  GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glClearColor(0,0,1,1);                                 // Hintergrundfarbe
       int programId = MyShaders.initShaders(gl,vShader,fShader);  // Compile/Link Shader-Programme
       mygl = new MyGLBase1(gl, programId, maxVerts);            // OpenGL Basis-Funktionen
       FPSAnimator anim = new FPSAnimator(canvas, 200, true);
       anim.start();
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT);             // Bildschirm loeschen
      mygl.setColor(1,0,0);                            // Farbe der Vertices
      float a = 0.2f;
      M = Mat4.rotate(drehwinkel, 0,0,1);
      drehwinkel += 0.1f;
      mygl.setM(gl, M);
      zeichneDreieck(gl,-a,-a,a,-a,0,a);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
   }

    @Override
    public void dispose(GLAutoDrawable drawable)  { }                  // not needed

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new MyFirst2D();
    }

    //  ---------  Window-Events  --------------------

    public void windowClosing(WindowEvent e)
    {   System.out.println("closing window");
        System.exit(0);
    }
    public void windowActivated(WindowEvent e) {  }
    public void windowClosed(WindowEvent e) {  }
    public void windowDeactivated(WindowEvent e) {  }
    public void windowDeiconified(WindowEvent e) {  }
    public void windowIconified(WindowEvent e) {  }
    public void windowOpened(WindowEvent e) {  }

}