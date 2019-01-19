package android.app;

import android.os.Bundle;
import android.widget.TextView;

public class Activity {
	
	public static class MockR {
		
		public static class MockLayOut{
			public String activity_main = "mock";
		}
		
		public static class MockId{
			public String tv = "mock";
		}
		
		public MockLayOut layout = new MockLayOut();
		
		public MockId id = new MockId();
	}
	
	protected MockR R = new MockR();
	
	protected void onCreate(Bundle savedInstanceState) {
		
	}

    protected void setContentView(String activity_main) {
		// TODO Auto-generated method stub
		
	}
    
	protected TextView findViewById(String tv) {
		return new TextView();
	}
}
