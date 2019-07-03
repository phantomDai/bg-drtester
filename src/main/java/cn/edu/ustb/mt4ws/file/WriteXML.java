package cn.edu.ustb.mt4ws.file;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import cn.edu.ustb.mt4ws.javabean.*;

public class WriteXML {

	/**
	 * 输出mrSet，遍历每个mr	
	 * @param writer
	 * @param mrList
	 * @throws IOException 
	 */
	public void writeMrList(OutputStreamWriter writer,
			List<MetamorphicRelation> mrList) throws IOException{
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.write("\n");
		writer
				.write("<mrSet wsdl=\"\" operation=\"\" xsi:noNamespaceSchemaLocation=\"MRDL.xsd\" ");
		writer
				.write("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		writer.write("\n");

		//遍历每个metamorphic
		for (int i = 0; i < mrList.size(); i++) {
			writeMr(writer, mrList.get(i));
		}
		
		writer.write("</mrSet>");
			
	}
	
	/**
	 * 输出每个mr
	 * @param writer
	 * @param mrList
	 * @throws IOException
	 */
	private void writeMr(OutputStreamWriter writer,
			MetamorphicRelation mr) throws IOException{
		writer.write("\t<mr>");
		writer.write("\n");
		//input
		writeInput(writer, mr.getRelationOfInput());
		//output
		writeOutput(writer,mr.getRelaitonOfOutput());
		
		writer.write("\t</mr>");
		writer.write("\n");
		
	}
	/**
	 * 输出每个mr中的Input
	 * @param writer
	 * @param input
	 * @throws IOException
	 */
	private void writeInput(OutputStreamWriter writer,
			RelationOfInput input)throws IOException{		
		writer.write("\t\t" + "<relationOfInput>");
		writer.write("\n");
		String start="\t\t\t";
		//写第一条relation
		writeRelation(writer, input.getRelation(),start);
		//判断是否有OpAndRelationList
		if (input.getListOpAndRe() != null) {
			List<OpAndRelation> listOpAndRe = input.getListOpAndRe();
			//遍历写完每个OpAndRe
			for (int i = 0; i < listOpAndRe.size(); i++) {
				writeOpAndRelation(writer, listOpAndRe.get(i));
			}
		}
		
		writer.write("\t\t" + "</relationOfInput>");
		writer.write("\n");
	}

	/**
	 * 输出每个mr中的output
	 * 
	 * @param writer
	 * @param output
	 * @throws IOException
	 */
	private void writeOutput(OutputStreamWriter writer, RelationOfOutput output)
			throws IOException {
		writer.write("\t\t" + "<relationOfOutput>");
		writer.write("\n");
		
		String start="\t\t\t";
		//写第一条relation
		writeRelation(writer, output.getRelation(),start);
		//判断是否有OpAndRelationList
		if(output.getListOpAndRe()!=null){
			List<OpAndRelation> listOpAndRe=output.getListOpAndRe();
			for (int i = 0; i < listOpAndRe.size(); i++) {
				//遍历写完每个OpAndRe
				writeOpAndRelation(writer, listOpAndRe.get(i));
			}
			
		}
				
		writer.write("\t\t" + "</relationOfOutput>");
		writer.write("\n");
	}

	/**
	 * 输出每条relation
	 * @param writer
	 * @param relation
	 * @throws IOException
	 */
	private void writeRelation(OutputStreamWriter writer,
			Relation relation,String spaceStr)throws IOException{
		writer.write(spaceStr + "<relation>");
		writer.write("\n");
		int type_not=relation.getNotOp().getType();
		int type_op = relation.getOp().getType();
		//not
		writer.write(spaceStr + "\t" + "<NEGOp>"
				+ relation.getNotOp().opNames[type_not] + "</NEGOp>");
		writer.write("\n");
		
		//expressionFollowUp
		writer.write(spaceStr + "\t" + "<expressionFollowUp>");
		writer.write("\n");
		//expDescription
		writer.write(spaceStr + "\t\t" + "<expDescription>"
				+ relation.getFunctionFollowUp().getFunctionDescription()
				+ "</expDescription>");
		writer.write("\n");
		writer.write(spaceStr + "\t" + "</expressionFollowUp>");
		writer.write("\n");
		
		//op
		writer.write(spaceStr + "\t" + "<operator>"
				+ relation.getOp().opNames[type_op]+ "</operator>");
		writer.write("\n");
		
		//expressionSource
		writer.write(spaceStr + "\t" + "<expressionSource>");
		writer.write("\n");
		//expDescription
		writer.write(spaceStr + "\t\t" + "<expDescription>"
				+ relation.getFunctionSource().getFunctionDescription()
				+ "</expDescription>");
		writer.write("\n");
		writer.write(spaceStr + "\t" + "</expressionSource>");
		writer.write("\n");
		
		
		writer.write(spaceStr + "</relation>");
		writer.write("\n");
		
	}
	
	/**
	 * 输出opAndrelaiton
	 * @param writer
	 * @param opAndrelation
	 * @throws IOException
	 */
	private void writeOpAndRelation(OutputStreamWriter writer,
			OpAndRelation opAndrelation)throws IOException{
		writer.write("\t\t\t" + "<opAndrelation>");
		writer.write("\n");
		//输出relation之间的Operator
		int type=opAndrelation.getOperator().getType();
		writer.write("\t\t\t\t" + "<reOperator>"
				+ opAndrelation.getOperator().opNames[type] + "</reOperator>");
		writer.write("\n");
		//输入relation
		writeRelation(writer, opAndrelation.getRelation(), "\t\t\t\t");

		writer.write("\t\t\t" + "</opAndrelation>");
		writer.write("\n");	
	}
	
	
}
