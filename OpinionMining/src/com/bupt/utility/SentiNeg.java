package com.bupt.utility;

/**
 * 情感词基本数据结构类
 * @author BUPT
 * @version 1.0
 */
public class SentiNeg {
	public String sentiment_word; //情感词
	public int polarity; //表示情感度正面、负面的权重度,只有+1、-1
	public int pos; //表示情感词所在分句块中的位置
	public int power; //表示情感词前后否定词的次数权重，一次以1递增，默认为1
	
	public SentiNeg(String s,int ps,int po,int pw)
	{
		sentiment_word = s;
		polarity = ps;
		pos = po;
		power = pw;
	}

	public String getSentiment_word() {
		return sentiment_word;
	}

	public void setSentiment_word(String sentimentWord) {
		sentiment_word = sentimentWord;
	}

	public int getPolarity() {
		return polarity;
	}

	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	
	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}
}
