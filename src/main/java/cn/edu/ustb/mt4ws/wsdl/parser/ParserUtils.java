package cn.edu.ustb.mt4ws.wsdl.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.soap.SchemaWSDLArrayType;

import cn.edu.ustb.mt4ws.tcg.XmlMessageFormat;
import cn.edu.ustb.mt4ws.tcg.XmlTcgUtils;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.support.xsd.SchemaUtils;
import com.eviware.soapui.settings.WsdlSettings;
import com.eviware.soapui.support.StringUtils;

/**
 * parse utility for WSDL Operation schema
 * @author WangGuan
 *
 */

public class ParserUtils {
	private XmlMessageFormat format = new XmlMessageFormat();
	private String operationName;

	private boolean _soapEnc;
	private boolean _exampleContent = false;// true的时候在sample
	// SOAP产生示例值，false的时候不产生
	private boolean _typeComment = false;
	private Set<QName> excludedTypes = new HashSet<QName>();
	private Map<QName, String[]> multiValues = null;
	private boolean _skipComments;
	private XmlTcgUtils tcgUtils = new XmlTcgUtils();

	public ParserUtils() {
	}

	public ParserUtils(boolean soapEnc, String operationName) {
		_soapEnc = soapEnc;
		this.setOperationName(operationName);
		excludedTypes.addAll(SchemaUtils.getExcludedTypes());
	}

	public boolean isSoapEnc() {
		return _soapEnc;
	}

	public boolean isExampleContent() {
		return _exampleContent;
	}

	public void setExampleContent(boolean content) {
		_exampleContent = content;
	}

	public boolean isTypeComment() {
		return _typeComment;
	}

	public void setTypeComment(boolean comment) {
		_typeComment = comment;
	}

	public void setMultiValues(Map<QName, String[]> multiValues) {
		this.multiValues = multiValues;
	}

	// public String createSample(SchemaType sType) {
	// XmlObject object = XmlObject.Factory.newInstance();
	// XmlCursor cursor = object.newCursor();
	// // Skip the document node
	// cursor.toNextToken();
	// // Using the type and the cursor, call the utility method to get a
	// // sample XML payload for that Schema element
	// createSampleForType(sType, cursor);
	// // Cursor now contains the sample payload
	// // Pretty print the result. Note that the cursor is positioned at the
	// // end of the doc so we use the original xml object that the cursor was
	// // created upon to do the xmlText() against.
	//
	// cursor.dispose();
	//
	// XmlOptions options = new XmlOptions();
	// options.put(XmlOptions.SAVE_PRETTY_PRINT);
	// options.put(XmlOptions.SAVE_PRETTY_PRINT_INDENT, 3);
	// options.put(XmlOptions.SAVE_AGGRESSIVE_NAMESPACES);
	// options.setSaveOuter();
	// String result = object.xmlText(options);
	//
	// return result;
	// }

	// public static String createSampleForElement(SchemaGlobalElement element)
	// {
	// XmlObject xml = XmlObject.Factory.newInstance();
	//
	// XmlCursor c = xml.newCursor();
	// c.toNextToken();
	// c.beginElement(element.getName());
	//
	// new ParserUtils(false).createSampleForType(element.getType(), c);
	//
	// c.dispose();
	//
	// XmlOptions options = new XmlOptions();
	// options.put(XmlOptions.SAVE_PRETTY_PRINT);
	// options.put(XmlOptions.SAVE_PRETTY_PRINT_INDENT, 3);
	// options.put(XmlOptions.SAVE_AGGRESSIVE_NAMESPACES);
	// options.setSaveOuter();
	// String result = xml.xmlText(options);
	//
	// return result;
	// }

	// public static String createSampleForType(SchemaType sType) {
	// XmlObject object = XmlObject.Factory.newInstance();
	// XmlCursor cursor = object.newCursor();
	// // Skip the document node
	// cursor.toNextToken();
	// // Using the type and the cursor, call the utility method to get a
	// // sample XML payload for that Schema element
	// new ParserUtils(false,null).createSampleForType(sType, cursor);
	// // Cursor now contains the sample payload
	// // Pretty print the result. Note that the cursor is positioned at the
	// // end of the doc so we use the original xml object that the cursor was
	// // created upon to do the xmlText() against.
	//
	// cursor.dispose();
	// XmlOptions options = new XmlOptions();
	// options.put(XmlOptions.SAVE_PRETTY_PRINT);
	// options.put(XmlOptions.SAVE_PRETTY_PRINT_INDENT, 3);
	// options.put(XmlOptions.SAVE_AGGRESSIVE_NAMESPACES);
	// options.setSaveOuter();
	// String result = object.xmlText(options);
	//
	// return result;
	// }

	private boolean ignoreOptional;

