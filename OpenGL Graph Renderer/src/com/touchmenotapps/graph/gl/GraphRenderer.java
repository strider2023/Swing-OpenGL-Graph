package com.touchmenotapps.graph.gl;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

public class GraphRenderer extends AbstractRenderer {

	private Graph mGraph;
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		super.reshape(drawable, x, y, width, height);
		recalculateScreen();
	}
	
	private void recalculateScreen() {
		mGraph.recalculate(mRatio);
	}
	
	public GraphRenderer(SamplesBuffer samplesBuffer, int r, int g, int b,  boolean showGrid) {
		mGraph = new Graph(samplesBuffer, r, g, b, showGrid);
	}

	@Override
	protected void draw(GL2 gl) {
		mGraph.draw(gl);		
	}

	public void updateData(long ts) {
		mGraph.update(ts);				
	}
}
