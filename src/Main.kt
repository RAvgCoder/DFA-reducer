import java.io.File
import java.io.FileNotFoundException
import java.util.*

// List to store the states of the DFA, initially containing two states: one final and one non-final
val states = mutableListOf(
    State(1, mutableListOf()), // Final state
    State(0, mutableListOf())  // Non-final state
)

// List to store the nodes representing states in the DFA
val nodes = mutableListOf<Node>()

// List to store the minimization table with iterations
val table = mutableListOf<List<Pair<Int, Int>>>()

// Queue to manage states during the DFA minimization process
val stateQueue: Queue<State> = LinkedList(listOf(states[1], states[0]))

// List to store the steps taken during the DFA minimization process
val steps: MutableList<String> = mutableListOf()


// Main function where the DFA minimization process is initiated
fun main() {
    // Create nodes representing states in the DFA
    createNodes()

    // Add the initial state configuration to the minimization table
    addIterationStateToTheTable()

    // Perform DFA minimization steps until the state queue is empty
    while (stateQueue.isNotEmpty()) {
        // Evaluate and process the next state in the queue
        if (evaluateState(stateQueue.remove())) {
            // If the state evaluation results in a change, add the new state configuration to the minimization table
            addIterationStateToTheTable()
        }
    }

    // Print the steps taken during the DFA minimization process
    steps.onEach(::println)

    // Print the final number of classes after minimization
    println("Num of classes are: ${states.size}")

    // Print the minimized DFA table with iterations
    table.forEachIndexed { index, pairs ->
        println("-------- Iteration $index ---------")
        pairs.forEach(::println)
        println()
    }

    // Write the minimized DFA table to a file
    writeTableToFile()

    // Write the DFA minimization steps to a file
    writeStepsToFile()
}

// Function to add the current state configuration to the minimization table for the current iteration
fun addIterationStateToTheTable() {
    // Map each node's state name based on its transitions 'a' and 'b', and add it to the table
    table.add(nodes.map { findState(it.nodeA()).name to findState(it.nodeB()).name })
}


// Function to evaluate the current state and perform necessary actions during the DFA minimization process
fun evaluateState(currState: State): Boolean {
    // Group nodes in the current state based on their transitions 'a' and 'b'
    val nodesGroups = currState.nodes.map { node: Node ->
        node to Pair(findState(node.nodeA()).name, findState(node.nodeB()).name)
    }.groupBy { it.second }
        .toList()
        .apply {
            // Add current state and node information to the steps' list for visualization
            steps.add("----------- Current Class {$currState} ------------")
            this.onEach { listPair ->
                listPair.second.onEach { steps.add("State(a,b): ${it.second}     \tNode: ${it.first}") }
            }
        }

    // If there are multiple groups of nodes based on transitions, perform state evaluation
    if (nodesGroups.size > 1) {
        // Flush current nodes in the state
        currState.nodes.clear()

        // Add back the nodes from the first subdivided class
        currState.nodes.addAll(nodesGroups.first().second.map { it.first }.toMutableList())

        // Create new classes for other subdivisions and add them to the state queue
        val newStates = mutableListOf<State>()
        for (ng in nodesGroups.drop(1)) {
            val newState = newState(ng.second.map { it.first })
            stateQueue.add(newState)
            states.add(newState)
            newStates.add(newState)
        }

        // Add current state and new classes information to the steps' list for visualization
        stateQueue.add(currState)
        steps.add("Divided into ${newStates.map { it.name() }.toMutableList().apply { this.add(currState.name()) }}\n\n")
        return true
    }

    // If no subdivisions are made, add information to the steps' list for visualization and return false
    steps.add("not divided\n\n")
    return false
}

// Function to create a new state with the given nodes
fun newState(nodesForNewClass: List<Node>): State {
    // Create a new state with a unique name
    val newClass = State(states.size, mutableListOf())
    // Add the provided nodes to the new state and update their state reference
    newClass.nodes.addAll(nodesForNewClass.onEach { it.state = newClass }.toMutableList())
    return newClass
}


