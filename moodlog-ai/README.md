# MoodLog AI

Aplikasi Android untuk mental health tracking + jurnal yang diberi insight oleh AI.
**Local-first**, privasi pengguna jadi prioritas — data mood/jurnal tersimpan di device, tidak otomatis di-backup ke cloud.

## Fitur MVP

| Status | Fitur |
|--------|-------|
| ✅ | Daily mood check-in (emoji + skor 1-10 + jurnal) |
| ✅ | Penyimpanan lokal Room |
| ✅ | Riwayat + chart 7/30 hari (Canvas chart) |
| ✅ | AI Weekly Insight (Gemini) |
| ✅ | Reminder harian (WorkManager + notifikasi 21:00) |
| ✅ | Bottom navigation (Home / Riwayat / Insight) |
| ⏳ | Subscription premium (Play Billing) |
| ⏳ | Export PDF & cloud backup |

## Tech Stack

- Kotlin + Jetpack Compose (Material 3)
- Hilt (DI), Room (DB), Coroutines + Flow
- Gemini SDK (`com.google.ai.client.generativeai`) untuk insight AI
- WorkManager untuk daily reminder
- Canvas-based chart custom (no extra dependency)

## Project Structure

```
app/src/main/java/com/moodlog/ai/
├── MoodLogApplication.kt        # @HiltAndroidApp
├── MainActivity.kt              # Compose host + reminder scheduling
├── data/
│   ├── local/                   # Room entity, DAO, database
│   ├── repository/              # MoodRepository
│   └── ai/                      # GeminiService (Gemini-1.5-flash)
├── di/                          # Hilt AppModule
├── notification/
│   ├── DailyReminderWorker.kt   # CoroutineWorker that posts reminder
│   ├── NotificationChannels.kt  # Channel registration
│   └── ReminderScheduler.kt     # Periodic 24h scheduling
└── ui/
    ├── theme/                   # Material3 theme
    ├── navigation/              # Bottom nav + NavHost
    ├── home/                    # Mood check-in
    ├── history/                 # Entry list + Canvas mood chart
    └── insights/                # AI weekly insight
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

1. **Onboarding flow** — intro slides + nama panggilan + custom reminder time
2. **Edit/Delete entry** dari History
3. **Heatmap calendar view** (alternatif visualisasi)
4. **Premium gate** — Play Billing untuk AI insight unlimited & export PDF
5. **Export PDF** — buat dibawa ke psikolog
6. **Cloud backup** opsional dengan E2E encryption
7. **Tests** — unit test untuk ViewModel, instrumented test untuk DAO

## Catatan Privasi

- `android:allowBackup="false"` — Google Backup di-disable
- Custom backup rules meng-exclude database & SharedPreferences
- Konten jurnal **tidak boleh** di-log ke logcat
- Sebelum kirim ke Gemini, pertimbangkan opt-in eksplisit dari user
