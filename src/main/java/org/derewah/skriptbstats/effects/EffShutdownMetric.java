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

public class EffShutdownMetric extends Effect {
    static{
        Skript.registerEffect(EffShutdownMetric.class,
                "(stop|shutdown|disable) [bstats] metric[s] with [service] id %integer%");
    }
    private Expression<Integer> exprServiceId;



    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult){
        if(getParser().getCurrentSkriptEvent() == null ||
                !getParser().getCurrentSkriptEvent().getClass().equals(EvtScript.class) ||
            (!getParser().getCurrentSkriptEvent().toString().contains("unload"))){
            Skript.error("You're stopping a bStats metric outside of an on unload event. Please use this effect only" +
                    " when unloading a skript to prevent being rate limited.");
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
            if(m != null){
                m.shutdown();
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        Integer serviceId = exprServiceId.getSingle(event);
        return "shutdown bstats metrics with id "
                + (serviceId != null ? serviceId.toString() : "");
    }
}
