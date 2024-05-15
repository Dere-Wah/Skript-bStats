package org.derewah.skriptbstats.wrappers;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import org.bstats.MetricsBase;
import org.bstats.charts.*;
import org.bstats.json.JsonObjectBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.derewah.skriptbstats.SkriptbStats;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public class SkriptMetric {

	private final Plugin plugin;
	private MetricsBase metricsBase = null;

	private final int serviceId;

	private List<CustomChart> customCharts = new ArrayList<>();

	/**
	 * Creates a new Metrics instance.
	 *
	 * @param serviceId The id of the service.
	 *                  It can be found at <a href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
	 */
	public SkriptMetric(int serviceId) {
		this.plugin = SkriptbStats.getInstance();
		this.serviceId = serviceId;
	}

	public boolean isActive(){
		return !(metricsBase == null);
	}


	public boolean startMetric(){
		if(isActive()){
			return false;
		}

		// Get the config file
		File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
		File configFile = new File(bStatsFolder, "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

		if (!config.isSet("serverUuid")) {
			config.addDefault("enabled", true);
			config.addDefault("serverUuid", UUID.randomUUID().toString());
			config.addDefault("logFailedRequests", false);
			config.addDefault("logSentData", false);
			config.addDefault("logResponseStatusText", false);

			// Inform the server owners about bStats
			config.options().header(
					"bStats (https://bStats.org) collects some basic information for plugin authors, like how\n" +
							"many people use their plugin and their total player count. It's recommended to keep bStats\n" +
							"enabled, but if you're not comfortable with this, you can turn this setting off. There is no\n" +
							"performance penalty associated with having metrics enabled, and data sent to bStats is fully\n" +
							"anonymous."
			).copyDefaults(true);
			try {
				config.save(configFile);
			} catch (IOException ignored) { }
		}

		// Load the data
		boolean enabled = config.getBoolean("enabled", true);
		String serverUUID = config.getString("serverUuid");
		boolean logErrors = config.getBoolean("logFailedRequests", false);
		boolean logSentData = config.getBoolean("logSentData", false);
		boolean logResponseStatusText = config.getBoolean("logResponseStatusText", false);

		metricsBase = new MetricsBase(
				"skript",
				serverUUID,
				serviceId,
				enabled,
				this::appendPlatformData,
				this::appendServiceData,
				submitDataTask -> Bukkit.getScheduler().runTask(plugin, submitDataTask),
				plugin::isEnabled,
				(message, error) -> this.plugin.getLogger().log(Level.WARNING, message, error),
				(message) -> this.plugin.getLogger().log(Level.INFO, message),
				logErrors,
				logSentData,
				logResponseStatusText
		);

		for (CustomChart c : customCharts){
			metricsBase.addCustomChart(c);
		}
		return true;
	}


	/**
	 * Creates a new Simple Pie Chart.
	 * @param chartId the string ID of the chart to add
	 * @param exprValue the expression callable of the value to calculate and send to charts.
	 * @param event the event in which the exprValue will be calculated.
	 * @return
	 */
	public boolean addSimplePie(String chartId, Expression<String> exprValue, Event event){
		if(isActive()){
			return false;
		}
		customCharts.add(new SimplePie(chartId, () -> exprValue.getSingle(event)));
		return true;
	}


	/**
	 * Creates a new Single Line Chart.
	 * @param chartId the string ID of the chart to add
	 * @param exprValue the expression callable of the value to calculate and send to charts.
	 * @param event the event in which the exprValue will be calculated.
	 * @return
	 */
	public boolean addSingleLine(String chartId, Expression<Integer> exprValue, Event event){
		if(isActive()){
			return false;
		}
		customCharts.add(new SingleLineChart(chartId, () -> exprValue.getSingle(event)));
		return true;
	}


	/**
	 * Creates a new Drilldown Pie Chart.
	 * @param chartId the string ID of the chart to add
	 * @param exprCategory the category which the value will be part of.
	 * @param exprValue the expression callable of the value to calculate and send to charts.
	 * @param exprWeight the weight of this value
	 * @param event the event in which the exprValue will be calculated.
	 * @return
	 */
	public boolean addDrilldownPie(String chartId, Expression<String> exprCategory, Expression<Integer> exprWeight,
								   Expression<String> exprValue, Event event){
		if(isActive()){
			return false;
		}
		customCharts.add(new DrilldownPie(chartId, () -> {
			String value = exprValue.getSingle(event);
			Integer weight = exprWeight.getSingle(event);
			String category = exprCategory.getSingle(event);
			if(value != null && weight != null && category != null){
				Map<String, Map<String, Integer>> map = new HashMap<>();
				Map<String, Integer> entry = new HashMap<>();
				entry.put(value, weight);
				map.put(category, entry);
				return map;
			}
			return null;
		}));
		return true;
	}


	/**
	 * Creates a new Drilldown Pie Chart.
	 * @param chartId the string ID of the chart to add
	 * @param exprValue the expression callable of the value to calculate and send to charts.
	 * @param exprWeight the weight of this value
	 * @param event the event in which the exprValue will be calculated.
	 * @return
	 */
	public boolean addAdvancedPie(String chartId, Expression<Integer> exprWeight,
								  Expression<String> exprValue, Event event){
		if(isActive()){
			return false;
		}
		customCharts.add(new AdvancedPie(chartId, () -> {
			String value = exprValue.getSingle(event);
			Integer weight = exprWeight.getSingle(event);
			if(value != null && weight != null){
				Map<String, Integer> map = new HashMap<>();
				map.put(value, weight);
				return map;
			}
			return null;
		}));
		return true;
	}


	public void shutdown(){
		if(isActive()){
			this.metricsBase.shutdown();
			metricsBase = null;
			customCharts.clear();
		}
	}

	private void appendPlatformData(JsonObjectBuilder builder) {
		builder.appendField("playerAmount", getPlayerAmount());
		builder.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
		builder.appendField("bukkitVersion", Bukkit.getVersion());
		builder.appendField("bukkitName", Bukkit.getName());
		builder.appendField("skriptVersion", Skript.getVersion().toString());

		builder.appendField("javaVersion", System.getProperty("java.version"));
		builder.appendField("osName", System.getProperty("os.name"));
		builder.appendField("osArch", System.getProperty("os.arch"));
		builder.appendField("osVersion", System.getProperty("os.version"));
		builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
	}

	private void appendServiceData(JsonObjectBuilder builder) {
		builder.appendField("pluginVersion", "Skript-bStats - " + plugin.getDescription().getVersion());
	}

	private int getPlayerAmount() {
		try {
			// Around MC 1.8 the return type was changed from an array to a collection,
			// This fixes java.lang.NoSuchMethodError: org.bukkit.Bukkit.getOnlinePlayers()Ljava/util/Collection;
			Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
			return onlinePlayersMethod.getReturnType().equals(Collection.class)
					? ((Collection<?>) onlinePlayersMethod.invoke(Bukkit.getServer())).size()
					: ((Player[]) onlinePlayersMethod.invoke(Bukkit.getServer())).length;
		} catch (Exception e) {
			return Bukkit.getOnlinePlayers().size(); // Just use the new method if the reflection failed
		}
	}


}