package programs;

//  -------------   2D-Stoss (Curling)  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class Stoss2D
       implements WindowListener, GLEventListener, KeyListener
{

    //  ---------  globale Daten  ---------------------------

    String windowTitle = "Zweidimensionaler Stoss";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = MyShaders.vShader1;                 // Vertex-Shader
    String fShader = MyShaders.fShader0;                 // Fragment-Shader
    int maxVerts = 2048;                                 // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas;                                     // OpenGL Window
    MyGLBase1 mygl;                                      // eigene OpenGL-Basisfunktionen
    Mat4 M = Mat4.ID;                                    // Transf.Matrix
    Mat4 P = Mat4.ID;                                    // Proj. Matrix
    double xleft = -2, xright = 2;                       // ViewingVol
    double ybottom, ytop;
    double znear = -100, zfar = 1000;

    double dt = 0.01;                                     // Zeitschritt
    boolean stopped = false;


    //  -------  Hilfsklasse  -------------
    class Kugel
    {
       double m,r,xm,ym,vx,vy;

       public Kugel(double r, double m,                  // Radius, Masse
                    double xm, double ym,                // Mittelpunktkoord.
                    double vx, double vy)                // Geschwindigkeit
       {  this.xm = xm;
          this.ym = ym;
          this.vx = vx;
          this.vy = vy;
          this.r = r;
          this.m = m;
      }

      public void zeichne(GL3 gl)                        // Kreis zeichnen
      {  int nPkte = 20;
         double phi = 2*Math.PI / (nPkte-1);
         double x,y;
         mygl.rewindBuffer(gl);
         mygl.putVertex((float)xm,(float)ym,0);
         for (int i=0; i < nPkte; i++)
         {  x = xm + r * Math.cos(i*phi);
            y = ym + r * Math.sin(i*phi);
            mygl.putVertex((float)x,(float)y, 0);
         }
         mygl.copyBuffer(gl);
         mygl.drawArrays(gl,GL3.GL_TRIANGLE_FAN);
      }

      public void move(double dt)                        // Bewegungsschritt
      {  xm += vx*dt;
         ym += vy*dt;
      }

    }



    Kugel k1 = new Kugel(0.1, 0.02, 0, 0, 0, 0);
    Kugel k2 = new Kugel(0.05, 0.01, xleft, 0.1, 1, 0);
    


    //  ---------  Methoden  ----------------------------------


    public void zeichneAchsen(GL3 gl, float a, float b)      // Koordinatenachsen zeichnen
    {  mygl.rewindBuffer(gl);
       mygl.putVertex(-a,0,0);
       mygl.putVertex(a,0,0);
       mygl.putVertex(0,-b,0);
       mygl.putVertex(0,b,0);
       mygl.copyBuffer(gl);
       mygl.drawArrays(gl, GL3.GL_LINES);
    }


    public Stoss2D()                                          // Konstruktor
    { createFrame();
    }
    
    public void stoss (Kugel k1, Kugel k2, double stossZahl){
    	double dx = k2.xm-k1.xm;
    	double dy = k2.ym-k1.ym;
    	double distance = Math.sqrt(dx*dx+dy*dy);
    	if (distance > k1.r + k2.r)
    		return;
    	
    	Vec3 v1 = new Vec3(k1.vx, k1.vy, 0);
    	Vec3 v2 = new Vec3(k2.vx, k2.vy, 0);
    	Vec3 n = new Vec3(k2.xm-k1.xm, k2.ym-k1.ym, 0);
    	n = n.normalize();
    	double v1n = v1.dot(n);
    	double v2n = v2.dot(n);
    	Vec3 vv1n = n.scale((float)v1n);
    	Vec3 vv2n = n.scale((float)v2n);
    	Vec3 vv1p = v1.subtract(vv1n);
    	Vec3 vv2p = v2.subtract(vv2n);
    	
    	double [] vv = stoss1D(k1.m,v1n,k2.m,v2n,stossZahl);
    	v1n = vv[0];
    	v2n = vv[1];
    	v1 = (n.scale((float)v1n)).add(vv1p);     // v1 = v1n*n + vv1p
     	v2 = (n.scale((float)v2n)).add(vv2p);
     	k1.vx = v1.x;
     	k1.vy = v1.y;
    	k2.vx = v2.x;
    	k2.vy = v2.y;
    	
    	
    }

    public double [] stoss1D(double m1, double v1, double m2, double v2, double k){
    	double [] vv = {(m1*v1+m2*v2-(v1-v2)*m2*k)/(m1+m2),
    			(m1*v1+m2*v2-(v2-v1)*m1*k)/(m1+m2)
    	};
    	
    	return vv;
    }
    
    //Kugel k1 = new Kugel(0.2,0.02, 0 ,0,0,0);
    
    
    void createFrame()                                       // Fenster erzeugen
    {  Frame f = new Frame(windowTitle);
       f.setSize(windowWidth, windowHeight);
       f.addWindowListener(this);
       GLProfile glp = GLProfile.get(GLProfile.GL3);
       GLCapabilities glCaps = new GLCapabilities(glp);
       canvas = new GLCanvas(glCaps);
       canvas.addGLEventListener(this);
       f.add(canvas);
       f.setVisible(true);
       f.addKeyListener(this);
       canvas.addKeyListener(this);
    };


    //  ----------  OpenGL-Events   ---------------------------

    @Override
    public void init(GLAutoDrawable drawable)                      //  Initialisierung
    {  GL3 gl = drawable.getGL().getGL3();
       System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
       System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
       System.out.println();
       gl.glClearColor(0.2f,0.4f,1.0f,1);                                   // Hintergrundfarbe
       int programId = MyShaders.initShaders(gl,vShader,fShader);  // Compile/Link Shader-Programme
       mygl = new MyGLBase1(gl, programId, maxVerts);              // eigene OpenGL Basis-Funktionen
       
       
       FPSAnimator anim = new FPSAnimator(canvas, 200, true);      // Animations-Thread, 200 Frames/sek
       anim.start();
    }


    @Override
    public void display(GLAutoDrawable drawable)
    { GL3 gl = drawable.getGL().getGL3();
      gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
      M = Mat4.ID;
      mygl.setM(gl, M);
      mygl.setColor(0.6f,0.6f,0.6f);
      zeichneAchsen(gl, 10, 10);

      // ------  Kugel zeichnen -----
      mygl.setColor(1f, 0.9f, 0.2f);
      k1.zeichne(gl);
      k2.zeichne(gl);
      stoss(k1,k2,0.8);
      k1.move(dt);
      k2.move(dt);
    }


    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y,
                        int width, int height)
    {  GL3 gl = drawable.getGL().getGL3();
       // Set the viewport to be the entire window
       gl.glViewport(0, 0, width, height);
       double aspect = (double)height / width;
       ybottom = aspect * xleft;
       ytop = aspect * xright;
       P = Mat4.ortho((float)xleft, (float)xright,
                  (float)ybottom, (float)ytop,
                  (float)znear, (float)zfar);
       mygl.setP(gl, P);
   }


    @Override
    public void dispose(GLAutoDrawable drawable)  { }                  // not needed

    //  -----------  main-Methode  ---------------------------

    public static void main(String[] args)
    { new Stoss2D();
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

    public void keyPressed(KeyEvent e)
    { int key = e.getKeyCode();
    }

    public void keyReleased(KeyEvent e) { }

    public void keyTyped(KeyEvent e)
    {  char key = e.getKeyChar();
       switch ( key )
       {  case 's':
          case 'S': stopped = !stopped;
                    break;
      }
    }

}