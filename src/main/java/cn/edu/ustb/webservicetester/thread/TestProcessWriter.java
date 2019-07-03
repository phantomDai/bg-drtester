package cn.edu.ustb.webservicetester.thread;

import java.awt.Color;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TestProcessWriter implements Runnable {
	
	private StyledDocument sDoc;
	private Queue<String> procQue;
	
	public TestProcessWriter(StyledDocument sd, Queue<String> sq) {
		sDoc = sd;
		procQue = sq;
	}
	
	public void run() {
		while (true) {
			if (!procQue.isEmpty()) {
				SimpleAttributeSet attr = new SimpleAttributeSet();
				StyleConstants.setBold(attr, false);
				StyleConstants.setForeground(attr, Color.BLACK);
				try {
					sDoc.insertString(sDoc.getLength(), procQue.poll() + "\n", attr);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			try {
				TimeUnit.MILLISECONDS.sleep(1100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
