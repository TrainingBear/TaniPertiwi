# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep class com.trbear9.plants.CsvHandler {
##    ecocropcsv;
##    perawatan;
#}

-keep class com.trbear9.plants.Data { <fields>; }
-keep class com.trbear9.plants.parameters.** { *; }
-keep class com.trbear9.plants.** { *; }
-keep class com.trbear9.ui.model.** { *; }
-keep class org.apache.commons.csv.** { *; }

# Keep Java Serializable classes and fields for deserialization
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class com.fasterxml.jackson.databind.** { *; }
-keep class com.fasterxml.jackson.core.** { *; }
-keep class com.fasterxml.jackson.annotation.** { *; }

-dontwarn com.fasterxml.jackson.**

# Preserve line numbers and stacktrace information
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# AndroidX Startup Initializers
-keep class androidx.startup.** { *; }
-keep class * implements androidx.startup.Initializer { *; }

# WorkManager
-keep class androidx.work.impl.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# Room Database (used by WorkDatabase)
-keep class * extends androidx.room.RoomDatabase {
    public <init>();
}
-keepnames class * extends androidx.room.RoomDatabase
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

