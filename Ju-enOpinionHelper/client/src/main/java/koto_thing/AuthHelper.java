package koto_thing;

import java.net.http.HttpRequest;
import java.util.Base64;

/**
 * HTTP通信に認証ヘッダーを追加するユーティリティクラス
 */
public class AuthHelper {
    
    /**
     * Basic認証ヘッダーを生成
     * @param username ユーザー名
     * @param password パスワード
     * @return Basic認証ヘッダー文字列
     */
    public static String createBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }
    
    /**
     * HttpRequest.Builderに認証ヘッダーを追加
     * @param builder HttpRequest.Builder
     * @param settings AppSettings
     * @return 認証ヘッダー付きのHttpRequest.Builder
     */
    public static HttpRequest.Builder addAuthHeader(HttpRequest.Builder builder, AppSettings settings) {
        String username = settings.getAuthUsername();
        String password = settings.getAuthPassword();
        
        System.out.println("=== AuthHelper ===");
        System.out.println("Username: " + username);
        System.out.println("Password is set: " + (password != null && !password.isEmpty()));
        System.out.println("Password length: " + (password != null ? password.length() : 0));
        
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            String authHeader = createBasicAuthHeader(username, password);
            System.out.println("Auth header created: " + authHeader.substring(0, Math.min(20, authHeader.length())) + "...");
            builder.header("Authorization", authHeader);
        } else {
            System.out.println("WARNING: No auth header added! Username or password is empty.");
        }
        System.out.println("==================");
        
        return builder;
    }
}

