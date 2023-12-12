import Day08.part1
import Day08.part2

fun main() {
    val input = readInputLines("Day08")

    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day08 {
    /*
        A simple brute force will do the trick. We can traverse until we have found ZZZ. If we have not yet found ZZZ,
         increase the step and find the next element based on the direction.
     */
    fun part1(input: List<String>): Int {
        // Get the directions (LR)
        val directions = input.first().toCharArray()

        // Get all elements as a triple
        val elements = input
            // Drop the first two rows as these are the directions and an empty row
            .drop(2)
            .map {
                // Split the string into the current element and the left and right elements it points to
                val tokens = it.split(" = ")
                // Remove the first and last characters which are the parenthesis and split left and right
                val next = tokens.last().substring(1..<tokens.last().lastIndex).split(", ")

                // A triple consisting of the current element, left and right elements
                Triple(tokens.first(), next.first(), next.last())
            }

        // Start from AAA
        var currentElement = elements.find { it.first == "AAA" } ?: return 0

        var steps = 0
        // Stop once we have reached ZZZ
        while (currentElement.first != "ZZZ") {
            // Update the current element to have the next element according to the direction
            currentElement =
                elements.find { it.first == if (directions[steps % directions.size] == 'L') currentElement.second else currentElement.third }
                    ?: return 0
            steps++
        }

        return steps
    }

    /*
        Doing the same as part1 but for more elements at the same time will probably take millions of steps. Each of the
        starting elements its steps can be calculated independently. Eventually, there will be a list of steps for each
        of the starting elements until they reach an element ending in Z.

        Using the Least Common Multiple (LCM), the solution can be found.
     */
    fun part2(input: List<String>): Long {
        val directions = input.first().toCharArray()

        val elements = input
            .drop(2)
            .associate {
                val tokens = it.split(" = ")
                val next = tokens.last().substring(1..<tokens.last().lastIndex).split(", ")

                tokens.first() to Pair(next.first(), next.last())
            }

        val allSteps = elements.keys.filter { it.endsWith('A') }.map { startingElement ->
            var currentElement = startingElement
            var steps = 0L
            while (!currentElement.endsWith('Z')) {
                currentElement = elements[currentElement]?.let {
                    if (directions[(steps % directions.size).toInt()] == 'L') it.first else it.second
                } ?: return 0
                steps++
            }
            steps
        }

        return lcm(allSteps)
    }

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    private fun lcm(a: Long, b: Long) = a * b / gcd(a, b)

    private fun lcm(numbers: List<Long>): Long = numbers.reduce { acc, i -> lcm(acc, i) }
}