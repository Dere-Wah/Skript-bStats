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

public class EffStartMetrics extends Effect {
    static{
        Skript.registerEffect(EffStartMetrics.class,
                "start [bstats] metric[s] with [service] id %integer%");
    }
    private Expression<Integer> exprServiceId;



    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult){
        if(getParser().getCurrentSkriptEvent() == null ||
                !getParser().getCurrentSkriptEvent().getClass().equals(EvtScript.class) ||
            (getParser().getCurrentSkriptEvent().toString().contains("unload"))){
            Skript.error("You're starting a bStats metric outside of an on load event. Please use this effect only" +
                    " when loading a skript to prevent being rate limited.");
            return false;
        }

        exprServiceId = (Expression<Integer>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {

        Integer serviceId = exprServiceId.getSingle(event);

        if(serviceId != null){
            SkriptMetric m = SkriptbStats.getInstance().metricsManager.getMetric(serviceId);
            if(m == null){
                SkriptbStats.getInstance().getLogger().warning("[Skript-bStats] Could not find a registered metric" +
                        "with service id" + serviceId + ". Make sure to register it first, and then start it.");
            }else{
                boolean result = m.startMetric();

                if(!result){
                    SkriptbStats.getInstance().getLogger().info("[Skript-bStats] Did not start metric with id " +
                            serviceId + ". It probably is already started. Make sure to implement metric shutdown in your" +
                            "script unload event to update your metric charts on script reloads.");
                }
            }

        }
    }

    @Override
    public String toString(Event event, boolean b) {
        Integer serviceId = exprServiceId.getSingle(event);
        return "start bstats metrics with id "
                + (serviceId != null ? serviceId.toString() : "");
    }
}
