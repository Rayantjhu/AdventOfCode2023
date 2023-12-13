import Day09.part1
import Day09.part2

fun main() {
    val input = readInputLines("Day09")

    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day09 {
    /*
        Brute-forcing through this puzzle works quite well. The differences are calculated each time until all are 0.
        As long as it is not 0, the last difference is added to the last value of the history.
     */
    fun part1(input: List<String>): Int {
        // Parse input to a list of lists containing the numbers
        val histories = input.map { history -> history.split(' ').map { it.toInt() } }

        // Return the sum of the extrapolated values
        return histories.sumOf { history ->
            // Get all differences for the history
            var differences = history.zipWithNext { a, b -> b - a }
            // Start the result with the last value of the history
            var result = history.last()
            while (!differences.all { it == 0 }) {
                // Increase the result with the last difference
                result += differences.last()
                // Update the differences to have the differences of the previously calculated differences
                differences = differences.zipWithNext { a, b -> b - a }
            }
            // Use the result for the sum
            result
        }
    }

    /*
        The second part does not differ much from the first. The major difference is that we need to extrapolate
        backwards, instead of forward. The same can be done as the first part, mostly. However, since we are subtracting,
        we need to carefully look at the differences first, rather than simply just adding/subtracting the difference to
        the first/last value of the history.
     */
    fun part2(input: List<String>): Int {
        val histories = input.map { history -> history.split(' ').map { it.toInt() } }

        return histories.sumOf { history ->
            var currentDifferences = history.zipWithNext { a, b -> b - a }
            // Keep track of all differences
            val allDifferences = mutableListOf<List<Int>>()
            while (!currentDifferences.all { it == 0 }) {
                allDifferences.add(currentDifferences)
                currentDifferences = currentDifferences.zipWithNext { a, b -> b - a }
            }

            var result = 0
            // Find the amount needed to be subtracted from the first value
            for (i in allDifferences.lastIndex downTo 0) result = allDifferences[i].first() - result

            // Sum the backwards extrapolated value
            history.first() - result
        }
    }
}