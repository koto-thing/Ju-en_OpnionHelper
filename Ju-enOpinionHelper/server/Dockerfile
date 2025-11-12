FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# server ディレクトリ全体をコピー
COPY . .

# Gradle Wrapper に実行権限を付与してビルド
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 実行用の軽量イメージ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# ビルド成果物をコピー
COPY --from=build /app/build/libs/opinion-helper-server.jar app.jar

# ポートを公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]
