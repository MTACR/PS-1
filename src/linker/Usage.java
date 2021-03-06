package linker;

public class Usage {
    public String symbol;
    public int locationCounter;
    public char opsign;

    public Usage(String symbol, int locationCounter, char opsign) {
        this.symbol = symbol;
        this.locationCounter = locationCounter;
        this.opsign = opsign;
    }

    public void offset(int offset){
        locationCounter += offset;
    }

    @Override
    public String toString() {
        return "Usage {" +
                "symbol = '" + symbol + '\'' +
                ", locationCounter = " + locationCounter +
                ", opsign = " + opsign +
                '}';
    }
}
