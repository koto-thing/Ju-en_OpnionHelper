package koto_thing;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThreadPanel extends JPanel {
    private String topicName;
    private Long topicId;
    private ArrayList<Opinion> opinions;
    private JPanel opinionsContainer;
    private JTextField opinionTitleField;
    private JTextArea opinionContentArea;
    private Runnable onBackAction;
    private Map<Integer, Boolean> expandedStates;
    
    private String currentUserId = "default-user";

    public ThreadPanel(String topicName, Long topicId, Runnable onBackAction) {
        this.topicName = topicName;
        this.topicId = topicId;
        this.opinions = TopicManager.getInstance().getOpinions(topicName);
        this.onBackAction = onBackAction;
        this.expandedStates = new HashMap<>();

        setLayout(new BorderLayout());

        // 上部の戻るボタンとタイトル
        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("トピック一覧に戻る");
        backButton.setFont(new Font("Meiryo", Font.PLAIN, 12));
        backButton.addActionListener(event -> onBackAction.run());

        JLabel titleLabel = new JLabel("トピック: " + topicName);
        titleLabel.setFont(new Font("Meiryo", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // オピニオンカード用のコンテナ
        opinionsContainer = new JPanel();
        opinionsContainer.setLayout(new BoxLayout(opinionsContainer, BoxLayout.Y_AXIS));
        opinionsContainer.setBackground(Color.DARK_GRAY);

        JScrollPane opinionScrollPane = new JScrollPane(opinionsContainer);
        opinionScrollPane.setBorder(BorderFactory.createTitledBorder("オピニオン一覧"));
        opinionScrollPane.setPreferredSize(new Dimension(0, 300));

        // 中央下部のオピニオン入力パネル
        JPanel opinionDetailPanel = createOpinionDetailPanel();

        // 中央パネルを作成
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(opinionScrollPane, BorderLayout.CENTER);
        centerPanel.add(opinionDetailPanel, BorderLayout.SOUTH);

        // パネルに追加
        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // オピニオンを初期ロード
        loadOpinions();
    }

    private JPanel createOpinionDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("新しいオピニオンを投稿"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // タイトル入力
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        JLabel titleLabel = new JLabel("タイトル:");
        titleLabel.setFont(new Font("Meiryo", Font.PLAIN, 14));
        opinionTitleField = new JTextField();
        opinionTitleField.setFont(new Font("Meiryo", Font.PLAIN, 14));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(opinionTitleField, BorderLayout.CENTER);

        // 内容入力
        JPanel contentPanel = new JPanel(new BorderLayout(10, 5));
        JLabel contentLabel = new JLabel("内容:");
        contentLabel.setFont(new Font("Meiryo", Font.PLAIN, 14));
        opinionContentArea = new JTextArea(5, 40);
        opinionContentArea.setFont(new Font("Meiryo", Font.PLAIN, 14));
        opinionContentArea.setLineWrap(true);
        opinionContentArea.setWrapStyleWord(true);
        JScrollPane contentScrollPane = new JScrollPane(opinionContentArea);
        contentScrollPane.setPreferredSize(new Dimension(0, 150));
        contentPanel.add(contentLabel, BorderLayout.NORTH);
        contentPanel.add(contentScrollPane, BorderLayout.CENTER);

        // 送信ボタン
        JButton submitButton = new JButton("オピニオンを投稿");
        submitButton.setFont(new Font("Meiryo", Font.PLAIN, 14));
        submitButton.addActionListener(event -> submitOpinion());

        // パネルにコンポーネントを追加
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);

        return panel;
    }

    private void submitOpinion() {
        String title = opinionTitleField.getText().trim();
        String content = opinionContentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "タイトルと内容の両方を入力してください。",
                    "入力エラー",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        HttpClient client = HttpClient.newHttpClient();
        String json = String.format("{\"title\":\"%s\",\"content\":\"%s\"}",
                title.replace("\"", "\\\""),
                content.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/topics/" + topicId + "/opinions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        opinionTitleField.setText("");
                        opinionContentArea.setText("");
                        JOptionPane.showMessageDialog(this,
                                "オピニオンを投稿しました。",
                                "投稿成功",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadOpinions();
                    });
                })
                .exceptionally(e -> {
                    SwingUtilities.invokeLater(() -> {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                "オピニオンの投稿に失敗しました。",
                                "投稿エラー",
                                JOptionPane.ERROR_MESSAGE);
                    });

                    return null;
                });
    }

    private void loadOpinions() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/topics/" + topicId + "/opinions"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Gson gson = new Gson();
                            Opinion[] opinionsArray = gson.fromJson(response, Opinion[].class);

                            opinionsContainer.removeAll();

                            for (int i = 0; i < opinionsArray.length; i++) {
                                opinionsContainer.add(createOpinionCard(opinionsArray[i], i));
                                opinionsContainer.add(Box.createVerticalStrut(5));
                            }

                            opinionsContainer.revalidate();
                            opinionsContainer.repaint();
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(this,
                                    "オピニオンの読み込みに失敗しました。",
                                    "読み込みエラー",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                })
                .exceptionally(e -> {
                    SwingUtilities.invokeLater(() -> {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                "オピニオンの読み込みに失敗しました。",
                                "読み込みエラー",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    return null;
                });
    }

    private JPanel createOpinionCard(Opinion opinion, int index) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        card.setBackground(Color.DARK_GRAY);
        
        // タイトル
        JLabel titleLabel = new JLabel(opinion.getTitle());
        titleLabel.setFont(new Font("Meiryo", Font.BOLD, 14));
        
        // 内容
        JTextArea contentArea = new JTextArea(opinion.getContent());
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.DARK_GRAY);
        contentArea.setFont(new Font("Meiryo", Font.PLAIN, 12));
        
        // Ju-enボタン
        JButton juenButton = new JButton("Ju-en: " + opinion.getJuenCount());
        juenButton.setFocusPainted(false);

        // 既にJu-enを押している場合はボタンを無効化
        if (opinion.getJuenedUsers() != null &&
                opinion.getJuenedUsers().contains(currentUserId)) {
            juenButton.setEnabled(false);
        }

        juenButton.addActionListener(e -> addJuenToOpinion(opinion, juenButton));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.add(juenButton);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        // 右クリックメニューを追加
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("削除");
        deleteItem.addActionListener(event -> deleteOpinion(opinion));
        popupMenu.add(deleteItem);
        
        // カード全体に右クリックリスナーを追加
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        
        card.addMouseListener(mouseAdapter);
        titleLabel.addMouseListener(mouseAdapter);
        contentArea.addMouseListener(mouseAdapter);
        bottomPanel.addMouseListener(mouseAdapter);
        
        return card;
    }

    private void addJuenToOpinion(Opinion opinion, JButton button) {
        // 既にJu-enを押しているか確認
        if (opinion.getJuenedUsers() != null &&
                opinion.getJuenedUsers().contains(currentUserId)) {
            JOptionPane.showMessageDialog(this,
                    "既にJu-enを押しています。",
                    "Ju-en制限",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/topics/" + topicId +
                        "/opinions/" + opinion.getId() + "/juen?userId=" + currentUserId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        opinion.setJuenCount(opinion.getJuenCount() + 1);
                        if (opinion.getJuenedUsers() == null) {
                            opinion.setJuenedUsers(new java.util.HashSet<>());
                        }
                        opinion.getJuenedUsers().add(currentUserId);
                        button.setText("Ju-en: " + opinion.getJuenCount());
                        button.setEnabled(false); // ボタンを無効化
                    });
                })
                .exceptionally(e -> {
                    SwingUtilities.invokeLater(() -> {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                "Ju-enの操作に失敗しました。",
                                "Ju-enエラー",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    return null;
                });
    }
    
    private void deleteOpinion(Opinion opinion) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "本当にこのオピニオンを削除しますか？",
                "オピニオン削除の確認",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/topics/" + topicId +
                        "/opinions/" + opinion.getId()))
                .DELETE()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "オピニオンを削除しました。",
                                "削除成功",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadOpinions();
                    });
                })
                .exceptionally(e -> {
                    SwingUtilities.invokeLater(() -> {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                "オピニオンの削除に失敗しました。",
                                "削除エラー",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    return null;
                });
    }
}
