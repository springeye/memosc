-dontwarn
-keep class kotlinx.coroutines.** {*;}
-keep class org.jetbrains.skia.** {*;}
-keep class org.jetbrains.skiko.** {*;}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keep public class MainKt {
    public void main();
}

-keep class api.** {*;}
-keep class model.** {*;}