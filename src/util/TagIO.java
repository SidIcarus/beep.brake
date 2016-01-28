package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.opencv.core.Rect;

/**
 * reads / writes tags
 * @author richykapadia
 *
 */
public class TagIO {
	
	private final String path;
	
	public TagIO(String videoPath){
		String path = videoPath.substring(0, videoPath.indexOf('.'));
		path += ".tag";
		this.path = path;
	}
	
	public HashMap<Integer, ArrayList<Rect>> readTags(String path){
		HashMap<Integer, ArrayList<Rect>> tags = new HashMap<Integer, ArrayList<Rect>>();
				
		return tags;
	}
	
	public void writeTags(HashMap<Integer, ArrayList<Rect>> tags){
		try {
			FileWriter writer = new FileWriter(this.path);
			String line = new String();
			//set has duplicates, dedup	
			for( Integer currFrame : tags.keySet() ){
				line += currFrame + ":";
				for(Rect r : tags.get(currFrame)){
					String coord =  r.tl().x + "," + r.tl().y + ","
									+ r.br().x + "," + r.br().y + ";";
					line += coord;
				}
				line += "\n";
				writer.write(line);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
