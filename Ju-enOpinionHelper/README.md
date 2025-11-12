Ju-en Opinion Helper

ゲーム開発における意見共有ツール。チーム間でトピックとオピニオンを共有し、Ju-enリアクションで同意を表現できます。

## 主な機能

- 📝 **トピック管理**: プロジェクトごとにトピックを作成・管理
- 💬 **オピニオン投稿**: 各トピックに意見を投稿
- 👍 **Ju-enカウント**: 同意する意見にリアクション
- 🔒 **ユーザー認証**: データベース管理による複数ユーザー対応
- 🌐 **リアルタイム同期**: Railway でのクラウドデプロイ

## ⚠️ セキュリティ重要事項

### 管理者パスワードの設定（必須）

デプロイ前に **必ず** 環境変数 `ADMIN_PASSWORD` を設定してください:

**Railway の場合:**
1. **Variables** タブを開く
2. `ADMIN_PASSWORD` に**強力なパスワード**を設定（例: `MySecureP@ssw0rd2025!`）

**ローカル環境:**
```bash
export ADMIN_PASSWORD="your-strong-password"
./gradlew :server:bootRun
```

**環境変数が未設定の場合:**
- ランダムパスワードが自動生成されます
- 起動ログに表示されるので**必ず保存**してください

## 認証システム

### 初回起動時

環境変数 `ADMIN_PASSWORD` で設定したパスワードで管理者アカウントが作成されます:

- **ユーザー名**: `admin`
- **パスワード**: 環境変数で設定した値

### 新規ユーザーの追加

```bash
curl -X POST https://your-server-url/api/register \
  -H "Content-Type: application/json" \
  -d '{"username":"your-username","password":"your-password"}'
```

詳細は [AUTHENTICATION_TEST.md](./AUTHENTICATION_TEST.md) を参照してください。

## プロジェクト構成

```
Ju-enOpinionHelper/
├── client/              # Swingデスクトップクライアント
│   ├── src/
│   │   └── main/java/koto_thing/
│   │       ├── Main.java
│   │       ├── MainWindow.java
│   │       ├── HomePanel.java
│   │       ├── ThreadPanel.java
│   │       ├── SettingsPanel.java
│   │       ├── NotificationManager.java
│   │       ├── JuenCheckService.java
│   │       ├── UpdateChecker.java
│   │       ├── AppSettings.java
│   │       ├── AppVersion.java
│   │       ├── Theme.java
│   │       ├── Topic.java (クライアント用モデル)
│   │       └── Opinion.java (クライアント用モデル)
│   └── build.gradle
│
└── server/              # Spring Boot RESTサーバー
    ├── src/
    │   └── main/java/koto_thing/
    │       ├── JuenOpinionHelperApplication.java
    │       ├── Topic.java (JPAエンティティ)
    │       ├── Opinion.java (JPAエンティティ)
    │       ├── TopicRepository.java
    │       ├── OpinionRepository.java
    │       ├── TopicService.java
    │       ├── TopicController.java
    │       └── config/
    │           └── WebConfig.java
    └── build.gradle
```

## ビルド方法

### サーバー側

```bash
# サーバーのJARをビルド
cd server
./gradlew bootJar

# サーバーを起動
java -jar build/libs/opinion-helper-server.jar
```

サーバーは `http://localhost:8080` で起動します。

### クライアント側

```bash
# クライアントのJARをビルド
cd client
./gradlew jar

# クライアントを起動
java -jar build/libs/client-1.0.0.jar

# インストーラーを作成（Windows）
./gradlew createInstaller
```

## 開発環境

- Java 21
- Spring Boot 3.2.1
- Gradle 8.5
- FlatLaf 3.1
- Gson 2.10.1

## API エンドポイント

### トピック
- `GET /api/topics` - トピック一覧取得
- `POST /api/topics` - トピック作成
- `DELETE /api/topics/{id}` - トピック削除

### オピニオン
- `GET /api/topics/{id}/opinions` - オピニオン一覧取得
- `POST /api/topics/{id}/opinions` - オピニオン作成
- `DELETE /api/topics/{topicId}/opinions/{opinionId}` - オピニオン削除

### Ju-en
- `POST /api/topics/{topicId}/opinions/{opinionId}/juen?userId={userId}` - Ju-en追加

## デプロイ

### サーバーのデプロイ（Railway推奨）

1. [Railway](https://railway.app/)にサインアップ
2. GitHubリポジトリを接続
3. `server`ディレクトリを選択
4. 環境変数を設定（PostgreSQLは自動）
5. デプロイ

### クライアントの配布

1. `client/build/installer/JuenOpinionHelper-1.0.0.msi` を配布
2. GitHubリリースにアップロード

## ライセンス

MIT License
