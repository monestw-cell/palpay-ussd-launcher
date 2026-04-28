# USSD Launcher — دليل بناء APK

## متطلبات البيئة

| الأداة | الإصدار المطلوب | رابط التحميل |
|--------|----------------|-------------|
| Android Studio | Ladybug 2024.2.x أو أحدث | https://developer.android.com/studio |
| JDK | 17 أو أحدث (مضمن مع Android Studio) | — |
| Android SDK | مستوى API 35 (مع دعم minSdk 24) | داخل Android Studio |

---

## الخطوة 1 — استنساخ أو تحميل المشروع

إذا كنت تستخدم git:
```bash
git clone <repository-url>
cd android-ussd-launcher
```

أو قم بتحميل مجلد `android-ussd-launcher` كاملاً من Replit وضعه في أي مكان على جهازك.

---

## الخطوة 2 — فتح المشروع في Android Studio

1. افتح **Android Studio**
2. اختر **File → Open**
3. تصفح إلى مجلد **`android-ussd-launcher`** (المجلد الذي يحتوي على `settings.gradle.kts`)
4. اضغط **OK**
5. انتظر حتى ينتهي Gradle من مزامنة المشروع (قد يستغرق 2-5 دقائق في أول مرة)

> **ملاحظة:** إذا ظهرت رسالة "Gradle files have changed, sync now" — اضغط **Sync Now**

---

## الخطوة 3 — التحقق من SDK المطلوب

1. اذهب إلى **File → Project Structure → SDK Location**
2. تأكد من وجود **Android SDK** مثبتاً
3. اذهب إلى **Tools → SDK Manager**
4. تأكد من تثبيت **Android 15 (API 35)** و **Android 7 (API 24)**

---

## الخطوة 4 — بناء APK (Debug)

### الطريقة الأسهل (من Android Studio):
1. من القائمة العلوية: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. انتظر حتى ينتهي البناء
3. ستظهر رسالة "APK(s) generated successfully" في الأسفل
4. اضغط **locate** للوصول إلى الملف

### مسار ملف APK:
```
android-ussd-launcher/app/build/outputs/apk/debug/app-debug.apk
```

---

## الخطوة 5 — بناء APK من سطر الأوامر (بديل)

```bash
# في مجلد android-ussd-launcher
./gradlew assembleDebug

# على Windows:
gradlew.bat assembleDebug
```

> **ملاحظة:** إذا لم يكن ملف `gradlew` موجوداً، قم بتشغيل هذا الأمر أولاً:
> ```bash
> gradle wrapper --gradle-version 8.7
> ```
> أو افتح المشروع في Android Studio مباشرة — سيُنشئ الملف تلقائياً.

---

## الخطوة 6 — تثبيت APK على الجهاز

### عبر Android Studio:
- صِل جهازك بالحاسوب عبر USB مع تفعيل **USB Debugging**
- اضغط زر **Run** (المثلث الأخضر)

### يدوياً:
1. انسخ `app-debug.apk` إلى هاتفك
2. افتح الملف من مدير الملفات
3. فعّل "تثبيت من مصادر غير معروفة" إذا طُلب منك

---

## إعداد AccessibilityService بعد التثبيت

لتفعيل ميزة الالتقاط التلقائي لاسم المستفيد:

1. افتح التطبيق
2. اذهب إلى **الإعدادات**
3. فعّل مفتاح **"تفعيل الالتقاط التلقائي"**
4. اضغط **"فتح إعدادات إمكانية الوصول"**
5. في إعدادات الجهاز، ابحث عن **"USSD Launcher"**
6. فعّله واقبل الصلاحية

---

## هيكل المشروع

```
android-ussd-launcher/
├── app/
│   ├── build.gradle.kts                     # إعدادات بناء التطبيق
│   └── src/main/
│       ├── AndroidManifest.xml               # إعلان الصلاحيات والخدمات
│       ├── java/com/palpay/ussdlauncher/
│       │   ├── MainActivity.kt               # نقطة دخول التطبيق
│       │   ├── data/
│       │   │   ├── db/                       # Room Database
│       │   │   │   ├── AppDatabase.kt
│       │   │   │   ├── entity/               # جداول قاعدة البيانات
│       │   │   │   └── dao/                  # واجهات الاستعلام
│       │   │   └── prefs/
│       │   │       └── SecurePrefsManager.kt # EncryptedSharedPreferences
│       │   ├── repository/
│       │   │   └── AppRepository.kt          # طبقة البيانات
│       │   ├── service/
│       │   │   └── UssdAccessibilityService.kt # خدمة الالتقاط التلقائي
│       │   ├── ui/
│       │   │   ├── navigation/AppNavigation.kt
│       │   │   ├── screens/                  # شاشات Compose
│       │   │   │   ├── HomeScreen.kt
│       │   │   │   ├── RecipientsScreen.kt
│       │   │   │   ├── SendMoneyScreen.kt
│       │   │   │   ├── HistoryScreen.kt
│       │   │   │   └── SettingsScreen.kt
│       │   │   └── theme/                    # ألوان وخطوط
│       │   └── viewmodel/
│       │       └── MainViewModel.kt
│       └── res/
│           ├── drawable/                     # أيقونات Vector
│           ├── values/                       # نصوص وألوان
│           └── xml/config_service.xml        # إعداد AccessibilityService
├── gradle/
│   ├── libs.versions.toml                   # إصدارات المكتبات
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts                         # إعدادات المشروع الرئيسية
└── settings.gradle.kts
```

---

## استكشاف الأخطاء

### خطأ: "Gradle sync failed"
- تأكد من اتصالك بالإنترنت
- جرب: **File → Invalidate Caches → Invalidate and Restart**

### خطأ: "SDK not found"
- اذهب إلى **Tools → SDK Manager** وثبّت API 35

### خطأ في بناء AccessibilityService
- تأكد من وجود ملف `res/xml/config_service.xml`
- تأكد من إعلان الخدمة في `AndroidManifest.xml`

### التطبيق لا يلتقط ردود USSD
- تأكد من تفعيل الخدمة من **إعدادات إمكانية الوصول**
- بعض أجهزة Samsung وHuawei تحتاج خطوات إضافية لتفعيل الخدمة
- بعض إصدارات Android قد تستخدم حزمة مغايرة للمتصفح بدلاً من `com.android.phone`

---

## ملاحظات تقنية مهمة

- **CALL_PHONE:** التطبيق يستخدم `ACTION_DIAL` وليس `ACTION_CALL` عمداً — لا يطلب صلاحية المكالمات
- **PIN:** يُخزَّن مشفراً بـ AES256-GCM عبر `EncryptedSharedPreferences`
- **قاعدة البيانات:** تُخزَّن محلياً فقط — لا شبكة، تطبيق offline كامل
- **AccessibilityService:** يراقب فقط نوافذ تطبيق الهاتف (`com.android.phone`) ولا يقرأ أي شيء آخر
