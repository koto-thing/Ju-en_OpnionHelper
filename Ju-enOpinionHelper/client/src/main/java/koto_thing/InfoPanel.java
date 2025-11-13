package koto_thing;

import javax.print.event.PrintJobAttributeEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * アプリケーション起動時のメニュー欄左側の情報パネル
 * 情報パネルクラス
 */
public class InfoPanel extends JPanel {
    private final JLabel titleLabel = new JLabel();
    private final JLabel versionLabel = new JLabel();
    private final JLabel connectionLabel = new JLabel();
    private final JLabel connectionDot = new JLabel("●");
    private final JLabel lastSyncLabel = new JLabel();
    private final BadgeLabel topicCountBadge = new BadgeLabel("0");
    private final BadgeLabel notificationBadge = new BadgeLabel("0");
    private final JButton refreshButton = new JButton("更新");
    private final JButton openLogsButton = new JButton("ログを開く");
    
    private static final Color CARD_BG = new Color(40, 42, 44);
    private static final Color PANEL_BG = new Color(34, 36, 38);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Font SMALL = new Font("Dialog", Font.PLAIN, 12);
    private static final Font REGULAR = new Font("Dialog", Font.PLAIN, 13);
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public InfoPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));
        
        // カードコンテナ
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setLayout(new BorderLayout());
        
        // ヘッダ
        titleLabel.setText("<html><b>Ju-en Bulletin Board</b> <span style='color:#9aa0a6;'>v1.0.0</span></html>");
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleLabel, BorderLayout.WEST);
        
        // ボタンスタイル
        styleFlatButton(refreshButton);
        styleFlatButton(openLogsButton);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        buttonPanel.add(openLogsButton);
        header.add(buttonPanel, BorderLayout.EAST);
        
        card.add(header, BorderLayout.NORTH);
        
        // 情報行
        JPanel rows = new JPanel(new GridLayout());
        rows.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        
        addRow(rows, constraints, 0, "バージョン:", versionLabel);
        addRow(rows, constraints, 1, "接続:", createConnectionPanel());
        addRow(rows, constraints, 2, "最終同期:", lastSyncLabel);
        addRow(rows, constraints, 3, "トピック数:", topicCountBadge);
        addRow(rows, constraints, 4, "未読通知:", notificationBadge);
        
        card.add(rows, BorderLayout.CENTER);
        
        add(card, BorderLayout.CENTER);
        
        // 初期値
        setAppVersion("1.0.0");
        setConnectionStatus(false);
        setLastSync(null);
        setTopicCount(0);
        setNotificationsCount(0);
    }
    
    private void addRow(JPanel parent, GridBagConstraints constraints,  int row, String labelText, JComponent valueComponent) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(170, 170, 170));
        label.setFont(SMALL);
        parent.add(label, constraints);
        
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        valueComponent.setFont(REGULAR);
        parent.add(wrapValue(valueComponent), constraints);
    }
    
    private JPanel wrapValue(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        component.setForeground(TEXT);
        panel.add(component);
        
        return panel;
    }
    
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setOpaque(false);
        connectionDot.setFont(new Font("Dialog", Font.PLAIN, 14));
        connectionDot.setForeground(Color.GRAY);
        connectionLabel.setForeground(TEXT);
        connectionLabel.setFont(REGULAR);
        panel.add(connectionDot);
        panel.add(connectionLabel);
        
        return panel;
    }
    
    private void styleFlatButton(JButton button) {
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(6, 10, 6, 10));
        button.setBackground(new Color(60, 63, 65));
        button.setForeground(TEXT);
        button.setOpaque(true);
        button.setFont(SMALL);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int arc = 12;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // カード背景
        g2.setColor(CARD_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        // 内側の暗めのレイヤーで奥行き
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setAppVersion(String version) {
        versionLabel.setText(version);
        titleLabel.setText("<html><b>Ju-en Opinion Helper</b> <span style='color:#9aa0a6;'>v" + version + "</span></html>");
    }

    public void setConnectionStatus(boolean online) {
        connectionDot.setForeground(online ? new Color(94, 184, 92) : new Color(200, 60, 60));
        connectionLabel.setText(online ? "Online" : "Offline");
        connectionLabel.setForeground(TEXT);
    }

    public void setLastSync(LocalDateTime time) {
        lastSyncLabel.setText(time == null ? "-" : time.format(DT_FMT));
    }

    public void setTopicCount(int count) {
        topicCountBadge.setText(String.valueOf(count));
    }

    public void setNotificationsCount(int count) {
        notificationBadge.setText(String.valueOf(count));
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public JButton getOpenLogsButton() {
        return openLogsButton;
    }

    // 小さなバッジラベル
    private static class BadgeLabel extends JLabel {
        BadgeLabel(String text) {
            super(text, SwingConstants.CENTER);
            setOpaque(true);
            setBackground(new Color(70, 130, 180));
            setForeground(Color.WHITE);
            setBorder(new EmptyBorder(4, 8, 4, 8));
            setFont(new Font("Dialog", Font.BOLD, 12));
        }
    }
}
