package org.derewah.skriptbstats.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.events.EvtScript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.derewah.skriptbstats.SkriptbStats;

public class EffRegisterMetrics extends Effect {
    static{
        Skript.registerEffect(EffRegisterMetrics.class,
                "(create|register) [new] [bstats] metric[s] with [service] id %integer%");
    }
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

        exprServiceId = (Expression<Integer>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {

        Integer serviceId = exprServiceId.getSingle(event);

        if(serviceId != null){
            SkriptbStats.getInstance().metricsManager.registerMetric(serviceId);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        Integer serviceId = exprServiceId.getSingle(event);
        return "register bstats metrics with id "
                + (serviceId != null ? serviceId.toString() : "");
    }
}
