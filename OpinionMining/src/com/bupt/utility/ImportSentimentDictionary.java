package com.bupt.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.bupt.zconfigfactory.ConfigFactory;
/**
 * 导入红黑词典、比较模式库等
 * @author susie
 *
 */
public class ImportSentimentDictionary {	
	public static HashMap<String,Integer> sentiment_words = new HashMap<String,Integer>(); //情感词集合
	public static LinkedList<String> patterns = new LinkedList<String>(); //句法模块集合
	public static HashMap<String,Integer> topics = new HashMap<String,Integer>(); //主题词集合
	public static HashMap<String,Integer> compara_objects = new HashMap<String,Integer>(); //主题词集合
	public static LinkedList<String> negations = new LinkedList<String>(); //否定词集合
	public static LinkedList<String> action_words = new LinkedList<String>(); //二义性词集合
	public static HashMap<String,Integer> compara_words = new HashMap<String,Integer>(); //比较词集合
	public static HashMap<String,Integer> keywords = new HashMap<String,Integer>(); //比较关键字集合
	public static ArrayList<String> birules = new ArrayList<String>();//"比"字句规则
	public static ArrayList<String> zuirules = new ArrayList<String>(); //"最"字句规则
	public static ArrayList<String> yiyangrules = new ArrayList<String>(); //"一样"句规则
	
public	static void init() 
{
			try {
				sentiment_words=TextToDict.getHashMap(ConfigFactory.getString("TextToHash.sentiment_words"));
			    topics=TextToDict.getHashMap(ConfigFactory.getString("TextToHash.topics"));
			    compara_objects = TextToDict.getHashMap(ConfigFactory.getString("TextToHash.compare_objects"));
			    compara_objects.putAll(topics);
			    negations=TextToDict.getLinkList(ConfigFactory.getString("TextToHash.negation_words"));
			    patterns=TextToDict.getLinkList(ConfigFactory.getString("TextToHash.patterns"));
			    action_words=TextToDict.getLinkList(ConfigFactory.getString("TextToHash.action_words"));
			    negations.addAll(action_words);
			    compara_words=TextToDict.getHashMap(ConfigFactory.getString("TextToHash.compare_words"));
			    keywords=TextToDict.getHashMap(ConfigFactory.getString("TextToHash.compare_keywords"));	
			    birules = TextToDict.getArrayList(ConfigFactory.getString("TextToHash.bi"));
			    yiyangrules = TextToDict.getArrayList(ConfigFactory.getString("TextToHash.yiyang"));
			    zuirules = TextToDict.getArrayList(ConfigFactory.getString("TextToHash.zui"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			

	}

}






     		


