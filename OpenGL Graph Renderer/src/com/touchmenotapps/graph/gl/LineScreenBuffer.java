package com.touchmenotapps.graph.gl;

import javax.media.opengl.GL2;

public class LineScreenBuffer extends ScreenBuffer {
	
	@Override
	int getAllocation(int samplesInScreen) {
		return mSamplesInScreen*2 * 2 * 4 * 2;
	}
	
	public LineScreenBuffer(int samplesInScreen, float startX, float startY,
			float width, float height) {
				super(samplesInScreen, startX, startY, width, height);
			}
	
	/* Fill the vertex array with initial data. All y positions set to 0.
	 * This a circular array for lines.
	 * 
	 * For example let's look at an array for a screen of width of 3 elements(2 lines) so array will be built as:
	 * 
	 *  Line 1    Line 2     Dummy line for circularity
	 *  0,0, 1,0, 1,0, 2,0  2,0, 0,0
	 */
	@Override
	void fillVertexArrayX() {
		//Put 0,0 at beginning and end(See above)
		mFVertexBuffer.put((mSamplesInScreen-1)*4+2,0);
		mFVertexBuffer.put((mSamplesInScreen-1)*4+3,0);
		
		mFVertexBuffer.put(0,0);
		mFVertexBuffer.put(1,0);
		
		//Fill the data buffer with 0 value samples.
		
		float value;
		
		//Fill all the rest, each point twice
		for(int i=1;i<mSamplesInScreen;i+=1) {
			value = (float)(mWidth*(float)i)/(float)mSamplesInScreen;
			mFVertexBuffer.put(i*4-2,value);
			mFVertexBuffer.put(i*4-1,0);
			mFVertexBuffer.put(i*4,value);	
			mFVertexBuffer.put(i*4+1,0);
		}				
	}
	
	@Override
	void drawScreenBuffer(GL2 gl) {
		int readPointer = mBufferReadPointer;
		float xOffset = mStartX-mFVertexBuffer.get(readPointer*4);					
		gl.glLineWidth(2f);
		gl.glTranslatef(xOffset, mStartY-mHeight/2.f, 0);

		mFVertexBuffer.position(readPointer*4);				
		mFVertexBuffer.mark();
				
		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, mFVertexBuffer);				
		gl.glDrawArrays(GL2.GL_LINES, 0, (mSamplesInScreen-readPointer-1)*2);
		gl.glTranslatef(-xOffset, -mStartY+mHeight/2.0f, 0);
		mFVertexBuffer.reset();
			
		//If there is a right side - draw right side - from start of buffer to write point
		if(readPointer!=0) {	
			xOffset = mStartX+mWidth-(mWidth*readPointer)/mSamplesInScreen;
			mFVertexBuffer.position(0);
			mFVertexBuffer.mark();
			gl.glVertexPointer(2, GL2.GL_FLOAT, 0, mFVertexBuffer);
			gl.glTranslatef(xOffset, mStartY-mHeight/2.f, 0);
			gl.glDrawArrays(GL2.GL_LINES, 0, readPointer*2);
			gl.glTranslatef(-xOffset, -mStartY+mHeight/2.f, 0);
			mFVertexBuffer.reset();
		}
	}
	
	//Debug function
	void dumpBuffer() {
		int current = 0;//mBufferReadPointer;
		for(int i=0;i<mSamplesInScreen*4;i++) {
			current++;			
			if(current==(mSamplesInScreen*4)) current=0;						
		}
	}
	
	@Override
	void putSample(float sample) {
		int writePoint = mBufferWritePointer;		
		//If not first point in buffer - put the sample twice(See above)
		if(writePoint!=0)  {
			mFVertexBuffer.put(writePoint*4-1,sample);
			mFVertexBuffer.put(writePoint*4+1,sample);
		}
		else {
			//Put sample at end of buffer and start(Because of circularity)
			mFVertexBuffer.put(1,sample);
			mFVertexBuffer.put((mSamplesInScreen-1)*4-1,sample);
		}		
	}
}
