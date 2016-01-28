package swing;

import java.awt.Button;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Rect;

import main.VideoTagging;

public class Tagger extends JFrame{
	
	private static final long serialVersionUID = -8205670641876095314L;
	
	private JLabel status;
	private final Button next;
	private final Button prev; 
	private final Button clearFrame;
	private final Button saveTags;
	
	public ActionListener buttonListener = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == next){
				VideoTagging.next();
			}else if(e.getSource() == prev){
				VideoTagging.prev();
			}else if(e.getSource() == clearFrame){
				VideoTagging.clearFrame();
			}else if(e.getSource() == saveTags){
				VideoTagging.saveTags();
			}
		}
	};
	
	public Tagger(){
		super();
		//status 
		status = new JLabel();
		status.setText("Loading...");
		//setup buttons
		JPanel navButtonComp = new JPanel();
		navButtonComp.setLayout( new FlowLayout() );
		next = new Button("Next"); 
		prev = new Button("Prev");
		
		next.addActionListener(buttonListener);
		prev.addActionListener(buttonListener);
		navButtonComp.add(prev);
		navButtonComp.add(next);
		
		JPanel buttonComp = new JPanel();
		buttonComp.setLayout( new FlowLayout() );
		clearFrame = new Button("Clear Frame");
		clearFrame.addActionListener(buttonListener);
		saveTags = new Button("Save All Tags");
		saveTags.addActionListener(buttonListener);
		buttonComp.add(clearFrame);
		buttonComp.add(saveTags);
		
		GridLayout gLay = new GridLayout();
		gLay.setColumns(1);
		gLay.setRows(3);
		super.setLayout( gLay );
		super.setVisible(true);
		super.add(status);
		super.add(navButtonComp);
		super.add(buttonComp);
		super.pack();
		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void setStatus(String s){
		status.setText(s);
	}
	
}
