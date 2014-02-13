package com.bupt.sentence.simple;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.bupt.utility.ImportSentimentDictionary;
import com.bupt.utility.PathProcessor;
import com.bupt.utility.Punctuation;
import com.bupt.utility.SentiResult;
import com.bupt.utility.Word;
import com.bupt.zconfigfactory.ConfigFactory;
import ICTCLAS.kevin.zhang.ICTCLAS2010;
import ICTCLAS.kevin.zhang.WordSplitter;
import edu.stanford.nlp.trees.Tree;

/**
 * 情感分析模块
 * @author BUPT
 * @version 1.0
 */
public class ProcessSentence {
	private ICTCLAS2010 wordsplitter = new ICTCLAS2010(); //分词模块实例
	private PathProcessor PR = new PathProcessor(); //依赖关系树句法路径处理类实例
	public int orientation = 0;

	public ProcessSentence() {
			ConfigFactory.init("D:\\AnalysisConfig.xml");//配置文件初始化
			System.out.println("已初始化配置文件！");
			ImportSentimentDictionary.init();//导入词典
			System.out.println("已导入情感词词典！");


			}
	
	/**
	 * 基于依赖关系树的情感分析模型
	 * @param review 已经切分好的句子文本块
	 * @return 情感度数据列表
	 * @throws IOException 
	 */
	public LinkedList<SentiResult> syntaxAnalyze(String review) throws IOException
	{
		LinkedList<SentiResult> r = new LinkedList<SentiResult>();
		try {
			WordSplitter wsplitter = new WordSplitter();
			//导入用户分词词典
		    int n = wsplitter.importUserDict(ConfigFactory.getString("UserDic.name"));
		    System.out.println("用户词典数量："+n);
			//分句
			String taggedReview = wsplitter.split(review, 0);
			System.out.println(taggedReview);
			String[] sent = taggedReview.split(Punctuation.SPACE);
			LinkedList<String> ts = new LinkedList<String>();//主题词
			LinkedList<String> ss = new LinkedList<String>();//情感词
			LinkedList<Integer> negs = new LinkedList<Integer>();//否定词
			boolean found = false;
			boolean hasDun = false;
			//是否包含顿号
			if(taggedReview.contains(Punctuation.PAUSE))
			{
				hasDun = true;
			}
			int pos = -1; //否定词和主题词的距离
			int neg = 1;
			for(int i = 0 ; i < sent.length ; i++)
			{
				//主题词处理
				if(ImportSentimentDictionary.topics.containsKey(sent[i]))
				{
					found = true;
					ts.add(sent[i]);
				}
				//情感词处理
				else	if(ImportSentimentDictionary.sentiment_words.containsKey(sent[i]))
				{
					int a =0;
					if(!ss.contains(sent[i]))
					{
						//正面的情感词处理
						if(ImportSentimentDictionary.sentiment_words.get(sent[i])>0)
						{
							ss.addLast(sent[i]);
							negs.addLast(new Integer(1));
							a = 1;
						}
						//负面的情感词处理
						else
						{
							ss.addFirst(sent[i]);
							negs.addFirst(new Integer(1));
							a = 2;
						}
					}
					
					//有否定词，考虑否定词的影响
					if(pos!=-1)
					{
						if(i-pos<=3)
						{
							if(a ==1 )
							{
								int neN = negs.getLast()*-1;
								negs.removeLast();
								negs.addLast(neN);
							}
							else
							{
								int neN = negs.getFirst()*-1;
								negs.removeFirst();
								negs.addFirst(neN);
							}
						}
						pos = -1;
					}
				}
				//否定词处理
				else	if(ImportSentimentDictionary.negations.contains(sent[i]))
				{
					//已经有了否定词，判断相对于上次的否定词的距离
					if(pos!=-1)
					{
						//距离大于3不考虑
						if(i-pos>3)
						{
							pos = i;
							neg = -1;
						}
						else
						{
							pos = i;
							neg *=-1;
						}
					}
					//第一个否定词，设置距离
					else
					{
						pos = i;
						neg = -1;
					}
				}
			}
			//找到主题词，顿号后面的忽略
			if(found&&hasDun)
			{
				taggedReview = taggedReview.replaceFirst(".+、", ts.get(0)+Punctuation.PAUSE);
				taggedReview = taggedReview.replaceAll("、.+、", Punctuation.EMPTY);
			}
			//未找到主题词，忽略该行汉字
			if(!found&&hasDun)
			{
				taggedReview = taggedReview.replaceAll("[一-龥]+、", Punctuation.EMPTY);
			}
			//主题词、情感词过滤
			for(int i = 0 ; i < ts.size() ; i++)
			{
				if(!taggedReview.contains(ts.get(i)))
				{
					ts.remove(i);
					i--;
				}
			}
			for(int i = 0 ; i < ss.size() ; i++)
			{
				if(!taggedReview.contains(ss.get(i)))
				{
					ss.remove(i);
					i--;
				}
			}
			
			//遍历，检查句法模板中是否包含配对的主题词和情感词
			for(int i = 0 ; i < ts.size() ; i++)
			{
				for(int j = 0 ; j < ss.size() ; j++)
				{
					if(ImportSentimentDictionary.patterns.contains(PR.getPathOfCertainWords(taggedReview.trim(), ts.get(i), ss.get(j))))
					{
						r.add(new SentiResult(ts.get(i),ss.get(j),negs.get(j)*(ImportSentimentDictionary.sentiment_words.get(ss.get(j)))));
						ts.remove(i);
						ss.remove(j);
						i--;
						break;
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}
	

	/**
	 * 基于向量空间的句型结构的情感分析模型
	 * @param review 已经切分好的句子文本块
	 * @return 情感度数据列表
	 */
	public LinkedList<SentiResult>  windowAnalyze(String review)
	{
		orientation = 0;
		int sentiment = 0;
		int negation = 1;
		String topicw = "";
		ArrayList<Word> subSentence = new ArrayList<Word>();
		try 
		{
			//已做标注
			byte nativeBytes[] = wordsplitter.ICTCLAS_ParagraphProcess(review.getBytes("GB2312"), 1);
			String taggedReview = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			//以空格形式分词
			String[] arrTaggedReview = taggedReview.split("\\s");
			//split by space
			
			for(int i = 0; i < arrTaggedReview.length - 1; i++)
			{
				if(arrTaggedReview[i].indexOf("/") != -1)
				{
					//分离词和标注
					String srcWord = arrTaggedReview[i].substring(0, arrTaggedReview[i].indexOf("/"));
					String tag = arrTaggedReview[i].substring(arrTaggedReview[i].indexOf("/") + 1,arrTaggedReview[i].indexOf("/") + 2);
					Word word = new Word(srcWord, tag);
					subSentence.add(word);
								
					//标注指示结尾或词的最后一项
					if(tag.equals("w") || i == arrTaggedReview.length - 2) 
					{
						//递增的方式遍历
						for(int j = 0; j < subSentence.size(); j++)
						{
							//we search for a noun, possibly the topic of the sentence
							if(subSentence.get(j).getTag().equals("n"))
							{
								//确定是否为候选的主题词
								int topicPolarity = checkFeature(subSentence.get(j).getSrcWord());
								//if it is a candidate topic
								topicw = subSentence.get(j).getSrcWord();
								if(topicPolarity != 0)
								{								
									subSentence.get(j).setSTag("/topic");
									//向前计算情感词与主题词的距离
									int indexOfUpFirstModifier = upFirstModifierIndex(subSentence, j);
									//向后计算情感词与主题词的距离
									int indexOfDownModifier = downModifierIndex(subSentence, j);
										
									int up_Orientation = 0;
									int down_Orientation = 0;
									int up_Positive = 1;
									int down_Positive = 1;
									
									//前后都有修饰词
									if((indexOfUpFirstModifier != -1)&&(indexOfDownModifier != -1))
									{										
										int upDistance = j - indexOfUpFirstModifier;
										int downDistance = indexOfDownModifier - j;
										//后面的修饰词离主题词近一些
										if(upDistance > downDistance)
										{
											subSentence.get(indexOfDownModifier).setSTag("/modifier");
											if(subSentence.get(indexOfDownModifier).getTag().equals("a") || subSentence.get(indexOfDownModifier).getTag().equals("b"))
											{
												down_Orientation = checkAdjective(subSentence.get(indexOfDownModifier).getSrcWord());
											}
											if(subSentence.get(indexOfDownModifier).getTag().equals("v"))
											{
												down_Orientation = checkVerb(subSentence.get(indexOfDownModifier).getSrcWord());
											}
											down_Positive = isPositive(subSentence, j, indexOfDownModifier);
											down_Orientation *= down_Positive;
										}
										//前面的修饰词离主题词近一些
										else if (upDistance < downDistance)
										{
											subSentence.get(indexOfUpFirstModifier).setSTag("/modifier");
											if(subSentence.get(indexOfUpFirstModifier).getTag().equals("a") || subSentence.get(indexOfUpFirstModifier).getTag().equals("b"))
											{
												up_Orientation = checkAdjective(subSentence.get(indexOfUpFirstModifier).getSrcWord());
											}
											if(subSentence.get(indexOfUpFirstModifier).getTag().equals("v"))
											{
												up_Orientation = checkVerb(subSentence.get(indexOfUpFirstModifier).getSrcWord());
											}
												
											//需要进一步在前面的情感词和主题词之间找情感词
											int indexOfUpSecondModifier = upSecondModifierIndex(subSentence, indexOfUpFirstModifier);
											//做最近的情感词和主题词之间的否定判定
											up_Positive = isPositive(subSentence, indexOfUpSecondModifier, indexOfUpFirstModifier);
											up_Orientation *= up_Positive;
										}
										//前后的修饰词离主题词的距离相等，都做一下
										else
										{
											subSentence.get(indexOfDownModifier).setSTag("/modifier");
											subSentence.get(indexOfUpFirstModifier).setSTag("/modifier");
											if(subSentence.get(indexOfDownModifier).getTag().equals("a")|| subSentence.get(indexOfDownModifier).getTag().equals("b"))
											{
												down_Orientation = checkAdjective(subSentence.get(indexOfDownModifier).getSrcWord());
											}
											if(subSentence.get(indexOfDownModifier).getTag().equals("v"))
											{
												down_Orientation = checkVerb(subSentence.get(indexOfDownModifier).getSrcWord());
											}
											down_Positive = isPositive(subSentence, j, indexOfDownModifier);
											down_Orientation *= down_Positive;
												
											if(subSentence.get(indexOfUpFirstModifier).getTag().equals("a") || subSentence.get(indexOfUpFirstModifier).getTag().equals("b"))
											{
												up_Orientation = checkAdjective(subSentence.get(indexOfUpFirstModifier).getSrcWord());
											}
											if(subSentence.get(indexOfUpFirstModifier).getTag().equals("v"))
											{
												up_Orientation = checkVerb(subSentence.get(indexOfUpFirstModifier).getSrcWord());
											}
											
											int indexOfUpSecondModifier = upSecondModifierIndex(subSentence, indexOfUpFirstModifier);
											up_Positive = isPositive(subSentence, indexOfUpSecondModifier, indexOfUpFirstModifier);
											up_Orientation *= up_Positive;
										}										
									}
									//只有后面的修饰词
									else if(indexOfUpFirstModifier == -1 && indexOfDownModifier != -1)
									{
										subSentence.get(indexOfDownModifier).setSTag("/modifier");
										if(subSentence.get(indexOfDownModifier).getTag().equals("a") || subSentence.get(indexOfDownModifier).getTag().equals("b"))
										{
											down_Orientation = checkAdjective(subSentence.get(indexOfDownModifier).getSrcWord());
										}
										if(subSentence.get(indexOfDownModifier).getTag().equals("v"))
										{
											down_Orientation = checkVerb(subSentence.get(indexOfDownModifier).getSrcWord());
										}
										down_Positive = isPositive(subSentence, j, indexOfDownModifier);
										down_Orientation *= down_Positive;										
									}
									//只有前面的修饰词
									else if(indexOfDownModifier == -1 && indexOfUpFirstModifier != -1)
									{
										
										subSentence.get(indexOfUpFirstModifier).setSTag("/modifier");
										if(subSentence.get(indexOfUpFirstModifier).getTag().equals("a") || subSentence.get(indexOfUpFirstModifier).getTag().equals("b"))
										{
											up_Orientation = checkAdjective(subSentence.get(indexOfUpFirstModifier).getSrcWord());
										}
										if(subSentence.get(indexOfUpFirstModifier).getTag().equals("v"))
										{
											up_Orientation = checkVerb(subSentence.get(indexOfUpFirstModifier).getSrcWord());
										}
											
										int indexOfUpSecondModifier = upSecondModifierIndex(subSentence, indexOfUpFirstModifier);
										up_Positive = isPositive(subSentence, indexOfUpSecondModifier, indexOfUpFirstModifier);
										up_Orientation *= up_Positive;
									}
										
									orientation = up_Orientation + down_Orientation;
									/**/
									if(orientation != 0)
									{
										orientation*=topicPolarity;
									}
									else
									{
										continue;
									}

								}
							}
						}
						subSentence.clear();
					}
				}
			}			
		}
		catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		LinkedList<SentiResult> ret = new LinkedList<SentiResult>();
		if(orientation != 0)
		{
			SentiResult sr = new SentiResult(topicw,orientation);
			ret.add(sr);	
		}
		return ret;
	}
	/**
	 * 向前检查离主题词最近的情感词的距离
	 * @param midSen 欲判断的词
	 * @param featureIndex 初步距离
	 * @return 返回实际距离
	 */
	public int upFirstModifierIndex(ArrayList<Word> midSen, int featureIndex)
	{
		int indexOfUpFirstModifier = -1;
		int orientation = 0;
		//递减推进遍历，直到下一个名词
		for(int i = 1; (featureIndex - i >= 0) && (!midSen.get(featureIndex - i).getTag().equals("n")) ; i++)
		{
			//形容词或区别词
			if(midSen.get(featureIndex - i).getTag().equals("a") || midSen.get(featureIndex - i).getTag().equals("b"))
			{
				orientation = checkAdjective(midSen.get(featureIndex - i).getSrcWord());
			}
			//动词
			if(midSen.get(featureIndex - i).getTag().equals("v"))
			{
				orientation = checkVerb(midSen.get(featureIndex - i).getSrcWord());
			}
			if(orientation != 0)
			{
				//找到候选的情感词，记下距离
				indexOfUpFirstModifier = featureIndex - i;
				return indexOfUpFirstModifier;
			}				
		}
		return indexOfUpFirstModifier;
	}
	
	/**
	 * 向后检查离主题词最近的情感词的距离
	 * @param midSen 欲判断的词
	 * @param featureIndex 初步距离
	 * @return 返回实际距离
	 */
	public int downModifierIndex(ArrayList<Word> midSen, int featureIndex)
	{
		int indexOfDownModifier = -1;
		int orientation = 0;
		//递增推进遍历，直到下一个名词
		for(int i = 1; (featureIndex + i < midSen.size()) && (!midSen.get(featureIndex + i).getTag().equals("n")); i++)
		{
			//形容词或区别词
			if(midSen.get(featureIndex + i).getTag().equals("a") || midSen.get(featureIndex + i).getTag().equals("b"))
			{
				orientation = checkAdjective(midSen.get(featureIndex + i).getSrcWord());
			}
			//动词
			if(midSen.get(featureIndex + i).getTag().equals("v"))
			{
				orientation = checkVerb(midSen.get(featureIndex + i).getSrcWord());
			}
			if(orientation != 0)
			{
				//找到候选的情感词，记下距离
				indexOfDownModifier = featureIndex + i;
				return indexOfDownModifier;
			}				
		}
		return indexOfDownModifier;
	}
	
	/**
	 * 向前检查离主题词最近的情感词的距离
	 * @param midSen
	 * @param indexOfUpFirstModifier
	 * @return
	 */
	public int upSecondModifierIndex(ArrayList<Word> midSen, int indexOfUpFirstModifier)
	{
		int indexOfUpSecondModifier = -1;
		int orientation = 0;
		for(int i = 1; (indexOfUpFirstModifier - i >= 0) && (! midSen.get(indexOfUpFirstModifier - i).getTag().equals("n")) ; i++)
		{
			if(midSen.get(indexOfUpFirstModifier - i).getTag().equals("a") || midSen.get(indexOfUpFirstModifier - i).getTag().equals("b"))
			{
				orientation = checkAdjective(midSen.get(indexOfUpFirstModifier - i).getSrcWord());
			}
			if(midSen.get(indexOfUpFirstModifier - i).getTag().equals("v"))
			{
				orientation = checkVerb(midSen.get(indexOfUpFirstModifier - i).getSrcWord());
			}
			if(orientation != 0)
			{
				indexOfUpSecondModifier = indexOfUpFirstModifier - i;
				return indexOfUpSecondModifier;
			}				
		}
		return indexOfUpSecondModifier;
	}
	
	/**
	 * 检查当前词和情感词之间的否定词
	 * @param midSen
	 * @param preIndex 当前词位置
	 * @param postIndex 情感词位置
	 * @return 正负
	 */
	public int isPositive(ArrayList<Word> midSen, int preIndex, int postIndex)
	{
		int isPositive = 1;
		if(preIndex == -1)
		{
			for(int i = preIndex + 1; i < postIndex; i++)
			{
				//副词
				if(midSen.get(i).getTag().equals("d"))
				{
					isPositive *= checkNegative(midSen.get(i).getSrcWord());
				}
				//形容词
				else if(midSen.get(i).getTag().equals("a"))
				{
					isPositive *= checkAdjective(midSen.get(i).getSrcWord());
				}
			}
		}
		else
		{
			for(int i = preIndex; i < postIndex; i++)
			{
				if(midSen.get(i).getTag().equals("d"))
				{
					isPositive *= checkNegative(midSen.get(i).getSrcWord());
				}
				else if(midSen.get(i).getTag().equals("a"))
				{
					isPositive *= checkAdjective(midSen.get(i).getSrcWord());
				}
			}
		}
		return isPositive;
	}

	/**
	 * 检查形容词是否为情感词
	 * @param adjWord
	 * @return 该情感词的正负
	 */
	public int checkAdjective(String adjWord)
	{
		if(ImportSentimentDictionary.sentiment_words.containsKey(adjWord))
			return ImportSentimentDictionary.sentiment_words.get(adjWord);
		else
			return 0;
	}
	
	/**
	 * 检查名词是否为主题词
	 * @param nounWord 
	 * @return 该主题词的正负
	 */
	private int checkFeature(String nounWord)
	{
		if(ImportSentimentDictionary.topics.containsKey(nounWord))
			return ImportSentimentDictionary.topics.get(nounWord);
		else
			return 0;
	}
	
	/**
	 * 检查是否为负面词
	 * @param advWord
	 * @return
	 */
	public int checkNegative(String advWord)
	{
		for(int i = 0; i < ImportSentimentDictionary.negations.size();i++)
		{
			if(ImportSentimentDictionary.negations.get(i).equals(advWord))
			{
				return -1;
			}	
		}
		return 1;
	}
	
	/**
	 * 检查副词是否为情感词
	 * @param verbWord
	 * @return 该情感词的正负
	 */
	public int checkVerb(String verbWord)
	{
		if(ImportSentimentDictionary.sentiment_words.containsKey(verbWord))
			return ImportSentimentDictionary.sentiment_words.get(verbWord);
		else
			return 0;
	}

	/**
	 * 
	 * @param cur
	 * @param root
	 * @param way
	 * @return
	 */
	public String getValue(Tree cur,Tree root,String way)
	{
		
		String [] com = way.split("%");
		//System.out.println(way);
		try{
			for(int i = 0 ; i < com.length ; i++)
			{
				if(com[i].equals("$"))
				{
					//System.out.println(6191937);
					cur = cur.parent(root);
					//System.out.println(cur.value());
				}
				else if(com[i].contains("@"))
				{
					String[] temp = com[i].split("@");
					List<Tree> ts = null;
					if(temp[0].equals("s"))
					{
						ts = cur.siblings(root);
					}
					else if(temp[0].equals("c"))
					{
						ts = cur.getChildrenAsList();
					}
					if(temp[1].contains("="))
					{
						String[] temp2 = temp[1].split("=");
						int at = Integer.parseInt(temp2[0]);
						if(at>0)
						{
							if(ts.get(at-1).value().equals(temp2[1]))
							{
								cur = ts.get(at-1);
							//	System.out.println(cur.value());
							}
						}
						else if (at==0)
						{
							boolean found = false;
							for(int wl = 0 ; wl < ts.size() ; wl++)
							{
								if(ts.get(wl).value().equals(temp2[1]))
								{
									cur = ts.get(wl);
								//	System.out.println(cur.value());
									found = true;
									break;
								}
							}
							if(!found)
							{
								return null;
							}
						}
					}
					else
					{
						int at = Integer.parseInt(temp[1]);
						cur = ts.get(at-1);
						//System.out.println(cur.value());
					}
				}
				else if(com[i].contains("l")&&com[i].contains("#"))
				{
					if(cur.isLeaf())
						return cur.value();
					else
						return null;
				}
				
			}

		}catch(Exception e)
		{
			return null;
		}
		return null;
	}
	


	/**
	 * 基于统计的情感分析模型
	 * @param review 
	 *        已经切分好的句子文本块
	 * @return 
	 *        情感度数据列表
	 */
	public LinkedList<SentiResult> simpleAnalyze(String review) {
		// TODO Auto-generated method stub
		LinkedList<SentiResult> r = new LinkedList<SentiResult>();
		byte nativeBytes[];
		try {
			//分词
			nativeBytes = wordsplitter.ICTCLAS_ParagraphProcess(review.getBytes("GB2312"), 0);
			String taggedReview = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			String[] sent = taggedReview.split(Punctuation.SPACE);
			boolean foundT = false;
			boolean foundS = false;
			boolean foundV = false;
			for(int i = 0 ;  i < sent.length ; i++)
			{
				//主题词
				if(ImportSentimentDictionary.topics.containsKey(sent[i]))
					foundT = true;
				//二义词
				else if(ImportSentimentDictionary.action_words.contains(sent[i]))
				{
					foundV = true;
				}
				//负面的情感词且不包含二义词
				else if(ImportSentimentDictionary.sentiment_words.containsKey(sent[i])&&ImportSentimentDictionary.sentiment_words.get(sent[i])<0)
				{	
					if(!foundV)
					{
						foundS = true;
					}
				}
				
			}
			if(foundT&&foundS)
			{
				r.add(new SentiResult(-9));
			}
		}
		catch(Exception e)
		{
			
		}

		return r;
	}
}
