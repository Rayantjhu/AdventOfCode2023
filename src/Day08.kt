import Day08.part1
import Day08.part2

fun main() {
    val input = readInputLines("Day08")

    val test = """
        LLR

        AAA = (BBB, BBB)
        BBB = (AAA, ZZZ)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent().split('\n')

    val test2 = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent().split('\n')

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

    fun part2(input: List<String>): Int {
        return 0
    }
}