package org.derewah.skriptbstats;

import org.derewah.skriptbstats.wrappers.Metric;

import java.util.HashMap;

public class SkriptMetrics {

    private HashMap<Integer, Metric> createdMetrics = new HashMap<>();

    public void registerMetric(int serviceId){
        if(createdMetrics.containsKey(serviceId)){
            createdMetrics.get(serviceId).shutdownMetric();
            createdMetrics.remove(serviceId);
        }
        Metric newMetric = new Metric(serviceId);
        createdMetrics.put(serviceId, newMetric);
    }

    public Metric getMetric(int serviceId){
        if(createdMetrics.containsKey(serviceId)){
            return createdMetrics.get(serviceId);
        }
        return null;
    }

    public void cleanupMetrics(){
        for(Metric m : createdMetrics.values()){
            if(m.isActive()){
                m.getLiveMetric().shutdown();
            }
        }
        createdMetrics.clear();
    }

}
