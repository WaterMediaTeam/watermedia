package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.math.MathAPI;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.*;

public class Main {
    public static final Logger LOGGER = Logger.getLogger("WaterMedia");
    public static final JFrame window = new JFrame(WaterMedia.NAME + ": Diagnosis Tool");
    public static final DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

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

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new ConsoleFormatter(console));
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);

//        System.setOut(new PrintStream(new ConsoleOutputStream(console, false)));
//        System.setErr(new PrintStream(new ConsoleOutputStream(console, true)));

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
            new CollectionTask().start();
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

    private static void performDiagnosis() {
        // TODO: perform diagnosis
        LOGGER.info("Performing diagnosis");
        LOGGER.warning("Diagnosis Failed, WIP!");
    }

    private static class CollectionTask extends Thread {

        private final Path root;
        private final Result result = new Result();
        public CollectionTask() {
            File location = new File("");
            String locationStr = location.toString();
            if (locationStr.endsWith("/mods") || locationStr.endsWith("/mods/")) {
                location = location.getParentFile();
                LOGGER.info("Attached to folder '" + locationStr + "'");
            } else {
                LOGGER.warning("We are running outside mods folder, output might not be the ideal");
            }

            this.root = location.toPath();
        }

        @Override
        public void run() {
            LOGGER.info("Scanning crash-report folder...");

            Path crashReportFolder = root.resolve("crash-report");
            try {
                File[] crashReportFiles = crashReportFolder.toFile().listFiles();
                if (crashReportFiles == null || crashReportFiles.length == 0)
                    throw new NullPointerException("No such directory or is empty");

                Arrays.sort(crashReportFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                File crashReport = crashReportFiles[0];

                LOGGER.info("Collected files: " + Arrays.toString(crashReportFiles));


            } catch (Exception e) {
                LOGGER.warning("Failed to get crash-report files..." + e.getMessage());
            }


        }

        public static class Result {
            private File crashReport;
            private File log;
            private File debug;
            private File[] hsErrPids;

            public void setCrashReport(File crashReport) {
                this.crashReport = crashReport;
            }

            public void setDebug(File debug) {
                this.debug = debug;
            }

            public void setLog(File log) {
                this.log = log;
            }

            public void setHsErrPids(File[] hsErrPids) {
                this.hsErrPids = hsErrPids;
            }
        }
    }


    private static void performCollection() {
        LOGGER.info("Looking for data...");
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
