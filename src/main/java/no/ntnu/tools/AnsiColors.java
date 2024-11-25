package no.ntnu.tools;

public enum AnsiColors {
    
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    RED("\u001B[31m"),
    RESET("\u001B[0m");
    
    private final String color;
    
    AnsiColors(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return color;
    }
}
