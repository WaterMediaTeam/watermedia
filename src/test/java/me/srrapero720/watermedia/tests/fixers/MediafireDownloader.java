package me.srrapero720.watermedia.tests.fixers;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediafireDownloader {

    private static final int CHUNK_SIZE = 512 * 1024;  // 512KB

    public static String extractDownloadLink(String contents) {
        String pattern = "href=\"((http|https)://download[^\"]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(contents);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public static void download(String url, String output, boolean quiet) {
        String urlOrigin = url;

        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.5481.178 Safari/537.36");

            // This is the file
            while (!"Content-Disposition".equals(connection.getHeaderFieldKey(0))) {

                // Need to redirect with confirmation
                url = extractDownloadLink(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());

                if (url == null) {
                    System.err.println("Permission denied: " + urlOrigin);
                    System.err.println("Maybe you need to change permission over 'Anyone with the link'?");
                    return;
                }

                connection = (HttpURLConnection) new URL(url).openConnection();
            }

            if (output == null) {
                String contentDisposition = connection.getHeaderField("Content-Disposition");
                Pattern filenamePattern = Pattern.compile("filename=\"(.*)\"");
                Matcher filenameMatcher = filenamePattern.matcher(contentDisposition);
                if (filenameMatcher.find()) {
                    output = filenameMatcher.group(1);
                    output = new String(output.getBytes("ISO-8859-1"), "UTF-8");
                }
            }

            boolean outputIsPath = output != null;

            if (!quiet) {
                System.out.println("Downloading...");
                System.out.println("From: " + urlOrigin);
                System.out.println("To: " + (outputIsPath ? new File(output).getAbsolutePath() : output));
            }

            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            FileOutputStream out;
            if (outputIsPath) {
                String tmpFileName = File.createTempFile(new File(output).getName(), null).getAbsolutePath();
                out = new FileOutputStream(tmpFileName);
            } else {
                out = new FileOutputStream(output);
            }

            int total = connection.getContentLength();
            int bytesRead;
            byte[] buffer = new byte[CHUNK_SIZE];
            if (!quiet) {
                ProgressBar progressBar = new ProgressBar(total);
                progressBar.start();
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    progressBar.update(bytesRead);
                }
                progressBar.finish();
            } else {
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            in.close();
            out.close();

            if (outputIsPath) {
                File finalOutputFile = new File(output);
                File tempFile = new File(out.getChannel().toString());
                if (tempFile.renameTo(finalOutputFile)) {
                    tempFile.delete();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.err.println("Usage: MediafireDownloader <url>");
//            return;
//        }

        download("https://www.mediafire.com/file/mqo3olu8c0m89j7/Despedite_de_tu_cuenta_maquinola_desde_Suiza_360p.mp4", null, false);
    }

    static class ProgressBar {
        private int total;
        private int current = 0;

        public ProgressBar(int total) {
            this.total = total;
        }

        public void start() {
            System.out.print("Progress: ");
        }

        public void update(int bytesRead) {
            current += bytesRead;
            int percent = (int) (((double) current / total) * 100);
            System.out.print(percent + "% ");
        }

        public void finish() {
            System.out.println("100%");
        }
    }
}