/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
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
