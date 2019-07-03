package cn.edu.ustb.mt4ws.tcg;

public class WsdlOperationFormat {

	private String operationName;

	private XmlInputFormat xmlInputFormat;

	private XmlOutputFormat xmlOutputFormat;

	public WsdlOperationFormat() {

	}

	/**
	 * Builder模式
	 * 
	 * @param operationName
	 * @return
	 */
	public WsdlOperationFormat build(String operationName) {
		this.setOperationName(operationName);
		return this;
	}

	/**
	 * Builder模式
	 * 
	 * @param in
	 * @param xmlOutputFormat
	 * @return
	 */
	public WsdlOperationFormat build(XmlInputFormat in, XmlOutputFormat xmlOutputFormat) {
		this.xmlInputFormat = in;
		this.xmlOutputFormat = xmlOutputFormat;
		return this;
	}

	public String toString() {
		String str = null;
		str = this.xmlInputFormat.toString() + this.xmlOutputFormat.toString();
		return str;
	}

	public XmlInputFormat getInput() {
		return this.xmlInputFormat;

	}

	public XmlOutputFormat getOutput() {
		return this.xmlOutputFormat;

	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}
}
