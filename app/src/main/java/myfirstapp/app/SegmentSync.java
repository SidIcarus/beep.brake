	package myfirstapp.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SegmentSync {

	HashMap<String, ArrayList<Object>> aggData;
	HashMap<String, Object> singleData;

	public void MakeSegment() {
		//I'm not certain that this is the correct way to force atomicity
		synchronized(this){
			HashMap<String, ArrayList<Object>> tempAgg = new HashMap<>(aggData); // Proper Copy
			collectedData = new HashMap<String, ArrayList<Object>>();
		}
		synchronized(this){
			HashMap<String, Object> tempSing = new HashMap<>(singleData);
			singleData = new HashMap<String, Object>();
		}
		
		HashMap<String, Object> segMap = new HashMap<String, Object>();

		Iterator itAgg = tempAgg.entrySet().iterator();
		while(itAgg.hasNext()){
			Map.Entry pair = (Map.Entry)itAgg.next();
			String name = pair.getKey();
			ArrayList<Object> data = tempAgg.get(pair.getKey());
			int avgData = 0;

			for(int i = 0; i < data.size(); i++){
				avgData += data.get(i);
			}
			avgData = avgData / data.size();
			segMap.put(name, avgData);
		}

		Iterator itSing = tempSing.entrySet().iterator();
		while(itSing.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();

			segMap.put(pair.getKey(), tempSing.get(pair.getKey()));
		}
		Segment seg = new Segment(segMap);
		//Call BufferManager add method -> Needs Kevin's stuff
	}

	public void UpdateDataAgg(HashMap<String, Object> map) {
		Iterator it = map.entrySet().iterator();

		//Iterate over all items to be added
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			if (aggData.containsKey(pair.getKey())) {
				aggData.get(pair.getKey()).add(pair.getValue());
			} else {
				ArrayList<Object> a = new ArrayList<Object>();
				a.add(pair.getValue());
				aggData.put(pair.getKey().toString(), a);
			}
		}
	}

	public void UpdateDataSingle(HashMap<String, Object> map) {
		Iterator it = map.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			aggData.put(pair.getKey(), pair.getValue());
		}
	}

}
