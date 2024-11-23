package org.watermedia;

import me.srrapero720.watermedia.Main;
import org.watermedia.api.MathAPI;
import org.watermedia.tools.ThreadTool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.logging.*;

public class WaterMediaApp {
    public static final Logger LOGGER = Logger.getLogger("WaterMedia");
    public static final Executor EXECUTOR = ThreadTool.executorReduced("app-task");
    public static final String JAR_PATH = WaterMediaApp.class.getProtectionDomain().getCodeSource().getLocation().toString().replace("file:///", "").replace("file:/", "");
    public static final Path ROOT_PATH = new File(JAR_PATH).getParentFile().getParentFile().toPath();
    public static final DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final ClassLoader CLASS_LOADER = Main.class.getClassLoader();

    public static final JTextArea CONSOLE = new JTextArea();
    public static final JFrame WINDOW = new JFrame(WaterMedia.NAME + ": Diagnosis Tool");
    public static final JPanel PANEL = new JPanel(new BorderLayout());
    public static final JPanel ACTIONS_PANEL = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    public static boolean test_weAreOnModsFolder() {
        var root = ROOT_PATH.toFile();
        var files = root.listFiles();
        for (var f: files == null ? new File[0] : files) {
            if (f.getName().equals("mods")) {
                return true;
            }
        }

        return false;
    }

    public static void task_collectLogs() {
        var file = ROOT_PATH.toFile();
        for (var f: file.listFiles()) {

        }
    }

    public static void task_collectCrashReports() {

    }

    public static void task_collectHsErrPid() {

    }

    public static void task_collectOsDetails() {

    }

    public static void task_collectNetworkDetails() {

    }

    public static void runDiagnosisTool() {

    }

    public static void runLogCollections() {
        for (Component c: ACTIONS_PANEL.getComponents()) c.setEnabled(false);
        EXECUTOR.execute(WaterMediaApp::task_collectLogs);
        EXECUTOR.execute(WaterMediaApp::task_collectCrashReports);
        EXECUTOR.execute(WaterMediaApp::task_collectHsErrPid);
        EXECUTOR.execute(WaterMediaApp::task_collectOsDetails);
        EXECUTOR.execute(WaterMediaApp::task_collectNetworkDetails);
        EXECUTOR.execute(() -> {
            long logPrinted = System.currentTimeMillis();
            long targetTime = logPrinted + 10000; // +10 secs
            int count = 10;
            while (System.currentTimeMillis() < targetTime) {
                if (logPrinted < System.currentTimeMillis()) {
                    LOGGER.info("Closing in " + count--);
                    logPrinted = System.currentTimeMillis() + 1000;
                }
            }
            WINDOW.dispose();
        });
    }

    private static void onCreate() {
        LOGGER.info("Welcome to the diagnosis tool");
        LOGGER.info("==== Click on any of the options to start ====");

        if (!test_weAreOnModsFolder()) {
            for (var c: ACTIONS_PANEL.getComponents()) {
                c.setEnabled(false);
                LOGGER.severe("WaterMedia is not placed on Mods folder, please ask for personal support on Discord");
            }
        }
    }

    private static void createWindow() {
        WINDOW.setSize(1280, 720);
        WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WINDOW.setLocationRelativeTo(null);
        WINDOW.setIconImage(new ImageIcon(CLASS_LOADER.getResource("icon.png")).getImage());
        createLayout();
        PANEL.add(ACTIONS_PANEL, BorderLayout.SOUTH);
        WINDOW.add(PANEL);
        WINDOW.setVisible(true);
        onCreate();
    }

    private static void createLayout() {
        PANEL.setBackground(new Color(MathAPI.argb(255, 40, 40, 40)));

        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        imagePanel.setBackground(new Color(MathAPI.argb(255, 20 ,20, 20))); // Establecer el fondo oscuro

        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(new ImageIcon(CLASS_LOADER.getResource("banner.png")).getImage().getScaledInstance(600, 100, Image.SCALE_FAST)));
        imagePanel.add(logo, BorderLayout.WEST);
        PANEL.add(imagePanel, BorderLayout.NORTH);

