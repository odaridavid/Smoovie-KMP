# kotlinx.serialization — keep @Serializable classes and their generated $$serializer
# Critical: typed nav routes in Screen.kt are @Serializable and would be erased without this.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class ** {
    static <1>$Companion Companion;
    static <1>$$serializer INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class **$$serializer { *; }
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all @Serializable types in this app's package — covers Screen.kt routes, DTOs, and UI models.
-keep @kotlinx.serialization.Serializable class dev.odaridavid.smoovie.** { *; }

# Ktor — relies on reflection in places; logging can pull in optional deps that R8 can't resolve.
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }

# OkHttp / Okio (Ktor OkHttp engine on Android)
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Room — entities, DAOs, and the generated Impl classes.
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Coil 3
-dontwarn coil3.**
-keep class coil3.** { *; }

# Koin — reflective lookup of factories/singles.
-dontwarn org.koin.**
-keep class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# Compose Multiplatform / Jetpack Compose runtime
-dontwarn androidx.compose.**
-keep class androidx.compose.runtime.** { *; }

# Kotlin metadata — needed for reflection-using libs above.
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
-keepattributes Signature, Exceptions, EnclosingMethod

# Strip Napier debug logging in release builds.
-assumenosideeffects class io.github.aakira.napier.Napier {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
}
