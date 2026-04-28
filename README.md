# PalPay USSD Launcher

  تطبيق Android أصيل لإطلاق خدمات USSD المصرفية الفلسطينية بسرعة وسهولة.

  ## الخدمات المدعومة
  - **بنك فلسطين** (`*267#`) — تحويل فوري
  - **جوال باي** (`*110#`) — تحويل فوري  
  - **بال باي** — قريباً

  ## المتطلبات
  - Android 7.0+ (minSdk 24)
  - Android Studio Hedgehog أو أحدث
  - JDK 17

  ## البناء
  ```bash
  ./gradlew assembleDebug
  ```

  ## التقنيات
  - Kotlin + Jetpack Compose + Material 3
  - Room Database + EncryptedSharedPreferences  
  - AccessibilityService للالتقاط التلقائي
  - MVVM Architecture
  