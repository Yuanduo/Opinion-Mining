package com.bupt.document.simple;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import com.bupt.utility.ImportSentimentDictionary;
import com.bupt.utility.PathProcessor;
import com.bupt.utility.Punctuation;
import com.bupt.utility.SentiNeg;
import com.bupt.utility.SentiResult;
import com.bupt.utility.Word;

/**
 * 文档级情感分析中的简单句情感分析模块
 * @author BUPT
 */
public class ProcessSimpleSentence {
	public int orientation = 0;

	/**
	 * 用于文档级情感分析的基于语义指向的简单句子级情感分析模型
	 * @param review 
	 *        已经切分好的句子文本块
	 * @return result
	 *        情感度数据列表
	 */
	public LinkedList<SentiResult> syntaxAnalyzeForSemantic(String review)
	{
		LinkedList<SentiResult> result = new LinkedList<SentiResult>();
		ArrayList<Word> subSentence = new ArrayList<Word>();
		String[] sent = review.split(Punctuation.SPACE);
		LinkedList<String> ts = new LinkedList<String>(); //主题词集合
		LinkedList<Integer> negPos = new LinkedList<Integer>(); //否定词所在分句中的位置集合
		LinkedList<SentiNeg> sn = new LinkedList<SentiNeg>(); //情感词集合	
		boolean found = false; //是否找到主题词
		boolean hasDun = false;
		int tPOS=0;
		//是否包含顿号
		if(review.contains(Punctuation.PAUSE))
		{
			hasDun = true;
		}
		for(int i = 0 ;  i < sent.length ; i++)
		{
			if(sent[i].indexOf(Punctuation.SLASH) != -1)//如果分词块包含“/”
			{
			//分别把词和词性标签存入word中
			String srcWord = sent[i].substring(0, sent[i].indexOf(Punctuation.SLASH));
			Word word = new Word(srcWord, Punctuation.EMPTY);
			subSentence.add(word);}
		}
		for(int j = 0; j < subSentence.size(); j++)
		{
			//主题词处理
			if(ImportSentimentDictionary.topics.containsKey(subSentence.get(j).getSrcWord()))
			{
				found = true;
				ts.add(subSentence.get(j).getSrcWord());
				tPOS=j;
			}
			//否定词处理
			else if(ImportSentimentDictionary.negations.contains(subSentence.get(j).getSrcWord()))
			{
				negPos.add(j);
			}
			
			//情感词处理
			else if(ImportSentimentDictionary.sentiment_words.containsKey(subSentence.get(j).getSrcWord()))
			{	
	            
				if(ImportSentimentDictionary.sentiment_words.get(subSentence.get(j).getSrcWord())>0)//正面的情感词处理
				{						
					sn.add(new SentiNeg(subSentence.get(j).getSrcWord(),1,j,1));
				}					
				else//负面的情感词处理
				{
					sn.add(new SentiNeg(subSentence.get(j).getSrcWord(),-1,j,1));
				}
			}
			//判断是否情感词与后面的名字形成修饰关系
		
		}
		//未找到主题词，退出
		if(!found)
		{
			return result;
		}
		//找到主题词，替换顿号，情感倾向更明显
		
		/*
		if(found&&hasDun)

		{
			review = review.replaceFirst(".+、", ts.get(0)+"、");
			review = review.replaceAll("、.+、", "");
		}
		*/
		
		//情感词集合和否定词集合是一一对应的
		//遍历情感词，查找情感词前后词的距离小于5的否定词，重设对应的否定词集合
		for(int i = 0; i < sn.size(); i++)
		{
			for(int j = 0; j < negPos.size(); j++)
			{
				//在情感词距离小于5的前后范围查找否定词，找到，置反,同时权重加1
				if((Math.abs(sn.get(i).pos - negPos.get(j)) < 5))
				{
					//如果只有一个否定词，默认对所有的情感词都起作用
					if(negPos.size() != 1)
					{
						//判断该情感词之前的情感词与否定词是否存在修辞关系
						if(i - 1 >= 0)
						{
							//之前有更近情感词，则该否定词不对情感词有影响
							if(Math.abs(sn.get(i-1).pos - negPos.get(j)) < Math.abs(sn.get(i).pos - negPos.get(j))) 
							{
								continue;
							}
						}
						
						//判断该情感词之后的情感词与否定词是否存在修辞关系
						if(i +1 < sn.size())
						{
							//之后有更近情感词，则该否定词不对情感词有影响
							if(Math.abs(sn.get(i+1).pos - negPos.get(j)) < Math.abs(sn.get(i).pos - negPos.get(j))) 
							{
								continue;
							}
						}							
					}
					
					//确定该情感词与否定词存在修辞关系
					sn.set(i, new SentiNeg(sn.get(i).sentiment_word, -1*sn.get(i).polarity, sn.get(i).pos, ++sn.get(i).power));
				}
			}
		}
		
		//遍历，检查句法模板中是否包含配对的主题词和情感词
		int weigh = 0;
		int weighTopic = 0; //判断主题词的正负
		for(int i = 0 ; i < ts.size() ; i++)
		{
			for(int j = 0 ; j < sn.size() ; j++)
			{
				//if(ImportSentimentDictionary.patterns.contains(PR.getPathOfCertainWords(review.trim(), ts.get(i), sn.get(j).sentiment_word)))
				//{
					//sentiment为负面,polarity在数据库中默认为-9
					weigh = ImportSentimentDictionary.sentiment_words.get(sn.get(j).sentiment_word);
					//对权重的选取标准，负面情感词（3） > 正负面（2）
					//负面词的处理
					if(weigh == -9)
					{
						//情感词前后存在否定词
						if(sn.get(j).power != 1)
						{
							//正面，乘以3的倍数权重
							if(sn.get(j).polarity == 1)
							{
								//weigh=1;
								weigh = 3*sn.get(j).power;
							}
							//负面，乘以-3的倍数权重
							else
							{
								//weigh=-1;
								weigh = -3*sn.get(j).power;
							}								
						}
						//情感词前后没有否定词
						else
						{
							//weigh=1;
							weigh = -3;
						}
					}
					//正面词的处理
					else
					{
						//情感词前后存在否定词
						if(sn.get(j).power != 1)
						{								
							//正面，乘以2的倍数权重
							if(sn.get(j).polarity == 1)
							{
								//weigh=1;
								weigh = 2*sn.get(j).power;
							}
							//负面，乘以-2的倍数权重
							else
							{
								//weigh=-1;
								weigh = -2*sn.get(j).power;
							}								
						}
						else //默认为1
						{
						
						}
					}
					
					//判断主题词是否的正负 
					//负面主题词在数据库中默认为-9
					 weighTopic = ImportSentimentDictionary.topics.get(ts.get(i));
					 if(weighTopic == -9)
					 {
						 //情感词为负，则不对主题词权重加权
						 if(weigh < 0)
						 {
							 weighTopic = -1;	
						 }
						 //情感词为正，置主题词权重-2
						 else
						 {   //weighTopic=-2;
							 weighTopic = -4;	
						 }
					 }
					 else //主题词为正
					 {
						 weighTopic = 2;
						 if(weigh < 0)
						 {   //weighTopic=1;
							 weighTopic = 2;	
						 }
						 //情感词为正，置主题词权重-2
						 else
						 {
							 weighTopic = 1;	
						 }
					 
						 
					 }
					 result.add(new SentiResult(ts.get(i),sn.get(j).sentiment_word,weigh*weighTopic));
				//}
			}
		}
		return result;
	}
}
