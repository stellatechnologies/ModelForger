-dontoptimize
#-dontshrink
#-dontobfuscate


-injars       C:\Users\Jason\Projects\CyberModelingTool2\target\CyberDiagramer-2.0-jar-with-dependencies.jar
-outjars      C:\Users\Jason\Projects\CyberModelingTool2\target\ModelForger-2_0.jar
-printmapping outputfile.txt


-libraryjars  C:\Program Files\Java\jdk-21\jmods\java.base.jmod
-libraryjars  C:\Program Files\Java\jdk-21\jmods\java.desktop.jmod
-libraryjars C:\Program Files\Java\jdk-21\jmods\java.sql.jmod
-libraryjars C:\Users\Jason\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar
-libraryjars C:\Users\Jason\.m2\repository\org\json\json\20240303\json-20240303.jar


# Keep your main class and its main method
-keep public class com.adkin.cyberdiagramer.CyberDiagramer {
    public static void main(java.lang.String[]);
}

# Keep classes that are known to be used reflectively
-keep class com.adkin.cyberdiagramer.CyberDiagramer {
     *;
 }

-keep class shapes.* { *; }
-keep class non_shape_objects.* { *; }
-keep class panels.* { *; }

-keep class org.json.** { *; }



# Preserve all annotations
#-keepattributes *Annotation*

# Keep - Applications using Reflection, Enum, etc.
-keepattributes Signature,InnerClasses,EnclosingMethod,Exceptions,*Annotation*,SourceFile,LineNumberTable,StackMapTable


# Optimization options
-optimizationpasses 4
-allowaccessmodification
-dontpreverify
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames

-dontwarn jdk.internal.**
-dontwarn jdk.internal.jrtfs.**
-dontwarn jdk.internal.jimage.**
-dontwarn java.sql.**


-dontpreverify