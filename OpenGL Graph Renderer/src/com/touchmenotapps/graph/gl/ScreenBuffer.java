package com.touchmenotapps.graph.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

/**
 * A screen buffer for drawing an Open GL graph. Inherited by the line & point screen buffer classes.
 * The screen buffer is a cyclic buffer, so adding a sample moves the write pointer while not invalidating the
 * rest of the samples. If adding a sample will cause the buffer to overflow the sample at the read position 
 * is thrown away to free space.
 */
abstract public class ScreenBuffer {

	protected int mBufferWritePointer;
	protected int mBufferReadPointer;
	protected int mSamplesInScreen;
	protected float mStartX;
	protected float mStartY;
	protected float mHeight;
	protected float mWidth;
	private float mRComponent = 0;
	private float mGComponent = 1;
	private float mBComponent = 0;
	protected FloatBuffer mFVertexBuffer;
	private long mLastTimeStamp;
	private boolean mBufferFull = false, mCyclic = true;
	
	public void setCyclic(boolean cyclic) {
		mCyclic = cyclic;
	}	

	abstract void fillVertexArrayX();
	
	abstract int getAllocation(int samplesInScreen);
	
	abstract void putSample(float sample);
	
	abstract void drawScreenBuffer(GL2 gl);
	
	public ScreenBuffer(int samplesInScreen, float startX, float startY, float width, float height)  {
		mSamplesInScreen = samplesInScreen;
		mStartX = startX;
		mStartY = startY;
		mHeight = height;
		mWidth  = width;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(getAllocation(mSamplesInScreen));
		vbb.order(ByteOrder.nativeOrder());
		mFVertexBuffer = vbb.asFloatBuffer();
		
		fillVertexArrayX();					
	}
	
	private void advanceReadPointer() {
		mBufferReadPointer++;
		if( mBufferReadPointer == getBufferSize())
			mBufferReadPointer = 0;
	}
	
	private int getBufferSize() {
		return (mSamplesInScreen);
	}

	private void advanceWritePointer() {
		mBufferWritePointer++;
		if( mBufferWritePointer == getBufferSize())
			mBufferWritePointer = 0;
		//If no place left in buffer we just ran over a sample - in this case: Move the read pointer.
		if( mBufferWritePointer == mBufferReadPointer )
			mBufferFull = true;
	}
	
	synchronized boolean addSample(float sample) {		
		float putValue = sample;
		if(putValue>(mHeight/2)) 
			putValue = mHeight/2;
		
		if(putValue<(-mHeight/2))
			putValue = -mHeight/2;
		
		if(mBufferFull) {
			if(mCyclic==true) 
				advanceReadPointer();
			else 
				return false;
		}
		putSample(putValue);
		advanceWritePointer();
		return true;
	}
	
	
		
	int getNumberOfSamplesLeft(int readPointer) {
		return mSamplesInScreen - readPointer;
	}
	
	int getNumberOfSamplesRight(int readPointer) {
		return readPointer;
	}
	
	int getScreenBufferSize()
	{
		return mSamplesInScreen;
	}

	public void copyScreenBuffer(ScreenBuffer screenBuffer) {
		for(int i=0;i<mSamplesInScreen;i++)
		{
			float sample = mFVertexBuffer.get(((mBufferReadPointer+i)%mSamplesInScreen)*2+1);
			screenBuffer.addSample(sample);
		}
		
	}

	public void reset() {
		mBufferReadPointer = 0;
		mBufferWritePointer = 0;
		mBufferFull = false;
		mLastTimeStamp = Long.MAX_VALUE;
	}

	public long getSamplesInScreen() {
		return mSamplesInScreen;
	}

	public int getNumberOfValidSamples() {
		return 0;
	}

	void setRGB(float r, float g, float b) {
		mRComponent = r;
		mGComponent = g;
		mBComponent = b;
	}

	public void setColor(GL2 gl) {
		gl.glColor4f(mRComponent, mGComponent, mBComponent, 1);
	}
	
	public void setLastTimeStamp(long ts) {
		mLastTimeStamp = ts;
	}
	
	public long getLastTimeStamp() {
		return mLastTimeStamp;
	}
}
