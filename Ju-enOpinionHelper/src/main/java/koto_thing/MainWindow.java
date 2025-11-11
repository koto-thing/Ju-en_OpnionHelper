package koto_thing;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 *  メインウィンドウクラス
 */
public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel rightPanel;
    
    public MainWindow(){
        /* メインウィンドウの基本設定 */
        setTitle("Ju-enOpinion");
        setSize(1500, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /* 左側のメニューパネル */
        String[] menuItems = { "ホーム", "設定", "情報"};
        JList<String> menuList = new JList<>(menuItems);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuList.setFont(new Font("Meiryo", Font.PLAIN, 16));
        
        // JListをスクロール可能にする
        JScrollPane leftPanel = new JScrollPane(menuList);
        leftPanel.setMinimumSize(new Dimension(150, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("メニュー"));
        
        /* 右側のコンテンツパネル */
        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        
        // 各コンテンツパネルを作成して右側パネルに追加
        HomePanel homePanel = new HomePanel();
        
        // 右側パネルに各コンテンツパネルを追加
        rightPanel.add(homePanel, "ホーム");
        
        // JSplitPaneで左右のパネルを結合する
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(200);
        
        /* ウィンドウにJSplitPaneを追加する */
        add(splitPane);
        
        /* メニューが選択された時の処理を追加する */
        menuList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    String selectedMenu = menuList.getSelectedValue();
                    if (selectedMenu != null) {
                        cardLayout.show(rightPanel, selectedMenu);
                    }
                }
            }
        });
        
        /* ウィンドウを表示状態にする */
        menuList.setSelectedIndex(0);
        setVisible(true);
    }
}
