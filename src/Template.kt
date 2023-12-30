import Template.part1
import Template.part2

fun main() {
    val input = readInputLines("")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Template {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }
}