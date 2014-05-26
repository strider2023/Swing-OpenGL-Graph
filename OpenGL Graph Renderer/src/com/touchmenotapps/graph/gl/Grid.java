package com.touchmenotapps.graph.gl;

import java.nio.FloatBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.GL2;

/**
 * Draws a grid for a graph
 *
 */
public class Grid {
	
	private float mStartX, mStartY, mWidth;
	private float[] mCoordsMiddle;//Axes coordinates	
	private FloatBuffer mMiddleVertexBuffer;//Axes vertex buffer
	
	public void draw(GL2 gl) {
		gl.glColor4f(0.0f, 0.5f, 0.0f, 1f);
		gl.glTranslatef(mStartX, mStartY, 0.01f);//Move below graph level
		gl.glLineWidth(3f);//Draw axes
		gl.glEnableClientState (GL2.GL_VERTEX_ARRAY);
		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, mMiddleVertexBuffer);
		gl.glDrawArrays(GL2.GL_LINE_STRIP, 0, 2);
		gl.glDisableClientState (GL2.GL_VERTEX_ARRAY);
		gl.glTranslatef(-mStartX, -mStartY, -0.01f);
	}

	/**
	 * Regenerates the grid when graph display changes
	 * 
	 * @param startX
	 * @param startY
	 * @param graphWidth
	 * @param graphHeight
	 * @param divisionsX
	 * @param divisionsY
	 */
	public void setBounds(float startX, float startY, float graphWidth,
			float graphHeight, int lineHeight) {
		mStartX = startX;
		mStartY = startY;
		mWidth = graphWidth;
		
		mCoordsMiddle = new float[4];
		switch(lineHeight) {
		case 0:
			mCoordsMiddle[0] = (float) (startX + (mWidth/1.65));
			mCoordsMiddle[1] = -0.95625f;
			mCoordsMiddle[2] = (float) (startX + (mWidth/1.65));
			mCoordsMiddle[3] = -1.05f;
			break;
		case 1:
			mCoordsMiddle[0] = (float) (startX + (mWidth/1.65));
			mCoordsMiddle[1] = -0.9125f;
			mCoordsMiddle[2] = (float) (startX + (mWidth/1.65));
			mCoordsMiddle[3] = -1.1f;
			break;
		case 2:
			mCoordsMiddle[0] = (float) (startX + (mWidth/1.65));
			mCoordsMiddle[1] = -0.825f;
			mCoordsMiddle[2] = (float) (startX + (mWidth/1.65));
			mCoordsMiddle[3] = -1.2f;
			break;
		case 3:
			mCoordsMiddle[0] = (float) (startX + (mWidth/1.65));
			mCoordsMiddle[1] = -0.65f;
			mCoordsMiddle[2] = (float) (startX + (mWidth/1.65));
			mCoordsMiddle[3] = -1.4f;
			break;
		}
				
		ByteBuffer vbbMiddle = ByteBuffer.allocateDirect(4 * 2 * 4);
		vbbMiddle.order(ByteOrder.nativeOrder());
		mMiddleVertexBuffer = vbbMiddle.asFloatBuffer();
		
		for(int i=0;i<4;i++)
			mMiddleVertexBuffer.put(mCoordsMiddle[i]);		
		mMiddleVertexBuffer.position(0);
	}
}

