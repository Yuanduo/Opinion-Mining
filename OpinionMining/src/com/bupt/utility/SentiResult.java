package com.bupt.utility;

/**
 * 用作情感分析的情感度数据结构变量
 * @author BUPT
 * @version 1.0
 */
public class SentiResult {
	public String topic; //主题词
	public String sentiment_word; //情感词
	public int polarity; //表示情感度正面、负面的权重度

	public SentiResult(String t,String s,int p)
	{
		topic = t;
		sentiment_word = s;
		polarity = p;
	}
	public SentiResult(String t,int p)
	{
		topic = t;
		sentiment_word = "";
		polarity = p;
	}
	public SentiResult(int p)
	{
		topic = "";
		sentiment_word = "";
		polarity = p;
	}
}
