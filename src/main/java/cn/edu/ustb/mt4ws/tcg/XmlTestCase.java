package cn.edu.ustb.mt4ws.tcg;

import java.util.Map;

public class XmlTestCase extends TestCase {
	
	private String operationName;

	public XmlTestCase(String opName,Map<XmlVariable, String> input,
			Map<XmlVariable, String> expectedOutput) {
		super(input,expectedOutput);
		this.setOperationName(opName);
	}
	
	public XmlTestCase(String opName, int mrID,Map<XmlVariable, String> input,
			Map<XmlVariable, String> expectedOutput) {
		this(opName,input,expectedOutput);
		this.setMrID(mrID);
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}
}
