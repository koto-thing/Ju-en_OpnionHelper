# ビルド・実行手順

## プロジェクト構成完了

✅ クライアント側とサーバー側を完全に分離しました
✅ それぞれ独立してビルド・実行可能です

## 1. サーバー側のビルド・実行

### ビルド

```powershell
cd server
../gradlew bootJar
```

生成されるJAR: `server/build/libs/opinion-helper-server.jar`

### 実行

```powershell
java -jar build/libs/opinion-helper-server.jar
```

サーバーが `http://localhost:8080` で起動します。

### 動作確認

ブラウザで以下にアクセス:
```
http://localhost:8080/api/topics
```

空の配列 `[]` が返ってくればOKです。

## 2. クライアント側のビルド・実行

### ビルド

```powershell
cd client
../gradlew jar
```

生成されるJAR: `client/build/libs/client-1.0.0.jar`

### 実行

```powershell
java -jar build/libs/client-1.0.0.jar
```

デスクトップアプリケーションが起動します。

### インストーラー作成

```powershell
../gradlew createInstaller
```

生成されるインストーラー: `client/build/installer/JuenOpinionHelper-1.0.0.msi`

## 3. 開発時の起動手順

### ターミナル1: サーバー起動

```powershell
cd server
../gradlew bootRun
```

### ターミナル2: クライアント起動

```powershell
cd client
../gradlew run
```

または

```powershell
java -jar build/libs/client-1.0.0.jar
```

## 4. 設定

### サーバー側の設定

`server/src/main/resources/application.properties` で設定変更可能:

```properties
server.port=8080
spring.datasource.url=jdbc:h2:file:./data/opinion-helper
```

### クライアント側の設定

アプリケーションの設定画面から:
- サーバーURL (デフォルト: `http://localhost:8080`)
- ユーザーID
- フォントサイズ
- テーマ
- 自動更新間隔

## 5. トラブルシューティング

### サーバーが起動しない

- ポート8080が使用中でないか確認
- Java 21がインストールされているか確認

### クライアントが接続できない

- サーバーが起動しているか確認
- 設定画面でサーバーURLを確認
- ファイアウォールの設定を確認

### ビルドエラー

```powershell
# クリーンビルド
../gradlew clean build
```

## 6. デプロイ

### サーバーのクラウドデプロイ (Railway)

1. [Railway](https://railway.app/) にサインアップ
2. GitHubリポジトリを接続
3. `server` ディレクトリをルートに設定
4. PostgreSQL を追加 (自動設定)
5. デプロイ

デプロイ後、クライアント側の設定で:
```
サーバーURL: https://your-app-name.railway.app
```

### クライアントの配布

1. インストーラーを作成
2. GitHubのリリースページにアップロード
3. ユーザーにダウンロードリンクを共有

## 7. 次のステップ

- [ ] GitHubでリリースタグを作成 (`v1.0.0`)
- [ ] サーバーをRailwayにデプロイ
- [ ] クライアントインストーラーをテスト
- [ ] ユーザーマニュアルを作成
- [ ] フィードバックを収集

