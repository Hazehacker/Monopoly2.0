package com.Pojo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class exe {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setBounds(550,400,300,300);
        jFrame.setVisible(true);
        JButton jButton = new JButton("请点击！");
        jFrame.add(jButton);
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog jDialog = new JDialog(jFrame,"选着",true);
                jDialog.setLayout(new GridLayout(3,2));
                jDialog.add(new JPanel());
                jDialog.add(new JPanel());
                jDialog.add(new JButton("是"));
                jDialog.add(new JButton("否"));
                jDialog.add(new JPanel());
                jDialog.add(new JPanel());
                jDialog.setVisible(true);
            }
        });
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}