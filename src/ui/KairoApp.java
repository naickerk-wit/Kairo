package ui;

import javax.swing.JFrame;
import model.Workspace;

public class KairoApp {

    public static void main(String[] args) {

        // Create the workspace model
        Workspace workspace = new Workspace();

        //Create window
        JFrame window = new JFrame("Kairo");

        window.setSize(1000, 700);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        // Add main UI panel
        MainPanel panel = new MainPanel(workspace);
        window.add(panel);

        window.setVisible(true);
    }
}