package koto_thing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.beans.JavaBean;
import java.beans.XMLDecoder;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 *  メインウィンドウクラス
 */
public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel rightPanel;
    private HomePanel homePanel;
    private SettingsPanel settingsPanel;
    private AppSettings settings;
    private NotificationManager notificationManager;
    private JuenCheckService juenCheckService;
    
    public MainWindow(){
        settings = new AppSettings();
        
        settings.addChangeListener(this::applyTheme);
        settings.addChangeListener(this::restartJuenCheck);
        
        /* メインウィンドウの基本設定 */
        setTitle("Ju-enOpinion");
        setSize(1500, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /* 通知マネージャーを初期化 */
        notificationManager = new NotificationManager(this);
        
        // Ju-enチェックサービスを開始
        juenCheckService = new JuenCheckService(settings, notificationManager);
        juenCheckService.startChecking();
        
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
        homePanel = new HomePanel(settings);
        settingsPanel = new SettingsPanel();
        
        // 右側パネルに各コンテンツパネルを追加
        rightPanel.add(homePanel, "ホーム");
        rightPanel.add(settingsPanel, "設定");
        
        // JSplitPaneで左右のパネルを結合する
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(200);
        
        /* ウィンドウにJSplitPaneを追加する */
        add(splitPane);
        
        /* 通知パネルをレイヤーペインに追加する */
        JLayeredPane layeredPane = getLayeredPane();
        JPanel notifPanel = notificationManager.getNotificationPanel();
        layeredPane.add(notifPanel, JLayeredPane.POPUP_LAYER);
        
        // ウィンドウサイズ変更時に通知パネルの位置を更新する
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateNotificationPanelPosition();
            }
        });
        
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
        
        // テーマを適用する
        applyTheme();
        
        /* ウィンドウを表示状態にする */
        menuList.setSelectedIndex(0);
        setVisible(true);
        
        /* 起動時に更新チェック */
        Timer updatedCheckTimer = new Timer(3000, e -> {
           UpdateChecker updateChecker = new UpdateChecker(this);
           updateChecker.checkForUpdates();
            ((Timer)e.getSource()).stop();
        });
        updatedCheckTimer.setRepeats(false);
        updatedCheckTimer.start();
    }
    
    public void loadTopics() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/topics"))
                .GET()
                .build();
        
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::updateTopicList)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
    
    public AppSettings getSettings(){
        return settings;
    }

    private void updateTopicList(String jsonResponse) {
        SwingUtilities.invokeLater(() -> {
            try {
                Gson gson = new Gson();
                java.lang.reflect.Type listType = new TypeToken<List<Topic>>(){}.getType();
                List<Topic> topics = gson.fromJson(jsonResponse, listType);

                homePanel.updateTopics(topics);
            } catch (Exception e) {
                System.err.println("JSONパースエラー: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void applyTheme() {
        Theme theme = settings.getTheme().equals("Dark")
                ? Theme.getDarkTheme()
                : Theme.getLightTheme();

        getContentPane().setBackground(theme.getBackgroundColor());
        rightPanel.setBackground(theme.getBackgroundColor());
    }
    
    private void updateNotificationPanelPosition() {
        JPanel notifPanel = notificationManager.getNotificationPanel();
        int x = getWidth() - 320;
        int y = 10;
        notifPanel.setBounds(x, y, 310, getHeight() - 20);
    }
    
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
    
    private void restartJuenCheck() {
        juenCheckService.stopChecking();
        juenCheckService.startChecking();
    }
    
    @Override
    public void dispose() {
        if (juenCheckService != null){
            juenCheckService.stopChecking();
        }
        
        if (homePanel != null) {
            homePanel.stopAutoRefresh();
        }
        
        super.dispose();
    }
}
