package com.touchmenotapps.graph.gl.test;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.touchmenotapps.graph.gl.GLGraphPlotter;

public class GraphPlotterTest {
	
	public static void main(String[] args) {		
		GLGraphPlotter draw = new GLGraphPlotter(255, 0, 0, 1000);
		
		Frame frame = new Frame("OpenGL Graph Renderer");
		frame.setSize(500, 200);
		frame.add(draw.getCanvas());
		frame.setVisible(true);
		 
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) { }
			
			@Override
			public void windowIconified(WindowEvent e) { }
			
			@Override
			public void windowDeiconified(WindowEvent e) { }
			
			@Override
			public void windowDeactivated(WindowEvent e) { }
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) { }
			
			@Override
			public void windowActivated(WindowEvent e) { }
		});
		
		draw.startDummyDataRenderer();
	}
}
