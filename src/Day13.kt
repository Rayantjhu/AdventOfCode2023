import Day13.part1
import Day13.part2

fun main() {
    val input = readInput("Day13").split("\n\n").map { it.lines() }
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day13 {
    /*
        To find the reflection in a pattern, we iterate over indices of the rows/cols starting at 1. For each index,
        slice the pattern into an above and below list and check if these rows are the same. To not repeat code, when
        checking for columns, we simply transpose the pattern, essentially rotating the pattern by 90 degrees, so we can
        check by rows again.
     */
    fun part1(input: List<List<String>>): Int {
        return input.sumOf { pattern ->
            findMirror(pattern)?.let { return@sumOf it * 100 }
            findMirror(pattern.transpose())?.let { return@sumOf it }
            0
        }
    }

    private fun findMirror(pattern: List<String>, matchingDifference: Int = 0): Int? {
        for (i in 1..pattern.lastIndex) {
            val above = pattern.slice(0..<i).reversed()
            val below = pattern.slice(i..pattern.lastIndex)

            if (above.zip(below).sumOf { countDifferences(it.first, it.second) } == matchingDifference) return i
        }

        return null
    }

    private fun List<String>.transpose() = List(first().length) { i -> map { it[i] }.joinToString("") }

    fun part2(input: List<List<String>>): Int {
        return input.sumOf { pattern ->
            findMirror(pattern, 1)?.let { return@sumOf it * 100 }
            findMirror(pattern.transpose(), 1)?.let { return@sumOf it }
            0
        }
    }

    private fun countDifferences(left: String, right: String) = left.zip(right).count { it.first != it.second }
}