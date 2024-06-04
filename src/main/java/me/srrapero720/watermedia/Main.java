package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.math.MathAPI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Main {
    public static void main(String... args) {
        // Configuración de la ventana principal
        JFrame frame = new JFrame(WaterMedia.NAME + ": Bug reporter");
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Centrar la ventana en la pantalla

        // Establecer el icono de la ventana
        ImageIcon icon = new ImageIcon(Main.class.getClassLoader().getResource("icon.png")); // Ruta al icono
        frame.setIconImage(icon.getImage());

        // Crear un panel con un layout manager que centre el contenido
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(MathAPI.argb(255, 20 ,20, 20))); // Establecer el fondo oscuro

        // Crear el botón y agregarlo al panel
        JButton button = new JButton("Test");
        button.setBackground(Color.BLACK); // Color de fondo del botón
        button.setForeground(Color.WHITE); // Color del texto del botón
        button.setBorder(new LineBorder(Color.BLUE, 4));
        panel.add(button);

        // Agregar el panel a la ventana
        frame.add(panel);
        frame.setVisible(true);
    }
}
