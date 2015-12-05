# Android application template

# Remove all the injar/outjar/libraryjar junk, the android ant script takes care of this

-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*
-dontwarn

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keep public class * extends com.typingsolutions.passwordmanager.callbacks.BaseCallback {
    public <init>(android.content.Context, com.typingsolutions.passwordmanager.activities.LoginActivity);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep fragments

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

# Serializables

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

# Native Methods

-keepclasseswithmembernames class * {
    native <methods>;
}

# Android Support Library

-keep class android.** {*;}

# Button methods

-keepclassmembers class * {

public void *ButtonClicked(android.view.View);

}

# Reflection

-keepclassmembers class com.elsinga.sample.proguard.SensorDescriptionFragment {

public void updateFields(com.elsinga.sample.proguard.SensorData);

}

# Remove Logging
-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** w(...);
    public static *** wtf(...);
    public static *** d(...);
    public static *** v(...);
}
