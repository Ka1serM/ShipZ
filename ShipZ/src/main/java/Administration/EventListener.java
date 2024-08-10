package Administration;

import java.util.ArrayDeque;
import java.util.Queue;

public class EventListener {
    private final Queue<Event> eventQueue = new ArrayDeque<>();
    
    public void actionPerformed(Event e) {
        eventQueue.add(e);
    }

    public Event getNextAction() {
        return eventQueue.poll();
    }
}
