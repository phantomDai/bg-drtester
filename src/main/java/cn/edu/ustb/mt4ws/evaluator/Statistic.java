package cn.edu.ustb.mt4ws.evaluator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.edu.ustb.mt4ws.configuration.Project;

public class Statistic {

	private Project project;

	private List<OperationStatistic> opStatisticList;

	public void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public void setOpStatisticList(List<OperationStatistic> opStatisticList) {
		this.opStatisticList = opStatisticList;
	}

	public List<OperationStatistic> getOpStatisticList() {
		return opStatisticList;
	}
	
	public void print(){
		project.print();
		Iterator<OperationStatistic> iter = opStatisticList.iterator();
		while(iter.hasNext()){
			OperationStatistic opStatistic = iter.next();
			opStatistic.print();
		}
	}
	
	public List<OperationStatistic> getFaultOperationList(){
		List<OperationStatistic> faultOperationList = new ArrayList<OperationStatistic>();
		Iterator<OperationStatistic> iterOp = this.getOpStatisticList().iterator();
		while(iterOp.hasNext()){
			OperationStatistic opSta = iterOp.next();
			Iterator<MRStatistic> iterMR = opSta.getMrStatisticList().iterator();
			while(iterMR.hasNext()){
				MRStatistic mrSta = iterMR.next();
				if(mrSta.getValidTestCaseList().size()>0){
					faultOperationList.add(opSta);
					break;
					}
			}
		}
		return faultOperationList;
	}

}
