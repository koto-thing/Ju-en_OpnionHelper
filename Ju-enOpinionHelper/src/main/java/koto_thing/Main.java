package koto_thing;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Main {
    /**
     *  エントリーポイント
     *  @params  args コマンドライン引数
     */
    public static void main(String[] args) {
        // システムのルックアンドフィールを設定
        FlatDarkLaf.setup();
        
        // メインウィンドウを表示
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }
}