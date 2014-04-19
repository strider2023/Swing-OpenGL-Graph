package com.touchmenotapps.graph.gl;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public abstract class AbstractRenderer implements GLEventListener {

	float mRatio;
	GL2 gl;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		gl.glDisable(GL2.GL_DITHER);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST);
		gl.glClearColor(0, 0, 0, 0);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		gl = drawable.getGL().getGL2();
		gl.glViewport(0, 0, width, height);
		mRatio = (float) width/height;
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		//gl.glFrustumf(mRatio, -mRatio, -1, 1, 3, 7);
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		gl.glDisable(GL2.GL_DITHER);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		draw(gl);
	}
	
	protected abstract void draw(GL2 drawable);
}
