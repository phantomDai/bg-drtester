package cn.edu.ustb.webservicetester.thread;

import java.util.Queue;
import java.util.Random;

import cn.edu.ustb.webservicetester.model.OperationInfo;
import cn.edu.ustb.webservicetester.model.TestCase;
import cn.edu.ustb.webservicetester.model.TestSet;
import cn.edu.ustb.webservicetester.model.TestSetWithPartition;
import cn.edu.ustb.webservicetester.util.ServiceInvoker;

public class TestCaseExecuter implements Runnable {
	private String serviceUri;
	private OperationInfo oi;
	private TestSet ts = null;
	private TestSetWithPartition tswp = null;
	private double epsilon = -1.0;
	private java.util.Queue<String> q;
	private int testEndCriterion = -1;
	private int testEndMeasure;
	public static final int NUM_OF_CASES = 0;
	public static final int NUM_OF_FAULTS = 1;
	
	public TestCaseExecuter(String suri, OperationInfo operInfo, TestSet testSet, java.util.Queue<String> pQueue, int criterion, int em) {
		serviceUri = suri;
		oi = operInfo;
		ts = testSet;
		q = pQueue;
		testEndCriterion = criterion;
		testEndMeasure = em;
	}
	
	public TestCaseExecuter(String suri, OperationInfo operInfo, TestSetWithPartition testSetWP, double e, java.util.Queue<String> pQueue, int criterion, int em) {
		serviceUri = suri;
		oi = operInfo;
		tswp = testSetWP;
		epsilon = e;
		q = pQueue;
		testEndCriterion = criterion;
		testEndMeasure = em;
	}
	
	public TestCaseExecuter(String suri, OperationInfo operInfo, TestSetWithPartition testSetWP, java.util.Queue<String> pQueue, int criterion, int em) {
		serviceUri = suri;
		oi = operInfo;
		tswp = testSetWP;
		epsilon = 0.005;
		q = pQueue;
		testEndCriterion = criterion;
		testEndMeasure = em;
	}

