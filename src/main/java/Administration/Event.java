package Administration;

public class Event {
    private final String source;
    private final int id;
    private final String data;

    public Event(String source, int id, String data) {
        this.source = source;
        this.id = id;
        this.data = data;
    }
    
    public String getSource() {
        return source;
    }
    
    public int getID() {
        return id;
    }
    
    public String getData() {
        return data;
    }
}