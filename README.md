# DFA Minimizer

This Kotlin project implements a DFA (Deterministic Finite Automaton) minimizer. DFA minimization is a process used to simplify a DFA while preserving its behaviour. The DFA minimizer in this project aims to reduce the number of states in a given DFA by identifying and merging equivalent states.

## Features

- **State Partitioning**: The DFA minimizer partitions states into groups based on their behaviour in accepting or rejecting strings.
  
- **State Evaluation**: Each state is evaluated to determine its equivalence with other states based on their transitions.

- **Class Refinement**: The DFA is refined by splitting each partition into smaller partitions until no further splits are possible.

- **Equivalent State Merging**: States that are equivalent in terms of transitions are merged to minimize the DFA.

- **Step-by-Step Visualization**: The program provides a step-by-step DFA minimization process, showing the iterations and the steps taken to minimize the DFA.

## Usage

1. **Input Specification**: Provide the DFA specifications in a text file named `dfa_nodes.txt`. Ensure that the DFA has exactly two states, and the input format follows the convention `from_state:to_state`. 

   Example for a DFA with two states:
   - 0 : 1
   - 1 : 4


Note: The current implementation is designed for DFA with 2 states.

2. **Running the Program**: Run the `Main.kt` file to execute the DFA minimizer. The program will output the minimized DFA table to a file named `Table.txt` and the DFA minimization steps to a file named `Steps.txt`.

3. **Interpreting the Output**: The `Table.txt` file contains the minimized DFA table, while the `Steps.txt` file provides a step-by-step visualization of the DFA minimization process.

## Contents

- `Main.kt`: Contains the main logic for DFA minimization, including step-by-step visualization.
- `Node.kt`: Defines the `Node` class representing states in the DFA.
- `State.kt`: Defines the `State` class representing classes of states in the minimized DFA.
- `Table.txt`: Output file containing the minimized DFA table after running the program.
- `Steps.txt`: Output file containing the DFA minimization steps after running the program.
- `dfa_nodes.txt`: Input file containing the DFA specifications.

## Contributing

Contributions to improve the DFA minimizer or add new features are welcome! Please make your changes, and submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
