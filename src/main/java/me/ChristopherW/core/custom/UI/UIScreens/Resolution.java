package me.ChristopherW.core.custom.UI.UIScreens;

public class Resolution {
    public int width;
    public int height;
    public float aspectRatio;
    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
        this.aspectRatio = (float)width/(float)height;
    }
    public Resolution(String parsableResolution) {
        String[] dimensions = parsableResolution.split("x");
        this.width = Integer.parseInt(dimensions[0]);
        this.height = Integer.parseInt(dimensions[1]);
        this.aspectRatio = (float)width/(float)height;
    }

    public String toString() {
        return String.format("%dx%d", width, height);
    }
}
