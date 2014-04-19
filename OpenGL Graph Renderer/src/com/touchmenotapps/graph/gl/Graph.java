package com.touchmenotapps.graph.gl;

import javax.media.opengl.GL2;

public class Graph {

	private static final int SAMPLES_IN_SCREEN = 1000;
	float mGraphWidth;
	float mGraphHeight;
	private GraphDataSource mDataSource;
	private ScreenBuffer mScreenBuffer;
	private GraphColor graphColor;

	class GraphColor {
		public int R, G, B;
		public GraphColor(int inR,int inG,int inB) {
			R = inR;
			G = inG;
			B = inB;
		}
	}
	
	public Graph(SamplesBuffer samplesBuffer, int r, int g, int b) {		
		graphColor=new GraphColor(r, g, b);
		mDataSource = new GraphDataSource(samplesBuffer);
	}

	public void update(long ts) {
		mDataSource.updateScreenBuffer(ts, mScreenBuffer);
	}

	public void draw(GL2 gl) {
		gl.glEnableClientState (GL2.GL_VERTEX_ARRAY);
		gl.glLineWidth(2f);
		mScreenBuffer.setColor(gl);
		mScreenBuffer.drawScreenBuffer(gl);
		gl.glDisableClientState (GL2.GL_VERTEX_ARRAY);
	}
	
	public void recalculate(float ratio) {
		mGraphWidth = 2.f*ratio;
		mGraphHeight = 2.0f;			
		mScreenBuffer =  new LineScreenBuffer(SAMPLES_IN_SCREEN, -ratio, 1f, mGraphWidth, mGraphHeight);
		mScreenBuffer.setRGB(graphColor.R,graphColor.G,graphColor.B);
	}				
}


