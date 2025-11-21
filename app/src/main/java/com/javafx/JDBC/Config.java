package com.javafx.JDBC;


import java.util.Properties;

import java.io.InputStream;


public class Config {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            if (in == null) {
                throw new RuntimeException("application.properties no encontrado en resources.");
            }
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar application.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
