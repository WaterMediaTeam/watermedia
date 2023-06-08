package me.srrapero720.watermedia.compat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StardeosCompatTest2 {
    public static void main(String[] args) {
        String videoId = "63f46ee22ada8aa4fafc11cd"; // ID del video

        try {
            URL url = new URL("https://stardeos.com/api/v2/videos/" + videoId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Agregar los encabezados
            connection.setRequestProperty("Referer", "https://stardeos.com");
            connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
            connection.setRequestProperty("accept-language", "es-419,es;q=0.9,es-ES;q=0.8,en;q=0.7,en-GB;q=0.6,en-US;q=0.5");
            connection.setRequestProperty("cache-control", "max-age=0");
            connection.setRequestProperty("dnt", "1");
            connection.setRequestProperty("if-none-match", "W/\"937-q+BbFxUBt2FyxcUwnVmJUATAPUI\"");
            connection.setRequestProperty("sec-ch-ua", "\"Microsoft Edge\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"");
            connection.setRequestProperty("sec-ch-ua-mobile", "?0");
            connection.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            connection.setRequestProperty("sec-fetch-dest", "document");
            connection.setRequestProperty("sec-fetch-mode", "navigate");
            connection.setRequestProperty("sec-fetch-site", "none");
            connection.setRequestProperty("sec-fetch-user", "?1");
            connection.setRequestProperty("upgrade-insecure-requests", "1");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.57");


            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Analizar la respuesta JSON
                String jsonResponse = response.toString();
                ObjectMapper objectMapper = new ObjectMapper();

                try {
                    JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                    JsonNode filesNode = jsonNode.get("files");

                    List<FileUrlObject> fileUrlObjects = new ArrayList<>();

                    for (JsonNode fileNode : filesNode) {
                        String fileUrl = fileNode.get("fileUrl").asText();
                        int label = fileNode.get("label").asInt();
                        boolean alert = fileNode.get("alert").asBoolean();

                        FileUrlObject fileUrlObject = new FileUrlObject(fileUrl, label, alert);
                        fileUrlObjects.add(fileUrlObject);
                    }

                    // Utilizar la lista de objetos Java como desees
                    for (FileUrlObject fileUrlObject : fileUrlObjects) {
                        System.out.println("File URL: " + fileUrlObject.getFileUrl());
                        System.out.println("Label: " + fileUrlObject.getLabel());
                        System.out.println("Alert: " + fileUrlObject.isAlert());
                        System.out.println();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String fileUrl = parseFileUrl(jsonResponse);

                System.out.println("fileUrl: " + fileUrl);
            } else {
                System.out.println("Error en la solicitud. Codigo de respuesta: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String parseFileUrl(String jsonResponse) {
        // Analiza el JSON
        String fileUrl = null;
        int startIndex = jsonResponse.indexOf("\"files\":");
        if (startIndex != -1) {
            int endIndex = jsonResponse.indexOf("]", startIndex);
            if (endIndex != -1) {
                String filesJson = jsonResponse.substring(startIndex, endIndex + 1);
                int fileUrlIndex = filesJson.indexOf("\"fileUrl\":");
                if (fileUrlIndex != -1) {
                    int urlStartIndex = filesJson.indexOf("\"", fileUrlIndex + 1) + 1;
                    int urlEndIndex = filesJson.indexOf("\"", urlStartIndex);
                    fileUrl = filesJson.substring(urlStartIndex, urlEndIndex);
                }
            }
        }
        return fileUrl;
    }

    private static class FileUrlObject {
        private String fileUrl;
        private int label;
        private boolean alert;

        public FileUrlObject(String fileUrl, int label, boolean alert) {
            this.fileUrl = fileUrl;
            this.label = label;
            this.alert = alert;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public int getLabel() {
            return label;
        }

        public boolean isAlert() {
            return alert;
        }
    }
}