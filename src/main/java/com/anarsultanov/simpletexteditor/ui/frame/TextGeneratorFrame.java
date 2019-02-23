package com.anarsultanov.simpletexteditor.ui.frame;

import com.anarsultanov.simpletexteditor.core.MarkovTextGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextGeneratorFrame extends JFrame {

    private JPanel contentPane;
    private JTextArea textArea;
    private JTextField textField;

    private MarkovTextGenerator markovTextGenerator;

    public TextGeneratorFrame(String source) {
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        markovTextGenerator = new MarkovTextGenerator(source);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        scrollPane.setViewportView(textArea);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        Box verticalBox = Box.createVerticalBox();
        panel.add(verticalBox);

        Box horizontalBox = Box.createHorizontalBox();
        verticalBox.add(horizontalBox);

        JLabel lblNumWords = new JLabel("Number of words: ");
        lblNumWords.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblNumWords.setHorizontalAlignment(SwingConstants.LEFT);
        horizontalBox.add(lblNumWords);

        textField = new JTextField();
        horizontalBox.add(textField);
        textField.setColumns(10);

        JButton btnButton = new JButton("Generate");
        btnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verticalBox.add(btnButton);
        btnButton.addActionListener(e -> generate());

        JPanel panel_1 = new JPanel();
        contentPane.add(panel_1, BorderLayout.NORTH);

        JLabel lblMarkovTextGenerator = new JLabel("Markov Text Generator");
        lblMarkovTextGenerator.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel_1.add(lblMarkovTextGenerator);
    }

    private void generate() {
        if (isInputValid()) {
            String mText = markovTextGenerator.generateText(Integer.parseInt(textField.getText()));
            setResult(mText);
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(textArea, "Invalid input.\nMust enter number > 0.");
        }
    }

    private boolean isInputValid() {
        String numString = textField.getText();
        return !(numString.equals("") || !isInteger(numString)
                || (Integer.parseInt(numString) <= 0));
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void setResult(String result) {
        textArea.setText(result);
    }
}
