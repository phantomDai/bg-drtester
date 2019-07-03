package cn.edu.ustb.webservicetester.util;

import cn.edu.ustb.webservicetester.model.*;

public class Partitioner {
	
	public TestSetWithPartition makePartitions(TestSet testSet, java.util.List<java.util.Map<ParameterInfo, String>> partitionScenario) {
		java.util.List<Partition> partitionList = new java.util.LinkedList<Partition>();
		Partition tempPartition = null;
		java.util.Iterator<java.util.Map<ParameterInfo, String>> partScenaIter = partitionScenario.iterator();
		while (partScenaIter.hasNext()) {
			java.util.Map<ParameterInfo, String> r = partScenaIter.next();
			tempPartition = new Partition(r);
			partitionList.add(tempPartition);
		}
		for (int i = 0; i < testSet.numOfTestCases(); ++i) {
			TestCase tc = testSet.get(i);
			for (int j = 0; j < partitionList.size(); ++j) {
				tempPartition = partitionList.get(j);
				if (tempPartition.couldAdd(tc)) {
					tempPartition.add(tc);
				}
			}
		}
		for (int i = 0; i < partitionList.size(); ++i) {
			tempPartition = partitionList.get(i);
			if (tempPartition.numOfTestCases() == 0) {
				partitionList.remove(i);
				--i;
			}
		}
		Partition[] partitions = new Partition[partitionList.size()];
		for (int i = 0; i < partitionList.size(); ++i) {
			partitions[i] = partitionList.get(i);
			System.out.println(partitions[i]);
		}
		
		TestSetWithPartition tswp = null;
		tswp = new TestSetWithPartition(partitions);
		return tswp;
	}

}
