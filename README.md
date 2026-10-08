# Tremu Retro Call
Android 8-bit gri monochrome telefon + kişiler + SMS uygulaması.

## Kurulum
- Android 8.0+ (API 26), Java 17, AGP 8.7.3, Gradle 8.10.2, Android SDK 35.
- `gradle :app:assembleDebug` (veya `./gradlew :app:assembleDebug`).
- APK kurulduktan sonra varsayılan Telefon ve SMS rollerini uygulama içinden, Android'in sistem onay ekranları ile seçin.
- Eski uygulamalar **silinmez**. Telefona ait roller kullanıcının rızası ile değişir.
- Geliştirme amaçlı debug APK, üretim için kişisel upload key ile yeniden imzalanmalıdır. CI debug imzası üretim/kişisel imza değildir.

## Kapsam ve önemli eksikler
- Arama ekranı, gelen/aktif arama InCallService, rehber, arama kaydı, SMS al/gönder ve konuşma listesi.
- MMS için role uygun WAP_PUSH alıcısı tanımlandı ama tam MMS indirme/okuma/gönderme **henüz yok**. MMS kullanan cihazda uygulamayı günlük varsayılan SMS olarak kullanmadan önce destek tamamlanmalıdır.
- RCS / online durumu / okundu tikleri için sunucu tabanlı hizmet yoktur; çizimler örnektir, gerçek SMS'te bu özellikler kullanılmaz.
- Android/MIUI arkaplan bildirim ve çağrı ekranı izinleri cihaz bazında ayrı yapılandırma gerektirebilir. Fiziksel cihaz testleri yapılmalıdır.
- Hızlı aramada kişiler cihaz sistem rehberinden okunur; bağımsız sunucu veya bulut senkronizasyonu yapılmaz.
