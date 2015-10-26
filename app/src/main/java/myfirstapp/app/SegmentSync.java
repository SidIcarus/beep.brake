package myfirstapp.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SegmentSync {
	
	HashMap<String, ArrayList<Object>> collectedData;
	
	public void MakeSegment() {
		//DO THIS RYAN :D
	}
	
	public void UpdateData(HashMap<String, Object> map) {
		Iterator it = map.entrySet().iterator();
		
		//Iterate over all items to be added
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			if (collectedData.containsKey(pair.getKey())) {
				collectedData.get(pair.getKey()).add(pair.getValue());
			} else {
				ArrayList<Object> a = new ArrayList<Object>();
				a.add(pair.getValue());
				collectedData.put(pair.getKey().toString(), a);
			}
		}
	}
	
}
