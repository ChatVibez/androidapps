# Setup & Troubleshooting

## ⚠️ JANGAN: `sudo apt install gradle`

Ubuntu's apt repository ships **Gradle 4.4.1 (2012)** — terlalu tua, tidak kompatibel
dengan project ini. Pakai **SDKMAN** untuk Gradle modern.

## Quick Start (Ubuntu / Linux)

### Opsi A — Pakai Android Studio (paling simple)

Sebenarnya **kamu tidak butuh system gradle** kalau pakai Android Studio. Studio punya
bundled gradle dan auto-handle wrapper.

```bash
# 1. Install JDK 17
sudo apt install -y openjdk-17-jdk

# 2. Clone repo
git clone https://github.com/ChatVibez/androidapps.git
cd androidapps/moodlog-ai

# 3. Set Gemini API key
echo "GEMINI_API_KEY=AIzaSy_kunci_kamu" > local.properties

# 4. Buka di Android Studio
# File > Open > pilih moodlog-ai/
# Studio akan offer "Fix Gradle wrapper" → klik Fix
# Tunggu sync selesai → Run
```

### Opsi B — Pakai CLI dengan SDKMAN

```bash
# 1. Install JDK 17
sudo apt install -y openjdk-17-jdk

# 2. Install SDKMAN (one-line, no sudo)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# 3. Install Gradle 8.9 via SDKMAN
sdk install gradle 8.9
gradle --version  # Verify: "Gradle 8.9"

# 4. Clone repo + setup
git clone https://github.com/ChatVibez/androidapps.git
cd androidapps/moodlog-ai
echo "GEMINI_API_KEY=AIzaSy_kunci_kamu" > local.properties

# 5. Generate gradle wrapper jar
gradle wrapper --gradle-version 8.9

# 6. Make gradlew executable + build
chmod +x gradlew
./gradlew assembleDebug

# 7. Install ke device yang konek via USB
./gradlew installDebug
```

## Troubleshooting

### "Could not install Gradle distribution... Read timed out"

**Penyebab #1:** Network slow / blocked saat download Gradle dari `services.gradle.org`.

**Penyebab #2:** Android Studio nyoba pakai Gradle 9.x yang TIDAK kompatibel dengan AGP 8.7.

**Fix #1 — Pastikan pakai Gradle 8.9 (sudah di-pin di repo)**

File `gradle/wrapper/gradle-wrapper.properties` di repo ini sudah pin ke Gradle 8.9.
Kalau Studio masih coba 9.x, hapus cache yang corrupt:

```bash
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/wrapper/dists/gradle-9*
# Lalu: File > Sync Project with Gradle Files di Android Studio
# Atau: File > Invalidate Caches and Restart
```

**Fix #2 — Naikin timeout global**

Buat `~/.gradle/gradle.properties` (BUKAN di project):

```bash
mkdir -p ~/.gradle
cat >> ~/.gradle/gradle.properties << 'EOF'
systemProp.org.gradle.internal.http.connectionTimeout=180000
systemProp.org.gradle.internal.http.socketTimeout=180000
systemProp.http.connectionTimeout=180000
systemProp.http.socketTimeout=180000
EOF
```

**Fix #3 — Pakai system gradle (bypass wrapper)**

```bash
# Sudah punya Gradle 8.9 dari SDKMAN
gradle assembleDebug
# Ini tidak pakai wrapper, langsung pakai system gradle
```

**Fix #4 — Download manual dari mirror Asia**

```bash
cd /tmp
# Mirror Tencent (lebih cepat di Asia)
wget https://mirrors.cloud.tencent.com/gradle/gradle-8.9-bin.zip
# Atau Tsinghua mirror
# wget https://mirrors.tuna.tsinghua.edu.cn/gradle/distributions/gradle-8.9-bin.zip

# Extract dan pakai langsung (skip wrapper)
unzip gradle-8.9-bin.zip -d ~/gradle-8.9
export PATH=$PATH:~/gradle-8.9/gradle-8.9/bin
gradle --version
```

### "AGP X requires Gradle Y" mismatch

Project ini pakai **AGP 8.7.3** + **Gradle 8.9**. Jangan auto-update saat Studio nawarin
"Update Gradle" atau "Update Android Gradle Plugin" — bisa break compat.

### KSP / Hilt fail to generate

```bash
./gradlew clean
./gradlew --stop
./gradlew assembleDebug
```

### Out of memory saat build

Edit `gradle.properties` di project root, naikkan heap:

```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

### `./gradlew: Permission denied`

```bash
chmod +x gradlew
```

### `gradle-wrapper.jar` missing

File `.jar` adalah binary yang tidak ada di repo. Generate sekali pakai system gradle 8.x:

```bash
# Pastikan punya Gradle 8.x dari SDKMAN, BUKAN 4.x dari apt
gradle --version  # Harus 8.9 atau lebih baru
gradle wrapper --gradle-version 8.9
```

Setelah `gradle/wrapper/gradle-wrapper.jar` muncul, `./gradlew` jalan normal.

### Sudah install gradle dari apt, dapat versi 4.4.1

```bash
# Uninstall apt version
sudo apt remove gradle

# Install via SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle 8.9
```
