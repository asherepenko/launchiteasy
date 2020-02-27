-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes Exceptions
-keepattributes Deprecated
-keepattributes SourceFile
-keepattributes LineNumberTable
-keepattributes EnclosingMethod

-renamesourcefileattribute SourceFile

# JSR 305
-dontwarn javax.annotation.**

# Kotlin
-keep class kotlin.reflect.** {
    *;
}

-keep class kotlin.Metadata {
    *;
}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# Jackson
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}

-keepnames class com.fasterxml.jackson.** {
    *;
}

-keepclassmembers class * {
     @com.fasterxml.jackson.annotation.** *;
}
-keepclassmembers class * extends com.fasterxml.jackson.databind.JsonDeserializer {
    <init>(...);
}

-dontwarn com.fasterxml.jackson.databind.**

# OkHttp
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Fragments
-keepnames class * extends androidx.fragment.app.Fragment
