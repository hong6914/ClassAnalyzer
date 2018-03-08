package hong.tools.verify.classanalyzer;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * To look into the JAR packages and dig out info on the classes and their members and methods.
 * Save parsing results to outputFile
 */
public class ClassAnalyzer {

    public List<File> listJarsFromDir( File dir, List<File> jars ) throws java.io.IOException {
        if( jars == null ) {
            jars = new ArrayList<File>();
        }

        if( dir.isDirectory() ) {
            File[] files = dir.listFiles();
            for( int i = 0; i < files.length; i++ ) {
                if( files[i].isDirectory() ) {
                    listJarsFromDir( files[i], jars );
                } else if( files[i].getCanonicalPath().endsWith( ".jar" ) ) {
                    jars.add( files[i] );
                }
            }
        } else {
            jars.add( dir );
        }

        return jars;
    }

    public void getClassesFromJar( String jarFile, String logFile ) throws java.io.IOException {
        if( jarFile != null && jarFile.endsWith( ".jar" ) ) {
            getClassesFromJar( new JarFile( jarFile ), new File( logFile ) );
        }
    }

    public void getClassesFromJar( JarFile jarFile, File logFile ) {
        if( jarFile == null || logFile == null ) {
            throw new RuntimeException( "Invalid parameter" );
        }
        BufferedWriter bw = null;
        PrintWriter pw = null;

        try {
            bw = new BufferedWriter( new FileWriter( logFile, true ) );
            pw = new PrintWriter( bw );
            pw.println( "\r\n\r\n**********************************************************************************\r\n" +
                    "Jar File: " + jarFile.getName() + "\r\n" );

            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while( jarEntries.hasMoreElements() ) {
                JarEntry je = jarEntries.nextElement();
                if( je.getName().endsWith( ".class" ) ) {
                    ClassNode cn = new ClassNode();
                    InputStream isClassFile = jarFile.getInputStream( je );
                    try {
                        ClassReader cr = new ClassReader( isClassFile );
                        cr.accept( cn, 0 );

                        surfClass( cn, pw );
                    } finally {
                        isClassFile.close();
                    }
                }
            }
        } catch( Exception ex ) {
            System.out.println( ex.getMessage() );
        } finally {
            try {
                pw.close();
                bw.close();
            } catch( Exception ex ) {
            }
        }
    }

    public void surfClass( ClassNode classNode, PrintWriter pw ) {
        StringBuilder classDescription = new StringBuilder();
        pw.println( "----------------------------------------------------------------------------------" );
        Type classType = Type.getObjectType( classNode.name );

        if( ( classNode.access & Opcodes.ACC_PUBLIC ) != 0 ) {
            classDescription.append( "public " );
        }
        if( ( classNode.access & Opcodes.ACC_PRIVATE ) != 0 ) {
            classDescription.append( "private " );
        }
        if( ( classNode.access & Opcodes.ACC_PROTECTED ) != 0 ) {
            classDescription.append( "protected " );
        }
        if( ( classNode.access & Opcodes.ACC_ABSTRACT ) != 0 ) {
            classDescription.append( "abstract " );
        }
        if( ( classNode.access & Opcodes.ACC_INTERFACE ) != 0 ) {
            classDescription.append( "interface " );
        }

        classDescription.append( "\r\n\tclass " + classType.getClassName() ).append( "\r\n\r\n" );
        pw.println( classDescription.toString() );

        Iterator methodIterator = classNode.methods.iterator();
        while(methodIterator.hasNext()) {
            MethodNode mn = (MethodNode) methodIterator.next();
            surfMethod( mn, pw );
        }
    }

    public void surfMethod(MethodNode methodNode, PrintWriter pw ) {
        StringBuilder methodDescription = new StringBuilder();
        Type returnType = Type.getReturnType( methodNode.desc );
        Type[] argeumentTypes = Type.getArgumentTypes( methodNode.desc );

        @SuppressWarnings( "unchecked" )
        List<String> thrownInternalClassNames = methodNode.exceptions;

        if( ( methodNode.access & Opcodes.ACC_PUBLIC ) != 0 ) {
            methodDescription.append( "public " );
        }
        if( ( methodNode.access & Opcodes.ACC_PRIVATE ) != 0 ) {
            methodDescription.append( "private " );
        }
        if( ( methodNode.access & Opcodes.ACC_PROTECTED ) != 0 ) {
            methodDescription.append( "protected " );
        }
        if( ( methodNode.access & Opcodes.ACC_STATIC ) != 0 ) {
            methodDescription.append( "static " );
        }
        if( ( methodNode.access & Opcodes.ACC_ABSTRACT ) != 0 ) {
            methodDescription.append( "abstract " );
        }
        if( ( methodNode.access & Opcodes.ACC_SYNCHRONIZED ) != 0 ) {
            methodDescription.append( "synchronized " );
        }

        methodDescription.append( "\t\t" + returnType.getClassName() ).append( " " ).append( methodNode.name ).append( "(" );

        for( int i = 0; i < argeumentTypes.length; i++ ) {
            if( i > 0 ) {
                methodDescription.append( ", " );
            }

            // the first local variable is actually the "this" object.
            //LocalVariableNode lvn = methodNode.localVariables.get(i+1);
            methodDescription.append( argeumentTypes[i].getClassName() );
        }
        methodDescription.append( ")" );

        if( ! thrownInternalClassNames.isEmpty() ) {
            methodDescription.append( "\r\n\t\t\tthrows " );

            for( int i = 0; i < thrownInternalClassNames.size(); i++ ) {
                if( i > 0 ) {
                    methodDescription.append( ", " );
                }

                methodDescription.append( Type.getObjectType( thrownInternalClassNames.get(i) ).getClassName() );
            }
        }

        methodDescription.append( "\r\n" );
        pw.println( methodDescription.toString() );
    }


}
