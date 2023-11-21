package me.srrapero720.watermedia.api.config;

import me.srrapero720.watermedia.api.WaterInternalAPI;
import me.srrapero720.watermedia.api.config.values.IntegerValue;
import me.srrapero720.watermedia.loaders.IBootCore;
import me.srrapero720.watermedia.tools.ByteTools;
import me.srrapero720.watermedia.tools.IOTool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Only for internal usage
 */
public class ConfigAPI extends WaterInternalAPI {
    public static final IntegerValue VAL = new IntegerValue("", 1);


    private static final Config DEFAULT_CONFIG = new Config();

    private static Map<String, Object> readFile(Path configFile) {
        Map<String, Object> values = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile.toFile()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                else line = line.trim();

                // IS A COMMENT? SKIP IT
                if (line.charAt(0) == '#') continue;

                // GET KEY-VALUES
                String[] keyValue = line.split("=");
                if (keyValue.length != 2) throw new IllegalArgumentException("Config file is damaged!");

                if (keyValue[1].charAt(0) == '"') { // ALWAYS ASSUME IS A STRING VALUE
                    values.put(keyValue[0], keyValue[1].substring(1).substring(0, keyValue[1].length() - 2));
                } else if (keyValue[1].equals("true") || keyValue[1].equals("false")) { // IS A BOOLEAN?
                    values.put(keyValue[0], Boolean.valueOf(keyValue[1]));
                } else if (ByteTools.parseInt(keyValue[1]) != null) { // GET INT VALUE
                    values.put(keyValue[0], ByteTools.parseInt(keyValue[1]));
                } else { // String
                    values.put(keyValue[0], keyValue[1]);
                }
            }
            return values;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Override
    public Priority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public boolean prepare(IBootCore bootCore) throws Exception {
        return true;
    }

    @Override
    public void start(IBootCore bootCore) throws Exception {
        if (!DEFAULT_CONFIG.refresh(readFile(bootCore.processDir().resolve("config/watermedia.wt")))) {
            Files.write(bootCore.processDir().resolve("config/watermedia.wt"), DEFAULT_CONFIG.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);
        }
    }

    @Override
    public void release() {

    }
}