	/**
	 * Parse format for a schema element
	 * Create the sample SOAP Request for a WSDL operation
	 * @param colName name of the element
	 * @param stype SchemaType of the element
	 * @param xmlc XML Cursor who write sample SOAP Request 
	 * @return a XmlMessageFormat for a WSDL operation
	 */
	
	public XmlMessageFormat createSampleForType(String colName,
			SchemaType stype, XmlCursor xmlc) {
		_exampleContent = SoapUI.getSettings().getBoolean(
				WsdlSettings.XML_GENERATION_TYPE_EXAMPLE_VALUE);
		_typeComment = SoapUI.getSettings().getBoolean(
				WsdlSettings.XML_GENERATION_TYPE_COMMENT_TYPE);
		_skipComments = SoapUI.getSettings().getBoolean(
				WsdlSettings.XML_GENERATION_SKIP_COMMENTS);

		QName nm = stype.getName();
		if (nm == null && stype.getContainerField() != null)
			nm = stype.getContainerField().getName();

		if (nm != null && excludedTypes.contains(nm)) {
			if (!_skipComments)
				xmlc.insertComment("Ignoring type [" + nm + "]");
			return null;
		}

		if (_typeStack.contains(stype))
			return null;

		_typeStack.add(stype);

		try {
			if (stype.isSimpleType() || stype.isURType()) {
				processSimpleType(colName, stype, xmlc);
				return this.format;
			}

			// complex Type
			// <theElement>^</theElement>
			processAttributes(stype, xmlc);

			// <theElement attri1="string">^</theElement>
			switch (stype.getContentType()) {
			case SchemaType.NOT_COMPLEX_TYPE:
			case SchemaType.EMPTY_CONTENT:
				// noop
				break;
			case SchemaType.SIMPLE_CONTENT: {
				processSimpleType(colName, stype, xmlc);
			}
				break;
			case SchemaType.MIXED_CONTENT:
				xmlc.insertChars(tcgUtils.pick(tcgUtils.WORDS) + " ");
				if (stype.getContentModel() != null) {
					processParticle(stype.getContentModel(), xmlc, true);
				}
				xmlc.insertChars(tcgUtils.pick(tcgUtils.WORDS));
				break;
			case SchemaType.ELEMENT_CONTENT:
				if (stype.getContentModel() != null) {
					processParticle(stype.getContentModel(), xmlc, false);
				}
				break;
			}
		} finally {
			_typeStack.remove(_typeStack.size() - 1);
		}
		// printFormat();
		return this.format;
	}

	private void processSimpleType(String colName, SchemaType stype,
			XmlCursor xmlc) {
		// TODO
		if (stype != null)
			this.format.simpleTypes.put(colName, stype);
		else if (_soapEnc) {
			QName typeName = stype.getName();
			if (typeName != null) {
				xmlc.insertAttributeWithValue(XSI_TYPE, tcgUtils.formatQName(
						xmlc, typeName));
			}
		}

		String sample = "?";
		xmlc.insertChars(sample);
	}

	/**
	 * Cracks a combined QName of the form URL:localname
	 */
	public static QName crackQName(String qName) {
		String ns;
		String name;

		int index = qName.lastIndexOf(':');
		if (index >= 0) {
			ns = qName.substring(0, index);
			name = qName.substring(index + 1);
		} else {
			ns = "";
			name = qName;
		}

		return new QName(ns, name);
	}

	/**
	 * Cursor position: Before this call: <outer><foo/>^</outer> (cursor at the
	 * ^) After this call: <<outer><foo/><bar/>som text<etc/>^</outer>
	 */
	private void processParticle(SchemaParticle sp, XmlCursor xmlc,
			boolean mixed) {
		if (sp.getName() != null && sp.getType() != null) {
			if (sp.getType().isBuiltinType() == false) {
				this.format.complexTypes.add((SchemaLocalElement) sp);
			}
		}

		// TODO
		// Input temp = new Input("", format);
		// System.out.println(temp.toString());

		int loop = determineMinMaxForSample(sp, xmlc);

		while (loop-- > 0) {
			switch (sp.getParticleType()) {
			case (SchemaParticle.ELEMENT):
				processElement(sp, xmlc, mixed);
				break;
			case (SchemaParticle.SEQUENCE):
				processSequence(sp, xmlc, mixed);
				break;
			case (SchemaParticle.CHOICE):
				processChoice(sp, xmlc, mixed);
				break;
			case (SchemaParticle.ALL):
				processAll(sp, xmlc, mixed);
				break;
			case (SchemaParticle.WILDCARD):
				processWildCard(sp, xmlc, mixed);
				break;
			default:
				// throw new Exception("No Match on Schema Particle Type: " +
				// String.valueOf(sp.getParticleType()));
			}
		}
	}

