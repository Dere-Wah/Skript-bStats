package org.derewah.skriptbstats.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.events.EvtScript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.derewah.skriptbstats.SkriptbStats;
import org.derewah.skriptbstats.wrappers.SkriptMetric;

public class EffAddAdvancedPieChart extends Effect {
    static{
        Skript.registerEffect(EffAddAdvancedPieChart.class,
                "send value %string% with weight %integer% " +
                        "to advanced pie chart with id %string% of metric %integer%");
    }

    private Expression<String> exprValue;
    private Expression<Integer> exprWeight;

    private Expression<String> exprChartId;
    private Expression<Integer> exprServiceId;


    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult){
        if(getParser().getCurrentSkriptEvent() == null ||
                !getParser().getCurrentSkriptEvent().getClass().equals(EvtScript.class) ||
                (getParser().getCurrentSkriptEvent().toString().contains("unload"))){
            Skript.error("You're registering a bStats metric outside of an on load event. Please use this effect only" +
                    " when loading a skript to prevent being rate limited.");
            return false;
        }

        exprValue = (Expression<String>) expressions[0];
        exprWeight = (Expression<Integer>) expressions[1];
        exprChartId = (Expression<String>) expressions[2];
        exprServiceId = (Expression<Integer>) expressions[3];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Integer serviceId = exprServiceId.getSingle(event);
        String chartId = exprChartId.getSingle(event);
        String value = exprValue.getSingle(event);
        Integer weight = exprWeight.getSingle(event);
        if(serviceId != null && chartId != null && value != null && weight != null){
            SkriptMetric m = SkriptbStats.getInstance().metricsManager.getMetric(serviceId);
            if(m == null){
                SkriptbStats.getInstance().getLogger().warning("[Skript-bStats] Could not find a registered metric" +
                        "with service id" + serviceId + ". Make sure to register it first, and then start it.");
            }else{
                boolean result = m.addAdvancedPie(chartId,  exprWeight, exprValue, event);
                if (!result){
                    SkriptbStats.getInstance().getLogger().warning("[Skript-bStats] Could not send advanced pie chart " +
                            "value to metric " +
                            serviceId + ". Probably the metric was already started.");
                }
            }


        }
    }

    @Override
    public String toString(Event event, boolean b) {
        Integer serviceId = exprServiceId.getSingle(event);
        String chartId = exprChartId.getSingle(event);
        String value = exprValue.getSingle(event);
        return "send value " +
                (value != null ? value.toString() : "") +
                "to advanced pie chart with id" +
                (chartId != null ? chartId.toString() : "") +
                " of metric"
                + (serviceId != null ? serviceId.toString() : "");
    }
}
