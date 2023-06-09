package me.srrapero720.watermedia.api.media.compat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

// Planned
public class StardeosCompat extends CompatVideoUrl {
    private static final Pattern regex = Pattern.compile("\\/video\\/([a-zA-Z0-9]+)$");

    @Override
    public boolean isValid(@NotNull URL url) {
        return url.getPath().contains("stardeos.com/video/");
    }

    @Override
    public String build(@NotNull URL url) {
        super.build(url);

        // OBTEN EL VIDEO ID
        var matcher = regex.matcher(url.toString());
        if (!matcher.find()) return null;
        var videoId = matcher.group(1);

        // INICIA CONEXION CON EL SERVIDOR EXTERNO
        HttpURLConnection connection = null;
        BufferedReader in = null;
        try {
            connection = (HttpURLConnection) new URL("https://stardeos.com/api/v2/videos/" + videoId).openConnection();
            connection.setRequestMethod("GET");

            // ENCABEZADOS
            connection.setRequestProperty("Referer", "https://stardeos.com");
            connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
            connection.setRequestProperty("accept-language", "es-419,es;q=0.9,es-ES;q=0.8,en;q=0.7,en-GB;q=0.6,en-US;q=0.5");
            connection.setRequestProperty("cache-control", "max-age=0");
            connection.setRequestProperty("dnt", "1");
            connection.setRequestProperty("if-none-match", "W/\"937-q+BbFxUBt2FyxcUwnVmJUATAPUI\"");
            connection.setRequestProperty("upgrade-insecure-requests", "1");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.57");

            // IF RESPONSE CODE IS SUCCESSFULY
            var response = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) response.append(inputLine);

                // Analizar la respuesta JSON
                String jsonResponse = response.toString();
                ObjectMapper objectMapper = new ObjectMapper();

                for (var fileNode : objectMapper.readTree(jsonResponse).get("files")) {
                    var fileUrl = fileNode.get("fileUrl").asText();
                    boolean alert = fileNode.get("alert").asBoolean();
                    if (fileUrl.contains("high") && !alert) return fileUrl;
                }

            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                LOGGER.error("Detected Stardeos.com but request to get real URL wasn't successfuly \n\nStatus code: {}\n\nResponse: {}", connection.getResponseCode(), response);
            }
        } catch (IOException e) {
            LOGGER.error("Detected Stardeos.com but Compat failed to get real URL", e);
        } finally {
            if (in != null) IOUtils.closeQuietly(in);
            if (connection != null) connection.disconnect();
        }

        return null;
    }
}
