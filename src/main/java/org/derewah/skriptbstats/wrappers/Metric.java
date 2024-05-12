package org.derewah.skriptbstats.wrappers;

import ch.njol.skript.lang.Expression;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.*;
import org.bukkit.event.Event;
import org.derewah.skriptbstats.SkriptbStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metric {

	private int serviceId;
	private ArrayList<CustomChart> chartList = new ArrayList<>();

	@Getter
	private boolean active = false;

	@Getter
	private Metrics liveMetric = null;


	public Metric(int serviceId){
		this.serviceId = serviceId;
	}

	public boolean addSimplePie(String chartId, Expression<String> exprValue, Event event){
		if(active){
			return false;
		}
		chartList.add(new SimplePie(chartId, () -> exprValue.getSingle(event)));
		return true;
	}

	public boolean addSingleLine(String chartId, Expression<Integer> exprValue, Event event){
		if(active){
			return false;
		}
		chartList.add(new SingleLineChart(chartId, () -> exprValue.getSingle(event)));
		return true;
	}

	public boolean addDrilldownPie(String chartId, Expression<String> exprCategory, Expression<Integer> exprWeight,
								   Expression<String> exprValue, Event event){
		if(active){
			return false;
		}
		chartList.add(new DrilldownPie(chartId, () -> {
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

	public boolean addAdvancedPie(String chartId, Expression<Integer> exprWeight,
								   Expression<String> exprValue, Event event){
		if(active){
			return false;
		}
		chartList.add(new AdvancedPie(chartId, () -> {
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

	public boolean startMetric(){
		if(active){
			return false;
		}
		active = true;
		liveMetric = new Metrics(SkriptbStats.getInstance(), serviceId);
		for (CustomChart c : chartList){
			liveMetric.addCustomChart(c);
		}
		return true;
	}

	public void shutdownMetric(){
		if(!active){
			return;
		}
		liveMetric.shutdown();
		liveMetric = null;
		chartList.clear();
		active = false;
	}


}
