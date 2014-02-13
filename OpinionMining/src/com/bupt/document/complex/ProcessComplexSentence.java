package com.bupt.document.complex;

import com.bupt.utility.Punctuation;


/**
 * 复杂句转换为简单句处理模块
 * input：String 原复杂句 & type 复杂句类型
 * output：restring 转换后的简单句
 * @author BUPT
 *
 */
public class ProcessComplexSentence {
	public static final String CONJUNCTION_A = "即使/c";
	public static final String CONJUNCTION_B = "即便/c";	 
	public static final String CONJUNCTION_C = "不论/c";
	public static final String CONJUNCTION_D = "不管/c";
	public static final String CONJUNCTION_E = "无论/c";
	public static final String CONJUNCTION_F = "虽然/c";
	public static final String CONJUNCTION_G = "没有/v";
	public static final String CONJUNCTION_H = "没有/d";
	
	
	/**
	 * 复杂句转换为简单句
	 * @param str
	 *        分词后的复杂句
	 * @param type
	 *        复杂句类型
	 * @return
	 * 
	 * 
	 */
	public static String processComplexSentence(String str,int type){
		String restring = "";
		
		//类别一的复杂句处理，即整句不一定为真，整句省略
		if(type==1);
		
		//类别二的复杂句处理，即无论条件子句真或假，结果子句定为真，只取结果子句
		else if(type==2)
		{
			String []Review = str.split(Punctuation.COMMA_A);//按逗号分句
			for(int counter = 0 ; counter < Review.length ; counter++)
			{
				//System.out.println(Review[counter]);
				String[] sent = Review[counter].split(Punctuation.SPACE);

				for(int i = 0; i < sent.length; i++)
				{
					if(sent[i].contentEquals(CONJUNCTION_A)||sent[i].contentEquals(CONJUNCTION_B)||sent[i].contentEquals(CONJUNCTION_C)
							||sent[i].contentEquals(CONJUNCTION_D)||sent[i].contentEquals(CONJUNCTION_E)||sent[i].contentEquals(CONJUNCTION_F))
					{
					Review[counter]="";break;
					}
				
				}
			 if(!Review[counter].isEmpty()){
				if(restring.isEmpty())restring = restring+Review[counter];
			    else restring = restring+Punctuation.COMMA_A+Review[counter];
			 }
		   }
		}
		
		//类别三的复杂句处理，条件子句和结果子句都为真
		else if(type==3)
		{
			String[] Review = str.split(Punctuation.SPACE);
			for(int counter = 0; counter < Review.length; counter++)
			{
				if(Review[counter].contentEquals(CONJUNCTION_G)||Review[counter].contentEquals(CONJUNCTION_H))continue;
				else{
					restring = restring+Punctuation.SPACE+Review[counter];
				}
			}
		}

		return restring;
	}
}
