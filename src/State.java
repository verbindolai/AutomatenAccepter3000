import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nikolai Wieczorek
 * Represents a State of an finite Automata.
 */
public class State {

    public final String name;
    private Map<String, ArrayList<State>> followStates = new HashMap<>();

    public State(String name) {
        this.name = name;
    }

    public State(String name, Map<String, ArrayList<State>> followStates) {
        this.name = name;
        this.followStates = followStates;
    }

    /**
     * Adds a following state to the state.
     *
     * @param symbol The symbol which leads to this state.
     * @param state  The state.
     */
    public void addFollowState(String symbol, State state) {
        if (followStates.containsKey(symbol)) {
            followStates.get(symbol).add(state);
        } else {
            ArrayList<State> states = new ArrayList<>();
            states.add(state);
            followStates.put(symbol, states);
        }
    }

    /**
     * Reads a symbol and returns the following states or null if there are no following states or the symbol can not be read.
     *
     * @param symbol Symbol to read
     * @return ArrayList of following States or null
     */
    public ArrayList<State> readSymbol(String symbol) {

        ArrayList<State> followStates = this.followStates.get(symbol);
        ArrayList<State> following = null;
        /*
        Checks for states which have an "e" leading to a following state.
         */
        if (followStates != null) {
            following = new ArrayList<>(followStates);
            ArrayList<State> eStates = new ArrayList<>();
            add_E_States(following, eStates);
            following.addAll(eStates);
        }

        return following;
    }

    //Adds all States to eStates which follow the input of the empty word recursively.
    private void add_E_States(ArrayList<State> following, ArrayList<State> eStates){
        for (State state : following) {
            if (state.followStates.containsKey(Automata.EMPTY_WORD_SYMBOL)) {
                add_E_States(state.followStates.get(Automata.EMPTY_WORD_SYMBOL), eStates);
                eStates.addAll(state.followStates.get(Automata.EMPTY_WORD_SYMBOL));
            }
        }
    }


    public String toString(){
        return this.name;
    }

}
