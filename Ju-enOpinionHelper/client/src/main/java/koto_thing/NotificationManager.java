package koto_thing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private JFrame parentFrame;
    private List<NotificationPanel> notifications;
    private JPanel notificationPanel;
    private static final int MAX_NOTIFICATIONS = 5;

    public NotificationManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.notifications = new ArrayList<>();
        initNotificationPanel();
    }

    private void initNotificationPanel() {
        notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
        notificationPanel.setOpaque(false);
    }

    public void showNotification(String title, String message, NotificationType type) {
        SwingUtilities.invokeLater(() -> {
            if (notifications.size() >= MAX_NOTIFICATIONS) {
                removeOldestNotification();
            }

            NotificationPanel notification = new NotificationPanel(title, message, type);
            notifications.add(notification);
            notificationPanel.add(notification);

            updateNotificationPositions();

            Timer timer = new Timer(5000, e -> removeNotification(notification));
            timer.setRepeats(false);
            timer.start();
        });
    }

    private void removeNotification(NotificationPanel notification) {
        SwingUtilities.invokeLater(() -> {
            notifications.remove(notification);
            notificationPanel.remove(notification);
            updateNotificationPositions();
        });
    }

    private void removeOldestNotification() {
        if (!notifications.isEmpty()) {
            NotificationPanel oldest = notifications.get(0);
            removeNotification(oldest);
        }
    }

    private void updateNotificationPositions() {
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    public JPanel getNotificationPanel() {
        return notificationPanel;
    }

    public enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR, JUEN
    }

    private class NotificationPanel extends JPanel {
        public NotificationPanel(String title, String message, NotificationType type) {
            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(300, 80));
            setPreferredSize(new Dimension(300, 80));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(getColorForType(type), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            setBackground(new Color(255, 255, 255, 230));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Meiryo", Font.BOLD, 14));
            titleLabel.setForeground(getColorForType(type));

            JLabel messageLabel = new JLabel("<html>" + message + "</html>");
            messageLabel.setFont(new Font("Meiryo", Font.PLAIN, 12));

            JButton closeButton = new JButton("✕");
            closeButton.setPreferredSize(new Dimension(20, 20));
            closeButton.setBorderPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.addActionListener(e -> removeNotification(this));

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            headerPanel.add(titleLabel, BorderLayout.CENTER);
            headerPanel.add(closeButton, BorderLayout.EAST);

            add(headerPanel, BorderLayout.NORTH);
            add(messageLabel, BorderLayout.CENTER);
        }

        private Color getColorForType(NotificationType type) {
            return switch (type) {
                case INFO -> new Color(33, 150, 243);
                case SUCCESS -> new Color(76, 175, 80);
                case WARNING -> new Color(255, 152, 0);
                case ERROR -> new Color(244, 67, 54);
                case JUEN -> new Color(156, 39, 176);
            };
        }
    }
}