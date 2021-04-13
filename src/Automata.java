import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nikolai Wieczorek
 * Represents an finite automata.
 */
public class Automata {

    private final Set<State> states;
    private final State start;
    private final Set<State> endStates;
    private final Connection[] stateConnections;

    static final String EMPTY_WORD_SYMBOL = "e";

    public Automata(Set<State> states, State start, Set<State> endStates, Connection[] stateConnections) throws Exception {
        this.states = states;
        this.stateConnections = stateConnections;

        for (Connection con : this.stateConnections){
            con.getStart().addFollowState(con.getSymbol(), con.getEnd());
        }

        if (states.contains(start)) {
            this.start = start;
        } else {
            throw new Exception("Start state is not included in all States.");
        }

        if (states.containsAll(endStates)) {
            this.endStates = endStates;
        } else {
            throw new Exception("Not all end States are included in all States.");
        }
    }

    public boolean checkWord(String input){
        ArrayList<String> word = new ArrayList<>(Arrays.asList(input.split(",")));

        //All possible state sequences. The state number per sequence is equal to the number of symbols in the word + 1.
        ArrayList<ArrayList<State>> allSequences = this.allSequences(word);

        return allSequences
                .stream()
                //Condition 1 & 3, Only sequences which start with the start state and end with an end state must be considered.
                .filter(sequence -> sequence.get(0).equals(this.start) && this.endStates.contains(sequence.get(sequence.size() - 1)))
                .anyMatch(sequence -> checkSequence(word, sequence));
    }

    private boolean checkSequence(ArrayList<String> word, ArrayList<State> sequence){
        boolean seq = true;
        ArrayList<String> tmpWord = new ArrayList<>(word);
        AtomicInteger wordIndex = new AtomicInteger();

        //Condition 2, Every "State-Pair" together with the current symbol must be included in the connection array if the word is accepted by the automata.
        for (int i = 0; i < sequence.size() - 1; i++){
            String symbol;

            if (wordIndex.get() >= tmpWord.size()){
                symbol = "NO_SYMBOL";
            } else {
                symbol = tmpWord.get(wordIndex.get());
            }

            State start = sequence.get(i);
            State end = sequence.get(i+1);

            seq &= checkConnections(start, end, symbol, wordIndex);

        }
        return seq;
    }

    private boolean checkConnections(State start, State end, String symbol, AtomicInteger wordIndex){
        return Arrays.stream(this.stateConnections)
                .anyMatch(con -> {
                    if (con.getStart().equals(start) && con.getEnd().equals(end)){
                        if (con.getSymbol().equals(symbol)) {
                            wordIndex.getAndIncrement();
                            return true;
                        } else return con.getSymbol().equals(EMPTY_WORD_SYMBOL);
                    }
                    return false;
                });
    }


    private int get_E_SymbolNum(){
        return (int) Arrays.stream(this.stateConnections).filter(connection -> connection.getSymbol().equals(EMPTY_WORD_SYMBOL)).count();
    }

    private ArrayList<ArrayList<State>> allSequences(ArrayList<String> word) {
        ArrayList<ArrayList<State>> result = new ArrayList<>();

        State[] states = this.states.toArray(new State[0]);
        int stateNum = states.length;

        //Needs to be one bigger then the word size because of the start state.
        int minSequenceLength = word.size() + 1;

        int maxSequenceLength = minSequenceLength + get_E_SymbolNum();

        //For NFA's the possible sequence length changes, all sequence lengths between min and max ar possible.
        for (int sequenceLength = minSequenceLength; sequenceLength <= maxSequenceLength; sequenceLength++){

            //Works like counting in a x-complement. SequenceLength gives the bit number and stateNum the complement.
            //E.G: sequence length = 4 && stateNum = 3
            //0000      0011
            //0001      0012
            //0002      0013
            //0003      0020
            //0010      0021 ... and so on
            int sequenceNumber = (int) Math.pow(stateNum, sequenceLength);
            for(int i = 0; i < sequenceNumber; i++){
                ArrayList<State> sequence = new ArrayList<>();
                int sequenceIndex = i;
                int [] sequenceIndices = new int[sequenceLength];

                for (int k = 0; k < sequenceLength; k++) {
                    sequenceIndices[k] = sequenceIndex % stateNum;
                    sequenceIndex = sequenceIndex / stateNum;
                }
                for (int k : sequenceIndices) {
                    sequence.add(states[k]);
                }
                result.add(sequence);
            }

        }
        return result;
    }

    /**
     * Reads a word and checks whether or not the automata accepts the word.
     * @param input A string which represents the word which should be checked. Every symbol needs to be separated by a comma like so "W,O,R,D".
     * @return True if the automata can read the word, false if not.
     */
    public boolean readWord(String input) {
        ArrayList<String> word = new ArrayList<>(Arrays.asList(input.split(",")));

        //The states the automata is currently in. (Doesnt need to be a stack)
        Stack<State> currentStates = new Stack<>();

        currentStates.push(start);

        /*
        Read every symbol of the word.
         */
        while (!word.isEmpty()) {
            String symbol = word.remove(0);
            Stack<State> newStates = new Stack<>();

            /*
            Gets all new states, which follow the input of the symbol, for each of the current states.
             */
            currentStates.forEach(state -> {
                ArrayList<State> followStates = state.readSymbol(symbol);
                if (followStates != null) {
                    newStates.addAll(followStates);
                }
            });
            /*
            Set the new states as current states so the next symbol can be read.
            If the new states are empty, we landed in a dead end and doesnt have to compute further, because the states wont change.
             */
            if (!newStates.empty()) {
                currentStates = newStates;
            } else {
                break;
            }
        }
        System.out.println("Endstates: ");
        currentStates.forEach(System.out::println);

        /*
        If one of the states in which the automata, is also an accept state, it means the word is accepted by the automata.
         */
        return currentStates.stream().anyMatch(endStates::contains);
    }

}
