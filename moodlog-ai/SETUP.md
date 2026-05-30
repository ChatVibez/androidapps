# Setup & Troubleshooting

## Quick Start (Ubuntu / Linux)

```bash
# 1. Install JDK 17
sudo apt install -y openjdk-17-jdk

# 2. Clone repo
git clone https://github.com/ChatVibez/androidapps.git
cd androidapps/moodlog-ai

# 3. Generate Gradle wrapper jar (one-time, butuh system gradle)
sudo apt install -y gradle
gradle wrapper --gradle-version 8.9

# 4. Set Gemini key
echo "GEMINI_API_KEY=AIzaSy_kunci_kamu" > local.properties

# 5. Make gradlew executable
chmod +x gradlew

# 6. Build
./gradlew assembleDebug
```

## Troubleshooting

### "Could not install Gradle distribution... Read timed out"

**Penyebab:** Network slow / blocked saat download Gradle dari `services.gradle.org`,
ATAU Android Studio nyoba pakai Gradle 9.x yang tidak kompatibel dengan AGP 8.7.

**Fix #1 — Pastikan pakai Gradle 8.9 (sudah di-pin di repo)**

File `gradle/wrapper/gradle-wrapper.properties` di repo ini sudah pin ke Gradle 8.9.
Kalau Studio masih coba 9.x, hapus cache:

```bash
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/wrapper/dists/gradle-9*
# Lalu File > Sync Project with Gradle Files di Android Studio
```

**Fix #2 — Download manual lalu taruh di cache**

```bash
mkdir -p ~/.gradle/wrapper/dists/gradle-8.9-bin
cd /tmp
# Mirror utama
wget https://services.gradle.org/distributions/gradle-8.9-bin.zip
# Atau mirror Asia kalau lambat:
# wget https://mirrors.cloud.tencent.com/gradle/gradle-8.9-bin.zip

# Hitung hash distribution untuk folder cache
HASH=$(echo -n "gradle-8.9-bin.zip" | sha256sum | cut -d' ' -f1)
mkdir -p ~/.gradle/wrapper/dists/gradle-8.9-bin/$HASH
mv gradle-8.9-bin.zip ~/.gradle/wrapper/dists/gradle-8.9-bin/$HASH/
touch ~/.gradle/wrapper/dists/gradle-8.9-bin/$HASH/gradle-8.9-bin.zip.ok
```

**Fix #3 — Pakai sistem Gradle (bypass wrapper download)**

```bash
sudo apt install gradle
gradle assembleDebug
# Atau set GRADLE_OPTS untuk timeout lebih lama
export GRADLE_OPTS="-Dorg.gradle.internal.http.connectionTimeout=180000 \
                   -Dorg.gradle.internal.http.socketTimeout=180000"
```

**Fix #4 — Naikin timeout di network properties**

Tambahkan ke `~/.gradle/gradle.properties` (bukan project-nya):

```properties
systemProp.org.gradle.internal.http.connectionTimeout=180000
systemProp.org.gradle.internal.http.socketTimeout=180000
systemProp.http.connectionTimeout=180000
systemProp.http.socketTimeout=180000
```

### "AGP X requires Gradle Y" mismatch

Project ini pakai **AGP 8.7.3** + **Gradle 8.9**. Jangan auto-update saat Studio nawarin
"Update Gradle".

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

File `.jar` adalah binary yang tidak ada di repo. Generate sekali:

```bash
sudo apt install -y gradle
cd moodlog-ai
gradle wrapper --gradle-version 8.9
# Ini akan membuat gradle/wrapper/gradle-wrapper.jar
```

Setelah itu `./gradlew` akan jalan normal.
