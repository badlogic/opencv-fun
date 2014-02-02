package pool.utils;

import java.util.ArrayList;
import java.util.List;

public class EventQueue {
	List<Runnable> events = new ArrayList<Runnable>();
	
	public synchronized void add(Runnable runnable) {
		events.add(runnable);
	}
	
	public synchronized List<Runnable> poll() {
		List<Runnable> copy = new ArrayList<Runnable>(events);
		events.clear();
		return copy;
	}
}
