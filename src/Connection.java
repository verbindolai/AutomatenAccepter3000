/**
 * @author Nikolai Wieczorek
 * Represents a connection between two states.
 */
public class Connection {

    private final State start;
    private final String symbol;
    private final State end; //Should be ArrayList for NFA's ?

    public Connection(State start, String symbol, State end) {
        this.start = start;
        this.symbol = symbol;
        this.end = end;
    }

    public State getStart() {
        return start;
    }

    public String getSymbol() {
        return symbol;
    }

    public State getEnd() {
        return end;
    }
}
