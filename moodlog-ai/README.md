# MoodLog AI

Aplikasi Android untuk mental health tracking + jurnal yang diberi insight oleh AI.
**Local-first**, privasi pengguna jadi prioritas — data mood/jurnal tersimpan di device, tidak otomatis di-backup ke cloud.

## Fitur MVP

| Status | Fitur |
|--------|-------|
| ✅ | Daily mood check-in (emoji + skor 1-10 + jurnal) |
| ✅ | Penyimpanan lokal Room |
| ⏳ | Riwayat & chart 7/30 hari |
| ⏳ | AI Weekly Insight (Gemini) |
| ⏳ | Reminder harian (WorkManager) |
| ⏳ | Subscription premium (Play Billing) |

## Tech Stack

- Kotlin + Jetpack Compose (Material 3)
- Hilt (DI), Room (DB), Coroutines + Flow
- Gemini SDK (`com.google.ai.client.generativeai`) untuk insight AI
- Vico untuk chart (akan dipakai di History screen)

## Project Structure

```
app/src/main/java/com/moodlog/ai/
├── MoodLogApplication.kt        # @HiltAndroidApp
├── MainActivity.kt              # Compose host
├── data/
│   ├── local/                   # Room entity, DAO, database
│   ├── repository/              # MoodRepository
│   └── ai/                      # GeminiService (insight)
├── di/                          # Hilt AppModule
└── ui/
    ├── theme/                   # Material3 theme
    ├── navigation/              # NavHost
    └── home/                    # HomeScreen + ViewModel (mood check-in)
```

## Setup

### 1. Prasyarat
- Android Studio Ladybug+ (AGP 8.7+)
- JDK 17
- Android SDK 35

### 2. Konfigurasi Gemini API Key
Daftar di [Google AI Studio](https://aistudio.google.com/) untuk mendapatkan API key gratis.
Tambahkan ke `~/.gradle/gradle.properties` atau `local.properties` (jangan commit):

```properties
GEMINI_API_KEY=your_api_key_here
```

App tetap jalan tanpa key — fitur AI akan return error message saja.

### 3. Build & Run
```bash
./gradlew :app:installDebug
```

## Roadmap Selanjutnya

1. **History screen** — daftar entry + chart Vico (line/heatmap)
2. **Insights screen** — panggil `GeminiService.weeklyInsight()` & tampilkan
3. **Daily reminder** — WorkManager + notifikasi
4. **Onboarding & permissions** — request POST_NOTIFICATIONS (API 33+)
5. **Premium gate** — Play Billing untuk fitur AI/export PDF
6. **Export & cloud backup** — opsional, dengan E2E encryption

## Catatan Privasi

- `android:allowBackup="false"` — Google Backup di-disable
- Custom backup rules meng-exclude database & SharedPreferences
- Konten jurnal **tidak boleh** di-log ke logcat
- Sebelum kirim ke Gemini, pertimbangkan opt-in eksplisit dari user
