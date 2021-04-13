import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        State Q0 = new State("Q0");
        State Q1 = new State("Q1");
        State Q2 = new State("Q2");
        State Q3 = new State("Q3");
        State Q4 = new State("Q4");

        Set<State> allStates = new HashSet<>();
        allStates.add(Q0);
        allStates.add(Q1);
        allStates.add(Q2);
        allStates.add(Q3);
        //allStates.add(Q4);

        Set<State> endStates = new HashSet<>();
        endStates.add(Q3);

        try {
            Automata dfa = new Automata(allStates, Q0, endStates, new Connection[]{
                    new Connection(Q0, "A", Q0),
                    new Connection(Q0, "B", Q1),
                    new Connection(Q1, "e", Q2),
                    new Connection(Q2, "e", Q3),
                   // new Connection(Q3, "B", Q4),


            });
            System.out.println(dfa.readWord("A,B"));
            System.out.println(dfa.checkWord("A,B"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }

        //String test = "[Q1=0:Q1;1:Q2],[*Q2=0:Q3;1:Q2],[Q3=0,1:Q2]";
    }
}
