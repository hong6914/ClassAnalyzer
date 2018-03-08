package hong.tools.verify.classanalyzer;

import java.util.List;
import java.io.File;

/**
 * The tool surfs one directory and looks up for all JAR packages and print out to a log file on all the classes within
 * each JAR, and all the declared methods with parameter types.
 * */

public final class Main {

    public static void main(String[] argv) throws IllegalArgumentException {
        File rootDir = null;
        File logFile = null;

        if( argv.length != 4 ) {
            throw new IllegalArgumentException( "Usage: -dir <directory> -log <log file to store all class/method info>" );
        }

        for( int i = 0; i < argv.length; i++ ) {
            if( argv[i].equalsIgnoreCase( "-dir" ) ) {
                rootDir = new File( argv[ ++i ] );
            } else if( argv[i].equalsIgnoreCase( "-log" ) ) {
                logFile = new File( argv[ ++i ] );
            }
        }

        if( ! rootDir.exists() ) {
            throw new IllegalArgumentException( "Directory not exist" );
        } else if( ! rootDir.isDirectory() ) {
            throw new IllegalArgumentException( "It is not a directory" );
        }

        ClassAnalyzer cs = new ClassAnalyzer();
        try {
            List<File> jars = null;
            jars = cs.listJarsFromDir( rootDir, jars );
            if( jars.size() > 0 ) {
                for( File oneJar: jars ) {
                    cs.getClassesFromJar( oneJar.getCanonicalPath(), logFile.getCanonicalPath() );
                }
            }
        } catch( Exception ex ) {
        }
    }
}
