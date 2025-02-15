package frc.entech.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Map;

public class PresetHandler {
    public static final String USB_SETTINGS_FILE_PATH = "/u/presets.json";

    private static File getFile(){
        return new File(USB_SETTINGS_FILE_PATH);
    }

    public static void writePresets( Map<String,Double> presets){
        File file = getFile();
    }

    public static Map<String, Double> readPresetsJson() {

        // Correct path for USB stick
        File file = getFile();
        

        // Check if the file exists on the USB stick
        if (!file.exists()) {
            return Map.of();  // Return empty map if file doesn't exist
        }

        try (FileReader reader = new FileReader(file)) {
            // Define the type for Map<String, Double>
            MapPresetReader.readPresets(reader);
        } catch (IOException e) {
            // Handle any errors (e.g., file reading issues)
            e.printStackTrace();
        }

        return Map.of();  // Return empty map in case of an error
    }

    public static void main(String[] args) {
        // Test the function
        Map<String, Double> presets = readPresetsJson();
        System.out.println(presets);
    }
}
