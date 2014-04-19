package com.touchmenotapps.graph.gl;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * Data structure for storing samples data. 
 * Samples buffer is circular, the start time of the buffer is
 * always set to the read pointer time
 *
 */
public class SamplesBuffer {

	private int [] mSamples;	
	private int mBufferStartIndex=0, mBufferSize;
	private int MAX_PLOT_VALUE = 256;
	private long mStartTimeStamp = Long.MAX_VALUE;
	private long mLatestSample = 0;	
	private Semaphore mSemaphore;
	
	public SamplesBuffer(int bufferSize) {
		mSamples = new int[bufferSize];				
		mBufferSize = bufferSize;
		mSemaphore = new Semaphore(1, true);		
		invalidateWholeBuffer();
	}

	void setTimestamp(long timeStamp) {
		mStartTimeStamp = timeStamp;
	}

	int getIndexOffsetFromTimestamp(long timeStamp) {
		return (int)((timeStamp-mStartTimeStamp+1)/2);
	}

	/**
	 * @param timeStamp
	 * @return
	 */
	int getSampleAtTime(long timeStamp) {		
		try {
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int index = getIndexOffsetFromTimestamp(timeStamp);		
		int returnValue = MAX_PLOT_VALUE;
		//Just update sample - no need to move the buffer!
		if((timeStamp>=mStartTimeStamp) && (timeStamp<=(mStartTimeStamp+mBufferSize*2))) {
			//Position is relative, must add to it buffer start index
			int position = wrap(mBufferStartIndex+index);
			returnValue =  mSamples[position];
		}
		mSemaphore.release();		
		return returnValue;
	}

	protected int wrap(int i) {
		if(i>=mBufferSize) 
			return i-mBufferSize;
		else {
			if(i<0)
				i = mBufferSize+i;
		}		
		return i;
	}
	
	public long getLatestTimeStamp() {
		return mLatestSample;
	}
	
	public int addSample(int sampleValue, long timeStamp, boolean isHeartBeat) {
		return addSample(sampleValue, timeStamp);
	}

	int lastIndex = 0;
	
	public int addSample(int sampleValue, long timeStamp) {			
		try {
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(mStartTimeStamp==Long.MAX_VALUE) 
			mStartTimeStamp = timeStamp;
				
		if(timeStamp>mLatestSample) 
			mLatestSample = timeStamp;
				
		int index = getIndexOffsetFromTimestamp(timeStamp);
		lastIndex = index;		
		int position =0;
		//If index falls inside current buffer -  Simply write it in the appropriate position
		//Just update sample - no need to move the buffer!
		if((index<mBufferSize) && (index>0)) {
			//Position is relative, must add to it buffer start index
			position = wrap(mBufferStartIndex+index);
			mSamples[position] = sampleValue;
		}		
		//If index is in the future
		else if(index>=mBufferSize)
		{
			int delta = index+1-mBufferSize;
			//Now invalidate everything from write position to start position(If needed)
			//If have to move less than buffer size forward...			
			if(delta<mBufferSize) {	
				//Save the old last position of the buffer
				int oldStart = mBufferStartIndex;
				//Move the start pointer so that it points to write position-buffer size
				mBufferStartIndex=wrap(mBufferStartIndex+delta);				
				mStartTimeStamp += delta*2;
				//Find the new position after buffer is moved. Sample is now last in buffer
				position = wrap(mBufferStartIndex+(mBufferSize-1));				
				//Fill area between old position and new position with uninitialized values.
				while(oldStart!=mBufferStartIndex) {
					mSamples[oldStart]=MAX_PLOT_VALUE;
					oldStart=wrap(oldStart+1);
				}
			}
			
			//Moved too much - have to erase the whole buffer
			else {
				if(delta>=mBufferSize) {
					invalidateWholeBuffer();
					mBufferStartIndex = 0;
					position  = 0;
					mStartTimeStamp = timeStamp;
				}
			}
		} else if(index<0) { //If index is in the past 
			//Buffer samples still relevant
			if(index>=(-mBufferSize)) {
				int oldStartPosition = mBufferStartIndex;
				//Move the start position to the write position.
				mBufferStartIndex = wrap(mBufferStartIndex+index);
				//Now invalidate everything from new start position to old start position
				while(mBufferStartIndex!=oldStartPosition) {
					mSamples[wrap(oldStartPosition+mBufferSize-1)]=MAX_PLOT_VALUE;
					oldStartPosition = wrap(oldStartPosition-1);
				}
				position = mBufferStartIndex;				
				mStartTimeStamp += index*2;				
			}
			//Moved too much - erase all buffer
			else {
				invalidateWholeBuffer();
				mBufferStartIndex = 0;
				position  = 0;
				mStartTimeStamp = timeStamp;
			}
		}
		mSamples[position] = sampleValue;		
		mSemaphore.release();		
		return position;
	}

	private void invalidateWholeBuffer() {
		Arrays.fill(mSamples, MAX_PLOT_VALUE);		
	}

	public long getStartTs() {
		return mStartTimeStamp;
	}

	public long lastSampleTime() {		
		if(mLatestSample>(mStartTimeStamp+mBufferSize*2)) {
			mLatestSample = mStartTimeStamp+mBufferSize*2;
		}
		return mLatestSample;
	}

	public void reset() {
		try {
			mSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}				
		mBufferStartIndex = 0;
		mStartTimeStamp = Long.MAX_VALUE;
		invalidateWholeBuffer();		
		mSemaphore.release();
	}

	public int getBufferSize() {
		return mBufferSize;
	}
	
	public long getTimeAtIndex(int index) {
		int delta = index - mBufferStartIndex;		
		if(delta<0)
			delta = mBufferSize+delta;
		return mStartTimeStamp + (long)delta*2; 
	}

	public long getStartTsWithOffset() {		
		return 0;
	}
}
