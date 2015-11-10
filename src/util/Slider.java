package util;
 
import java.awt.*;
import java.awt.event.*;
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
	
	//Image Stuff
	static final int H_MIN = 0;
	static final int H_MAX = 256;
	static final int S_MIN = 0;
	static final int S_MAX = 256;
	static final int V_MIN = 0;
	static final int V_MAX = 256;
	
    //Set up animation parameters.
    static final int Slider_MIN = 0;
    static final int Slider_MAX = 256;
    static final int Slider_INIT_MIN = 88;    
    static final int Slider_INIT_MAX = 168;    
    
    
    //sliders
    //Create the slider.
    static JSlider hSliderMax = new JSlider(JSlider.HORIZONTAL, Slider_MIN, Slider_MAX, Slider_INIT_MAX);
    static JSlider hSliderMin = new JSlider(JSlider.HORIZONTAL, Slider_MIN, Slider_MAX, Slider_INIT_MIN);
    static JSlider sSliderMax = new JSlider(JSlider.HORIZONTAL, Slider_MIN, Slider_MAX, Slider_INIT_MAX);
    static JSlider sSliderMin = new JSlider(JSlider.HORIZONTAL, Slider_MIN, Slider_MAX, Slider_INIT_MIN);
    static JSlider vSliderMax = new JSlider(JSlider.HORIZONTAL, Slider_MIN, Slider_MAX, Slider_INIT_MAX);
    static JSlider vSliderMin = new JSlider(JSlider.HORIZONTAL, Slider_MIN, Slider_MAX, Slider_INIT_MIN);
     
 
    public Slider() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
  
        //Create the label.
        Font text = new Font("Serif", Font.ITALIC, 15);
        JLabel hMaxLabel = new JLabel("Hue Max", JLabel.CENTER);
        JLabel hMinLabel = new JLabel("Hue Min", JLabel.CENTER);
        JLabel sMaxLabel = new JLabel("Saturation Max", JLabel.CENTER);
        JLabel sMinLabel = new JLabel("Saturation Min", JLabel.CENTER);
        JLabel vMaxLabel = new JLabel("Value Max", JLabel.CENTER);
        JLabel vMinLabel = new JLabel("Value Min", JLabel.CENTER);
        hMaxLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hMinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sMaxLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sMinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        vMaxLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        vMinLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

 
        /* hue Slider Max*/
        hSliderMax.addChangeListener(this);
 
        //Turn on labels at major tick marks.
        hSliderMax.setMajorTickSpacing(50);
        hSliderMax.setMinorTickSpacing(1);
        hSliderMax.setPaintTicks(true);
        hSliderMax.setPaintLabels(true);
        hSliderMax.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        hSliderMax.setFont(text);
        
        //Saturation Slider
        hSliderMax.addChangeListener(this);
        
        /* hue Slider Min */
        hSliderMin.addChangeListener(this);
 
        //Turn on labels at major tick marks.
        hSliderMin.setMajorTickSpacing(50);
        hSliderMin.setMinorTickSpacing(1);
        hSliderMin.setPaintTicks(true);
        hSliderMin.setPaintLabels(true);
        hSliderMin.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        hSliderMin.setFont(text);
        
        /* Saturation Slider Max */
        sSliderMax.addChangeListener(this);
        
        //Turn on labels at major tick marks.
        sSliderMax.setMajorTickSpacing(50);
        sSliderMax.setMinorTickSpacing(1);
        sSliderMax.setPaintTicks(true);
        sSliderMax.setPaintLabels(true);
        sSliderMax.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        sSliderMax.setFont(text);
        
        /* Saturation Slider Min */
        sSliderMin.addChangeListener(this);
        
        //Turn on labels at major tick marks.
        sSliderMin.setMajorTickSpacing(50);
        sSliderMin.setMinorTickSpacing(1);
        sSliderMin.setPaintTicks(true);
        sSliderMin.setPaintLabels(true);
        sSliderMin.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        sSliderMin.setFont(text);
        
        
        /* Value Slider Max */
        vSliderMax.addChangeListener(this);
        
        //Turn on labels at major tick marks.
        vSliderMax.setMajorTickSpacing(50);
        vSliderMax.setMinorTickSpacing(1);
        vSliderMax.setPaintTicks(true);
        vSliderMax.setPaintLabels(true);
        vSliderMax.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        vSliderMax.setFont(text);
 
        /* Value Slider Min*/
        vSliderMin.addChangeListener(this);
        
        //Turn on labels at major tick marks.
        vSliderMin.setMajorTickSpacing(50);
        vSliderMin.setMinorTickSpacing(1);
        vSliderMin.setPaintTicks(true);
        vSliderMin.setPaintLabels(true);
        vSliderMin.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        vSliderMin.setFont(text);
 
        //Put everything together.
        add(hMaxLabel);
        add(hSliderMax);
        add(hMinLabel);
        add(hSliderMin);
        
        add(sMaxLabel);
        add(sSliderMax);
        add(sMinLabel);
        add(sSliderMin);
        
        add(vMaxLabel);
        add(vSliderMax);
        add(vMinLabel);
        add(vSliderMin);
        
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    }
    
    public static void setInitalPositions(Scalar min, Scalar max){
    	hSliderMin.setValue((int) min.val[0]);
    	sSliderMin.setValue((int) min.val[1]);
    	vSliderMin.setValue((int) min.val[2]);
    	hSliderMax.setValue((int) max.val[0]);
    	sSliderMax.setValue((int) max.val[1]);
    	vSliderMax.setValue((int) max.val[2]);
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
    public static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("SliderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Slider animator = new Slider();
                 
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

	public static Scalar getMin() {
		// TODO Auto-generated method stub
		Scalar min = new Scalar(hSliderMin.getValue(),
								sSliderMin.getValue(),
								vSliderMin.getValue());
		return min;
	}
	
	public static Scalar getMax(){
		Scalar max = new Scalar(hSliderMax.getValue(),
				sSliderMax.getValue(),
				vSliderMax.getValue());
		return max;
		
	}
}