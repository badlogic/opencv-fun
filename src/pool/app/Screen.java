package pool.app;

public abstract class Screen {
	protected final PoolApp app;
	
	public Screen(PoolApp app) {
		this.app = app;
	}
	
	public abstract void initialize();
	
	public abstract void update();
	
	public abstract void dispose();
}
