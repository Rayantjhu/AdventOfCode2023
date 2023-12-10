import Day04.part1
import Day04.part2

fun main() {
    val input = readInputLines("Day04")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

/** Gets the number of winning numbers for the given card. */
fun getAmountOfWinningNumbers(card: String): Int {
    // Split the card into its winning and owned numbers and filter out any blank strings
    val allNumbers = card.substringAfter(": ").split(" | ")
    val winningNumbers = allNumbers.first().split(' ').filter { it.isNotBlank() }
    val ownedNumbers = allNumbers.last().split(' ').filter { it.isNotBlank() }

    // Count all the owned numbers that are in the winning numbers
    return ownedNumbers.count { winningNumbers.contains(it) }
}

private object Day04 {
    /*
    Each scratchcard has winning numbers in the first part, delimited by a vertical bar (|) are the numbers that
    you have. Simply put, we can divide the card into its winning numbers and the numbers owned. Each number is
    separated by a space, so each can be obtained by splitting both winning and owned numbers. Then check for each
    owned number if it is in the winning numbers and calculate the card score
 */
    fun part1(input: List<String>): Int {
        return input.sumOf { card ->
            // Get the number of winning numbers for the card
            val winningAmount = getAmountOfWinningNumbers(card)
            // Double the number until we have the winning amount to calculate the score
            if (winningAmount == 0) 0 else (1..winningAmount).reduce { acc, _ -> acc * 2 }
        }
    }

    /*
        This time we need to find the number of instances for each card. The first instinct is: recursion. For each card,
        find all the winning numbers and find its copies and return the number of copies plus 1 (the original card).
        To make sure the runtime won't take extremely long, memoization is used to save already calculated amounts for a
        card.
     */
    fun part2(input: List<String>): Int {
        // Keep track of a map that puts in the already calculated copies for a card
        val savedCalculations = mutableMapOf<Int, Int>()
        return input.withIndex().sumOf { (index, value) -> findAmountOfCopies(value, input, index, savedCalculations) }
    }

    /** Finds the number of copies for the given card recursively. */
    fun findAmountOfCopies(
        card: String?,
        allCards: List<String>,
        currentIndex: Int,
        savedCalculations: MutableMap<Int, Int>
    ): Int {
        if (card == null) return 0

        // If the number of instances is already calculated, return it
        savedCalculations[currentIndex]?.let { return it }

        // Get the number of winning numbers for the card
        val winningAmount = getAmountOfWinningNumbers(card)

        // If there are no winning numbers, we don't need to look for more copies, so we return this instance, which is 1
        if (winningAmount == 0) {
            // Save the number of instances for this card
            savedCalculations[currentIndex] = 1
            return 1
        }

        // The starting amount is 1 because we also have to count the current card
        var amount = 1
        for (i in 1..winningAmount) {
            val newIndex = currentIndex + i
            // Increment the amount by recursively looking for the number of copies
            amount += findAmountOfCopies(allCards.getOrNull(newIndex), allCards, newIndex, savedCalculations)
        }

        // Save the number of instances for this card
        savedCalculations[currentIndex] = amount
        return amount
    }
}