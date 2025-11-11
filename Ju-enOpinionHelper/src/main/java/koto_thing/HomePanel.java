package koto_thing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomePanel extends JPanel {
    private DefaultListModel<String> topicListModel;
    private JList<String> topicList;
    private JTextField topicInputField;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public HomePanel() {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // トピック一覧パネル
        JPanel topicListPanel = createTopicListPanel();
        mainPanel.add(topicListPanel, "TOPIC_LIST");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTopicListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // トピックリスト
        topicListModel = new DefaultListModel<>();
        topicList = new JList<>(topicListModel);
        topicList.setFont(new Font("Meiryo", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(topicList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("トピック一覧"));

        // トピック入力パネル
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topicInputField = new JTextField();
        topicInputField.setFont(new Font("Meiryo", Font.PLAIN, 14));

        JButton addButton = new JButton("トピックを追加");
        addButton.setFont(new Font("Meiryo", Font.PLAIN, 14));

        inputPanel.add(new JLabel("新しいトピック:"), BorderLayout.WEST);
        inputPanel.add(topicInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        // イベント処理
        addButton.addActionListener(e -> addTopic());
        topicInputField.addActionListener(e -> addTopic());

        topicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedTopic = topicList.getSelectedValue();
                    if (selectedTopic != null) {
                        showThreadPanel(selectedTopic);
                    }
                }
            }
        });

        return panel;
    }

    private void addTopic() {
        String topic = topicInputField.getText().trim();
        if (!topic.isEmpty()) {
            topicListModel.addElement(topic);
            topicInputField.setText("");
        }
    }

    private void showThreadPanel(String topicName) {
        ThreadPanel threadPanel = new ThreadPanel(topicName, () -> {
            cardLayout.show(mainPanel, "TOPIC_LIST");
        });
        mainPanel.add(threadPanel, "THREAD_" + topicName);
        cardLayout.show(mainPanel, "THREAD_" + topicName);
    }
}
