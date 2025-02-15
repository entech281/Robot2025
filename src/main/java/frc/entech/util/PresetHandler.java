package frc.entech.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PresetHandler {
    public static final String USB_PATH = "/u/";
    public static final String USB_SETTINGS_FILE_PATH = "/u/presets.json";

    private static File getFile(){
        return new File(USB_SETTINGS_FILE_PATH);
    }

    public static boolean USBStickConnected() {
        return (new File(USB_PATH).exists());
    }

    public static boolean presetFileExists() {
        return getFile().exists();
    }

    public static void writePresets( Map<String,Double> presets){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Write JSON to a file
        try (FileWriter writer = new FileWriter(getFile())) {
            gson.toJson(presets, writer);
        } catch (IOException e) {
        }
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
            return MapPresetReader.readPresets(reader);
        } catch (IOException e) {
        }

        return Map.of();  // Return empty map in case of an error
    }

    public static void main(String[] args) {
        // Test the function
        Map<String, Double> presets = readPresetsJson();
        System.out.println(presets);
    }
}
