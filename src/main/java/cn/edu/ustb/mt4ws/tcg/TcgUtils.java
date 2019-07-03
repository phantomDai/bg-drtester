package cn.edu.ustb.mt4ws.tcg;

import java.util.Random;

public class TcgUtils {
	
	static Random rd=new Random();
	
	public static String createRandomValue(int type){
		String value=null;
		switch(type){
		case Type.INT:
			value=Integer.toString(rd.nextInt(2000));
			break;
		case Type.DOUBLE:
			value=Double.toString(rd.nextDouble());
			break;
		default:
			break;
		}
		return value;
	}
	

}
