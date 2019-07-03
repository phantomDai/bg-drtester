package cn.edu.ustb.mt4ws.mr.jsp;

import java.util.ArrayList;
import java.util.List;

public class TestProgress implements Runnable {
	public List<String> msgList;

	public void printTestProgress() {
		msgList = new ArrayList<String>();
		try {
			for (int i = 0; i < 10; i++) {
				msgList.add("progress " + i);
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public void run() {
		printTestProgress();
		// TODO Auto-generated method stub
	}

}
