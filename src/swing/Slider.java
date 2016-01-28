package swing;
 
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import org.opencv.core.Scalar;

/*
 * SliderDemo.java requires all the files in the images/doggy
 * directory.
 */
public class Slider extends JPanel implements 	ActionListener,
                                   				WindowListener,
                                   				ChangeListener {

	private static final long serialVersionUID = 1L;
	
    static ArrayList<JSlider> allSliders = new ArrayList<JSlider>();
    
    public Slider(String[] sliders) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      //Create the label.
        Font text = new Font("Serif", Font.ITALIC, 15);
        for(String s : sliders){
            JLabel label = new JLabel(s, JLabel.CENTER);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            JSlider slider = new JSlider();
            slider.addChangeListener(this);
            //Turn on labels at major tick marks.
            slider.setMajorTickSpacing(50);
            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
            slider.setFont(text);
            slider.setMaximum(300);

            //add components
            add(label);
            add(slider);
            //store in list
            allSliders.add(slider);

        }
    }
    
    public static void setInitalPositions(int[] values){
    	for(int i = 0; i < allSliders.size(); i++){
    		JSlider slider = new JSlider();
    		slider.setValue(values[i]);
    	}
    }
 
    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }
 
    //React to window events.
    public void windowIconified(WindowEvent e) {
        stopAnimation();
    }
    public void windowDeiconified(WindowEvent e) {
        startAnimation();
    }
 
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {

    }
 
    public void startAnimation() {

    }
 
    public void stopAnimation() {

    }
 
    //Called when the Timer fires.
    public void actionPerformed(ActionEvent e) {

    }
 
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(String[] sliders) {
    	//Create and set up the window.
        JFrame frame = new JFrame("SliderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Slider animator = new Slider(sliders);
                 
        //Add content to the window.
        frame.add(animator, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        animator.startAnimation(); 
    }
    
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

	public static int[] getSliderValues(){
		int[] values = new int[allSliders.size()];
		for( int i = 0; i < allSliders.size(); i++){
			values[i] = allSliders.get(i).getValue();
		}
		return values;
	}
}