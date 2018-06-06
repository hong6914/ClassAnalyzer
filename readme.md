# ClassAnalyzer
An easy Java tool based on ASM to parse the JAR files in directories recursively, and print out all the classes, the fields and member methods for quick search and reversing analysis.
 
## Dependencies
- Gradle 1.12
- ASM 5.2

## Build
*gradlew jar*

## Run
Sample command:
*C:\code\Java\ClassAnalyzer\build\libs>java -cp classanalyzer-0.1-SNAPSHOT.jar hong.tools.verify.classanalyzer/Main -dir `your dir` -log `your log file name`*

The log file stores the package, the embedded classes and their fields and methods in a tree-style list.
There are various reversing tools that can show the details of a Java class file, but very few would do that over directories to find out the relationships among the packages and classes. This tool is simple enough to do the job by quickly searching over the names in the generated log file. By comparing the logs over different versions of the Android Gradle Plugins for example, we could tell roughly what changes Google does, per package or per class.