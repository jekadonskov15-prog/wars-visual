package com.wars.visual;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** config/warsvisual.json */
public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("warsvisual.json");
    public static Config I = new Config();

    public static final String[] MODULES = {
        "watermark", "info", "list", "keystrokes", "armor", "effects", "viewmodel", "swing", "ambience", "optimizer"
    };

    public String theme = "Nebula";
    public Map<String, Boolean> modules = new LinkedHashMap<>();
    /** модуль (или "gui") -> код клавиши GLFW */
    public Map<String, Integer> binds = new LinkedHashMap<>();

    // view model
    public float vmX = 0.10f, vmY = -0.05f, vmZ = -0.10f, vmScale = 0.9f, vmRotY = 5f;
    // ambience
    public int skyPreset = 1;       // 0 выкл, 1 Galaxy, 2 Sunset, 3 Aqua, 4 Crimson, 5 Toxic
    // optimizer
    public int optView = 6, optSim = 5;
    public float optEnt = 0.6f;
    public boolean optShadows = true, optParticles = true;
    // время и погода
    public int worldTime = 18000;   // 0..24000: 0 рассвет, 6000 полдень, 12000 закат, 18000 ночь
    public int weather = 2;         // 0 ясно, 1 дождь, 2 гроза
    // swing
    public int swingDuration = 10;
    public float swingArc = 40f, swingLift = 0.1f;

    public boolean on(String m) { return modules.getOrDefault(m, true); }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                Config c = GSON.fromJson(Files.readString(FILE), Config.class);
                if (c != null) I = c;
            }
            if (I.modules == null) I.modules = new LinkedHashMap<>();
            for (String m : MODULES) I.modules.putIfAbsent(m, true);
            if (I.binds == null) I.binds = new LinkedHashMap<>();
            I.binds.putIfAbsent("gui", 344); // Right Shift
            save();
        } catch (Exception e) {
            System.err.println("[WarsVisual] config error: " + e);
        }
    }

    public static void save() {
        try { Files.writeString(FILE, GSON.toJson(I)); } catch (Exception ignored) {}
    }
}
