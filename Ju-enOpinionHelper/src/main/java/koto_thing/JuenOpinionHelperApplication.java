package koto_thing;

import com.formdev.flatlaf.FlatDarkLaf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class JuenOpinionHelperApplication {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> new MainWindow());

        SpringApplication.run(JuenOpinionHelperApplication.class, args);
    } 
}
