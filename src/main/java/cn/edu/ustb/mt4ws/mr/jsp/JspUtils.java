package cn.edu.ustb.mt4ws.mr.jsp;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlbeans.SchemaType;

import cn.edu.ustb.mt4ws.mr.Limitation;
import cn.edu.ustb.mt4ws.mr.MRUtils;
import cn.edu.ustb.mt4ws.javabean.*;

import cn.edu.ustb.mt4ws.tcg.XmlMessageFormat;

public class JspUtils {

	/**
	 * 验证JSP中用户输入的Test Case是否合法
	 * 
	 * @param format
	 * @param testcase
	 * @return boolean数组，数组第i个成员为true表示第i个变量合法；为false表示不合法
	 */
	public boolean[] validateTestCase(XmlMessageFormat inputFormat,
			RelationOfInput relationOfInput, List<String> testcase)
			throws Exception {
		if (inputFormat.simpleTypes.size() != testcase.size()) {
			throw new Exception(
					"The number of variables doesn't equal to input format");
		}
		boolean[] result = new boolean[testcase.size()];
		Map<String, Limitation> variableLimit = MRUtils.getLimitionOfSourceTCNew(
				inputFormat, relationOfInput);
		Iterator<String> iterValue = testcase.iterator();
		Iterator<String> iterVar = inputFormat.simpleTypes.keySet().iterator();
		int i = 0;
		while (iterValue.hasNext() && iterVar.hasNext()) {
			String value = iterValue.next();
			String varName = iterVar.next();
			SchemaType type = inputFormat.simpleTypes.get(varName);
			boolean typeValid = validateVarByType(value, type);
			Limitation limit = variableLimit.get(varName);
			result[i] = typeValid && validateVarByMR(value, limit);// Short-circuit
			i++;
		}
		return result;

	}
	
	/**
	 * 验证JSP中用户输入的Test Case是否合法
	 * 
	 * @author wendy
	 * @param format
	 * @param testcase
	 * @return boolean数组，数组第i个成员为true表示第i个变量合法；为false表示不合法
	 */
	public boolean[] validateTC(XmlMessageFormat inputFormat,
			List<String> testcase) throws Exception {
		if (inputFormat.simpleTypes.size() != testcase.size()) {
			throw new Exception(
					"The number of variables doesn't equal to input format");
		}
		boolean[] result = new boolean[testcase.size()];
		
		Iterator<String> iterValue = testcase.iterator();
		Iterator<String> iterVar = inputFormat.simpleTypes.keySet().iterator();
		int i = 0;
		while (iterValue.hasNext() && iterVar.hasNext()) {
			String value = iterValue.next();
			String varName = iterVar.next();
			SchemaType type = inputFormat.simpleTypes.get(varName);
			boolean typeValid = validateVarByType(value, type);
			result[i] = typeValid ;// Short-circuit
			i++;
		}
		return result;
	}

	/**
	 * Check if the value of variable matches with specified SchemaType
	 * @param value Value of the variable to be checked
	 * @param sType If the value matches with this
	 * @return
	 */
	private boolean validateVarByType(String value, SchemaType sType) {
		boolean isValid = false;
		int baseTypeCode = sType.getPrimitiveType().getBuiltinTypeCode();
		switch (baseTypeCode) {
		case SchemaType.BTC_DECIMAL:
			switch (closestBuiltin(sType).getBuiltinTypeCode()) {
			case SchemaType.BTC_SHORT:
				break;
			case SchemaType.BTC_UNSIGNED_SHORT:
				break;
			case SchemaType.BTC_BYTE:
				break;
			case SchemaType.BTC_UNSIGNED_BYTE:
				break;
			case SchemaType.BTC_INT:
			case SchemaType.BTC_INTEGER:
				try {
					Integer.parseInt(value);			
					isValid = true;
				} catch (NumberFormatException e) {
					isValid = false;
				}
				break;
			case SchemaType.BTC_UNSIGNED_INT:
				try {
					int temp = Integer.parseInt(value);
					if (temp > 0) {
						isValid = true;
					} else {
						isValid = false;
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}
				break;
			case SchemaType.BTC_LONG:
				try {
					Long.parseLong(value);
					isValid = true;
				} catch (NumberFormatException e) {
					isValid = false;
				}
				break;
			case SchemaType.BTC_UNSIGNED_LONG:
				try {
					long temp = Long.parseLong(value);
					if (temp > 0) {
						isValid = true;
					} else {
						isValid = false;
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}
			case SchemaType.BTC_NON_POSITIVE_INTEGER:
				try {
					int temp = Integer.parseInt(value);
					if (temp <= 0) {
						isValid = true;
					} else {
						isValid = false;
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}
				break;
			case SchemaType.BTC_NEGATIVE_INTEGER:
				try {
					int temp = Integer.parseInt(value);
					if (temp < 0) {
						isValid = true;
					} else {
						isValid = false;
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}
				break;
			case SchemaType.BTC_NON_NEGATIVE_INTEGER:
				try {
					int temp = Integer.parseInt(value);
					if (temp >= 0) {
						isValid = true;
					} else {
						isValid = false;
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}
				break;
			case SchemaType.BTC_POSITIVE_INTEGER:
				try {
					int temp = Integer.parseInt(value);
					if (temp > 0) {
						isValid = true;
					} else {
						isValid = false;
					}
				} catch (NumberFormatException e) {
					isValid = false;
				}
				break;
			}
		case SchemaType.BTC_FLOAT:
			try {
				Float.parseFloat(value);
				isValid = true;
			} catch (NumberFormatException e) {
				isValid = false;
			}
			break;
		case SchemaType.BTC_DOUBLE:
			try {
				Double.parseDouble(value);
				isValid = true;
			} catch (NumberFormatException e) {
				isValid = false;
			}
			break;
		case SchemaType.BTC_STRING:
			if (sType.hasPatternFacet()) {
				Pattern pattern = Pattern.compile(sType.getPatterns()[0]);
				Matcher matcher = pattern.matcher(value);
				isValid = matcher.matches();
			} else {
				isValid = true;
			}
			break;
		case SchemaType.BTC_DATE:
			try {
				DateFormat.getDateInstance().parse(value);
				isValid = true;
			} catch (NumberFormatException e) {
				isValid = false;
			} catch (ParseException e) {
				isValid = false;
			}
			break;
		}
		return isValid;
	}

	private boolean validateVarByMR(String value, Limitation limit) {
		if (limit == null)
			return true;
		return limit.check(value, null);

	}

	public SchemaType closestBuiltin(SchemaType sType) {
		while (!sType.isBuiltinType())
			sType = sType.getBaseType();
		return sType;
	}

}
