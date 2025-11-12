package koto_thing;

import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JTextField userIdField;
    private JTextField serverUrlField;
    private JSpinner fontSizeSpinner;
    private JComboBox<String> themeComboBox;
    private JSpinner refreshSpinner;
    private JCheckBox autoRefreshCheckBox;
    private JCheckBox notificationCheckBox;
    private JCheckBox juenNotificationCheckBox;
    private AppSettings settings;

    public SettingsPanel() {
        settings = new AppSettings();
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ユーザーID設定
        JPanel userPanel = createSettingPanel("ユーザーID:",
                userIdField = new JTextField(20));

        // サーバーURL設定
        JPanel serverPanel = createSettingPanel("サーバーURL:",
                serverUrlField = new JTextField(20));

        // フォントサイズ設定
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(14, 10, 24, 1));
        JPanel fontPanel = createSettingPanel("フォントサイズ:", fontSizeSpinner);

        // テーマ設定
        themeComboBox = new JComboBox<>(new String[]{"Light", "Dark"});
        JPanel themePanel = createSettingPanel("テーマ:", themeComboBox);

        // 自動更新設定
        autoRefreshCheckBox = new JCheckBox("自動更新を有効にする");
        JPanel autoRefreshPanel = createSettingPanel("", autoRefreshCheckBox);

        // 自動更新間隔設定
        refreshSpinner = new JSpinner(new SpinnerNumberModel(30, 10, 300, 10));
        JPanel refreshPanel = createSettingPanel("自動更新間隔(秒):", refreshSpinner);
        
        // 通知設定
        notificationCheckBox = new JCheckBox("通知を有効にする");
        JPanel notificationPanel = createSettingPanel("", notificationCheckBox);
        
        // Ju-en通知設定
        juenNotificationCheckBox = new JCheckBox("Ju-en通知を表示");
        JPanel juenNotificationPanel = createSettingPanel("", juenNotificationCheckBox);

        contentPanel.add(userPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(serverPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(fontPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(themePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(autoRefreshPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(refreshPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(notificationPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(juenNotificationPanel);
        contentPanel.add(Box.createVerticalGlue());

        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("設定を保存");
        saveButton.addActionListener(e -> saveSettings());

        JButton resetButton = new JButton("デフォルトに戻す");
        resetButton.addActionListener(e -> resetSettings());

        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        loadSettings();
    }
    
    private JPanel createSettingPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(150, 25));
        panel.add(jLabel);
        panel.add(component);
        
        return panel;
    }

    private void saveSettings() {
        try {
            settings.setUserId(userIdField.getText());
            settings.setServerUrl(serverUrlField.getText());
            settings.setFontSize((Integer) fontSizeSpinner.getValue());
            settings.setTheme((String) themeComboBox.getSelectedItem());
            settings.setAutoRefreshEnabled(autoRefreshCheckBox.isSelected());
            settings.setAutoRefreshInterval((Integer) refreshSpinner.getValue());
            settings.setNotificationEnabled(notificationCheckBox.isSelected());
            settings.setJuenNotificationEnabled(juenNotificationCheckBox.isSelected());
            settings.save();

            JOptionPane.showMessageDialog(this,
                    "設定が保存され、適用されました。",
                    "保存完了",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "設定の保存に失敗しました: " + e.getMessage(),
                    "エラー",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSettings() {
        userIdField.setText(settings.getUserId());
        serverUrlField.setText(settings.getServerUrl());
        fontSizeSpinner.setValue(settings.getFontSize());
        themeComboBox.setSelectedItem(settings.getTheme());
        autoRefreshCheckBox.setSelected(settings.isAutoRefreshEnabled());
        refreshSpinner.setValue(settings.getAutoRefreshInterval());
        notificationCheckBox.setSelected(settings.isNotificationEnabled());
        juenNotificationCheckBox.setSelected(settings.isJuenNotificationEnabled());
    }

    private void resetSettings() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "設定をデフォルトに戻しますか?",
                "確認",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            userIdField.setText("");
            serverUrlField.setText("http://localhost:8080");
            fontSizeSpinner.setValue(14);
            themeComboBox.setSelectedItem("Light");
            autoRefreshCheckBox.setSelected(true);
            refreshSpinner.setValue(30);
            notificationCheckBox.setSelected(true);
            juenNotificationCheckBox.setSelected(true);
        }
    }
    
    public AppSettings getSettings() {
        return settings;
    }
}
