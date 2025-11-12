package koto_thing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppSettings {
    private static final String SETTINGS_FILE = "app.properties";
    private Properties properties;
    private List<SettingsChangeListener> listeners = new ArrayList<>();
    
    public interface SettingsChangeListener {
        void onSettingsChanged();
    }
    
    public AppSettings() {
        properties = new Properties();
        load();
    }
    
    public void addChangeListener(SettingsChangeListener listener) {
        listeners.add(listener);
    }
    
    private void notifyChangeListeners() {
        for (SettingsChangeListener listener : listeners) {
            listener.onSettingsChanged();
        }
    }
    
    public String getUserId() {
        return properties.getProperty("userId", "");
    }
    
    public void setUserId(String userId) {
        properties.setProperty("userId", userId);
    }
    
    public String getServerUrl() {
        return properties.getProperty("serverUrl", "http://localhost:8080");
    }
    
    public void setServerUrl(String serverUrl) {
        properties.setProperty("serverUrl", serverUrl);
    }
    
    public int getFontSize() {
        return Integer.parseInt(properties.getProperty("fontSize", "14"));
    }
    
    public void setFontSize(int fontSize) {
        properties.setProperty("fontSize", Integer.toString(fontSize));
    }
    
    public String getTheme() {
        return properties.getProperty("theme", "dark");
    }
    
    public void setTheme(String theme) {
        properties.setProperty("theme", theme);
    }
    
    public int getAutoRefreshInterval() {
        return Integer.parseInt(properties.getProperty("autoRefreshInterval", "60"));
    }
    
    public void setAutoRefreshInterval(int interval) {
        properties.setProperty("autoRefreshInterval", String.valueOf(interval));
    }
    
    public boolean isAutoRefreshEnabled() {
        return Boolean.parseBoolean(properties.getProperty("autoRefreshEnabled", "false"));
    }
    
    public void setAutoRefreshEnabled(boolean enabled) {
        properties.setProperty("autoRefreshEnabled", String.valueOf(enabled));
    }
    
    public boolean isNotificationEnabled() {
        return Boolean.parseBoolean(properties.getProperty("notificationEnabled", "true"));
    }
    
    public void setNotificationEnabled(boolean enabled) {
        properties.setProperty("notificationEnabled", String.valueOf(enabled));
    }
    
    public boolean isJuenNotificationEnabled() {
        return Boolean.parseBoolean(properties.getProperty("juenNotificationEnabled", "true"));
    }
    
    public void setJuenNotificationEnabled(boolean enabled) {
        properties.setProperty("juenNotificationEnabled", String.valueOf(enabled));
    }

    public String getAuthUsername() {
        return properties.getProperty("authUsername", "admin");
    }

    public void setAuthUsername(String username) {
        properties.setProperty("authUsername", username);
    }

    public String getAuthPassword() {
        return properties.getProperty("authPassword", "");
    }

    public void setAuthPassword(String password) {
        properties.setProperty("authPassword", password);
    }

    public void save() {
        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(out, "Ju-en Opinion Helper Settings");
            notifyChangeListeners();
        } catch (Exception e) {
            // エラーを無視
        }
    }
    
    public void load() {
        try (FileInputStream inputStream = new FileInputStream(SETTINGS_FILE)) {
            properties.load(inputStream);
        } catch (Exception e) {
            // ファイルが存在しない場合は無視
        }
    }
}
