package cn.edu.ustb.mt4ws.tcg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.flotsam.xeger.Xeger;

public class Temp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String regex = "[0-9]{10}";
		Xeger generator = new Xeger(regex);
		String result = generator.generate();
		System.out.println(result);
		
		Map<String,String> map=new HashMap<String,String>();
	}

}
