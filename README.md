# Skript-bStats ![Downloads](https://img.shields.io/github/downloads/Dere-Wah/Skript-bStats/total)

A Skript Addon that allows users to safely track their skripts' performance using bStats.

![Stats](https://bstats.org/signatures/bukkit/Skript-bStats.svg)

# Documentation

## Effects

### Register New bStats Metric
```
(create|register) [new] [bstats] metric[s] with [service] id %integer%
```

Register a new metric for a specific service id. A metric is a "link" that periodically sends data to the bStats API.
This effect can only be performed in an On Load event.

<details>
	<summary>Register New Metric</summary>

        ```
            on load:
                register new bstats metric with service id 21875
                #add your custom charts here
                start bstats metric with service id 21875
                #after starting a metric it can't be no longer modified.
        ```
</details>

### Start bStats Metric
```
start [bstats] metric[s] with [service] id %integer%
```

Start a bStats metric that you previously registered. Please note that if you did not register the metric first with the
register metric effect, this will do nothing. After starting a metric, you can no longer edit it or add custom charts.
You should add custom charts in between Registering and Starting a metric.
This effect can only be performed in an On Load event.

<details>
	<summary>Start Metric</summary>

        ```
            on load:
                register new bstats metric with service id 21875
                #add your custom charts here
                start bstats metric with service id 21875
                #after starting a metric it can't be no longer modified.
        ```
</details>

### Shutdown Metric
```
(stop|shutdown|disable) [bstats] metric[s] with [service] id %integer%
```

Stops a bStats metric. The metric will stop sending data to the bStats servers. It is good practice to always add this
effect in your skripts, if you're using this addon. This is to stop tracking data when players disable your skripts. This
effect can only be used in on unload events.

<details>
	<summary>Stop Metric</summary>

        ```
            on unload:
                stop bstats metric with service id 21875
        ```
</details>


### Add Simple Pie Chart to Metric
```
send value %string% to simple pie chart with id %string% of metric %integer%
```
This effect adds a new Custom Simple Pie Chart to an existing metric. The value that will be sent to the metric can be a string, a
string variable, or a function that returns a string. The addon will calculate the value each time, so you can send in
dynamic data. These executions, however, will be "event-context-free". This means that you'll have to specify all of the
values that in an on load event might be omitted. Check other chart types for a better explanation on this matter. This
feature is good for tracking which features of your skript people are using, e.g. if some options of your skript are
enabled or not.
This effect can only be performed in an On Load event.

![image](https://github.com/Dere-Wah/Skript-bStats/assets/160314410/1fd6110d-b4ba-401e-9190-923ae3013eed)


<details>
	<summary>Simple Pie Chart</summary>

        ```
            Options:
	            version: 1.0

            on load:
                register new bstats metric with service id 21875
                send value "{@version}" to simple pie chart with id "deretest_version" of metric 21875
                start bstats metric with service id 21875
                #after starting a metric it can't be no longer modified.
        ```

</details>

### Add Advanced Pie Chart to Metric
```
send value %string% with weight %integer% to advanced pie chart with id %string% of metric %integer%
```
This effect adds a new Custom Advanced Pie Chart to a metric. This pie allows you to send multiple values with different
weights. For the string and weight you can execute functions that return the right type.
This effect can only be performed in an On Load event.

![image](https://github.com/Dere-Wah/Skript-bStats/assets/160314410/6cf69a87-a4ed-4026-b2ae-470076e49fb0)


<details>
	<summary>Advanced Pie Metric</summary>

        ```
            on load:
                register new bstats metric with service id 21875
                send value "Apples" with weight countItems(apple) to advanced pie chart with id "famous_foods" of metric 21875
	            send value "Bread" with weight countItems(bread) to advanced pie chart with id "famous_foods" of metric 21875
                start bstats metric with service id 21875
                #after starting a metric it can't be no longer modified.


            local function countItems(i: item) :: integer:
	            loop all players:
		        add amount of {_i} in loop-player's inventory to {_x}
	            return {_x}
        ```

</details>


### Add Drilldown Pie Chart to Metric
```
send value %string% with weight %integer% in category %string% to drilldown pie chart with id %string% of metric %integer%
```
This effect adds a new Custom Drilldown Pie Chart to a metric. This pie is the most complex of them all. It allows you to
send a value and it's weight to a category, that will be displayed against other categories. For example, if you want to
count the amount of friendly and hostile monsters, these 2 categories, when clicked, will display the types of each single
entity you're tracking. (view the example). It is really important to specify "in all worlds" when using such expressions,
as when the plugin will execute these it will treat them as a function, which is event-context free.
This effect can only be performed in an On Load event.

![image](https://github.com/Dere-Wah/Skript-bStats/assets/160314410/6879ea3e-9c52-4232-9f9e-86ab95c1e0cb)

![image](https://github.com/Dere-Wah/Skript-bStats/assets/160314410/0c9c753f-4cd0-4f53-bad7-0f678473a08e)



<details>
	<summary>Drilldown Pie Metric</summary>

        ```
            on load:
                register new bstats metric with service id 21875
	            send value "Zombies" with weight (amount of zombies in all worlds) in category "Monsters" to drilldown pie chart with id "active_mobs" of metric 21875
	            send value "Sheeps" with weight (amount of sheeps in all worlds) in category "Animals" to drilldown pie chart with id "active_mobs" of metric 21875
                start bstats metric with service id 21875
                #after starting a metric it can't be no longer modified.
        ```

</details>


### Add Single Line Chart to Metric
```
send value %integer% to single line chart with id %string% of metric %integer%
```
This effect adds a new Custom Single Chart to an existing metric. The value that will be sent to the metric can be a string, a
string variable, or a function that returns an integer. The bStats api will sum all of these numbers together from all
the servers using your script, and will display a line with the trend. This is good for activity tracking, such as how
many players are online when using your skript, etc. (this is specific example is actually useless, as there already is
a default line chart with this data.)
This effect can only be performed in an On Load event.

![image](https://github.com/Dere-Wah/Skript-bStats/assets/160314410/fcd7d9e5-7ffb-449c-80d6-6b87d5e7a2a8)


<details>
	<summary>Single Line Chart</summary>

        ```
            on load:
                register new bstats metric with service id 21875
                send value (size of all players) to single line chart with id "skript_active_players" of metric 21875
                start bstats metric with service id 21875
                #after starting a metric it can't be no longer modified.
        ```

</details>


## FAQ

#### Is this a good idea?

I don't know. I'm making this addon for fun and to see how the use of this feature might be used by the community. I do
not recommend using this addon and syntax in your finished skripts YET, as it's still being tested. The ultimate goal would
be to have these features be implemented in SkriptLang itself, to avoid even having server owners have to install this
addon at all simply for data tracking. If anyone wants to discuss in private with me about this, feel free to DM me on
https://t.me/DereWah or discord @DereWah. If you have a suggestion or bug feel free to open an issue.


