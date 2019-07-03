package cn.edu.ustb.webservicetester.model;

import java.util.Map.Entry;

public class Partition extends TestSet {
	private java.util.Map<ParameterInfo, String> rules;
	
	public Partition() {
		super();
		rules = new java.util.HashMap<ParameterInfo, String>();
	}
	
	public Partition(java.util.Map<ParameterInfo, String> rs) {
		super();
		rules = rs;
	}
	
	public void addRule(ParameterInfo pi, String rStr) {
		rules.put(pi, rStr);
	}
	
	public String getRule(ParameterInfo pi) {
		if (!rules.containsKey(pi))
			return null;
		return rules.get(pi);
	}
	
	public boolean couldAdd(TestCase tc) {
		java.util.List<ParameterInfo> paraInfoList = tc.getParameters();
		boolean ca = true;
		for (int i = 0; i < paraInfoList.size() && ca; ++i) {
			ParameterInfo pi = paraInfoList.get(i);
			if (pi.getType() == ParameterInfo.WHATEVER) {
				continue;
			}
			
			if (!rules.containsKey(pi)) {
				ca = false;
				continue;
			}
			
			String ci = pi.getClassInfo();
			if (pi.getType() == ParameterInfo.SCATTERED) {
				ca = tc.getValueOfParameter(pi).equals(rules.get(pi));
			} else{
				String rule = rules.get(pi);
				String ss[] = rule.split(",");
				String s1 = ss[0].substring(1).trim();
				String s2 = ss[1].substring(0, ss[1].length() - 1).trim();
				String s = tc.getValueOfParameter(pi);
				if ("byte".equals(ci)
						|| "short".equals(ci)
						|| "int".equals(ci)
						|| "long".equals(ci)) {
					long l1 = new Long(s1);
					long l2 = new Long(s2);
					long l = new Long(s);
					if (l < l2 && l > l1) {
						ca = true;
					} else if (rule.startsWith("[") && l == l1) {
						ca = true;
					} else if (rule.endsWith("]") && l == l2) {
						ca = true;
					} else {
						ca = false;
					}
				} else if ("float".equals(ci) || "double".equals(ci)) {
					double d1 = new Double(s1);
					double d2 = new Double(s2);
					System.out.println(pi.getName());
					System.out.println(s);
					System.out.println(tc.containsParameter(pi));
					System.out.println(i);
					double d = new Double(s);
					if (d < d2 && d > d1) {
						ca = true;
					} else if (rule.startsWith("[") && Math.abs(d - d1) < 1e-5) {
						ca = true;
					} else if (rule.endsWith("]") && Math.abs(d2 - d) < 1e-5) {
						ca = true;
					} else {
						ca = false;
					}
				} else if ("char".equals(ci)
						&& s1.length() == 1
						&& s2.length() == 1
						&& s.length() == 1) {
					char c1 = s1.charAt(0);
					char c2 = s2.charAt(0);
					char c = s.charAt(0);
					if (c < c2 && c > c1) {
						ca = true;
					} else if (rule.startsWith("[") && c == c1) {
						ca = true;
					} else if (rule.endsWith("]") && c == c2) {
						ca = true;
					} else {
						ca = false;
					}
				} else {
					// 出错了
				}
			}
		}
		return ca;
	}
	
	public String toString() {
		StringBuffer rsb = new StringBuffer();
		for (Entry<ParameterInfo, String> ent : rules.entrySet()) {
			rsb.append(ent.getKey());
			rsb.append(": ");
			rsb.append(ent.getValue());
			rsb.append("\n");
		}
		rsb.append("[\n");
		java.util.Iterator<TestCase> tcIter = testCases.iterator();
		while (tcIter.hasNext()) {
			TestCase tc = tcIter.next();
			rsb.append(tc);
			rsb.append("\n");
		}
		rsb.append("]");
		return rsb.toString();
	}
	
}
