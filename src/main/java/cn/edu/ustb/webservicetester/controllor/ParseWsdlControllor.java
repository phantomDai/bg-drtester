package cn.edu.ustb.webservicetester.controllor;

import javax.swing.SwingUtilities;

import ui.OperationSelectFrame;
import ui.OperationSelectionFrame2;
import ui.PreferenceFrame;
import cn.edu.ustb.webservicetester.util.WsdlParser;

public class ParseWsdlControllor {
	
	public void parseWsdlControl(String wsdlUrl) {
		WsdlParser parser = new WsdlParser();
		final java.util.List opInfoList = parser.parseWsdl(wsdlUrl);
		final java.util.Map<String, String> setting = new java.util.HashMap<String, String>();
		setting.put("Web Service", wsdlUrl.substring(0, wsdlUrl.lastIndexOf("?wsdl")));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PreferenceFrame inst = new PreferenceFrame(setting, opInfoList);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

}
