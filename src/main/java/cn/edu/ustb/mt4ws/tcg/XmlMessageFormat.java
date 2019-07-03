package cn.edu.ustb.mt4ws.tcg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaType;

public class XmlMessageFormat {

//	public Set<SchemaLocalElement> builtInTypes;

	public Set<SchemaLocalElement> complexTypes;

	public Map<String, SchemaType> simpleTypes;

	public XmlMessageFormat() {
//		builtInTypes = new LinkedHashSet<SchemaLocalElement>();
		complexTypes = new LinkedHashSet<SchemaLocalElement>();
		simpleTypes = new LinkedHashMap<String, SchemaType>();
	}

	public XmlMessageFormat(Set<SchemaLocalElement> builtInTypes,
			Set<SchemaLocalElement> ComplexTypes,
			Map<String, SchemaType> simpleTypes) {
//		this.builtInTypes = builtInTypes;
		this.complexTypes = ComplexTypes;
		this.simpleTypes = simpleTypes;

	}

	public XmlMessageFormat merge(XmlMessageFormat tobeMerged) {
//		this.builtInTypes.addAll(tobeMerged.builtInTypes);
		this.simpleTypes.putAll(tobeMerged.simpleTypes);
		this.complexTypes.addAll(tobeMerged.complexTypes);
		return this;
	}

	public String toString() {
		StringBuffer strb = new StringBuffer(2048);
		strb.append(printComplexTypes()).append(
				printSimpleTypes());
		return strb.toString();
	}

//	public String printBuiltInTypes() {
//		StringBuffer strb = new StringBuffer(2048);
//		Iterator<SchemaLocalElement> iter = builtInTypes.iterator();
//		while (iter.hasNext()) {
//			SchemaLocalElement element = iter.next();
//			if (element != null)
//				strb.append(element.getName() + ":").append(element.getType())
//						.append("\r\n");
//		}
//		return strb.toString();
//	}

//	public Map<String, SchemaType> getBuiltInTypes() {
//		Iterator<SchemaLocalElement> iter = builtInTypes.iterator();
//		Map<String, SchemaType> nameAndType = new LinkedHashMap<String, SchemaType>();
//		while (iter.hasNext()) {
//			SchemaLocalElement element = iter.next();
//			if (element != null)
//				nameAndType
//						.put(element.getName().toString(), element.getType());
//		}
//		return nameAndType;
//	}

	public String printComplexTypes() {
		StringBuffer strb = new StringBuffer(2048);
		Iterator<SchemaLocalElement> iter2 = complexTypes.iterator();
		while (iter2.hasNext()) {
			SchemaLocalElement element = iter2.next();
			if (element != null)
				strb.append(element.getName() + ":").append(element.getType())
						.append("\r\n");
		}
		return strb.toString();
	}

	public String printSimpleTypes() {
		StringBuffer strb = new StringBuffer(2048);
		Set<Map.Entry<String, SchemaType>> entry = this.simpleTypes.entrySet();
		Iterator<Map.Entry<String, SchemaType>> iter3 = entry.iterator();
		Map.Entry<String, SchemaType> myentry = null;
		while (iter3.hasNext()) {
			myentry = iter3.next();
			strb.append(myentry.getKey() + ":").append(
					myentry.getValue().getName().getLocalPart()).append("\r\n");
		}
		return strb.toString();
	}

	public Map<String, SchemaType> getSimpleTypes() {
		return this.simpleTypes;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getVariables() {
		List<String> varList = new ArrayList<String>();
		Iterator<Map.Entry<String, SchemaType>> iter = this.simpleTypes
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, SchemaType> myEntry = iter.next();
			String var = myEntry.getKey();
			varList.add(var);
		}
//		Iterator<SchemaLocalElement> iter2 = this.builtInTypes.iterator();
//		while (iter2.hasNext()) {
//			SchemaLocalElement var = iter2.next();
//			varList.add(var.getName().getLocalPart());
//		}
		return varList;
	}

}
