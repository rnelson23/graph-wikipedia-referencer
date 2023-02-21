package referencer.application;

import referencer.Document;

import javax.swing.*;

public class GUI {
    private JComboBox<Document> comboBox1;
    private JPanel panel1;
    private JComboBox<Document> comboBox2;
    private JButton goButton;
    private JTextArea textArea1;

    public GUI() {
        for (Document document : Main.graph.corpus.values()) {
            comboBox1.addItem(document);
            comboBox2.addItem(document);
        }

        goButton.addActionListener(e -> {
            Document src = (Document) comboBox1.getSelectedItem();
            Document dst = (Document) comboBox2.getSelectedItem();

            Document[] path = Main.graph.calculatePath(src, dst);

            if (path == null) {
                textArea1.setText("No path found");
                return;
            }

            StringBuilder text = new StringBuilder();

            for (Document document : path) {
                text.append(document.link).append("\n");
            }

            textArea1.setText(text.toString());
        });
    }

    public void initializeGUI() {
        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
