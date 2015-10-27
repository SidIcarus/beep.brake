/*
 * Header file for Segment object
 */

#ifndef __BUFFER_MANAGEMENT__SEGMENT_H_
#define __BUFFER_MANAGEMENT__SEGMENT_H_

#include <ctime>

class Segment {
	public:
		Segment(Mat* frame, time_t time);

		//Was there an object in this frame
		bool objectInFrame();


		//Getters
		Mat* getImage();

		//Setters


	private:
		//Timestamp (using tm c structure)
		tm timestamp;
		//The image taken at this segment's time
		Mat* image;



		//Was a car found in this segment's image?
		bool objectDetected;


}

#endif /*__BUFFER_MANAGEMENT__SEGMENT_H_*/