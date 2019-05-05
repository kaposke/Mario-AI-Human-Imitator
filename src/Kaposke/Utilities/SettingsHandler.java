package Kaposke.Utilities;

import Kaposke.Models.SettingsModel;
import Kaposke.Utilities.UtilitySingleton;
import com.google.gson.Gson;

import java.io.*;

public class SettingsHandler {

    public static String toJson(SettingsModel settings) {
        return new Gson().toJson(settings);
    }

    public static SettingsModel fromJson(String json) {
        return new Gson().fromJson(json, SettingsModel.class);
    }

    public static void saveSettings(SettingsModel settings) throws IOException {
        String path = UtilitySingleton.getInstance().getSettingsPath();

        File file = new File(path);

        FileWriter writer = new FileWriter(file);

        // Convert settings to json string
        Gson gson = new Gson();
        String json = gson.toJson(settings);

        // Write json to file
        writer.write(json);

        writer.close();
    }

    public static SettingsModel loadSettings() throws IOException {
        String path = UtilitySingleton.getInstance().getSettingsPath();

        File file = new File(path);

        // Return a blank model if doesn't exists
        if (!file.exists()) {
            System.out.println("Settings file not found.");
            return new SettingsModel();
        }

        // Read file
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);

        String json = reader.readLine();

        Gson gson = new Gson();
        SettingsModel settings = gson.fromJson(json, SettingsModel.class);

        return settings;
    }

}
