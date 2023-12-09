fun main() {
    fun part1(input: List<String>, maxContents: Map<String, Int>): Int {
        return input.sumOf { game ->
            val impossibleResults = game
                // Remove the `Game X: ` part to only get the results
                .substringAfter(": ")
                // Split the string into separate results
                .split(", ", "; ")
                // Map each results into the amount and its color
                .map {
                    val tokens = it.split(' ')
                    tokens.first().toInt() to tokens.last()
                }
                // Take only the games where the amount exceeds the max allowed for its color
                .filter { it.first > maxContents.getValue(it.second) }

            // If there are no impossible results, take the id of the game and use it for the sum
            if (impossibleResults.isEmpty()) game.substringAfter("Game ").substringBefore(':').toInt()
            // Do not use the game if it has impossible results
            else 0
        }
    }

    fun part2(input: List<String>): Int {
        var result = 0

        input.forEach { game ->
            val results = game
                // Remove the `Game X: ` part to only get the results
                .substringAfter(": ")
                // Split the string into separate results
                .split(", ", "; ")
                // Group the results by colors and convert the amounts into actual integers
                .groupBy({ it.split(' ').last() }, { it.split(' ').first().toInt() })

            result += results.getValue("red").max() * results.getValue("green").max() * results.getValue("blue").max()
        }

        return result
    }

    val input = readInputLines("Day02")
    val maxContents = mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14
    )
    part1(input, maxContents).printFirstPart()
    part2(input).printSecondPart()
}