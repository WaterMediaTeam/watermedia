package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.math.MathAPI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Main {
    public static final JFrame window = new JFrame(WaterMedia.NAME + ": Diagnosis Tool");
    public static final ClassLoader classLoader = Main.class.getClassLoader();

    public static void main(String... args) {
        window.setSize(1280, 720);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null); // Center window
        window.setIconImage(new ImageIcon(classLoader.getResource("icon.png")).getImage());

        // ROOT PANEL
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(MathAPI.argb(255, 40, 40, 40)));

        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        imagePanel.setBackground(new Color(MathAPI.argb(255, 20 ,20, 20))); // Establecer el fondo oscuro

        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(new ImageIcon(classLoader.getResource("banner.png")).getImage().getScaledInstance(600, 100, Image.SCALE_FAST)));
        imagePanel.add(logo, BorderLayout.WEST);
        root.add(imagePanel, BorderLayout.NORTH);

        JLabel info = new JLabel();
        info.setText("Diagnosis Tool");
        info.setFont(new Font("Default", Font.BOLD, 24));
        info.setForeground(Color.WHITE);
        info.setBorder(new EmptyBorder(0, 0, 0, 20));
        imagePanel.add(info, BorderLayout.EAST);

        JTextArea console = new JTextArea();
        console.setEditable(true);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        console.setBackground(Color.DARK_GRAY);
        console.setForeground(Color.WHITE);
        console.setMargin(new Insets(5, 5, 5, 5));
        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        caret.setVisible(true);
        console.setCaret(caret);

        JScrollPane scrollPane = new JScrollPane(console);
        // Configurar la política de desplazamiento vertical
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        System.setOut(new PrintStream(new ConsoleOutputStream(console, false)));
        System.setErr(new PrintStream(new ConsoleOutputStream(console, true)));

        root.add(scrollPane, BorderLayout.CENTER);

        // BUTTONS PANEL
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(new Color(MathAPI.argb(255, 20 ,20, 20))); // Establecer el fondo oscuro

        // Crear el botón y agregarlo al panel
        JButton generateReport = new JButton("Collect Crash Report");
        generateReport.setText("Collect Crash Report");
        generateReport.setBackground(Color.BLACK); // Color de fondo del botón
        generateReport.setForeground(Color.WHITE); // Color del texto del botón
        generateReport.setFocusPainted(false); // Eliminar el borde de enfoque
        generateReport.setMargin(new Insets(15, 25, 15, 25));
        generateReport.setFont(new Font("Default", Font.PLAIN, 16));
        generateReport.addActionListener(e -> {
            for (Component c: actionsPanel.getComponents()) c.setEnabled(false);
            performCollection();
        });

        JButton diagnosis = new JButton("Diagnosis");

        diagnosis.setBackground(Color.BLACK); // Color de fondo del botón
        diagnosis.setForeground(Color.WHITE); // Color del texto del botón
        diagnosis.setFocusPainted(false); // Eliminar el borde de enfoque
        diagnosis.setMargin(new Insets(15, 25, 15, 25));
        diagnosis.setFont(new Font("Default", Font.PLAIN, 16));
        diagnosis.addActionListener(e -> {
            for (Component c: actionsPanel.getComponents()) c.setEnabled(false);
            performDiagnosis();
        });

        actionsPanel.add(diagnosis);
        actionsPanel.add(generateReport);
        root.add(actionsPanel, BorderLayout.SOUTH);

        // Agregar el panel a la ventana
        window.add(root);
        window.setVisible(true);
    }

    private static class ConsoleOutputStream extends ByteArrayOutputStream {

        JTextArea textArea;
        boolean err;
        public ConsoleOutputStream(JTextArea textArea, boolean err) {
            this.textArea = textArea;
            this.err = err;
        }

        @Override
        public synchronized void write(int b) {
            super.write(b);
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            super.write(b, off, len);
            textArea.append(new String(b, off, len));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    private static void performDiagnosis() {
        // TODO: perform diagnosis
        System.out.println("Performing diagnosis...");
        System.err.println("Diagnosis Failed, WIP!");
    }

    private static void performCollection() {
        // TODO: perform data collection
        System.out.println("Collecting");
        System.err.println("Collecting Failed, WIP!");
    }
}
