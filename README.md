# HMS Player

تطبيق IPTV (Xtream Codes) للموبايل وشاشات Android TV.

- Kotlin + Jetpack Compose + Media3 (ExoPlayer)
- أكتر من حساب Xtream (إضافة / تعديل / حذف / تبديل)
- تحكم باللمس أو بالريموت (أسهم + OK + أزرار الميديا) — من غير أرقام
- الموبايل بيفتح بالعرض دايماً
- مشغل: إعادة اتصال تلقائي، استكمال من آخر نقطة، الحلقة/القناة التالية، اختيار صوت/ترجمة، سرعة، حجم الصورة
- أنيميشن تنقل + ظهور متدرّج + تركيز متحرك للريموت

## الإصدارات والتحديثات (Google Play)

(targetSdk = 36 حسب شرط Google Play الحالي، مع AGP 8.9.1 وGradle 8.11.1.)

التحديثات بتوصل عن طريق Google Play فقط (اتشال التحديث من GitHub). زر "التحقق من التحديث" في الإعدادات بيفتح صفحة التطبيق على Google Play.

```
git tag v1.0.1
git push --tags
```

GitHub Actions بيبني **APK** (للتجربة) و**AAB** (لـ Google Play) ويرفعهم كـ Artifacts. رقم النسخة وversionCode بيتحسبوا من الـ tag (v1.2.3 ← versionName 1.2.3 / versionCode 10203). **مفيش نشر تلقائي** على Google Play: نزّل الـ AAB من Actions وارفعه يدويًا في Play Console. لازم كل إصدار جديد يكون رقمه أعلى من السابق.

## سياسة الخصوصية

الملف `PRIVACY_POLICY.md` جاهز. لازم يتنشر على رابط عام (مثلاً GitHub Pages) ويتحط في Play Console.

## بناء بـ Android Studio
افتح المجلد وسيب Gradle يخلّص Sync ← Build > Build APK(s).

## مفتاح التوقيع (سري — مش جوه المشروع)
المفتاح بيتخزن في **GitHub Secrets** مش في الكود. في المستودع: Settings ← Secrets and variables ← Actions ← New repository secret، وضيف 4 أسرار بالأسماء دي والقيم من ملف `GITHUB_SECRETS.txt`:
`KEYSTORE_B64` و `KEYSTORE_PASSWORD` و `KEY_ALIAS` و `KEY_PASSWORD`.
- **كل التحديثات لازم تتوقّع بنفس المفتاح** وإلا أندرويد يرفض التثبيت. احتفظ بنسخة من `hms-release.jks` في مكان آمن ومترفعوش على GitHub.

## تغيير اللوجو
`python3 design/make_logo.py`
