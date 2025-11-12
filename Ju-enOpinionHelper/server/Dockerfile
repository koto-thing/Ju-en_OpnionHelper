FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build

# ルートの Gradle Wrapper をコピー
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle

# ルートの settings.gradle をコピー (存在する場合)
COPY settings.gradle .

# server のビルドファイルをコピー
COPY server/build.gradle .
COPY server/src src

# アプリケーションをビルド
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 実行用の軽量イメージ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# ビルド成果物をコピー
COPY --from=build /build/build/libs/JuenOpinionHelper.jar app.jar

# ポートを公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]
