import Day18.part1
import Day18.part2
import kotlin.math.abs

fun main() {
    val input = readInputLines("Day18")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day18 {
    val NUM_TO_DIRECTION = mapOf(
        '0' to 'R',
        '1' to 'D',
        '2' to 'L',
        '3' to 'U'
    )

    /*
        We can save each point like a polygon and use those to calculate the area using the shoelace formula. However,
        in typical AoE fashion, using just the shoelace formula will not give us the result we're looking for because it
        each point starts at the middle of each square. Using Pick's theorem, we can calculate the interior area using
        the area calculated in the shoelace formula and add that up to the boundaries, and we have our solution.
     */
    fun part1(input: List<String>): Long {
        val preprocessed = input.map { digPlan ->
            digPlan.split(' ').let { (direction, meters) -> direction.first() to meters.toLong() }
        }

        return calculateArea(preprocessed)
    }

    fun calculateArea(input: List<Pair<Char?, Long>>): Long {
        val points = mutableListOf(0L to 0L)
        var currentRow = 0L
        var currentCol = 0L
        var boundaries = 0L
        input.forEach { (direction, meters) ->

            when (direction) {
                'L' -> currentCol -= meters
                'R' -> currentCol += meters
                'U' -> currentRow -= meters
                'D' -> currentRow += meters
            }

            boundaries += meters
            points.add(currentRow to currentCol)
        }

        val area = (0..<points.lastIndex).sumOf { i ->
            points[i].second * points[i + 1].first - points[i].first * points[i + 1].second
        }.then { abs(it) / 2 }

        val interior = area - boundaries / 2 + 1

        return interior + boundaries
    }

    /*
        The only difference in the second part is the way we read data. The meters are just many times bigger.
     */
    fun part2(input: List<String>): Long {
        val preprocessed = input.map { digPlan ->
            digPlan
                .substringAfter('#')
                .substringBefore(')')
                .let { NUM_TO_DIRECTION[it.last()] to it.dropLast(1).toLong(radix = 16) }
        }

        return calculateArea(preprocessed)
    }
}