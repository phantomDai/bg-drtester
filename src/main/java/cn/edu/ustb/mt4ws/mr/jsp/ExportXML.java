package cn.edu.ustb.mt4ws.mr.jsp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import cn.edu.ustb.mt4ws.mr.model.*;

public class ExportXML {

	public void exportXML(OutputStreamWriter writer,
			List<MetamorphicRelation> mrList) throws IOException {
		// 输出头文件信�?

		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.write("\n");
		writer
				.write("<mrSet wsdl=\"\" operation=\"\" xsi:noNamespaceSchemaLocation=\"MRDL.xsd\" ");
		writer
				.write("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		writer.write("\n");

		// 输出mrList信息
		for (int i = 0; i < mrList.size(); i++) {
			exportMrXML(writer, mrList.get(i));
		}

		writer.write("</mrSet>");
	}

	// 输出Mr函数
	public void exportMrXML(OutputStreamWriter writer, MetamorphicRelation mr)
			throws IOException {
		writer.write("\t<mr>");
		writer.write("\n");
		exportRelationofInput(writer, mr.getRelationOfInput());
		exportRelationofOutput(writer, mr.getRelationOfOutput());
		writer.write("\t</mr>");
		writer.write("\n");
	}

	// 输出Input的关�?
	public void exportRelationofInput(OutputStreamWriter writer,
			RelationOfInput relationofinput) throws IOException {
		String start1 = "\t\t";
		String start2 = "\t\t\t";
		String start3 = "\t\t\t\t";
		String start4 = "\t\t\t\t\t";

		writer.write(start1 + "<relationOfInput>");
		writer.write("\n");
		for (int j = 0; j < relationofinput.getRelationOfInput().size(); j++) {
			int k = relationofinput.getRelationOfInput().get(j).getOp()
					.getType();
			writer.write(start2 + "<relation>");
			writer.write("\n");
			writer.write(start3 + "<functionFollowUp>");
			writer.write("\n");
			writer.write(start4
					+ "<funcDescription>"
					+ relationofinput.getRelationOfInput().get(j)
							.getFunctionFollowUp().getFunctionDescription()
					+ "</funcDescription>");
			writer.write("\n");
			writer.write(start3 + "</functionFollowUp>");
			writer.write("\n");
			writer.write(start3 + "<functionSource>");
			writer.write("\n");
			writer.write(start4
					+ "<funcDescription>"
					+ relationofinput.getRelationOfInput().get(j)
							.getFunctionSource().getFunctionDescription()
					+ "</funcDescription>");
			writer.write("\n");
			writer.write(start3 + "</functionSource>");
			writer.write("\n");
			writer
					.write(start3
							+ "<operator>"
							+ relationofinput.getRelationOfInput().get(j)
									.getOp().opNames[k] + "</operator>");
			writer.write("\n");
			writer.write(start2 + "</relation>");
			writer.write("\n");

		}

		writer.write(start1 + "</relationOfInput>");
		writer.write("\n");

	}

	// 输出Output的关�?
	public void exportRelationofOutput(OutputStreamWriter writer,
			RelationOfOutput relationOfOutput) throws IOException {
		String start1 = "\t\t";
		String start2 = "\t\t\t";
		String start3 = "\t\t\t\t";
		String start4 = "\t\t\t\t\t";

		writer.write(start1 + "<relationOfOutput>");
		writer.write("\n");
		for (int j = 0; j < relationOfOutput.getRelationOfOutput().size(); j++) {
			int k = relationOfOutput.getRelationOfOutput().get(j).getOp()
					.getType();
			writer.write(start2 + "<relation>");
			writer.write("\n");
			writer.write(start3 + "<functionFollowUp>");
			writer.write("\n");
			writer.write(start4
					+ "<funcDescription>"
					+ relationOfOutput.getRelationOfOutput().get(j)
							.getFunctionFollowUp().getFunctionDescription()
					+ "</funcDescription>");
			writer.write("\n");
			writer.write(start3 + "</functionFollowUp>");
			writer.write("\n");
			writer.write(start3 + "<functionSource>");
			writer.write("\n");
			writer.write(start4
					+ "<funcDescription>"
					+ relationOfOutput.getRelationOfOutput().get(j)
							.getFunctionSource().getFunctionDescription()
					+ "</funcDescription>");
			writer.write("\n");
			writer.write(start3 + "</functionSource>");
			writer.write("\n");
			writer
					.write(start3
							+ "<operator>"
							+ relationOfOutput.getRelationOfOutput().get(j)
									.getOp().opNames[k] + "</operator>");
			writer.write("\n");
			writer.write(start2 + "</relation>");
			writer.write("\n");

		}

		writer.write(start1 + "</relationOfOutput>");
		writer.write("\n");

	}

}
