package com.company;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Kuleczki");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new Panel());

        frame.setPreferredSize(new Dimension(800,600));
        frame.pack();
        frame.setVisible(true);
    }
}
