package com.bupt.utility;


/**
 * 用作比较句识别的数据结构类
 * @author BUPT
 * @version 1.0
 */
public class SVMResult {
	public int type; //关键词类型
	public boolean iscomp; //是否比较句

	public SVMResult(int t,boolean b)
	{
		type= t;
		iscomp = b;
	}

	
}

