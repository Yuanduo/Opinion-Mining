package com.bupt.document.complex;


import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import java.util.LinkedList;
import java.util.Iterator;
import org.dom4j.io.SAXReader; 
import com.bupt.utility.XMLResult;
import java.util.regex.Pattern;

/**
 * 复合句识别
 * @author BUPT
 *
 */
public class IdentifyComplex{
	
	/**
	 * XML复合句识别
	 * @param content
	 *        待复合句识别的句子
	 * @param file
	 *        复合句模式库路径
	 * @return
	 */
	public  LinkedList<XMLResult> identifyComplex(String content,String file){
		LinkedList<XMLResult> res = new LinkedList<XMLResult>();
		try{
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(file);
			Element root = document.getRootElement();
			Iterator iter= root.elementIterator("rule");  
			while (iter.hasNext())
			{
				Element node= (Element) iter.next(); 
				String regEx=node.getText(); //正则表达式形式
				boolean result=Pattern.compile(regEx).matcher(content).find();
				if(result)
				{
					Node node2 =node.selectSingleNode("CompareType");
					int type = Integer.parseInt(node2.getText());
					res.add(new XMLResult(regEx,type));
				}
         	}
          }
		catch (Exception ex)
		{   
		   ex.printStackTrace();   
		} 
		return res;	
	}
}