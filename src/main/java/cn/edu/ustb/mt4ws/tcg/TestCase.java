package cn.edu.ustb.mt4ws.tcg;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TestCase {

	/**
	 * Map(variable,value)
	 */
	private int id;
	private int mrID;
	private Map input;
	private Map expectedOutput;

	public TestCase(Map input, Map expectedOutput) {
		this.setInput(input);
		this.setExpectedOutput(expectedOutput);
	}

	public void setInput(Map input) {
		this.input = input;
	}

	public Map getInput() {
		return input;
	}

	public void setExpectedOutput(Map expectedOutput) {
		this.expectedOutput = expectedOutput;
	}

	public Map getExpectedOutput() {
		return expectedOutput;
	}

	public String printTC() {
		StringBuffer output = new StringBuffer();
		Iterator<Map.Entry> iter = input.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry myEntry = iter.next();
			String varName = ((Variable) myEntry.getKey()).getName();
			String value = (String) myEntry.getValue();
			output.append(varName + ":" + value + "\n");
		}
		return output.toString();
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setMrID(int mrID) {
		this.mrID = mrID;
	}

	public int getMrID() {
		return mrID;
	}
}