	private int determineMinMaxForSample(SchemaParticle sp, XmlCursor xmlc) {

		int minOccurs = sp.getIntMinOccurs();
		int maxOccurs = sp.getIntMaxOccurs();

		if (minOccurs == maxOccurs)
			return minOccurs;

		if (minOccurs == 0 && ignoreOptional)
			return 0;

		int result = minOccurs;
		if (result == 0)
			result = 1;

		if (sp.getParticleType() != SchemaParticle.ELEMENT)
			return result;

		// it probably only makes sense to put comments in front of individual
		// elements that repeat

		if (!_skipComments) {
			if (sp.getMaxOccurs() == null) {
				// xmlc.insertComment("The next " + getItemNameOrType(sp, xmlc)
				// + "
				// may
				// be repeated " + minOccurs + " or more times");
				if (minOccurs == 0)
					xmlc.insertComment("Zero or more repetitions:");
				else
					xmlc.insertComment(minOccurs + " or more repetitions:");
			} else if (sp.getIntMaxOccurs() > 1) {
				xmlc.insertComment(minOccurs + " to "
						+ String.valueOf(sp.getMaxOccurs()) + " repetitions:");
			} else {
				xmlc.insertComment("Optional:");
			}
		}

		return result;
	}

	/*
	 * Return a name for the element or the particle type to use in the comment
	 * for minoccurs, max occurs
	 */
	@SuppressWarnings("unused")
	private String getItemNameOrType(SchemaParticle sp, XmlCursor xmlc) {
		String elementOrTypeName = null;
		if (sp.getParticleType() == SchemaParticle.ELEMENT) {
			elementOrTypeName = "Element (" + sp.getName().getLocalPart() + ")";
		} else {
			elementOrTypeName = printParticleType(sp.getParticleType());
		}
		return elementOrTypeName;
	}

