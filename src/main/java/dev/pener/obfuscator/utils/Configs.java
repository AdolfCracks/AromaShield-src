package dev.pener.obfuscator.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Settings;
import dev.pener.obfuscator.core.Transformer;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Configs {

    public String FILE_NAME;

    public Configs(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
                System.err.println("Config file doesn't exist! [ " + FILE_NAME + " ]");
            }

            Path path = Paths.get(FILE_NAME);
            String content = new String(Files.readAllBytes(path));
            if (!content.startsWith("{") || !content.endsWith("}")) {
                file.delete();
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                fw.append("{}");
                fw.flush();
                fw.close();
            }
            content = new String(Files.readAllBytes(path));

            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new StringReader(content));
            JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);

            ArrayList<Transformer> transformers = Obfuscator.getTransformers();
            boolean allModules = true;
            for (Transformer transformer : transformers) {
                String s = transformer.getName();
            //    System.out.println(s);
                boolean visible = transformer.isVisible();
                if (visible) {
                    if (!jsonObject.has(s)) {
                        allModules = false;
                        jsonObject.addProperty(s, true);
                    }
                }
            }

            String jarfileIN = "";
            String jarfile_OUT = "";

            if (jsonObject.has("JarFile_IN")) {
                jarfileIN = jsonObject.get("JarFile_IN").getAsString();
            } else {
                jsonObject.addProperty("JarFile_IN", "example.jar");
                System.err.println("Jarfile input not found!");
            }

            if (jsonObject.has("JarFile_OUT")) {
                jarfile_OUT = jsonObject.get("JarFile_OUT").getAsString();
            } else {
                jsonObject.addProperty("JarFile_OUT", "example-obf.jar");
                System.err.println("Jarfile output not found!");
            }

            List<String> renameExclude = new ArrayList<>();
            if (jsonObject.has("renameExclude")) {
                renameExclude = gson.fromJson(jsonObject.get("renameExclude"), new TypeToken<List<String>>() {}.getType());
            } else {
                allModules = false;
                JsonArray jsonArray = new JsonArray();
                jsonArray.add("package/");
                jsonObject.add("renameExclude",jsonArray);
            }

            List<String> fullExclude = new ArrayList<>();
            if (jsonObject.has("fullExclude")) {
                fullExclude = gson.fromJson(jsonObject.get("fullExclude"), new TypeToken<List<String>>() {}.getType());
            } else {
                allModules = false;
                JsonArray jsonArray = new JsonArray();
                jsonArray.add("package/");
                jsonObject.add("fullExclude",jsonArray);
            }

            List<String> packageToRename = new ArrayList<>();
            if (jsonObject.has("packageToRename")) {
                packageToRename = gson.fromJson(jsonObject.get("packageToRename"), new TypeToken<List<String>>() {}.getType());
            } else {
                allModules = false;
                JsonArray jsonArray = new JsonArray();
                jsonArray.add("package/");
                jsonObject.add("packageToRename",jsonArray);
            }
            String renameMode = "";
            if (jsonObject.has("renameMode")) {
                renameMode = jsonObject.get("renameMode").getAsString();
            } else {
                allModules = false;
                jsonObject.addProperty("renameMode","long");
            }
            if (!renameMode.equalsIgnoreCase("short") && !renameMode.equalsIgnoreCase("long")) {
                System.err.println("Invalid rename mode setting to long!");
                renameMode = "long";
            }
            if (!allModules) {
                System.err.println("Invalid config json detected setting all values to true!");
                saveJsonData(FILE_NAME, jsonObject);
                return;
            }


            List<Transformer> TransformerConfig = new ArrayList<>();
            for (String key : jsonObject.keySet()) {
                if (jsonObject.get(key).isJsonPrimitive() && jsonObject.get(key).getAsJsonPrimitive().isBoolean()) {
                    boolean value = jsonObject.get(key).getAsBoolean();
                    for (Transformer t : transformers) {
                        if (key.equalsIgnoreCase(t.getName()) && value) {
                            TransformerConfig.add(t);
                        }
                    }
                }
            }

            File file2 = new File(jarfile_OUT);
            if (!file2.exists()) {
                file2.createNewFile();
            }

            Settings settings = new Settings();
            for (String s : renameExclude) {
                settings.excludeFromRenaming(s);
            }
            for (String s : fullExclude) {
                settings.fullExclude(s);
            }
            for (String s : packageToRename) {
                settings.addPackagesToRename(s);
            }
            settings.renameTo(renameMode);
            try {
                Obfuscator.getInstance().obfuscate(jarfileIN, jarfile_OUT, TransformerConfig, settings);
            } catch (Exception e) {
                System.err.println("Something went wrong while obfuscating!");
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveJsonData(String filePath, JsonObject jsonObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(gson.toJson(jsonObject));
        } catch (IOException e) {
            System.err.println("Error occurred saving json!");
        }
    }
}
