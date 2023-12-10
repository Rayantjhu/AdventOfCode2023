fun main() {
    /*
        The main focus for this part is finding what type the hand is and its sorting. We can first transform the input
        to have its type calculated already, or this can be done in the sorting algorithm. Because algorithms typically
        compare objects more than once, these calculations have to be made multiple times, which would be a waste.
        Instead, the calculations will be done beforehand the sorting.

        Determining the type of the hand is really easy; in fact, all that needs to be done is finding the number of
        groups and its sizes. If a hand consists of only one group, all cards are the same; five of a kind. If it has
        two groups and the biggest group has a size of 4; four of a kind. If the biggest group has a size of 3, it's a
        full-house, etc.

        Sorting the hands based on the built-in sorting can be challenging as we need to check the cards if the type is
        the same for two cards, hence the merge-sort will be used.
     */
    fun part1(input: List<String>): Int {
        // Precalculate the hand, bid and the hand type for each line and store it in a triple
        val hands = input.map { line ->
            val tokens = line.split(' ')
            val hand = tokens.first()
            val bid = tokens.last().toInt()

            Triple(hand, bid, getType(hand))
        }

        // Sort the hands and sum the bid multiplied by its rank
        return mergeSort(hands).withIndex().sumOf { (index, hand) ->
            // Multiply the bid by its rank
            hand.second * (index + 1)
        }
    }

    /*
        This time the J card acts as a wildcard. This means that it can act as any card to achieve the highest possible
        type of hand. At the same time, J now is the weakest standalone card.

        Determining the type of hand is now a bit more challenging, we need to check if a hand has one or more jokers
        and then how it affects the hand. For a detailed walkthrough, see the `getType` function. This function has been
        reworked for the second part to reuse the code using in the first part. Alongside the `getType`, the mergeSort
        and `isBetterOrEqualHand` have been slightly reworked to take the jokers as a wildcard into account.
     */
    fun part2(input: List<String>): Int {
        // Precalculate the hand, bid and the hand type for each line and store it in a triple
        val hands = input.map { line ->
            val tokens = line.split(' ')
            val hand = tokens.first()
            val bid = tokens.last().toInt()

            Triple(hand, bid, getType(hand, withJoker = true))
        }

        return mergeSort(hands, withJoker = true).withIndex().sumOf { (index, hand) ->
            hand.second * (index + 1)
        }
    }

    val input = readInputLines("Day07")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

/** Finds the type of the given hand. By default, does not take joker as wildcard into account. */
fun getType(hand: String, withJoker: Boolean = false): Int {
    val groups = mutableMapOf<Char, Int>()

    hand.forEach { groups[it] = groups.getOrDefault(it, 0) + 1 }

    val groupsCount = groups.size
    val jokerCount = groups.getOrDefault('J', 0)
    val hasJoker = jokerCount > 0 && withJoker
    val biggestGroup = groups.maxOf { it.value }

    /*
    Five of a kind:
    AAAAA <- without wildcard
    AAAAJ
    AAAJJ
    AAJJJ
    AJJJJ
    ^^^^^ Always 2 groups
    Here we only care that it has 2 groups, the number of jokers does not matter

    Four of a kind:
    AAAAB <- without wildcard
    AAAJB
    AAJJB
    AJJJB
    ^^^^^ Always 3 groups
    Here we care about the number of jokers if the biggest group is 2, then there should be 2 jokers, otherwise if
    there's only one joker, it would be a full house. If the biggest group is 3, the number of jokers can only be 1
    or 3, and it does not make a difference.

    Full-house:
    AABBB <- without wildcard
    AABBJ
    AAJBB
    ^^^^^ Always 3 groups but only one joker (more than 1 joker will be either four or five of a kind)
    Here we only care that the biggest group is 2 and there is one joker.

    Three of a kind:
    AAA12 <- without wildcard
    AAJ12
    AJJ12
    ^^^^^ Always 4 groups and max always 2
    Here we only care that there's 4 groups and the biggest group always is 2.

    Two pair:
    AABB1 <- without wildcard
    AJBB1 <- Three of a kind at best
    JJBB1 <- Four of a kind at best
    Using a joker you can never get two pair as the best.

    One pair:
    AA123 <- without wildcard
    AJ123
    AA12J <- Three of a kind at best
    ^^^^^ Always 5 groups and 1 joker
    */

    // Five of a kind
    return if (groupsCount == 1) 7
    // Five of a kind with a joker
    else if (hasJoker && groupsCount == 2) 7
    // Four of a kind
    else if (groupsCount == 2 && biggestGroup == 4) 6
    // Four of a kind with a joker
    else if (hasJoker && groupsCount == 3 && (biggestGroup == 3 || (biggestGroup == 2 && jokerCount == 2))) 6
    // Full-house
    else if (groupsCount == 2 && biggestGroup == 3) 5
    // Full-house with a joker
    else if (hasJoker && groupsCount == 3 && biggestGroup == 2) 5
    // Three of a kind
    else if (groupsCount == 3 && biggestGroup == 3) 4
    // Three of a kind with a joker
    else if (hasJoker && groupsCount == 4 && biggestGroup == 2) 4
    // Two pair
    else if (groupsCount == 3 && biggestGroup == 2) 3
    // One pair
    else if (groupsCount == 4 && biggestGroup == 2) 2
    // One pair with a joker
    else if (hasJoker && groupsCount == 5) 2
    // High card
    else 1

}

fun mergeSort(
    hands: List<Triple<String, Int, Int>>,
    withJoker: Boolean = false,
    leftIndex: Int = 0,
    rightIndex: Int = hands.lastIndex,
): List<Triple<String, Int, Int>> {
    // Nothing to sort
    if (hands.size <= 1) return hands

    // Find the middle
    val middle = (leftIndex + rightIndex) / 2

    // Slice the arrays into a left and right list
    val leftList = hands.slice(leftIndex..middle)
    val rightList = hands.slice(middle + 1..rightIndex)

    // Merge the sorted halves
    return merge(mergeSort(leftList, withJoker), mergeSort(rightList, withJoker), withJoker)
}

fun merge(
    leftList: List<Triple<String, Int, Int>>,
    rightList: List<Triple<String, Int, Int>>,
    withJoker: Boolean = false
): List<Triple<String, Int, Int>> {
    // Start with an array to have a predefined size
    val mergedArray = arrayOfNulls<Triple<String, Int, Int>>(leftList.size + rightList.size)

    var leftIndex = 0
    var rightIndex = 0
    var mergedIndex = 0

    // Merge the lists
    while (leftIndex < leftList.size && rightIndex < rightList.size) {
        // If the left card is better than the right, place the right card in the array
        if (leftList[leftIndex].isBetterOrEqualHand(rightList[rightIndex], withJoker)) {
            mergedArray[mergedIndex] = rightList[rightIndex]
            rightIndex++
        }
        // The left card is worse, place it in the array
        else {
            mergedArray[mergedIndex] = leftList[leftIndex]
            leftIndex++
        }
        mergedIndex++
    }

    // Place the remaining left hands in the array
    while (leftIndex < leftList.size) {
        mergedArray[mergedIndex] = leftList[leftIndex]
        leftIndex++
        mergedIndex++
    }

    // Place the remaining right hands in the array
    while (rightIndex < rightList.size) {
        mergedArray[mergedIndex] = rightList[rightIndex]
        rightIndex++
        mergedIndex++
    }

    return mergedArray.requireNoNulls().toList()
}

/**
 * A helper function to check whether this hand is better or equal to the other.
 *
 * The hand is better when it has a higher type than the other. If both types are equal, the hand with the first highest
 * card is better.
 */
fun Triple<String, Int, Int>.isBetterOrEqualHand(other: Triple<String, Int, Int>, withJoker: Boolean = false): Boolean {
    // If this type is greater than the other, this hand is better
    if (this.third > other.third) return true
    // If this type is lower than the other, this hand is worse
    if (this.third < other.third) return false

    // If withJoker is true, it means that the joker is seen as a wildcard, so its singular value is the lowest
    val cards = if (withJoker) listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')
    else listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')

    // The type is the same, so we have to find the first card that differs and check whether it is better
    for (i in this.first.indices) {
        // Find the strength for both cards
        val thisStrength = cards.indexOf(this.first[i])
        val otherStrength = cards.indexOf(other.first[i])

        // If the strength is the same, the card is the same
        return if (thisStrength == otherStrength) continue
        // If this cards strength is greater, then this hand is better
        else if (thisStrength > otherStrength) true
        // Otherwise the other cards strength is greater, then the other hand is better
        else false
    }

    // Exactly the same card, so it is equal to the other.
    return true
}