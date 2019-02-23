package com.anarsultanov.simpletexteditor.ui;

import com.anarsultanov.simpletexteditor.core.SimpleDocument;

import javax.swing.*;
import java.awt.*;

public class FleschIndexPanel extends JPanel {

    private JTextField textField;

    public FleschIndexPanel(JTextArea textArea) {
        JLabel lblFlesch = new JLabel("Flesch Index");
        lblFlesch.setFont(new Font("Tahoma", Font.BOLD, 11));
        this.add(lblFlesch);

        textField = new JTextField();
        textField.setEditable(false);
        this.add(textField);
        textField.setColumns(10);

        JButton btnFlesch = new JButton("Count");
        this.add(btnFlesch);
        btnFlesch.addActionListener(e -> calculateFleschIndex(textArea.getText()));
    }

    private void calculateFleschIndex(String text) {
        double fIndex;

        // check if text input
        if (!text.equals("")) {

            // create Document representation of  current text
            SimpleDocument doc = new SimpleDocument(text);

            fIndex = doc.getFleschScore();

            //get string with two decimal places for index to
            String fString = String.format("%.2f", fIndex);

            // display string in text field
            textField.setText(fString);

        } else {
            // reset text field
            textField.setText("");
            //mainApp.showInputErrorDialog("No text entered.");
        }
    }
}
