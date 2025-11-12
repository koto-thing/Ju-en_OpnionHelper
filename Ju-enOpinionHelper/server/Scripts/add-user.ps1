Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  Ju-en Bulletin Board" -ForegroundColor Cyan
Write-Host "  ユーザー追加ツール (対話式)" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# サーバーURL
$defaultUrl = "http://localhost:8080"
$serverUrl = Read-Host "サーバーURL [$defaultUrl]"
if ([string]::IsNullOrWhiteSpace($serverUrl)) {
    $serverUrl = $defaultUrl
}

# ユーザー名
do {
    $username = Read-Host "ユーザー名"
    if ([string]::IsNullOrWhiteSpace($username)) {
        Write-Host "エラー: ユーザー名を入力してください" -ForegroundColor Red
    }
} while ([string]::IsNullOrWhiteSpace($username))

# パスワード（非表示）
do {
    $securePassword = Read-Host "パスワード (16文字以上推奨)" -AsSecureString
    $BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $password = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)
    [System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($BSTR)

    if ($password.Length -lt 8) {
        Write-Host "警告: パスワードが短すぎます（最低8文字）" -ForegroundColor Red
    }
} while ($password.Length -lt 8)

# 確認
Write-Host ""
Write-Host "登録内容:" -ForegroundColor Yellow
Write-Host "  サーバー: $serverUrl" -ForegroundColor Cyan
Write-Host "  ユーザー名: $username" -ForegroundColor Cyan
Write-Host "  パスワード: ********" -ForegroundColor Cyan
Write-Host ""
$confirm = Read-Host "登録しますか? (y/N)"

if ($confirm -ne "y" -and $confirm -ne "Y") {
    Write-Host "キャンセルしました" -ForegroundColor Yellow
    exit 0
}

# ユーザー登録実行
& "$PSScriptRoot\add-user.ps1" -Username $username -Password $password -ServerUrl $serverUrl
