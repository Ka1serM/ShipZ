package Administration;

public class Event {
    private Object source;
    private int id;
    private String data;

    public Event(Object source, int id, String data) {
        this.source = source;
        this.id = id;
        this.data = data;
    }
    
    public Object getSource() {
        return source;
    }
    
    public int getID() {
        return id;
    }
    
    public String getData() {
        return data;
    }
}