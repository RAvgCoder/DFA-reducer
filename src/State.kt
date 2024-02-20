
// Class representing a state in the minimized DFA, containing a name and a list of nodes belonging to the state
class State(val name: Int, val nodes: MutableList<Node>) {

    // Function to generate a string representation of the state
    fun name(): String {
        return "C$name"
    }

    // Override equals method to compare states based on their names
    override fun equals(other: Any?): Boolean {
        return if (other is State) other.name == this.name
        else false
    }

    // Override hashCode method to generate hash code based on state's name
    override fun hashCode(): Int {
        return name
    }

    // Override toString method to generate a string representation of the state
    override fun toString(): String {
        return "Class_Name: C$name Nodes:${nodes.map { it.name() }}"
    }
}