// Function to find the state containing a given node
fun findState(node: Node): State {
    // Iterate through all states
    for (state in states) {
        // Check if the state's node list contains the given node
        if (state.nodes.contains(node)) return state
    }
    // If no state is found, throw an exception
    throw IllegalStateException("Cannot find class for $node")
}


// Function to create nodes representing states in the DFA based on specifications provided in a text file
fun createNodes() {
    // Specify the file path
    val filePath = "dfa_nodes.txt"

    // Create a File instance
    val file = File(filePath)

    // Check if the file exists
    if (!file.exists()) {
        throw FileNotFoundException("File not found: $filePath")
    }

    var rawNodes: List<Pair<Int, Int>> = emptyList()
    try {
        // Open the file for reading using a BufferedReader
        file.bufferedReader().apply {
            // Read the file line by line
            this.useLines { lines ->
                rawNodes = lines.map { line ->
                    // Split each line by ":" and trim whitespace, then convert to pairs of integers
                    val num = line.split(":").map { it.trim().toInt() }
                    num[0] to num[1]
                }.toList()
            }
            // Close the BufferedReader
        }.close()

    } catch (e: Exception) {
        println("Error reading the file: ${e.message}")
    }

    // Create nodes based on the number of raw nodes specified
    rawNodes.indices.forEach { i -> nodes.add(Node(i, mutableListOf(), mutableListOf(), states[0])) }

    // Connect nodes based on transitions specified in the raw node data
    rawNodes.forEachIndexed { index, pair ->
        nodes[index].a.add(nodes[pair.first])
        nodes[index].b.add(nodes[pair.second])
        // Set the initial state for the first two nodes as the non-final state
        if (index < 2) nodes[index].state = states[1]
    }

    // Add each node to the list of nodes belonging to its respective state
    nodes.forEach {
        it.state.nodes.add(it)
    }
}

// Function to write the minimized DFA table to a file
fun writeTableToFile() {
    // Specify the file name
    val tableFile = "Table.txt"

    // Create a File instance
    val tableOutputFile = File(tableFile)

    // Write content to the file
    try {
        // Open the file for writing using a BufferedWriter
        val tableBufferedWriter = tableOutputFile.bufferedWriter()

        // Write content to the file
        tableBufferedWriter.write("Num of classes are: ${states.size}\n")
        tableBufferedWriter.write("--------- State Names -------------\n")
        // Write state names to the file
        nodes.onEach { tableBufferedWriter.write("${it.state.name}\n") }
        tableBufferedWriter.write("\n")

        // Write minimized DFA table to the file, along with iteration numbers
        table.forEachIndexed { index, pairs ->
            tableBufferedWriter.write("-------- Iteration ${index + 1} ---------\n")
            pairs.forEach { tableBufferedWriter.write("${it.first}\t${it.second}\n") }
            tableBufferedWriter.write("\n")
        }

        // Close the BufferedWriter
        tableBufferedWriter.close()

        println("Table has been written to ${tableOutputFile.absoluteFile}.")
    } catch (e: Exception) {
        println("Error writing table to the file: ${e.message}")
    }
}
// Function to write the DFA minimization steps to a file
fun writeStepsToFile() {
    // Specify the file name
    val stepsFile = "Steps.txt"

    // Create a File instance
    val stepsOutputFile = File(stepsFile)

    try {
        // Open the file for writing using a BufferedWriter
        val stepsBufferedWriter = stepsOutputFile.bufferedWriter()

        // Write each step to the file
        steps.forEach { stepsBufferedWriter.write("$it\n") }

        // Close the BufferedWriter
        stepsBufferedWriter.close()

        println("Steps have been written to ${stepsOutputFile.absoluteFile}.")
    } catch (e: Exception) {
        println("Error writing Steps to the file: ${e.message}")
    }
}

