package org.ehh.layout;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class LayoutLoader {

    public static LayoutConfig load(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(path), LayoutConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("No pude leer layout.json: " + e.getMessage(), e);
        }
    }
}