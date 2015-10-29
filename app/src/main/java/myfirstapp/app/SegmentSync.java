opepackage myfirstapp.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SegmentSync {

	HashMap<String, ArrayList<Object>> collectedData;

	public void MakeSegment() {
		//I'm not certain that this is the correct way to force atomicity
		synchronized(this){
			HashMap<String, ArrayList<Object>> temp = collectedData;
			collectedData = new HashMap<String, ArrayList<Object>>();
		}

		Iterator it = temp.entrySet().iterator();
		HashMap<String, Object> segMap = new HashMap<String, Object>();

		while(it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			String name = pair.getKey()
			ArrayList<Object> data = temp.get(pair.getKey());

			//This is bad, we need to improve our data management
			//Likely should have some guarantee that numeric data is the
			//only data where size > 1
			if(data.size() > 1){
				int avgData = 0;

				for(int i = 0; i < data.size(); i++){
					avgData += data.get(i);
				}
				avgData = avgData / data.size();
				segMap.put(name, avgData);
			}else{
				segMap.out(name, data.get(0));
			}
		}
		Segment seg = new Segment(segMap);
		//Call BufferManager add method -> Needs Kevin's stuff
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
