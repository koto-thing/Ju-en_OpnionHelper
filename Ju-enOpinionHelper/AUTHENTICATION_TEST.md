# データベース管理認証のテストガイド

## 自動作成される管理者アカウント

サーバー起動時に以下のアカウントが自動作成されます:

- **ユーザー名**: `admin`
- **パスワード**: `admin123`

## テスト手順

### 1. サーバーの起動確認

#### ローカル環境

```bash
./gradlew :server:bootRun
```

ログで以下を確認:

```
Admin user created with username: admin, password: admin123
Started JuenOpinionHelperApplication
```

#### Railway 環境

1. Railway Dashboard → Deployments タブ
2. ログで `Admin user created` が表示されることを確認

### 2. 管理者アカウントでログイン

クライアントアプリケーションで:

1. 設定画面を開く
2. 以下を入力:
   - **サーバーURL**: `http://localhost:8080` (ローカル) または `https://ju-enopinionhelper-production.up.railway.app` (Railway)
   - **ユーザー名**: `admin`
   - **パスワード**: `admin123`
3. **設定を保存**
4. ホーム画面でトピックを作成してテスト

### 3. 新規ユーザーの登録

#### Postman を使用

**エンドポイント**: `POST /api/register`

**リクエストボディ**:
```json
{
  "username": "developer1",
  "password": "dev1password"
}
```

**成功レスポンス**:
```
User registered successfully
```

#### curl を使用

```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{"username":"developer1","password":"dev1password"}'
```

### 4. 登録したユーザーでログイン

クライアントアプリケーションで:

1. 設定画面を開く
2. 認証情報を変更:
   - **ユーザー名**: `developer1`
   - **パスワード**: `dev1password`
3. **設定を保存**
4. ホーム画面でトピックを作成してテスト

### 5. ユーザー一覧の取得（管理者のみ）

管理者アカウントで認証して:

```bash
curl -X GET http://localhost:8080/api/users \
  -u admin:admin123
```

## トラブルシューティング

### エラー: "User not found"

**原因**: データベースが初期化されていない

**解決方法**:
1. アプリケーションを再起動
2. ログで `Admin user created` を確認
3. H2 データベースファイル `data/opinion-helper.mv.db` が作成されていることを確認

### エラー: "Username already exists"

**原因**: 既に同じユーザー名が登録済み

**解決方法**: 別のユーザー名を使用

### エラー: "Bad credentials"

**原因**: ユーザー名またはパスワードが間違っている

**解決方法**: 正しい認証情報を入力

## Railway デプロイ時の注意点

### 環境変数の削除

以下の環境変数は**不要になりました**（削除してください）:

- ~~`ADMIN_USER`~~
- ~~`ADMIN_PASSWORD`~~

### 必要な環境変数

```env
SPRING_PROFILES_ACTIVE=railway
```

PostgreSQL の接続情報は自動的に設定されます。

## セキュリティ上の推奨事項

### 本番環境での管理者パスワード変更

1. 初回デプロイ後、すぐに管理者パスワードを変更
2. 環境変数 `ADMIN_DEFAULT_PASSWORD` を設定して、自動生成パスワードを変更:

```java
// DataInitializer.java の修正例
String defaultPassword = System.getenv("ADMIN_DEFAULT_PASSWORD");
if (defaultPassword == null) {
    defaultPassword = "admin123"; // デフォルト
}
admin.setPassword(passwordEncoder.encode(defaultPassword));
```

### パスワードポリシー

将来的に追加すべき機能:
- パスワードの最小文字数（8文字以上）
- 英数字・記号の組み合わせ
- パスワードの定期変更

## 次のステップ

- [ ] ローカル環境でテスト完了
- [ ] Railway 環境でテスト完了
- [ ] チームメンバーのアカウント作成
- [ ] 本番環境の管理者パスワード変更

