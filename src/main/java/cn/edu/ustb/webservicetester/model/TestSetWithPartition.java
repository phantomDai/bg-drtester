package cn.edu.ustb.webservicetester.model;

public class TestSetWithPartition {
	
	private Partition[] partitions;
	private double[] profile;

	public TestSetWithPartition() {
		partitions = null;
		profile = null;
	}
	
	public TestSetWithPartition(int numOfPartitions) {
		partitions = new Partition[numOfPartitions];
		setProfileEvenly();
	}
	
	public TestSetWithPartition(Partition[] ps) {
		setPartitions(ps);
		setProfileEvenly();
	}
	
	public TestSetWithPartition(Partition[] ps, double[] prf) {
		setPartitions(ps);
		setProfile(prf);
	}
	
	public void setPartitions(Partition[] ps) {
		if (ps != null && ps.length > 0) {
			int i = 0;
			for (Partition p : ps) {
				if (p.numOfTestCases() > 0) {
					++i;
				}
			}
			partitions = new Partition[i];
			i = 0;
			for (int j = 0; j < ps.length; ++j) {
				if (ps[j].numOfTestCases() > 0) {
					partitions[i] = ps[j];
					++i;
				}
			}
		}
	}

	public void setProfile(double[] prf) {
		if (prf != null && prf.length > 0) {
			profile = new double[prf.length];
			for (int i = 0; i < prf.length; ++i) {
				profile[i] = prf[i];
			}
		}
	}

	public void setProfileEvenly() {
		if (partitions != null && partitions.length > 0) {
			profile = new double[partitions.length];
			for (int i = 0; i < profile.length; ++i) {
				profile[i] = 1.0 / profile.length;
			}
		}
	}
	
	public int numOfPartitions() {
		if (partitions == null)
			return 0;
		return partitions.length;
	}
	
	public int numOfTestCases() {
		int n = 0;
		if (partitions != null && partitions.length > 0) {
			for (Partition p : partitions) {
				n += p.numOfTestCases();
			}
		}
		return n;
	}
	
	public Partition[] getPartitions() {
		return partitions;
	}
	
	public Partition getPartition(int i) {
		return partitions[i];
	}
	
	/**
	 * 
	 * @param pIndex 分区序号
	 * @param tcIndex 分区内测试用例的序号
	 * @return
	 */
	public TestCase getTestCase(int pIndex, int tcIndex) {
		Partition p = partitions[pIndex];
		return p.get(tcIndex);
	}
	
	public double[] getProfile() {
		return profile;
	}
}
