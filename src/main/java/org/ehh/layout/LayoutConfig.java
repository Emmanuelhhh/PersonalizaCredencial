package org.ehh.layout;

import org.ehh.model.CredencialType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LayoutConfig {

    public Card card;
    public Print print;
    public Map<CredencialType, TypeConfig> types;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        public int w;
        public int h;
        public String orientation; // "LANDSCAPE"
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Print {
        public String printerNameContains;
        public int backRotateDeg;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TypeConfig {
        public String frontPath;
        public String backPath;
        public Back back;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Back {
        public Rect photo;
        public Map<String, Field> fields; // nombre, curp, discapacidad, contacto
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rect {
        public int x;
        public int y;
        public int w;
        public int h;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Field {
        public int x;
        public int y;
        public int maxW;
        public int fontSize;
        public boolean bold;
    }
}