	private void processElement(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {

		// cast as schema local element
		SchemaLocalElement element = (SchemaLocalElement) sp;
		// Add comment about type
		addElementTypeAndRestricionsComment(element, xmlc);

		// / ^ -> <elemenname></elem>^
		if (_soapEnc)
			xmlc.insertElement(element.getName().getLocalPart()); // soap
		// encoded?
		// drop
		// namespaces.
		else
			xmlc.insertElement(element.getName().getLocalPart(), element
					.getName().getNamespaceURI());
		// / -> <elem>^</elem>
		// processAttributes( sp.getType(), xmlc );
		xmlc.toPrevToken();
		// -> <elem>stuff^</elem>

		String[] values = null;
		if (multiValues != null)
			values = multiValues.get(element.getName());
		if (values != null)
			xmlc.insertChars(StringUtils.join(values, ","));
		else if (sp.isDefault())
			xmlc.insertChars(sp.getDefaultText());
		else
			createSampleForType(element.getName().getLocalPart(), element
					.getType(), xmlc);
		// -> <elem>stuff</elem>^
		xmlc.toNextToken();
	}

	@SuppressWarnings("unused")
	private void moveToken(int numToMove, XmlCursor xmlc) {
		for (int i = 0; i < Math.abs(numToMove); i++) {
			if (numToMove < 0) {
				xmlc.toPrevToken();
			} else {
				xmlc.toNextToken();
			}
		}
	}

	private static final QName HREF = new QName("href");
	private static final QName ID = new QName("id");
	public static final QName XSI_TYPE = new QName(
			"http://www.w3.org/2001/XMLSchema-instance", "type");
	public static final QName ENC_ARRAYTYPE = new QName(
			"http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
	private static final QName ENC_OFFSET = new QName(
			"http://schemas.xmlsoap.org/soap/encoding/", "offset");

	public static final Set<QName> SKIPPED_SOAP_ATTRS = new HashSet<QName>(
			Arrays.asList(new QName[] { HREF, ID, ENC_OFFSET }));

	private void processAttributes(SchemaType stype, XmlCursor xmlc) {
		if (_soapEnc) {
			QName typeName = stype.getName();
			if (typeName != null) {
				xmlc.insertAttributeWithValue(XSI_TYPE, tcgUtils.formatQName(
						xmlc, typeName));
			}
		}

		SchemaProperty[] attrProps = stype.getAttributeProperties();
		for (int i = 0; i < attrProps.length; i++) {
			SchemaProperty attr = attrProps[i];
			if (attr.getMinOccurs().intValue() == 0 && ignoreOptional)
				continue;

			if (attr.getName().equals(
					new QName("http://www.w3.org/2005/05/xmlmime",
							"contentType"))) {
				xmlc.insertAttributeWithValue(attr.getName(), "application/?");
				continue;
			}

			if (_soapEnc) {
				if (SKIPPED_SOAP_ATTRS.contains(attr.getName()))
					continue;
				if (ENC_ARRAYTYPE.equals(attr.getName())) {
					SOAPArrayType arrayType = ((SchemaWSDLArrayType) stype
							.getAttributeModel().getAttribute(attr.getName()))
							.getWSDLArrayType();
					if (arrayType != null)
						xmlc.insertAttributeWithValue(attr.getName(), tcgUtils
								.formatQName(xmlc, arrayType.getQName())
								+ arrayType.soap11DimensionString());
					continue;
				}
			}

			String value = null;
			if (multiValues != null) {
				String[] values = multiValues.get(attr.getName());
				if (values != null)
					value = StringUtils.join(values, ",");
			}
			if (value == null)
				value = attr.getDefaultText();
			if (value == null)
				value = tcgUtils.sampleDataForSimpleType(attr.getType(), null);
			
			//wendy created at 2012-12-4
			this.format.simpleTypes.put(attr.getName().getLocalPart(), attr.getType());
			xmlc.insertAttributeWithValue(attr.getName(), value);
			
		
		}
	}

	private void processSequence(SchemaParticle sp, XmlCursor xmlc,
			boolean mixed) {
		SchemaParticle[] spc = sp.getParticleChildren();
		for (int i = 0; i < spc.length; i++) {
			// / <parent>maybestuff^</parent>
			processParticle(spc[i], xmlc, mixed);
			// <parent>maybestuff...morestuff^</parent>
			if (mixed && i < spc.length - 1)
				xmlc.insertChars(tcgUtils.pick(tcgUtils.WORDS));
		}
	}

	private void processChoice(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
		SchemaParticle[] spc = sp.getParticleChildren();
		if (!_skipComments)
			xmlc.insertComment("You have a CHOICE of the next "
					+ String.valueOf(spc.length) + " items at this level");

		for (int i = 0; i < spc.length; i++) {
			processParticle(spc[i], xmlc, mixed);
		}
	}

	private void processAll(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
		SchemaParticle[] spc = sp.getParticleChildren();
		if (!_skipComments)
			xmlc.insertComment("You may enter the following "
					+ String.valueOf(spc.length) + " items in any order");

		for (int i = 0; i < spc.length; i++) {
			processParticle(spc[i], xmlc, mixed);
			if (mixed && i < spc.length - 1)
				xmlc.insertChars(tcgUtils.pick(tcgUtils.WORDS));
		}
	}

	private void processWildCard(SchemaParticle sp, XmlCursor xmlc,
			boolean mixed) {
		if (!_skipComments)
			xmlc.insertComment("You may enter ANY elements at this point");
		// xmlc.insertElement("AnyElement");
	}

	/**
	 * This method will get the base type for the schema type
	 */

	@SuppressWarnings("unused")
	private static QName getClosestName(SchemaType sType) {
		while (sType.getName() == null)
			sType = sType.getBaseType();

		return sType.getName();
	}

	private String printParticleType(int particleType) {
		StringBuffer returnParticleType = new StringBuffer();
		returnParticleType.append("Schema Particle Type: ");

		switch (particleType) {
		case SchemaParticle.ALL:
			returnParticleType.append("ALL\n");
			break;
		case SchemaParticle.CHOICE:
			returnParticleType.append("CHOICE\n");
			break;
		case SchemaParticle.ELEMENT:
			returnParticleType.append("ELEMENT\n");
			break;
		case SchemaParticle.SEQUENCE:
			returnParticleType.append("SEQUENCE\n");
			break;
		case SchemaParticle.WILDCARD:
			returnParticleType.append("WILDCARD\n");
			break;
		default:
			returnParticleType.append("Schema Particle Type Unknown");
			break;
		}

		return returnParticleType.toString();
	}

	private ArrayList<SchemaType> _typeStack = new ArrayList<SchemaType>();

	public boolean isIgnoreOptional() {
		return ignoreOptional;
	}

	public void setIgnoreOptional(boolean ignoreOptional) {
		this.ignoreOptional = ignoreOptional;
	}

	private void addElementTypeAndRestricionsComment(
			SchemaLocalElement element, XmlCursor xmlc) {

		SchemaType type = element.getType();
		if (_typeComment && (type != null && type.isSimpleType())) {
			String info = "";

			XmlAnySimpleType[] values = type.getEnumerationValues();
			if (values != null && values.length > 0) {
				info = " - enumeration: [";
				for (int c = 0; c < values.length; c++) {
					if (c > 0)
						info += ",";

					info += values[c].getStringValue();
				}

				info += "]";
			}

			if (type.isAnonymousType())
				xmlc.insertComment("anonymous type" + info);
			else
				xmlc.insertComment("type: " + type.getName().getLocalPart()
						+ info);
		}
	}

	public void printFormat() {
		System.out.println("@@@@@@@@@@@@@@@@@@@" + format.toString());
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}
}
