# Hava Durumu Uygulaması

Bu Android uygulaması, kullanıcılara anlık hava durumu bilgilerini sunar. Uygulama, kullanıcının mevcut konumunu veya seçtiği bir şehir bilgisini kullanarak hava durumu verilerini sağlar. **Java** dilinde yazılmış olan bu uygulama, modern mobil geliştirme prensiplerine uygun olarak tasarlanmıştır ve kullanıcı arayüzü açısından dikkatlice ele alınmıştır. Ayrıca, uygulama hem **açık** hem de **karanlık tema** desteği sunmaktadır. Proje, geriye uyumluluk gözetilerek geliştirilmiş ve Android SDK minimum sürüm 21 (Lollipop) ile çalışabilir.

## İçindekiler
1. [Proje Tanımı](#proje-tanımı)
2. [Özellikler](#özellikler)
3. [Kullanılan Teknolojiler](#kullanılan-teknolojiler)
4. [İzinler](#izinler)
5. [Veritabanı](#veritabanı)
6. [Ekran Görüntüleri](#ekran-görüntüleri)
7. [Kurulum](#kurulum)

---

## Proje Tanımı
Bu uygulama, **OpenWeatherMap API** kullanarak hava durumu verilerini kullanıcıya sunar. Kullanıcı, mevcut konumunu kullanarak veya şehir ismi ile hava durumu verilerine ulaşabilir. Uygulama aynı zamanda haftalık hava durumu tahminlerini de gösterir.

## Özellikler
- Kullanıcının **mevcut konumuna** göre hava durumu bilgileri.
- Şehir ismine göre **arama yapabilme**.
- **Haftalık** hava durumu tahminleri.
- **SQLite** veritabanında, kullanıcıların eklediği şehir isimleri saklanır ve bu şehirler üzerinden hava durumu bilgileri gösterilebilir.
- **Açık/Karanlık Tema** desteği.
- **Konum Bilgisi** izinleri, hava durumu verilerinin doğru bir şekilde gösterilmesi için alınır.

## Kullanılan Teknolojiler
- **Java**: Ana programlama dili olarak.
- **SQLite**: Şehir veritabanı için.
- **OpenWeatherMap API**: Hava durumu verilerinin çekilmesi için.
- **Android SDK 21 (Lollipop)** ve üzerinde çalışma.
- **Geriye Uyumluluk**: Minimum SDK 21 ile çalışabilir, hedeflenen SDK 34'tür.
- **Material Design**: Modern kullanıcı arayüzü ve deneyimi için.

## İzinler
Uygulama, düzgün çalışabilmek için aşağıdaki izinleri talep eder:
- `android.permission.INTERNET`: Hava durumu verilerini almak için internet erişimi gerektirir.
- `android.permission.ACCESS_NETWORK_STATE`: İnternet bağlantısının olup olmadığını kontrol eder.
- `android.permission.ACCESS_COARSE_LOCATION`: Kullanıcının mevcut konumunu almak için gereklidir.
- `android.permission.ACCESS_FINE_LOCATION`: Daha hassas konum verilerini almak için gereklidir.

## Veritabanı
Uygulama, **SQLite** kullanarak şehir bilgilerini saklar. Kullanıcıların eklediği şehirler veritabanında tutulur ve daha sonra hava durumu verileri bu şehirler üzerinden getirilir.

**City Entity**:
| Değişken Adı | Tipi    | Açıklama                                |
| ------------ | ------- | --------------------------------------- |
| id           | int     | Şehrin benzersiz kimliği (Primary Key)   |
| cityName     | String  | Şehrin adı                              |
| latitude     | double  | Şehrin enlem bilgisi                    |
| longitude    | double  | Şehrin boylam bilgisi                   |

**City DAO (Data Access Object)**:
| Fonksiyon Adı       | Tipi    | Açıklama                              |
| ------------------- | ------- | ------------------------------------- |
| insert              | void    | Yeni bir şehir ekleme                 |
| getAllCities        | List    | Tüm şehirleri listeleme               |
| deleteCityByName    | void    | Şehri ismine göre silme               |

## Ekran Görüntüleri
- **Giriş Ekranı**: Kullanıcıdan konum izni talep edilen ekran.
  - ![girişekrani](https://github.com/user-attachments/assets/4036656b-2f82-4296-83d7-5ace18752ae3)
  
- **Ana Sayfa**: Mevcut konum veya seçilen şehir ile hava durumu bilgileri gösterilen ekran.
  - ![ikinci ekranlar](https://github.com/user-attachments/assets/298133e5-6e41-4120-911c-e45e4176f1e8)

- **Haftalık Hava Durumu**: Kullanıcının seçtiği şehir için haftalık hava durumu tahminleri.
  - ![üçüncü ekranlar](https://github.com/user-attachments/assets/efcff496-a0fd-4624-b537-551a828b26a5)
  
- **Ayarlar**: Uygulamanın tema (Açık/Karanlık) ve Metrik(Celcius-Fahrenayt) ayarlarının yapıldığı ekran.
  - ![ayarlar](https://github.com/user-attachments/assets/73c766c8-a286-4b96-91f7-5baae7198475)
    
- **Ayarlar**: Uygulamanın tema (Açık/Karanlık) ayarlarının yapıldığı ekran.
  - ![ayarlar](https://github.com/user-attachments/assets/73c766c8-a286-4b96-91f7-5baae7198475)

## Kurulum
1. **Projeyi klonlayın**:
   ```bash
   git clone 
