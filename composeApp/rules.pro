-dontwarn org.apache.**
-dontwarn com.sun.**
-dontwarn javax.**
-dontwarn org.**
-keep class kotlinx.coroutines.** {*;}
-keep class org.jetbrains.skia.** {*;}
-keep class org.jetbrains.skiko.** {*;}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keepclasseswithmembers public class com.github.springeye.memosc.MainKt {
    public void main();
}
-keepattributes *Annotation*

-keep class kotlin.Metadata { *; }


-keep class com.github.springeye.memosc.api.** {*;}
-keep class com.github.springeye.memosc.model.** {*;}