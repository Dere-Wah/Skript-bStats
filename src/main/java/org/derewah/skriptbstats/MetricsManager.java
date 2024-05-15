package org.derewah.skriptbstats;

import org.derewah.skriptbstats.wrappers.SkriptMetric;

import java.util.HashMap;

public class MetricsManager {

    private HashMap<Integer, SkriptMetric> createdMetrics = new HashMap<>();

    public void registerMetric(int serviceId){
        if(createdMetrics.containsKey(serviceId)){
            createdMetrics.get(serviceId).shutdown();
            createdMetrics.remove(serviceId);
        }
        SkriptMetric newMetric = new SkriptMetric(serviceId);
        createdMetrics.put(serviceId, newMetric);
    }

    public SkriptMetric getMetric(int serviceId){
        if(createdMetrics.containsKey(serviceId)){
            return createdMetrics.get(serviceId);
        }
        return null;
    }

    public void cleanupMetrics(){
        for(SkriptMetric m : createdMetrics.values()){
            if(m.isActive()){
                m.shutdown();
            }
        }
        createdMetrics.clear();
    }

}
