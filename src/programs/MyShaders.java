package programs;
//  ------------  Vertex- und Fragment-Shaders  -----------------------------------------
import com.jogamp.opengl.*;

public class MyShaders
{

    /*  -----------  Vertex-Shader (Pass-thru Shader) ---------- */
    public static String vShader0 =
    "   #version 130                  /* Shader Language Version */   \n" +
    "   in vec4 vPosition, vColor;    /* Vertex-Attribute */          \n" +
    "   out vec4 fColor;              /* Fragment-Farbe */            \n" +
    "   void main()                                                   \n" +
    "   {  gl_Position = vPosition;                                   \n" +
    "      fColor = vColor;                                           \n" +
    "   }";


    /* -----------  Fragment-Shader (Pass-thru Shader) ---------  */
    public static String fShader0 =
    "    #version 130                   \n" +
    "    in  vec4 fColor;               \n" +
    "    out vec4 fragColor;            \n" +
    "    void main()                    \n" +
    "    {  fragColor = fColor;         \n" +
    "    }";


    /* -----------  Vertex-Shader mit Vertex-Transformationen  */
    public static String vShader1 =
    "   #version 130                                                              \n" +
    "   uniform mat4 M, P;                       /* Transformations-Matrizen */   \n" +
    "   in vec4 vPosition, vColor, vNormal;      /* Vertex-Attribute */           \n" +
    "   out vec4 fColor;                         /* Fragment-Farbe */             \n" +
    "   void main()                                                               \n" +
    "   {  vec4 vertex = M * vPosition;          /* ModelView-Transformation */   \n" +
    "      gl_Position = P * vertex;             /* Projektion */                 \n" +
    "      fColor = vColor;                                                       \n" +
    "   }";


    /* -----------  Vertex-Shader mit Transformations-Matrizen und Beleuchtung  ------  */
    public static String vShader2 =
    "   #version 130                                         /* Shader Language Version */                \n" +
    "   /*  -------- Input/Output Variabeln  ----------- */                                               \n" +
    "                                                                                                     \n" +
    "   uniform mat4 M, P;                                   /* Transformations-Matrizen */               \n" +
    "   uniform vec4 lightPosition;                          /* Position Lichtquelle (im Cam.System) */   \n" +
    "   uniform int shadingLevel;                            /* 0 ohne Beleucht, 1 diffuse Reflexion */   \n" +
    "   uniform float ambient;                               /* ambientes Licht */                        \n" +
    "   uniform float diffuse;                               /* diffuse Reflexion */                      \n" +
    "   in vec4 vPosition, vColor, vNormal;                  /* Vertex-Attribute */                       \n" +
    "   out vec4 fColor;                                     /* Fragment-Farbe */                         \n" +
    "   void main()                                                                                       \n" +
    "   {  vec4 vertex = M * vPosition;                      /* ModelView=Transformation */               \n" +
    "      gl_Position = P * vertex;                         /* Projektion */                             \n" +
    "      fColor = vColor;                                                                               \n" +
    "      float Id;                                         /* Helligkeit diffuse Reflexion */           \n" +
    "      if (shadingLevel >= 1)                                                                         \n" +
    "      { vec3 normal = normalize((M * vNormal).xyz);                                                  \n" +
    "        vec3 toLight = normalize(lightPosition.xyz - vertex.xyz);                                    \n" +
    "        Id = diffuse * dot(toLight, normal);            /* Gesetz von Lambert */                     \n" +
    "        if ( Id < 0 ) Id = 0;                                                                        \n" +
    "        vec3 whiteColor = vec3(1,1,1);                                                               \n" +
    "        vec3 reflectedLight =  (ambient + Id) * vColor.rgb;                                           \n" +
    "        fColor.rgb = min(reflectedLight, whiteColor);                                                \n" +
    "      }                                                                                              \n" +
    "   }";


    public static int initShaders(GL3 gl,
                                   String vShader,   // Vertex-Shader
                                   String fShader)   // Fragment-Shader
    {
       int vShaderId = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
       int fShaderId = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);


       gl.glShaderSource(vShaderId, 1, new String[] { vShader }, null);
       gl.glCompileShader(vShaderId);                                      // Compile Vertex Shader
       System.out.println("VertexShaderLog:");
       System.out.println(getShaderInfoLog(gl, vShaderId));
       System.out.println();


       gl.glShaderSource(fShaderId, 1, new String[] { fShader }, null);
       gl.glCompileShader(fShaderId);                                     // Compile Fragment Shader
       System.out.println("FragmentShaderLog:");
       System.out.println(getShaderInfoLog(gl, fShaderId));
       System.out.println();

       int programId = gl.glCreateProgram();
       gl.glAttachShader(programId, vShaderId);
       gl.glAttachShader(programId, fShaderId);
       gl.glLinkProgram(programId);                                       // Link Program
       gl.glUseProgram(programId);                                        // Activate Programmable Pipeline
       System.out.println("ProgramInfoLog:");
       System.out.println(getProgramInfoLog(gl, programId));
       System.out.println();
       return programId;
    }


    public static String getProgramInfoLog(GL3 gl, int obj)               // Info- and Error-Messages
    {
       int params[] = new int[1];
       gl.glGetProgramiv(obj, GL3.GL_INFO_LOG_LENGTH, params, 0);         // get log-length
       int logLen = params[0];
       if (logLen <= 0)
         return "";
       byte[] bytes = new byte[logLen + 1];
       int[] retLength = new int[1];
       gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);       // get log-data
       String logMessage = new String(bytes);
       int iend = logMessage.indexOf(0);
       if (iend < 0 ) iend = 0;
       return logMessage.substring(0,iend);
    }



    static public String getShaderInfoLog(GL3 gl, int obj)               // Info- and Error-Messages
    {  int params[] = new int[1];
       gl.glGetShaderiv(obj, GL3.GL_INFO_LOG_LENGTH, params, 0);         // get log-length
       int logLen = params[0];
       if (logLen <= 0)
         return "";
       // Get the log
       byte[] bytes = new byte[logLen + 1];
       int[] retLength = new int[1];
       gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);       // get log-data
       String logMessage = new String(bytes);
       int iend = logMessage.indexOf(0);
       if (iend < 0 ) iend = 0;
       return logMessage.substring(0,iend);
    }


}