        JLabel info = new JLabel();
        info.setText("Diagnosis Tool");
        info.setFont(new Font("Default", Font.BOLD, 24));
        info.setForeground(Color.WHITE);
        info.setBorder(new EmptyBorder(0, 0, 0, 20));
        imagePanel.add(info, BorderLayout.EAST);

        CONSOLE.setEditable(true);
        CONSOLE.setLineWrap(true);
        CONSOLE.setWrapStyleWord(true);
        CONSOLE.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        CONSOLE.setBackground(Color.DARK_GRAY);
        CONSOLE.setForeground(Color.WHITE);
        CONSOLE.setMargin(new Insets(5, 5, 5, 5));
        DefaultCaret caret = new DefaultCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        caret.setVisible(true);
        CONSOLE.setCaret(caret);

        JScrollPane scrollPane = new JScrollPane(CONSOLE);
        // Configurar la política de desplazamiento vertical
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new ConsoleFormatter(CONSOLE));
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);

        PANEL.add(scrollPane, BorderLayout.CENTER);

        // BUTTONS PANEL
        ACTIONS_PANEL.setBackground(new Color(MathAPI.argb(255, 20 ,20, 20))); // Establecer el fondo oscuro

        // Crear el botón y agregarlo al panel
        JButton generateReport = new JButton("Collect Crash Report");
        generateReport.setText("Collect Crash Report");
        generateReport.setBackground(Color.BLACK); // Color de fondo del botón
        generateReport.setForeground(Color.WHITE); // Color del texto del botón
        generateReport.setFocusPainted(false); // Eliminar el borde de enfoque
        generateReport.setMargin(new Insets(15, 25, 15, 25));
        generateReport.setFont(new Font("Default", Font.PLAIN, 16));
        generateReport.addActionListener(e -> runLogCollections());

        JButton diagnosis = new JButton("Diagnosis");

        diagnosis.setBackground(Color.BLACK); // Color de fondo del botón
        diagnosis.setForeground(Color.WHITE); // Color del texto del botón
        diagnosis.setFocusPainted(false); // Eliminar el borde de enfoque
        diagnosis.setMargin(new Insets(15, 25, 15, 25));
        diagnosis.setFont(new Font("Default", Font.PLAIN, 16));
        diagnosis.addActionListener(e -> {
            for (Component c: ACTIONS_PANEL.getComponents()) c.setEnabled(false);
            runDiagnosisTool();
        });

        ACTIONS_PANEL.add(diagnosis);
        ACTIONS_PANEL.add(generateReport);
    }

    public static void main(String... args) {
        createWindow();
    }

    private static class ConsoleFormatter extends Formatter {
        public static final String RESET = "\u001B[0m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String PURPLE = "\u001B[35m";

        JTextArea textArea;
        public ConsoleFormatter(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public String format(LogRecord record) {
            String color = getColorLevel(record.getLevel());
            String time = "[" + FORMAT.format(new Date(record.getMillis())) + "]";
            String threadLevel = "[" + Thread.currentThread().getName() + "/" + record.getLevel().toString().substring(0, 4) + "]";
            String loggerMarker = "[" + record.getLoggerName() + "/" + "]";

            String r = time + " " + threadLevel + " " + loggerMarker + ": " + record.getMessage() + (record.getThrown() != null ? record.getThrown().toString() : "") + "\n";

            textArea.append(r);
            textArea.setCaretPosition(textArea.getDocument().getLength());
            return color + r + RESET;
        }

        public String getColorLevel(Level level) {
            if (level.equals(Level.SEVERE)) {
                return RED;
            } else if (level.equals(Level.WARNING)) {
                return YELLOW;
            } else if (level.equals(Level.INFO)) {
                return GREEN;
            } else if (level.equals(Level.CONFIG)) {
                return BLUE;
            } else if (level.intValue() <= Level.FINE.intValue()) {
                return PURPLE;
            } else {
                return RESET;
            }
        }
    }
}
