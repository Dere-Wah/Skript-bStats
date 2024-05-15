package org.derewah.skriptbstats;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SkriptbStats extends JavaPlugin {

    static SkriptbStats instance;
    SkriptAddon addon;
    public static FileConfiguration config;

    public MetricsManager metricsManager = null;
    public void onEnable(){
        instance = this;
        addon = Skript.registerAddon(this);
        try {
            addon.loadClasses("org.derewah.skriptbstats");
        } catch(IOException e){
            e.printStackTrace();
        }
        addon.setLanguageFileDirectory("lang");

        //new UpdateChecker(this, 110009).getVersion(version -> {
        //    if(this.getDescription().getVersion().equals(version)){
        //        getInstance().getLogger().info("Skript-bStats is up to date! Current:" + version);
        //    }else{
        //        getInstance().getLogger().info("Skript-bStats is out of date. Current: " +
        //                this.getDescription().getVersion() + " Please update. New version: " + version);
        //    }
        //});

        metricsManager = new MetricsManager();



        Bukkit.getLogger().info("[Skript-bStats] has been enabled!");
    }

    public static SkriptbStats getInstance(){
        return instance;
    }

    public SkriptAddon getAddonInstance(){
        return addon;
    }

    public void onDisable(){
        metricsManager.cleanupMetrics();
    }
}
