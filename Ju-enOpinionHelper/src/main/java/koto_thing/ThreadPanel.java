package koto_thing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ThreadPanel extends JPanel {
    private String topicName;
    private ArrayList<Opinion> opinions;
    private DefaultListModel<Opinion> opinionListModel;
    private JList<Opinion> opinionList;
    private JTextField opinionTitleField;
    private JTextArea opinionContentArea;
    private Runnable onBackAction;
    private Map<Integer, Boolean> expandedStates;

    public ThreadPanel(String topicName, Runnable onBackAction) {
        this.topicName = topicName;
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

        // 中央のオピニオンリスト
        opinionListModel = new DefaultListModel<>();
        // 既存のオピニオンをロード
        for (Opinion opinion : opinions) {
            opinionListModel.addElement(opinion);
        }

        opinionList = new JList<>(opinionListModel);
        opinionList.setFont(new Font("Meiryo", Font.PLAIN, 14));
        opinionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        opinionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout(5, 5));
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));

                Opinion opinion = (Opinion) value;
                JLabel titleLabel = new JLabel(opinion.toString());
                titleLabel.setFont(new Font("Meiryo", Font.BOLD, 14));
                panel.add(titleLabel, BorderLayout.NORTH);

                Boolean isExpanded = expandedStates.getOrDefault(index, false);
                if (isExpanded) {
                    JTextArea contentArea = new JTextArea(opinion.getContent());
                    contentArea.setFont(new Font("Meiryo", Font.PLAIN, 12));
                    contentArea.setLineWrap(true);
                    contentArea.setWrapStyleWord(true);
                    contentArea.setEditable(false);
                    contentArea.setOpaque(false);
                    contentArea.setRows(3);
                    panel.add(contentArea, BorderLayout.CENTER);
                }

                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                    titleLabel.setForeground(list.getSelectionForeground());
                } else {
                    panel.setBackground(list.getBackground());
                    titleLabel.setForeground(list.getForeground());
                }

                panel.setOpaque(true);
                return panel;
            }
        });

        opinionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = opinionList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Rectangle cellBounds = opinionList.getCellBounds(index, index);
                        if (cellBounds != null && cellBounds.contains(e.getPoint())) {
                            Boolean currentState = expandedStates.getOrDefault(index, false);
                            expandedStates.put(index, !currentState);
                            opinionList.updateUI();
                        }
                    }
                }
            }
        });

        JScrollPane opinionScrollPane = new JScrollPane(opinionList);
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

        // Ju-enボタンの処理
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem juenMenuItem = new JMenuItem("Ju-enを押す");
        juenMenuItem.addActionListener(event -> addJuen());
        popupMenu.add(juenMenuItem);

        opinionList.setComponentPopupMenu(popupMenu);
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
                    "タイトルと内容を両方入力してください。",
                    "入力エラー",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Opinion opinion = new Opinion(title, content);
        TopicManager.getInstance().addOpinion(topicName, opinion);
        opinionListModel.addElement(opinion);

        JOptionPane.showMessageDialog(this,
                "オピニオンが投稿されました。",
                "投稿成功",
                JOptionPane.INFORMATION_MESSAGE);

        opinionTitleField.setText("");
        opinionContentArea.setText("");
    }

    private void addJuen() {
        Opinion selectedOpinion = opinionList.getSelectedValue();
        if (selectedOpinion != null) {
            selectedOpinion.addJuen();
            opinionList.repaint();
            JOptionPane.showMessageDialog(this,
                    "Ju-enを押しました。",
                    "Ju-en成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
