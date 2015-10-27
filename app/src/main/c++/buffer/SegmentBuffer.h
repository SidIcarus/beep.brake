/*
 * Header file for SegmentBuffer object
 *
 * Maintains a buffer of segments of at least the minimum required time
 * 		(i.e. 6 seconds currently)
 */

#ifndef __BUFFER_MANAGEMENT__SEGMENT_BUFFER_H_
#define __BUFFER_MANAGEMENT__SEGMENT_BUFFER_H_

#include <list>

class SegmentBuffer {
	public:
		SegmentBuffer();

	private:
		std::list buffer;//doubly linked list

}


#endif /*__BUFFER_MANAGEMENT__SEGMENT_BUFFER_H_*/