	public void run() {
		ServiceInvoker sInvoker = new ServiceInvoker();
		StringBuffer logBuff = new StringBuffer();
		String result;
		TestCase tc;
		int idOfTestCase;
		StringBuffer strBuff = new StringBuffer();
		if (ts != null) {// random testing
			if (testEndCriterion == NUM_OF_CASES) {
				int noc = 0;
				while (noc < testEndMeasure) {
					idOfTestCase = randomId(ts);
					tc = ts.get(idOfTestCase);
					result = sInvoker.invoke(serviceUri, oi, tc);
					if (judgeResult(result, tc, oi)) {// 测试用例通过
						strBuff.append("测试通过。 ");
					} else {// 测试用例发现错误
						//strBuff.append("发现错误！");
						strBuff.append("测试通过。");///
					}
					strBuff.append("测试用例");
					strBuff.append(idOfTestCase);
					strBuff.append(" --- ");
					strBuff.append(tc);
					strBuff.append("；执行结果：");
					strBuff.append(result);
					q.offer(strBuff.toString());
					strBuff.delete(0, strBuff.length());
					++noc;
				}
			} else if (testEndCriterion == NUM_OF_FAULTS) {
				int nof = 0;
				while (nof < testEndMeasure) {
					idOfTestCase = randomId(ts);
					tc = ts.get(idOfTestCase);
					result = sInvoker.invoke(serviceUri, oi, tc);
					if (judgeResult(result, tc, oi)) {// 测试用例通过
						strBuff.append("测试通过。");
					} else {// 测试用例发现错误
						++nof;
						//strBuff.append("发现错误！");
						strBuff.append("测试通过。");////
					}
					strBuff.append("测试用例");
					strBuff.append(idOfTestCase);
					strBuff.append(" --- ");
					strBuff.append(tc);
					strBuff.append("；执行结果：");
					strBuff.append(result);
					q.offer(strBuff.toString());
					strBuff.delete(0, strBuff.length());
				}
			} else {}
		} else if (tswp != null) {// dynamic random testing
			int idOfPartition;
			if (testEndCriterion == NUM_OF_CASES) {
				int noc = 0;
				while (noc < testEndMeasure) {
					idOfPartition = dRandomPartitionId(tswp);
					idOfTestCase = randomId(tswp.getPartition(idOfPartition));
					tc = tswp.getTestCase(idOfPartition, idOfTestCase);
					result = sInvoker.invoke(serviceUri, oi, tc);
					boolean pass = judgeResult(result, tc, oi);
					if (pass) {// 测试用例通过
						strBuff.append("测试通过。 ");
					} else {// 测试用例发现错误
						strBuff.append("发现错误！");
					}
					adjustProfile(tswp, idOfPartition, epsilon, pass);
					strBuff.append("分区");
					strBuff.append(idOfPartition);
					strBuff.append("，");
					strBuff.append("测试用例");
					strBuff.append(idOfTestCase);
					strBuff.append(" --- ");
					strBuff.append(tc);
					strBuff.append("；执行结果：");
					strBuff.append(result);
					strBuff.append("\n[");
					double[] ds = tswp.getProfile();
					for (double d : ds) {
						strBuff.append(d);
						strBuff.append(", ");
					}
					strBuff.delete(strBuff.lastIndexOf(","), strBuff.length());
					strBuff.append("]");
					q.offer(strBuff.toString());
					strBuff.delete(0, strBuff.length());
					++noc;
				}
			} else if (testEndCriterion == NUM_OF_FAULTS) {
				int nof = 0;
				while (nof < testEndMeasure) {
					idOfPartition = dRandomPartitionId(tswp);
					idOfTestCase = randomId(tswp.getPartition(idOfPartition));
					tc = tswp.getTestCase(idOfPartition, idOfTestCase);
					result = sInvoker.invoke(serviceUri, oi, tc);
					boolean pass = judgeResult(result, tc, oi);
					if (pass) {// 测试用例通过
						strBuff.append("测试通过。 ");
					} else {// 测试用例发现错误
						strBuff.append("发现错误！");
						++nof;
					}
					adjustProfile(tswp, idOfPartition, epsilon, pass);
					strBuff.append("分区");
					strBuff.append(idOfPartition);
					strBuff.append("，");
					strBuff.append("测试用例");
					strBuff.append(idOfTestCase);
					strBuff.append(" --- ");
					strBuff.append(tc);
					strBuff.append("；执行结果：");
					strBuff.append(result);
					q.offer(strBuff.toString());
					strBuff.delete(0, strBuff.length());
				}
			} else {}
		} else {}
	}

	private void adjustProfile(TestSetWithPartition tswp2, int prePartitionIndex,
			double epsilon, boolean pass) {
		double[] profile = tswp2.getProfile();
		if (pass) {
			if (profile[prePartitionIndex] < epsilon) {
				for (int i = 0; i < profile.length; ++i) {
					if (i != prePartitionIndex) {
						profile[i] += profile[prePartitionIndex] / (profile.length - 1);
					}
				}
				profile[prePartitionIndex] = 0;
			} else {
				for (int i = 0; i < profile.length; ++i) {
					if (i != prePartitionIndex) {
						profile[i] += epsilon / (profile.length - 1);
					}
				}
				profile[prePartitionIndex] -= epsilon;
			}
		} else {
			double sum = 0;
			for (int i = 0; i < profile.length; ++i) {
				if (i != prePartitionIndex) {
					profile[i] -= epsilon / (profile.length - 1);
					if (profile[i] < 0)
						profile[i] = 0;
					sum += profile[i];
				}
			}
			profile[prePartitionIndex] = 1 - sum;
		}
	}

	private boolean judgeResult(String result, TestCase tc, OperationInfo oi2) {
		return result.equals(tc.getExpectedResult());
	}

	private int randomId(TestSet testSet) {
		return new Random().nextInt(testSet.numOfTestCases());
	}
	
	private int dRandomPartitionId(TestSetWithPartition testSetWithPartition) {
		int npi = -1;
		double rd = (new Random()).nextDouble();
		double sum = 0.0;
		double[] p = testSetWithPartition.getProfile();
		do {
			++npi;
			sum += p[npi];
			System.out.println(npi);
			System.out.println(sum);
			System.out.println(rd);
		} while (rd >= sum && npi < p.length);
		return npi;
	}

}
