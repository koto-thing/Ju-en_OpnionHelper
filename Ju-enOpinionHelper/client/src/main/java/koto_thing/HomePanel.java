package koto_thing;

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HomePanel extends JPanel {
    private DefaultListModel<Topic> topicListModel;
    private JList<Topic> topicList;
    private JTextField topicInputField;
    private JButton addButton;
    private JLabel topicLabel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel lastUpdatedLabel;
    
    private AppSettings settings;
    private Timer autoRefreshTimer;
    
    public HomePanel(AppSettings settings) {
        this.settings = settings;
        settings.addChangeListener(this::applyFontSettings);
        settings.addChangeListener(this::restartAutoRefresh);
        
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // トピック一覧パネル
        JPanel topicListPanel = createTopicListPanel();
        mainPanel.add(topicListPanel, "TOPIC_LIST");

        add(mainPanel, BorderLayout.CENTER);
        
        applyFontSettings();
        startAutoRefresh();
        
        loadTopics();
    }

    private JPanel createTopicListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // トピックリスト
        topicListModel = new DefaultListModel<>();
        topicList = new JList<>(topicListModel);

        JScrollPane scrollPane = new JScrollPane(topicList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("トピック一覧"));

        // トピック入力パネル
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topicInputField = new JTextField();
        addButton = new JButton("トピックを追加");
        topicLabel = new JLabel("新しいトピック:");

        inputPanel.add(topicLabel, BorderLayout.WEST);
        inputPanel.add(topicInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        // イベント処理
        addButton.addActionListener(e -> addTopic());
        topicInputField.addActionListener(e -> addTopic());

        // ダブルクリックでスレッド表示
        topicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Topic selectedTopic = topicList.getSelectedValue();
                    if (selectedTopic != null) {
                        showThreadPanel(selectedTopic);
                    }
                }
            }

            // 右クリックメニューを追加
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }

            private void showPopupMenu(MouseEvent e) {
                int index = topicList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    topicList.setSelectedIndex(index);
                    Topic selectedTopic = topicList.getSelectedValue();

                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("削除");
                    deleteItem.addActionListener(ev -> deleteTopic(selectedTopic));
                    popupMenu.add(deleteItem);

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        // 最終更新時刻ラベル
        lastUpdatedLabel = new JLabel("最終更新: -");
        lastUpdatedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lastUpdatedLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(lastUpdatedLabel, BorderLayout.NORTH);

        return panel;
    }

    private void addTopic() {
        String topicTitle = topicInputField.getText().trim();
        if (!topicTitle.isEmpty()) {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"name\":\"%s\"}", topicTitle);

            HttpRequest request = AuthHelper.addAuthHeader(
                    HttpRequest.newBuilder()
                        .uri(URI.create(settings.getServerUrl() + "/api/topics"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json)),
                    settings
            ).build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        SwingUtilities.invokeLater(() -> {
                            topicInputField.setText("");
                            JOptionPane.showMessageDialog(this,
                                    "トピックが正常に追加されました。",
                                    "成功",
                                    JOptionPane.INFORMATION_MESSAGE);
                            
                            Timer timer = new Timer(500, e -> {
                                loadTopics();
                                ((Timer) e.getSource()).stop();
                            });
                            timer.setRepeats(false);
                            timer.start();
                        });
                    })
                    .exceptionally(e -> {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this,
                                    "トピックの追加に失敗しました。\n" + e.getMessage(),
                                    "エラー",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                        return null;
                    });
        }
    }

    private void deleteTopic(Topic topic) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "トピック「" + topic.getName() + "」を削除してもよろしいですか？\n関連するオピニオンもすべて削除されます。",
                "削除確認",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = AuthHelper.addAuthHeader(
                HttpRequest.newBuilder()
                    .uri(URI.create(settings.getServerUrl() + "/api/topics/" + topic.getId()))
                    .DELETE(),
                settings
        ).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "トピックを削除しました。",
                                "削除成功",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadTopics();
                    });
                })
                .exceptionally(e -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "トピックの削除に失敗しました。",
                                "削除エラー",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    return null;
                });
    }

    private void showThreadPanel(Topic topic) {
        ThreadPanel threadPanel = new ThreadPanel(
                topic.getName(),
                topic.getId(),
                () -> {
                    cardLayout.show(mainPanel, "TOPIC_LIST");
                },
                settings
        );
        mainPanel.add(threadPanel, "THREAD_" + topic.getId());
        cardLayout.show(mainPanel, "THREAD_" + topic.getId());
    }
    
    public void updateTopics(List<Topic> topics) {
        topicListModel.clear();
        for (Topic topic : topics) {
            topicListModel.addElement(topic);
        }
    }

    private void loadTopics() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = AuthHelper.addAuthHeader(
                HttpRequest.newBuilder()
                    .uri(URI.create(settings.getServerUrl() + "/api/topics"))
                    .GET(),
                settings
        ).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 401) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this,
                                "認証に失敗しました。ユーザー名とパスワードを確認してください。\n" +
                                "ユーザー名: " + settings.getAuthUsername(),
                                "認証エラー",
                                JOptionPane.ERROR_MESSAGE);
                        });
                        throw new RuntimeException("Authentication failed (401)");
                    }
                    return response;
                })
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Gson gson = new Gson();
                            Topic[] topicsArray = gson.fromJson(response, Topic[].class);

                            topicListModel.clear();
                            for (Topic topic : topicsArray) {
                                topicListModel.addElement(topic);
                            }

                            topicList.revalidate();
                            topicList.repaint();

                            // 最終更新時刻を表示
                            java.time.LocalDateTime now = java.time.LocalDateTime.now();
                            java.time.format.DateTimeFormatter formatter =
                                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
                            lastUpdatedLabel.setText("最終更新: " + now.format(formatter));
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(this,
                                "トピックの解析に失敗しました: " + e.getMessage(),
                                "エラー",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                })
                .exceptionally(e -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "トピックの取得に失敗しました:\n" + e.getMessage(),
                            "接続エラー",
                            JOptionPane.ERROR_MESSAGE);
                    });
                    return null;
                });
    }

    private void applyFontSettings() {
        int fontSize = settings.getFontSize();
        Font font = new Font("Meiryo", Font.PLAIN, fontSize);

        topicList.setFont(font);
        topicInputField.setFont(font);
        addButton.setFont(font);
        topicLabel.setFont(font);

        // テーマを適用
        applyTheme();

        revalidate();
        repaint();
    }

    private void applyTheme() {
        Theme theme = settings.getTheme().equals("Dark")
                ? Theme.getDarkTheme()
                : Theme.getLightTheme();

        // 背景色と前景色を設定
        setBackground(theme.getBackgroundColor());
        mainPanel.setBackground(theme.getBackgroundColor());

        // トピックリスト
        topicList.setBackground(theme.getPanelColor());
        topicList.setForeground(theme.getForegroundColor());

        // 入力フィールド
        topicInputField.setBackground(theme.getPanelColor());
        topicInputField.setForeground(theme.getForegroundColor());
        topicInputField.setCaretColor(theme.getForegroundColor());

        // ボタン
        addButton.setBackground(theme.getButtonColor());
        addButton.setForeground(theme.getButtonTextColor());

        // ラベル
        topicLabel.setForeground(theme.getForegroundColor());
    }

    private void startAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }

        if (settings.isAutoRefreshEnabled()) {
            int interval = settings.getAutoRefreshInterval() * 1000; // 秒をミリ秒に変換
            autoRefreshTimer = new Timer(interval, e -> loadTopics());
            autoRefreshTimer.start();
        }
    }

    private void restartAutoRefresh() {
        startAutoRefresh();
    }

    public void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
    }
}
