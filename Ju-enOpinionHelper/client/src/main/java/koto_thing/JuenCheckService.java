package koto_thing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JuenCheckService {
    private AppSettings settings;
    private NotificationManager notificationManager;
    private Timer checkTimer;
    private Set<String> notifiedJuens;
    private HttpClient httpClient;
    
    public JuenCheckService(AppSettings settings, NotificationManager notificationManager) {
        this.settings = settings;
        this.notificationManager = notificationManager;
        this.notifiedJuens = new HashSet<>();
        this.httpClient = HttpClient.newHttpClient();
    }
    
    public void startChecking() {
        if (checkTimer != null) {
            checkTimer.stop();
        }
        
        if (settings.isJuenNotificationEnabled()) {
            checkTimer = new Timer(30000, e -> checkForNewJuens());
            checkTimer.start();
        }
    }
    
    public void stopChecking() {
        if (checkTimer != null) {
            checkTimer.stop();
        }
    }
    
    private void checkForNewJuens() {
        String userId = settings.getUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(settings.getServerUrl() + "/api/opinions/user/" + userId))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::processOpinions)
                .exceptionally(e -> null);
    }

    private void processOpinions(String jsonResponse) {
        try {
            Gson gson = new Gson();
            java.lang.reflect.Type listType = new TypeToken<List<Opinion>>(){}.getType();
            List<Opinion> opinions = gson.fromJson(jsonResponse, listType);

            for (Opinion opinion : opinions) {
                checkOpinionJuens(opinion);
            }
        } catch (Exception e) {
            // エラーを無視
        }
    }

    private void checkOpinionJuens(Opinion opinion) {
        Set<String> juenUsers = opinion.getJuenedUsers();
        if (juenUsers == null || juenUsers.isEmpty()) {
            return;
        }

        String currentUser = settings.getUserId();
        for (String juenUser : juenUsers) {
            // 自分自身のJu-enは除外
            if (juenUser.equals(currentUser)) {
                continue;
            }

            String notificationKey = opinion.getId() + "_" + juenUser;
            if (!notifiedJuens.contains(notificationKey)) {
                notifiedJuens.add(notificationKey);
                showJuenNotification(opinion, juenUser);
            }
        }
    }

    private void showJuenNotification(Opinion opinion, String juenUser) {
        SwingUtilities.invokeLater(() -> {
            String title = "🎉 新しいJu-en!";
            String message = juenUser + "さんがあなたの意見に同意しました";

            if (settings.isNotificationEnabled() && settings.isJuenNotificationEnabled()) {
                notificationManager.showNotification(
                        title,
                        message,
                        NotificationManager.NotificationType.JUEN
                );
            }
        });
    }

    public void clearNotificationHistory() {
        notifiedJuens.clear();
    }
}
