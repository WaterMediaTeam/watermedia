package org.watermedia.core.tools;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.WaterMedia;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PidTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");
    private static final Pattern PID_PATTERN = Pattern.compile("^(.+?)\\s+(\\d+)\\s+(\\S+)\\s+(\\d+)\\s+([\\d,]+\\s+\\S+)$");

    // NOTE FOR PANIC-ED PEOPLE: THIS IS A "TO DEFEAT YOUR ENEMY YOU HAVE TO BECOME YOUR ENEMY" LAUNCHER EDITION...
    // WITH AN EXTRA T
    public static List<ProcessData> getWindowsPids() {
        final ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "tasklist");
        builder.redirectErrorStream(true);

        try {
            Process p = builder.start();

            final List<ProcessData> process = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    if (i++ <= 1) continue;

                    Matcher matcher = PID_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        process.add(new ProcessData(
                                matcher.group(1).trim(),
                                Integer.parseInt(matcher.group(2)),
                                matcher.group(3),
                                Integer.parseInt(matcher.group(4)),
                                matcher.group(5).trim())
                        );
                    }
                }
            }

            int exitCode = p.waitFor();

            WaterMedia.LOGGER.debug(IT, "All PIDs collected, finished with status code {}", exitCode);

            return process;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static class ProcessData {
        private final String pidName;
        private final int pid;
        private final String sessionName;
        private final int sid;
        private final String mem;

        public ProcessData(String pidName, int pid, String sessionName, int sid, String mem) {
            this.pidName = pidName;
            this.pid = pid;
            this.sessionName = sessionName;
            this.sid = sid;
            this.mem = mem;
        }

        @Override
        public String toString() {
            return "ProcessData{" +
                    "pidName='" + pidName + '\'' +
                    ", pid=" + pid +
                    ", sessionName='" + sessionName + '\'' +
                    ", sid=" + sid +
                    ", mem='" + mem + '\'' +
                    '}';
        }
    }
}
