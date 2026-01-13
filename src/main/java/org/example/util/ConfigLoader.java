package org.example.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    private static final String CONFIG_FILE_NAME = "config.json";

    public static AppConfig loadConfig() throws ConfigException {
        Path configPath = Path.of(CONFIG_FILE_NAME); // Obiect Path, independent de SO
        Gson gson = new Gson();

        // tratarea erorii de fișier lipsă
        if (!Files.exists(configPath)) {
            throw new ConfigException("Eroare: Fisierul de configurare '" + CONFIG_FILE_NAME +
                    "' lipseste. Aplicatia se inchide.",
                    new IOException("File not found: " + CONFIG_FILE_NAME));
        }

        try {
            // citirea întregului conținut ca String
            String jsonContent = Files.readString(configPath);

            // aruncă JsonSyntaxException la erori de format
            return gson.fromJson(jsonContent, AppConfig.class);

        } catch (IOException e) {
            // Prindem erori I/O neașteptate (permisiuni, etc.)
            throw new ConfigException("Eroare I/O neașteptată la citirea fisierului de configurare.", e);

        } catch (JsonSyntaxException e) {
            // Prindem erori de parsare JSON (fișier corupt)
            throw new ConfigException("Eroare: Fisierul de configurare este corupt sau formatat gresit (JSON invalid).", e);

        } catch (IllegalArgumentException e) {
            // Prindem eroarea aruncată de constructorul compact al AppConfig (date invalide)
            throw new ConfigException("Date invalide in configuratie: " + e.getMessage(), e);
        }
    }
}