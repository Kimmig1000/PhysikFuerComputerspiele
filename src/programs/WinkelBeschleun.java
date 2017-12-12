package programs;

//  -------------   Fall-Winkel-Beschleunigung  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;
import com.jogamp.opengl.awt.*;

public class WinkelBeschleun
       implements WindowListener, GLEventListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "WinkelBeschleunigung";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = MyShaders.vShader1;                 // Vertex-Shader
    String fShader = MyShaders.fShader0;                 // Fragment-Shader
    int maxVerts = 2048;                                 // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas;                                     // OpenGL Window
    MyGLBase1 mygl;                                      // OpenGL-Funktionen

    Mat4 M = Mat4.ID;                                    // ModelView-Matrix
    Mat4 P = Mat4.ID;                                    // projektions-Matrix
    float left=-1, right=3, bottom, top, near=-10, far=1000; // ViewingVolume

    final double g = 9.81;                              // Erdbeschleunigung
    double dt = 0.005;                                  // Zeitschritt

    double len = 1.1;                                   // Brett
    double m = 1;
    double I = m*len*len / 3;                           // Traegheitsmom.
    double phi=80*Math.PI/180, omega=0;                 // Anfangswerte fuer Brett

    double[] xBrett = {phi, omega};                     // Zustand Brett

    double x=1.5, y=len*Math.sin(phi);                  // Position Kugel
    double vx=0, vy=0;                                  // Geschwindigkeiten

    class PendelDynamics extends Dynamics
    {
     public double[] f(double[] x)
     { double phi = x[0];
       double omega = x[1];
       double[] y = { omega, -0.5*len*m*g*Math.cos(phi)/I};
       return y;
     }

    }

    PendelDynamics pendel = new PendelDynamics();

    //  ---------  Methoden  ----------------------------------

    public WinkelBeschleun()
    { createFrame();
    }


    void createFrame()
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


    public void zeichneStrecke(GL3 gl, float x1, float y1,
                             float x2, float y2)
    {  mygl.rewindBuffer(gl);
       mygl.putVertex(x1,y1,0);
       mygl.putVertex(x2,y2,0);
       int nVertices = 2;
       mygl.copyBuffer(gl);
       mygl.drawArrays(gl,GL3.GL_LINES);
    }


    public void zeichneKreis(GL3 gl, float r, float xm, float ym, int nPunkte)
    {  mygl.rewindBuffer(gl);
       double phi = 2*Math.PI / nPunkte;
       mygl.putVertex(xm,ym,0);                          // Mittelpunkt
       for (int i=0; i <= nPunkte; i++)
          mygl.putVertex((float)(xm+r*Math.cos(i*phi)),
                          (float)(ym+r*Math.sin(i*phi)),0);
       mygl.copyBuffer(gl);
       mygl.drawArrays(gl,GL3.GL_TRIANGLE_FAN);
    }


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)                         //  Initialisierung
    {  GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glClearColor(0,0,1,1);                                      // Hintergrundfarbe
       int programId = MyShaders.initShaders(gl,vShader,fShader);
       mygl = new MyGLBase1(gl, programId, maxVerts);
       FPSAnimator animator = new FPSAnimator(canvas,40,true);
       animator.start();
   }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT);                            // Bildschirm loeschen
      mygl.setM(gl,M);
      mygl.setColor(1,1,0);                                           // Zeiechenfarbe
      zeichneStrecke(gl,left,0,right,0);                              // Boden
      zeichneKreis(gl,0.02f, (float)x, (float)y, 20);                 // Kugel (freier Fall)
      y += vy*dt;
      vy -= g*dt;
      zeichneStrecke(gl,0,0,                                          // Brett
                     (float)(len*Math.cos(xBrett[0])),
                     (float)(len*Math.sin(xBrett[0])));
      xBrett = pendel.runge(xBrett, dt);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       float aspect = (float)height/width;
       top = aspect * right;
       bottom = aspect * left;
       P = Mat4.ortho(left,right,bottom,top,near,far);
       mygl.setP(gl,P);
     }

    @Override
    public void dispose(GLAutoDrawable drawable)  { }                  // not needed

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new WinkelBeschleun();
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