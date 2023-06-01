package me.srrapero720.watermedia.api.compat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.srrapero720.watermedia.watercore_supplier.ThreadUtil;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MegaNZCompat extends AbstractCompat {
    @Override
    public boolean isValid(@NotNull URL url) {
        return url.getHost().equals("mega.nz") && url.getPath().startsWith("/file/") && url.getRef() != null && !url.getRef().isEmpty();
    }

    @Override
    public String build(@NotNull URL url) {
        super.build(url);
        var path = url.getPath();
        var id = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('#'));
        var key = url.getRef(); // FILE ENCRYPT KEY (concat with ID if videos can't be loaded)

        var domain = "meganz";
        var lang = "en";
        var apiURL = "https://eu.api.mega.co.nz/cs?domain=" + domain + "&lang=" + lang;

        String[][] value = {{"a", "g"}, {"g", "1"}, {"ssl", "0"}, {"p", id}};

        var rawPOST = new Gson().toJson(value);
        return ThreadUtil.tryAndReturn(defaultVar -> {
            var req = (HttpURLConnection) new URL(apiURL).openConnection();
            req.setRequestMethod("POST");
            req.setDoOutput(true);
            req.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            var output = req.getOutputStream();
            output.write(rawPOST.getBytes());
            output.flush();
            output.close();

            var reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            var res = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                res.append(line);
            }

            reader.close();
            req.disconnect();

            var result = JsonParser.parseString(res.toString()).getAsJsonArray().get(0).getAsJsonObject();
            var link = result.get("g").getAsString();
            var size = result.get("s").getAsString(); // Useful in a future

            return link;
        }, null);
    }
}
