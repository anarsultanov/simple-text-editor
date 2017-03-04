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

package com.anarsultanov.simpletexteditor;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class EditDistance extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textWord1;
	private JTextField textWord2;
	private JPanel panel;
	/**
	 * Create the frame.
	 */
	public EditDistance() {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		contentPane.add(panel);
		
		JLabel  lblWord1 = new JLabel("Word 1:");
		lblWord1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblWord1.setBounds(115, 103, 67, 19);
		lblWord1.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		textWord1 = new JTextField();
		textWord1.setBounds(206, 104, 117, 20);
		textWord1.setColumns(10);
		
		JLabel  lblWord2 = new JLabel("Word 2:");
		lblWord2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblWord2.setBounds(115, 133, 67, 20);
		
		textWord2 = new JTextField();
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
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleTextEditorMain.getEditDistanceFrame().dispose();
			}
		});
		
		Button buttonOK = new Button("OK");
		buttonOK.setFont(new Font("Dialog", Font.PLAIN, 12));
		buttonOK.setBounds(229, 169, 70, 22);
		panel.add(buttonOK);
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleOk();
			}
		});
	}

    private void handleOk() {
        if(isInputValid()) {
    			WPTree wp = new WPTree();
    			List<String> path = wp.findPath(textWord1.getText(), textWord2.getText());
    			if (path != null){
    				String words = "";
	    			int count = path.size()-1;
	    			for (String w: path) {
	    				words = words.concat(w);
	    				if (w != path.get(path.size()-1)){
	    					words = words.concat(" -> ");
	    				}
	    			}
	    			JOptionPane.showMessageDialog(this,
	    					words + "\nNumber of steps: " + count,
	    				    "Word Path",
	    				    JOptionPane.PLAIN_MESSAGE);
    			} else {
    				JOptionPane.showMessageDialog(this,
    						"Can not count Edit Distance for words:\n" + textWord1.getText() + " and " + textWord2.getText(),
	    				    "Something went wrong",
	    				    JOptionPane.PLAIN_MESSAGE);
    			}
    	    } else {
    		Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(textWord1, "You must input two words for Edit Distance.");
    	}
            
    }
    
    private boolean isInputValid() {
    	return !(textWord1.getText().equals("") || textWord2.getText().equals(""));
    }
}

