fun main() {
    val machine = parseInput(readInput("Day20"))
    repeat(1000){
        machine.hitButton()
    }
    machine.printSummary()
    machine.pulseSummary().println()
    fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    listOf<Long>(3793, 3911, 3917, 3929).reduce(::findLCM).println()
}

private fun parseInput(input: List<String>): DesertMachine {
    fun String.toModule() = split(" -> ")
        .let { (id, destinations) ->
            id to destinations.split(", ")
        }
        .let { (id, destinations) ->
            when (id.first()) {
                '%' -> FlipFlop(destinations = destinations, id = id.drop(1))
                '&' -> Conjunction(destinations = destinations, id = id.drop(1))
                else -> Broadcast(destinations = destinations)
            }
        }

    return input.map {
        it.toModule()
    }.associateBy { it.id }
        .let { DesertMachine(it) }
}

private interface Module {
    val id: String
    val destinations: List<String>
    fun accept(input: PulseTransmission): List<PulseTransmission>
}

private data class FlipFlop(var on: Boolean = false, override val destinations: List<String>, override val id: String) :
    Module {
    override fun accept(input: PulseTransmission): List<PulseTransmission> {
        if (input.level == PulseLevel.HIGH) return emptyList()
        if (on) {
            on = false
            return destinations.map { PulseTransmission(input.to, it, PulseLevel.LOW) }
        } else {
            on = true
            return destinations.map { PulseTransmission(input.to, it, PulseLevel.HIGH) }
        }
    }
}

private data class Conjunction(
    val latestInputs: MutableMap<String, PulseLevel> = mutableMapOf(),
    override val destinations: List<String>, override val id: String
) : Module {
    override fun accept(input: PulseTransmission): List<PulseTransmission> {
        latestInputs[input.from] = input.level
        return destinations
            .map { destination ->
                PulseTransmission(
                    input.to,
                    destination,
                    if (latestInputs.values.all { it == PulseLevel.HIGH }) PulseLevel.LOW else PulseLevel.HIGH
                )
            }

    }
}

private data class Broadcast(override val destinations: List<String>) : Module {
    override val id = "broadcaster"
    override fun accept(input: PulseTransmission): List<PulseTransmission> {
        return destinations.map { PulseTransmission(input.to, it, input.level) }
    }
}

private enum class PulseLevel {
    HIGH, LOW;
}

private data class PulseTransmission(val from: String, val to: String, val level: PulseLevel)

private data class DesertMachine(val modules: Map<String, Module>) {
    private var highs = 0L
    private var lows = 0L
    private val queue = ArrayDeque<PulseTransmission>()
    private var buttonPresses = 0L
    init {
        for(module in modules.values){
            for(destination in module.destinations){
                modules[destination]
                    ?.let { it as? Conjunction }
                    ?.let { it.latestInputs[module.id] = PulseLevel.LOW }
            }
        }
    }
    fun hitButton() {
        buttonPresses++
        enqueTransmission(PulseTransmission("button", "broadcaster", PulseLevel.LOW))
        while(queue.isNotEmpty()){
            val transmission = queue.removeFirst()
            if(transmission.to == "vd" && transmission.level == PulseLevel.HIGH){
                println("sending $transmission to conjunction with state ${(modules[transmission.to]!! as Conjunction).latestInputs} on buttonPress $buttonPresses")}
            modules[transmission.to]?.accept(transmission)?.forEach {
                enqueTransmission(it)
            }
        }
    }

    fun pulseSummary() = highs * lows
    private fun enqueTransmission(transmission: PulseTransmission){
        if(transmission.level == PulseLevel.HIGH) highs++ else lows++
        queue.addLast(transmission)
        //transmission.println()
    }

    fun printSummary() {
        println("Highs: $highs lows $lows product ${pulseSummary()}")
    }
}