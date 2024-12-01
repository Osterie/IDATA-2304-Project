package no.ntnu.constants;

public enum Resources {
    BASE_PATH("resources/"),
    IMAGES(BASE_PATH.getPath() + "images/"),
    AUDIO(BASE_PATH.getPath() + "audiofiles/");

    private final String path;


    Resources(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
