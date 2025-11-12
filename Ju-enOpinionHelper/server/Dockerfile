FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# 必要なツールをインストール
RUN apk add --no-cache curl unzip

# server ディレクトリ全体をコピー
COPY . .

# Gradle Wrapper に実行権限を付与
RUN chmod +x gradlew

# Gradle をキャッシュしてビルド（ネットワークエラー対策）
RUN --mount=type=cache,id=gradle,target=/root/.gradle \
    ./gradlew clean build -x test --no-daemon --stacktrace || \
    (sleep 5 && ./gradlew clean build -x test --no-daemon --stacktrace)

# 実行用の軽量イメージ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# ビルド成果物をコピー
COPY --from=build /app/build/libs/opinion-helper-server.jar app.jar

# ポートを公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]


