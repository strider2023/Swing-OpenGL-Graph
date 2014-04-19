package com.touchmenotapps.graph.gl;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;

/**
 * 
 * @author Arindam Nath
 * @version 1.0
 *
 */
public class GLGraphPlotter {
	
	private int SAMPLES_IN_SCREEN = 1000;
	private SamplesBuffer mSamplesBuffer;
	private GraphRenderer mGraphRenderer;
	private long currentTime = 0;
	private GLCanvas glCanvas;
	private Animator animator;
	
	/**
	 * Class constructor
	 * @param r - Graph red color content value
	 * @param g - Graph green color content value
	 * @param b - Graph blue color content value
	 * @param max_plot_points - Max number of plot points on the x-axis
	 */
	public GLGraphPlotter(int r, int g, int b, int max_plot_points) {
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities glcaps = new GLCapabilities(glp);
		glCanvas = new GLCanvas(glcaps);
		SAMPLES_IN_SCREEN = max_plot_points;
		
		mSamplesBuffer = new SamplesBuffer(SAMPLES_IN_SCREEN);
		mGraphRenderer = new GraphRenderer(mSamplesBuffer, r, g, b);
		glCanvas.addGLEventListener(mGraphRenderer);
		
		animator = new Animator();
		animator.add(glCanvas);
	}
	
	/**
	 * Get the Open GL canvas reference
	 * @return - View's <b>GLCanvas</b> instance
	 */
	public GLCanvas getCanvas() {
		return glCanvas;
	}
	
	/**
	 * Start the real time rendering animation
	 */
	public void startRendering() {
		animator.start();
	}
	
	/**
	 * Stop the real time rendering animation
	 */
	public void stopRendering() {
		animator.stop();
	}
	
	/**
	 * Start the dummy real time rendering animation
	 */
	public void startDummyDataRenderer() {
		FakeDataGenerator thread = new FakeDataGenerator();
		thread.start();
		animator.start();
	}
	
	/**
	 * Add the next the plot point to be displayed on the graph
	 * @param value - <b>short</b> Value to be updated
	 */
	public void setPlotPoint(short value) {
		mSamplesBuffer.addSample(value, currentTime);
		mGraphRenderer.updateData(currentTime);
		glCanvas.invalidate();		
		currentTime+=1;
	}
	
	/**
	 * Generate and add a new dummy data
	 */
	private void updateDummyData() {
		double sinValue = Math.sin((double)(((currentTime/4)%360+1*45)*Math.PI/180.));
		short sampleValue = (short)(80.*sinValue);
		mSamplesBuffer.addSample(sampleValue, currentTime);
		mGraphRenderer.updateData(currentTime);
		glCanvas.invalidate();		
		currentTime+=1;
	}
	
	/**
	 * 
	 * @author Arindam Nath
	 *
	 */
	private class FakeDataGenerator extends Thread {
		@Override
		public void run() {
			while(true) {
				updateDummyData();
				try {
					sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
