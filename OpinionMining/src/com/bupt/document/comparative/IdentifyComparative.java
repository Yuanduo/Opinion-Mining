package com.bupt.document.comparative;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.bupt.utility.ImportSentimentDictionary;
import com.bupt.utility.Punctuation;
import com.bupt.utility.SVMResult;
import com.bupt.zconfigfactory.ConfigFactory;

/**
 * 比较句识别
 * @author BUPT
 *
 */

public class IdentifyComparative {
	public int type;

    /**
     * 比较关键字查找
	 * @param content
	 *       待进行关键字查找的句子
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  boolean haveComparativeKeyword(String content){
	
		Object key = null;
		boolean foundKey = false;

		for (Iterator iter = ImportSentimentDictionary.keywords.keySet().iterator(); iter.hasNext();) {
		      key = iter.next();		   
		      if(content.contains((String)key)){
				foundKey = true;					
				}
		}
		return foundKey;
	}
	



	/**
	 * XML比较句识别
	 * @param content
	 *        待进行比较句识别的句子
	 * @param file
	 * 		  比较句模式库路径
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  LinkedList<SVMResult> identifyComparativeByXML(String content, String file){
		
		LinkedList<SVMResult> res = new LinkedList<SVMResult>();
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
					type = Integer.parseInt(node2.getText());
					@SuppressWarnings("unused")
					String keyword;
				
					 res.add(new SVMResult(type,result));
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
