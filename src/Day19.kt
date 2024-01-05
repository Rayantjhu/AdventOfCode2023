import Day19.part1
import Day19.part2

fun main() {
    val input = readInput("Day19")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day19 {
    fun part1(input: String): Int {
        val (workflows, ratings) = parseInput(input)

        return ratings.sumOf { rating ->
            if (acceptOrRejectRating(workflows, workflows["in"] ?: return 0, rating)) rating.values.sum() else 0
        }
    }

    /**
     * This parses the input by splitting the string into the workflows and its ratings.
     *
     * It returns a pair of the processed workflows and the ratings.
     *
     * The workflows; this is a map, which contains the name of the workflow as the key,
     * and the list of rules for as the value. A rule contains the variable to be used in the rule, the check and the
     * true statement.
     *
     * The ratings; this is a list, which contains all variables (x, m, a, s) as keys and its numbers as values.
     */
    fun parseInput(input: String, skipRatings: Boolean = false): Pair<Map<String, List<Rule>>, List<Map<Char, Int>>> {
        return input.split("\n\n").let { (unprocessedWorkflows, unprocessedRatings) ->
            // Create the workflows for the given string, map over each line
            val workflows = unprocessedWorkflows.lines().associate { unprocessedWorkflow ->
                val name = unprocessedWorkflow.substringBefore('{')
                // Get all the rules and split them by commas to get a list of rules
                val rules = unprocessedWorkflow.substringAfter('{').dropLast(1).split(',').map { rule ->
                    // Split the rule by a colon to get a condition and its statement
                    val tokens = rule.split(':')

                    // If there's only one token, it means there's no condition, so we create a workflow with only a statement
                    if (tokens.size == 1) Rule(statement = tokens.first())
                    else {
                        // Get the variable that should be used in the check
                        val variable = tokens.first().first()
                        // Get the operator and check by dropping which variable is used
                        val (operator, number) = tokens.first().drop(1).let { check ->
                            // The operator is either lower than or greater than
                            val operator = check.first()
                            val number = check.drop(1).toInt()
                            operator to number
                        }

                        // Return the triple with its variable to be used, the check and the true statement
                        Rule(variable, operator, number, tokens.last())
                    }
                }
                name to rules
            }

            if (skipRatings) workflows to listOf()
            else {
                // Create the ratings for the given string, map over each line
                val ratings = unprocessedRatings.lines().filter { it.isNotBlank() }.map { unprocessedRating ->
                    // Remove the curly brackets and split by comma to receive all variables in the rating
                    unprocessedRating.substring(1..<unprocessedRating.lastIndex).split(',').associate { variable ->
                        // Associate the name of the variable to its number
                        variable.first() to variable.substring(2).toInt()
                    }
                }

                workflows to ratings
            }
        }
    }

    data class Rule(
        /** The variable which should be used in the check */
        val variable: Char? = null,
        /** The operator used in the check */
        val operator: Char? = null,
        /** The number used in the operation */
        val number: Int? = null,
        /**
         * The statement that should be executed if the check is true, if the check is null, this should always be
         * executed.
         */
        val statement: String,
        /**
         * Whether it is the last statement. True, if there's no operator, variable or number, only the statement
         * exists.
         */
        val isElseStatement: Boolean = variable == null && operator == null && number == null,
        /**
         * The check which is made at initialization. If the operator, number and variable are null, it will create a
         * lambda which always returns false.
         */
        val check: ((Int) -> Boolean) =
            if (operator == null || number == null || variable == null) { _ -> false }
            else if (operator == '<') { x -> x < number }
            else { x -> x > number }
    )

    /** Checks whether the for the given rules the rating can be accepted or not. */
    fun acceptOrRejectRating(workflows: Map<String, List<Rule>>, rules: List<Rule>, rating: Map<Char, Int>): Boolean {
        for (rule in rules) {
            // If this rule is an else statement or its check returns true, use its statement otherwise, we should
            // continue to the next rule.
            val statement = if (rule.isElseStatement || rule.check(rating[rule.variable]!!)) rule.statement else null

            return when (statement) {
                // Check is false
                null -> continue
                "A" -> true
                "R" -> false
                // Recursively go to the next workflow
                else -> acceptOrRejectRating(workflows, workflows[statement]!!, rating)
            }
        }

        return false
    }

    fun part2(input: String): Long {
        val (workflows, _) = parseInput(input, skipRatings = true)
        val ranges = "xmas".associate { it to 1..4000 }

        return count2(ranges, "in", workflows)
    }

    fun count2(ranges: Map<Char, IntRange>, workflowName: String, workflows: Map<String, List<Rule>>): Long {
        if (workflowName == "R") return 0L
        if (workflowName == "A") return ranges.values.fold(1L) { acc, range -> acc * (range.last - range.first + 1) }

        var total = 0L
        val updatedRanges = ranges.toMutableMap()
        for (rule in workflows[workflowName] ?: return 0L) {
            // The else statement has been reached, so we pass the left-over ranges to the else statement
            if (rule.isElseStatement) {
                total += count2(updatedRanges, rule.statement, workflows)
                break
            }

            val ruleVariable = rule.variable ?: return 0L
            val ruleNumber = rule.number ?: return 0L

            val (start, end) = updatedRanges[ruleVariable]?.let { it.first to it.last } ?: return 0L
            val (trueHalf, falseHalf) = when (rule.operator) {
                '<' -> start..minOf(ruleNumber - 1, end) to ruleNumber..end
                '>' -> maxOf(ruleNumber + 1, start)..end to start..ruleNumber
                else -> throw IllegalArgumentException("Unknown operator: ${rule.operator}")
            }

            // True half is not empty
            if (trueHalf.first <= trueHalf.last) {
                total += count2(
                    updatedRanges.toMutableMap().apply { this[ruleVariable] = trueHalf },
                    rule.statement,
                    workflows
                )
            }

            if (falseHalf.first <= falseHalf.last) updatedRanges[ruleVariable] = falseHalf else break
        }

        return total
    }
}