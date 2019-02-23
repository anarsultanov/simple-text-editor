/*
Copyright (c) 2017 Anar Sultanov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.anarsultanov.simpletexteditor.ui.frame;

import com.anarsultanov.simpletexteditor.core.NearbyWords;
import com.anarsultanov.simpletexteditor.core.WPTree;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class EditDistanceFrame extends JFrame {

    private WPTree wpTree;

    /**
     * Create the frame.
     */
    public EditDistanceFrame(NearbyWords nearbyWords) {
        wpTree = new WPTree(nearbyWords);
        JFrame editDistanceFrame = this;
        setBounds(100, 100, 450, 300);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        contentPane.add(panel);

        JLabel lblWord1 = new JLabel("Word 1:");
        lblWord1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblWord1.setBounds(115, 103, 67, 19);
        lblWord1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField textWord1 = new JTextField();
        textWord1.setBounds(206, 104, 117, 20);
        textWord1.setColumns(10);

        JLabel lblWord2 = new JLabel("Word 2:");
        lblWord2.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblWord2.setBounds(115, 133, 67, 20);

        JTextField textWord2 = new JTextField();
        textWord2.setBounds(206, 135, 117, 20);
        textWord2.setColumns(10);
        panel.setLayout(null);
        panel.add(lblWord1);
        panel.add(textWord1);
        panel.add(lblWord2);
        panel.add(textWord2);

        JLabel lblEnter = new JLabel("Enter two words...");
        lblEnter.setForeground(Color.BLACK);
        lblEnter.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblEnter.setBounds(100, 11, 285, 55);
        panel.add(lblEnter);

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 77, 436, 2);
        panel.add(separator);

        Button buttonCancel = new Button("Cancel");
        buttonCancel.setFont(new Font("Dialog", Font.PLAIN, 12));
        buttonCancel.setBounds(134, 169, 70, 22);
        panel.add(buttonCancel);
        buttonCancel.addActionListener(e -> editDistanceFrame.dispose());

        Button buttonOK = new Button("OK");
        buttonOK.setFont(new Font("Dialog", Font.PLAIN, 12));
        buttonOK.setBounds(229, 169, 70, 22);
        panel.add(buttonOK);
        buttonOK.addActionListener(e -> countDistance(textWord1.getText(), textWord2.getText()));
    }

    private void countDistance(String textWord1, String textWord2) {
        if (isInputValid(textWord1, textWord2)) {
            List<String> path = wpTree.findPath(textWord1, textWord2);
            if (path != null) {
                String words = "";
                int count = path.size() - 1;
                for (String w : path) {
                    words = words.concat(w);
                    if (!w.equals(path.get(path.size() - 1))) {
                        words = words.concat(" -> ");
                    }
                }
                JOptionPane.showMessageDialog(this,
                        words + "\nNumber of steps: " + count,
                        "Word Path",
                        JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Can not count Edit Distance for words:\n" + textWord1 + " and " + textWord2,
                        "Something went wrong",
                        JOptionPane.PLAIN_MESSAGE);
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "You must input two words for Edit Distance.");
        }

    }

    private boolean isInputValid(String textWord1, String textWord2) {
        return !(textWord1.isEmpty() || textWord2.isEmpty());
    }
}

