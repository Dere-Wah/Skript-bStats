package org.derewah.skriptbstats;

import org.bstats.bukkit.Metrics;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.derewah.skriptbstats.utils.UpdateChecker;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkriptbStats extends JavaPlugin {

    static SkriptbStats instance;
    SkriptAddon addon;
    public static FileConfiguration config;

    public SkriptMetrics skriptMetrics = null;
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

        skriptMetrics = new SkriptMetrics();


        Bukkit.getLogger().info("[Skript-bStats] has been enabled!");
    }

    public static SkriptbStats getInstance(){
        return instance;
    }

    public SkriptAddon getAddonInstance(){
        return addon;
    }

    public void onDisable(){
        skriptMetrics.cleanupMetrics();
    }
}
