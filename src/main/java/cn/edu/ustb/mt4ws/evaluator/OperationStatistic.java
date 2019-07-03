package cn.edu.ustb.mt4ws.evaluator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OperationStatistic {

	private String operationName;
	private List<MRStatistic> mrStatisticList;

	public void setMrStatisticList(List<MRStatistic> mrStatisticList) {
		this.mrStatisticList = mrStatisticList;
	}

	public List<MRStatistic> getMrStatisticList() {
		return mrStatisticList;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}
	
	public void print(){
		System.out.println("OperationStatistic:"+operationName);
		Iterator<MRStatistic> iter = mrStatisticList.iterator();
		while(iter.hasNext()){
			MRStatistic mrStatistic = iter.next();
			mrStatistic.print();
		}
	}
	
	public List<MRStatistic> getFaultMRList() {
		List<MRStatistic> faultMRList = new ArrayList<MRStatistic>();
		Iterator<MRStatistic> iterMR = this.getMrStatisticList().iterator();
		while (iterMR.hasNext()) {
			MRStatistic mrSta = iterMR.next();
			if (mrSta.getValidTestCaseList().size() > 0) {
				if (faultMRList.contains(mrSta)) {// 是否包含mrSta
					continue;
				} else {
					faultMRList.add(mrSta);
				}
			}
		}
		return faultMRList;
	}
}
