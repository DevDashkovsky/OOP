package ru.nsu.dashkovskii.util;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import ru.nsu.dashkovskii.model.Configuration;

/**
 * Утилитарный класс для загрузки конфигурации из JSON файла.
 */
public class JsonConfigLoader {

    /**
     * Загружает конфигурацию из указанного JSON файла.
     *
     * @param filename имя файла для загрузки
     * @return объект Configuration
     */
    public Configuration load(String filename) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (stream != null) {
                try (Reader reader = new InputStreamReader(stream)) {
                    return new Gson().fromJson(reader, Configuration.class);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config from resources: " + filename, e);
        }

        java.io.File file = new java.io.File(filename);
        if (file.exists()) {
            try (Reader reader = new java.io.FileReader(file)) {
                return new Gson().fromJson(reader, Configuration.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load config from file: " + filename, e);
            }
        }

        java.io.File devFile = new java.io.File("src/main/resources/" + filename);
        if (devFile.exists()) {
            try (Reader reader = new java.io.FileReader(devFile)) {
                return new Gson().fromJson(reader, Configuration.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load config from dev path: " + filename, e);
            }
        }

        throw new RuntimeException("Config file not found anywhere: " + filename);
    }
}
