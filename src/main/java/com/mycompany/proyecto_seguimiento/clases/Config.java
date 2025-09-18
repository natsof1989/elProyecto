package com.mycompany.proyecto_seguimiento.clases;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties props = new Properties();

    static {
        try (InputStream input = Config.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo config.properties");
            }
            props.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error al cargar config.properties", ex);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
