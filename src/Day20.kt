import Day20.part1
import Day20.part2
import java.util.*

fun main() {
    val input = readInputLines("Day20")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day20 {
    fun part1(input: List<String>): Int {
        val modules = parseInput(input)
        populateConjunctions(modules)

        var lowPulses = 0
        var highPulses = 0
        // Keep track of the origin, its target and its pulse
        val queue: Queue<Triple<String, String, Boolean>> = LinkedList()
        for (x in 1..1000) {
            // Each button press is a low pulse being sent
            lowPulses++

            // Queue all the broadcaster targets with a low pulse
            queue.addAll(modules["broadcaster"]!!.getPulsesToBeSent(false, "broadcaster"))

            while (queue.isNotEmpty()) {
                val (origin, target, pulse) = queue.poll()

                if (pulse) highPulses++ else lowPulses++

                // If the target is not a module, we continue to the next in the queue.
                val module = modules[target] ?: continue

                // Add the pulses that should be sent for the given module, its pulse and the origin
                queue.addAll(module.getPulsesToBeSent(pulse, origin))
            }
        }

        return lowPulses * highPulses
    }

    /**
     * Parses the input as a map of the modules' name and its module, which is either a broadcaster, flip-flop or a
     * conjunction.
     */
    fun parseInput(input: List<String>): Map<String, Module> {
        return input.filter { it.isNotBlank() }.associate { line ->
            val (left, destinations) = line.split(" -> ").let { it[0] to it[1].split(", ") }

            createModule(left, destinations).let { it.name to it }
        }
    }

    /** Returns a module based on the left side of the input line and the destinations. */
    fun createModule(left: String, destinations: List<String>): Module {
        if (left == "broadcaster") return Broadcaster(destinations)

        val type = left.first()
        val name = left.drop(1)

        return if (type == '%') FlipFlop(name, destinations) else Conjunction(name, destinations)
    }

    /** Populate the conjunctions with the memory for each of the modules that points to the conjunction. */
    fun populateConjunctions(modules: Map<String, Module>) {
        for (module in modules.values) {
            for (destination in module.destinations)
            // Only if the destination module is a conjunction, add this module to the memory of that conjunction
                if (modules[destination] is Conjunction) (modules[destination] as Conjunction).updatePulse(module.name)
        }
    }

    abstract class Module(val name: String, val destinations: List<String>) {
        abstract fun getPulsesToBeSent(pulse: Boolean, origin: String): List<Triple<String, String, Boolean>>
    }

    class Broadcaster(destinations: List<String>) : Module("broadcaster", destinations) {
        override fun getPulsesToBeSent(pulse: Boolean, origin: String) = destinations.map { Triple(name, it, false) }
    }

    class FlipFlop(name: String, destinations: List<String>) : Module(name, destinations) {
        var status = false
            private set

        override fun getPulsesToBeSent(pulse: Boolean, origin: String): List<Triple<String, String, Boolean>> {
            // High pulse is ignored
            if (pulse) return listOf()

            // Flip the status
            status = !status

            // Return a list of all pulses to be sent
            return destinations.map { Triple(name, it, status) }
        }
    }

    class Conjunction(name: String, destinations: List<String>) : Module(name, destinations) {
        val memory: Map<String, Boolean> = mutableMapOf()

        override fun getPulsesToBeSent(pulse: Boolean, origin: String): List<Triple<String, String, Boolean>> {
            // First update the memory
            updatePulse(origin, pulse)

            // True if all pulses are high pulses, false otherwise
            val allHighPulse = memory.values.all { it }
            // Low pulse is sent when all pulses in memory are high, otherwise it's a high pulse, so we negate the bool
            return destinations.map { Triple(name, it, !allHighPulse) }
        }

        fun updatePulse(name: String, pulse: Boolean = false) {
            (memory as MutableMap)[name] = pulse
        }
    }

    fun part2(input: List<String>): Long {
        val modules = parseInput(input)
        populateConjunctions(modules)

        val feed = modules.values.first { "rx" in it.destinations }.name
        val cycleLengths = mutableMapOf<String, Int>()
        val seen = modules.entries.filter { feed in it.value.destinations }.associate { it.key to 0 }.toMutableMap()

        var presses = 0
        // Keep track of the origin, its target and its pulse
        val queue: Queue<Triple<String, String, Boolean>> = LinkedList()
        while (seen.values.any { it == 0 }) {
            presses++

            // Queue all the broadcaster targets with a low pulse
            queue.addAll(modules["broadcaster"]!!.getPulsesToBeSent(false, "broadcaster"))

            while (queue.isNotEmpty()) {
                val (origin, target, pulse) = queue.poll()

                // If the target is not a module, we continue to the next in the queue.
                val module = modules[target] ?: continue

                if (feed == module.name && pulse) {
                    seen[origin] = seen[origin]!! + 1
                    cycleLengths.putIfAbsent(origin, presses)
                }

                // Add the pulses that should be sent for the given module, its pulse and the origin
                queue.addAll(module.getPulsesToBeSent(pulse, origin))
            }
        }

        return lcm(cycleLengths.values.map { it.toLong() })
    }

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    private fun lcm(a: Long, b: Long) = a * b / gcd(a, b)

    private fun lcm(numbers: List<Long>): Long = numbers.reduce { acc, i -> lcm(acc, i) }
}