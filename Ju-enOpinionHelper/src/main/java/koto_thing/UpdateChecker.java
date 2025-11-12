package koto_thing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateChecker {
    private JFrame parentFrame;
    private HttpClient httpClient;
    
    public UpdateChecker(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.httpClient = HttpClient.newHttpClient();
    }
    
    public void checkForUpdates() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppVersion.UPDATE_CHECK_URL))
                .header("Accept", "application/vnd.github.v3+json")
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::processUpdateResponse)
                .exceptionally(e -> {
                    System.err.println("更新チェックエラー: " + e.getMessage());
                    return null;
                });
    }

    private void processUpdateResponse(String jsonResponse) {
        try {
            JsonObject release = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String latestVersion = release.get("tag_name").getAsString().replace("v", "");
            String downloadUrl = release.get("html_url").getAsString();

            if (isNewerVersion(latestVersion, AppVersion.VERSION)) {
                showUpdateDialog(latestVersion, downloadUrl);
            }
        } catch (Exception e) {
            System.err.println("更新情報の解析エラー: " + e.getMessage());
        }
    }

    private boolean isNewerVersion(String latest, String current) {
        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");

        for (int i = 0; i < Math.min(latestParts.length, currentParts.length); i++) {
            int latestNum = Integer.parseInt(latestParts[i]);
            int currentNum = Integer.parseInt(currentParts[i]);

            if (latestNum > currentNum) {
                return true;
            } else if (latestNum < currentNum) {
                return false;
            }
        }

        return latestParts.length > currentParts.length;
    }

    private void showUpdateDialog(String version, String downloadUrl) {
        SwingUtilities.invokeLater(() -> {
            String message = String.format(
                    "新しいバージョン %s が利用可能です。\n現在のバージョン: %s\n\n更新ページを開きますか?",
                    version,
                    AppVersion.VERSION
            );

            int result = JOptionPane.showConfirmDialog(
                    parentFrame,
                    message,
                    "アップデート利用可能",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().browse(URI.create(downloadUrl));
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            parentFrame,
                            "ブラウザを開けませんでした。\n手動でアクセスしてください: " + downloadUrl,
                            "エラー",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }
}
