package com.bupt.document.comparative;
import java.util.ArrayList;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.bupt.utility.ImportSentimentDictionary;
import com.bupt.utility.Punctuation;
import com.bupt.utility.SentiResult;
import com.bupt.utility.Word;

/**
 * 比较句情感分析类
 * @author BUPT
 *
 */
public class ProcessComparativeSentence {
	public static final String COMPAREWORD_BI = "比/p";
	public static final String COMPAREWORD_BI_A = "比";
	public static final String COMPAREWORD_ZUI = "最/d";
	public static final String COMPAREWORD_D = "d";
	public static final String COMPAREWORD_ZUI_A = "最";
	public static final String COMPAREWORD_YIYANG = "一样/uyy";
	public static final String COMPAREWORD_YIYANG_A = "一样";
	public static final String COMPAREWORD_YIYANG_A2 = "那样";
	public static final String COMPAREWORD_YU = "于";
	private static final Object COMPAREWORD_YIYANG2 = null;

	private static Log log = LogFactory.getLog(ProcessComparativeSentence.class);
	
/**
 * 比较句情感分析模块
 * @param keyword
 * 		比较关键字
 * @param str
 * 		比较句
 * @return
 *      比较句情感得分集
 */
	public  static LinkedList<SentiResult> processcomparativesentence(int type,String str){
    	LinkedList<SentiResult> result = new LinkedList<SentiResult>();//比较句情感分析结果集
    	ProcessComparativeSentence ps = new ProcessComparativeSentence();//比较句情感分析类实例化
      
    	if(type==1)
    	{
    		result = ps.biAnalyze(str);//"比"字句处理
    		}
    	else if(type==2)
    	{
    		result = ps.superiorAnalyze(str);//"最"字句处理
    		}
    	else if(type==3)
    	{
    		result = ps.equalityAnalyze(str);//"一样"句型处理
    		}  
   
    return result;
	}

	/**
	 * 极比句的情感分析流程
	 * @param review 
	 *         分词完的字符串
	 * @return LinkedList<SentiResult> 
	 *         极比句的情感得分集
	 * 
	 */
	public  LinkedList<SentiResult> superiorAnalyze(String review) {
		
		LinkedList<SentiResult> r = new LinkedList<SentiResult>();//极比句的情感得分集
		LinkedList<Integer> negPos = new LinkedList<Integer>(); //否定词所在分句中的位置集合
		LinkedList<Integer> negPos1 = new LinkedList<Integer>(); //否定词所在分句中的位置集合
		boolean foundT = false;//是否有主题词
		boolean foundS = false;//是否有情感词
		boolean foundN = false;//是否有否定词
		boolean foundZ = false;//是否有比较关键词“最”
		int poszui = 0;//“最”的位置
		int spos=0;//情感词位置
		int s =1 ;//情感词极性
		String sentiword = null;//情感词内容
		int p = 0;//主题词极性
		String topic  = null;//主题词内容
		int negcount = 0;//否定词与“最”前的距离在5以内的否定词个数
		int negcount1 = 0;//否定词与“最”后的距离在5以内的否定词个数
		try {
			String[] sent = review.split(Punctuation.SPACE);
			ArrayList<Word> subSentence = new ArrayList<Word>();

			for(int i = 0 ;  i < sent.length ; i++)
			{
				//如果分词块包含“/”
				if(sent[i].indexOf(Punctuation.SLASH) != -1)
				{
				//分别把词和词性标签存入word中
				String srcWord = sent[i].substring(0, sent[i].indexOf(Punctuation.SLASH));
				String tag = sent[i].substring(sent[i].indexOf(Punctuation.SLASH) + 1,sent[i].indexOf(Punctuation.SLASH) + 2);
				Word word = new Word(srcWord, tag);
				subSentence.add(word);}
			}
			for(int j = 0; j < subSentence.size(); j++)
			{
				//主题词
				if(ImportSentimentDictionary.topics.containsKey(subSentence.get(j).getSrcWord()))
				{
					foundT = true;//找到主题词
					p = ImportSentimentDictionary.topics.get(subSentence.get(j).getSrcWord());//记下主题词的情感值
					topic = subSentence.get(j).getSrcWord();//记下主题词的内容
					
				}
			
				//比较关键词
				else if(subSentence.get(j).getSrcWord().equals(COMPAREWORD_ZUI_A)&&subSentence.get(j).getTag().equals(COMPAREWORD_D))
				{
					foundZ = true;//找到比较关键字“最”
					poszui = j;//记录“最”的位置
				}
				//否定词
				else if(ImportSentimentDictionary.negations.contains(subSentence.get(j).getSrcWord()))
				{
					foundN = true;
					if(!foundZ){
					negPos.add(j);//记录“最”之前的否定词
					}
					else negPos1.add(j);//记录“最”之后否定词
				}
				//情感词
				else if(ImportSentimentDictionary.sentiment_words.containsKey(subSentence.get(j).getSrcWord())&&foundZ&&!foundS){
					foundS = true;//“最”字后的第一个情感词
					s = ImportSentimentDictionary.sentiment_words.get(subSentence.get(j).getSrcWord());//情感词倾向
					//System.out.println("找到情感词:"+subSentence.get(j).getSrcWord()+"情感词倾向为:"+s);
					spos=j;
					if(s>0&&!foundN)s = 1;
					else if(s>0&&foundN) s = 2;//有否定词，加权重为2
					else if(s<0) s = -3;//负面情感词，权重为-3
					//System.out.println("s"+s);
					sentiword = subSentence.get(j).getSrcWord();
				}
				if(j-spos<=2)
					if(subSentence.get(j).getTag().startsWith("n")&&ImportSentimentDictionary.compara_objects.containsKey(subSentence.get(j).getSrcWord())){
					if(ImportSentimentDictionary.compara_objects.get(subSentence.get(j).getSrcWord())<0)
					s=s*(-1);
					}
			}
			//否定词与比较关键词的距离
			for(int neg = 0; neg < negPos.size(); neg++)
			{//否定词与“最”前的距离在5以内，否则不考虑
				if(Math.abs(poszui - negPos.get(neg)) < 5){
					foundN = true;
					negcount++;
				}
			}
			for(int neg = 0; neg < negPos1.size(); neg++)
			{//否定词与“最”后的距离在5以内，否则不考虑
				if(Math.abs(poszui - negPos1.get(neg)) < 5){
					//foundN1 = true;
					negcount1++;
					s = -1*s;
					//System.out.println(s);
				}
			}
			if(foundT){
				//负面的情感词
				if(foundS&&s<0)
				{	//负面的情感词且不包含否定词且主题词为正面
					if(!foundN&&p>=0||(negcount%2==0)&&p>=0){
						r.add(new SentiResult(topic,sentiword,-5+s*2*(negcount+negcount1+1)));
					}
					//负面的情感词且不包含否定词且主题词为负面
					else if(!foundN&&p<0||(negcount%2==0)&&p<0){
						r.add(new SentiResult(topic,sentiword,5+s*(-1)*(negcount+negcount1+1)));
					}
					//负面的情感词且包含否定词且主题词为正面
					else if(foundN&&p>=0||(negcount%2==1)&&p>=0){
						r.add(new SentiResult(topic,sentiword,5+s*2*(negcount+negcount1+1)));
					}
					//负面的情感词且包含否定词且主题词为负面
					else if(foundN&&p<0||(negcount%2==1)&&p<0){
						r.add(new SentiResult(topic,sentiword,-5+s*(-4)*(negcount+negcount1+1)));
					}
				}
				//正面的情感词
				else if(foundS&&s>0){
					//正面的情感词且不包含否定词且主题词为正面
					if(!foundN&&p>=0||(negcount%2==0)&&p>=0){
						r.add(new SentiResult(topic,sentiword,5+s*2*(negcount+negcount1+1)));
					}
					//正面的情感词且不包含否定词且主题词为负面
					else if(!foundN&&p<0||(negcount%2==0)&&p<0){
						r.add(new SentiResult(topic,sentiword,-5+s*(-4)*(negcount+negcount1+1)));
					}
					//正面的情感词且包含否定词且主题词为正面
					else if(foundN&&p>=0||(negcount%2==1)&&p>=0){
						r.add(new SentiResult(topic,sentiword,-5+s*2*(negcount+negcount1+1)));
					}
					//正面的情感词且包含否定词且主题词为负面
					else if(foundN&&p<0||(negcount%2==1)&&p<0){
						r.add(new SentiResult(topic,sentiword,-5+s*(-1)*(negcount+negcount1+1)));
					}
				}
			}
		}
		catch(Exception e)
		{
			log.info("superiorAnalyze() ERROR!");
			e.printStackTrace();
		}

		return r;
		
	}

	/**
	 * 等比句的情感分析流程
	 * @param review 
	 * 			分好词的字符串
	 * @return LinkedList<SentiResult> 
	 * 			等比句的情感得分集
	 */
	public LinkedList<SentiResult> equalityAnalyze(String review) {
		LinkedList<SentiResult> r = new LinkedList<SentiResult>();//等比句的情感得分集
		LinkedList<Integer> negPos = new LinkedList<Integer>(); //“一样”之前的否定词所在分句中的位置集合
		LinkedList<Integer> negPos1 = new LinkedList<Integer>(); //“一样”之后否定词所在分句中的位置集合
		int p = 0;//比较客体的极性
		int k = 0;//比较主体的极性
		int s = 0;//情感词极性
		int c = 0;//比较词极性
		int negcount = 0;
		int negcount1 = 0;
		int tPOS=0;
		boolean foundT = false;//是否找到主题词
		boolean foundC = false;//是否找到比较词
		boolean foundS = false;//是否找到情感词
		boolean foundN = false;//是否找到否定词
		boolean foundY = false;//是否找到“一样”
		boolean foundD = false;//是否找到逗号
		int posy = 0;//“一样”的位置
		String topic = null;
		String sentiword  = null;
		try {
			String[] sent = review.split(Punctuation.SPACE);
			ArrayList<Word> subSentence = new ArrayList<Word>();
			for(int i = 0 ;  i < sent.length ; i++)
			{
				if(sent[i].indexOf(Punctuation.SLASH) != -1)//如果分词块包含“/”
				{
				//分别把词和词性标签存入word中
				String srcWord = sent[i].substring(0, sent[i].indexOf(Punctuation.SLASH));
				String tag = sent[i].substring(sent[i].indexOf(Punctuation.SLASH) + 1,sent[i].indexOf(Punctuation.SLASH) + 2);
				Word word = new Word(srcWord, tag);
				subSentence.add(word);}
				
			}
			for(int j = 0; j < subSentence.size(); j++)
			{
				//主题词
				if(ImportSentimentDictionary.compara_objects.containsKey(subSentence.get(j).getSrcWord()))
				{
					foundT = true;//找到主题词
					
					//未找到比较词
					if(!foundC)
					{
						k = ImportSentimentDictionary.compara_objects.get(subSentence.get(j).getSrcWord());
											
						//记录比较主体的极性
						topic = subSentence.get(j).getSrcWord();//记录比较主体的内容
						tPOS=j;
						}
					//找到比较词
					else if(foundC&&p==0&&!foundY)
					{
						p = ImportSentimentDictionary.compara_objects.get(subSentence.get(j).getSrcWord());
						//记录比较客体的极性
					}
				}
				//比较词
				else if(ImportSentimentDictionary.compara_words.containsKey(subSentence.get(j).getSrcWord())&&!foundY)
				{
					foundC = true;
					c = ImportSentimentDictionary.compara_words.get(subSentence.get(j).getSrcWord());
				}
				else if(((subSentence.get(j).getSrcWord().equals(COMPAREWORD_YIYANG_A))||(subSentence.get(j).getSrcWord().equals(COMPAREWORD_YIYANG_A2)))&&foundC){
					foundY = true;
					posy = j;
				}
				else if(ImportSentimentDictionary.sentiment_words.containsKey(subSentence.get(j).getSrcWord())&&foundC&&!foundD&&!foundS)
				{
					foundS = true;
					s=ImportSentimentDictionary.sentiment_words.get(subSentence.get(j).getSrcWord());
					if(s>0&&!foundN)s = 1;
					else if(s>0&&foundN) s = 2;
					else if(s<0) s = -3;
					sentiword = subSentence.get(j).getSrcWord();
					}
				else if(subSentence.get(j).getSrcWord().contains(Punctuation.COMMA_FULL)&&foundC&&!foundD)
				{
					foundD = true;
				}
				else if(ImportSentimentDictionary.negations.contains(subSentence.get(j).getSrcWord())&&!foundS&&!foundD)
				{
					foundN = true;
					if(!(!foundT&&(subSentence.get(j).getSrcWord().equals("没有")))){
						if(!foundY){
						negPos.add(j);//记录“一样”之前的否定词
						}
						else negPos1.add(j);//记录“一样”之后否定词
				}
				}
			}
			for(int neg = 0;( (neg < negPos.size())); neg++)
			{//否定词与“一样”前的距离在5以内，否则不考虑，或者否定词在比较主题词前
				
				if((Math.abs(posy - negPos.get(neg)) < 5)||( Math.abs(negPos.get(neg)-tPOS)<5)){
					foundN = true;
					negcount++;
				}
			}
			for(int neg = 0; neg < negPos1.size(); neg++)
			{//否定词与“一样”后的距离在5以内，否则不考虑
				if(Math.abs(posy - negPos1.get(neg)) < 5){
					negcount1++;
					s = -1*s;
				}
			}
	
			
			if(foundT){
				//没有情感词
				if(!foundS&&s==0)
				{	//比较主体和比较客体都为正面
					if(k>=0&&p>=0){
						r.add(new SentiResult(topic,sentiword,1));
					}
					//比较主体或比较客体中至少一个为负面
					else {
						r.add(new SentiResult(topic,sentiword,-6));
					}
				}
				//正面的情感词
				else if(foundS&&s>0){
					//比较客体为负面
					if(p<0){
						r.add(new SentiResult(topic,sentiword,-4*s*(negcount+negcount1+1)));
					}
					//比较主体为负面比较客体为正面且没有否定意义
					else if(c>0&&k<=0&&p>=0&&!foundN||c>0&&k<0&&p>=0&&(negcount%2==0)){
						r.add(new SentiResult(topic,sentiword,-4*s*(negcount+negcount1+1)));
					}
					//比较主体为正面比较客体为正面且没有否定意义
					else if(c>0&&k>=0&&p>=0&&!foundN||c>0&&k>=0&&p>=0&&(negcount%2==0)){
						r.add(new SentiResult(topic,sentiword,2*s*(negcount+negcount1+1)));
					}
					//比较主体为负面比较客体为正面且有否定意义
					else if(c<0&&k<=0&&p>=0&&!foundN||c>0&&k<0&&p>=0&&(negcount%2==1)){
						r.add(new SentiResult(topic,sentiword,s*(negcount+negcount1+1)));
					}
					//比较主体为正面比较客体为正面且有否定意义
					else if(c<0&&k>=0&&p>=0&&!foundN||c>0&&k>=0&&p>=0&&(negcount%2==1)){
						r.add(new SentiResult(topic,sentiword,-2*s*(negcount+negcount1+1)));
					}
				}
				//负面的情感词
				else if(foundS&&s<0){
					//比较主体为负比较客体为负面且有否定意义
					if(c<0&&k<=0&&p<0&&!foundN||c>0&&k<0&&p<0&&(negcount%2==1)){
						r.add(new SentiResult(topic,sentiword,s*(negcount+negcount1+1)));
					}
					//比较主体为正比较客体为负面且有否定意义
					else if(c<0&&k>=0&&p<0&&!foundN||c>0&&k>0&&p<0&&(negcount%2==1)){
						r.add(new SentiResult(topic,sentiword,(-1)*s*(negcount+negcount1+1)));
					}
					//比较主体为负面比较客体为负面且没有否定意义
					else if(c>0&&k<=0&&p<0&&!foundN||c>0&&k<0&&p<0&&(negcount%2==0)){
						r.add(new SentiResult(topic,sentiword,(-2)*s*(negcount+negcount1+1)));
					}
					//比较主体为正面比较客体为负面且没有否定意义
					else if(c>0&&k>=0&&p<0&&!foundN||c<0&&k>=0&&p<0&&(negcount%2==0)){
						r.add(new SentiResult(topic,sentiword,4*s*(negcount+negcount1+1)));
					}
					//比较主体为负比较客体为正面
					else if(k<=0&&p>=0&&(negcount%2==1)){
						r.add(new SentiResult(topic,sentiword,-2*s*(negcount+negcount1+1)));
					}
					else if(k<=0&&p>=0&&(negcount%2==0)){
						r.add(new SentiResult(topic,sentiword,2*s*(negcount+negcount1+1)));
					}
					//比较主体为正比较客体为正面且有否定意义
					else if(k>=0&&p>=0&&(negcount%2==1)){
						r.add(new SentiResult(topic,sentiword,-1*s*(negcount+negcount1+1)));
					}
					else if(k>=0&&p>=0&&(negcount%2==0)){
						r.add(new SentiResult(topic,sentiword,1*s*(negcount+negcount1+1)));
					}
				}
			}
		}
		catch(Exception e)
		{
			log.info("equalityAnalyze() ERROR!");
			e.printStackTrace();
		}
		return r;
	}
	
	/**
	 * 比字句的情感分析流程
	 * @param review 
	 * 			分好词的字符串
	 * @return LinkedList<SentiResult> 
	 * 			比字句的情感得分集
	 */
	public  LinkedList<SentiResult> biAnalyze(String review) {
		LinkedList<SentiResult> r = new LinkedList<SentiResult>();
		LinkedList<String> ts = new LinkedList<String>(); //情感词集合
		LinkedList<Integer> negPos = new LinkedList<Integer>(); //否定词所在分句中的位置集合
		LinkedList<Integer> negPos1 = new LinkedList<Integer>(); //否定词所在分句中的位置集合
		int p = 0;
		int s = 0;
		int k = 0;
		int c = 0;
		int negcount = 0;
		int negcount1 = 0;
		boolean foundT = false;
		boolean foundN = false;
		boolean foundC = false;
		boolean foundD = false;
		boolean hasDun = false;
		int pos = 0;
		String topic  = null;
		String sentiword = null;
		try {
			String[] sent = review.split(Punctuation.SPACE);//按空格切分
			ArrayList<Word> subSentence = new ArrayList<Word>();
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
				String tag = sent[i].substring(sent[i].indexOf(Punctuation.SLASH) + 1,sent[i].indexOf(Punctuation.SLASH) + 2);
				Word word = new Word(srcWord, tag);
				subSentence.add(word);}
			}
			for(int j = 0; j < subSentence.size(); j++)
			{
					//主题词
					if(ImportSentimentDictionary.compara_objects.containsKey(subSentence.get(j).getSrcWord()))
					{
						foundT = true;
						//在“比”字前出现的词为比较主体
						if(!foundC&&!foundD)
						{
							k = ImportSentimentDictionary.compara_objects.get(subSentence.get(j).getSrcWord());
							topic = subSentence.get(j).getSrcWord();
							System.out.println("topic"+topic);
							}
						//在“比”字后出现的词为比较客体
						else if(foundC&&!foundD)
						{	
							p = ImportSentimentDictionary.compara_objects.get(subSentence.get(j).getSrcWord());
						
						}
					}
					//否定词
					//“比”字出现的分句之前的所有否定词
					else if(ImportSentimentDictionary.negations.contains(subSentence.get(j).getSrcWord())&&!foundD)
					{
						
						//System.out.println("情感词为"+subSentence.get(j).getSrcWord()+"情感词的倾向为"+s);
						foundN = true;
						if(!(!foundT&&(subSentence.get(j).getSrcWord()=="没有"))){
						if(!foundC){							
							negPos.add(j);//记录“比”之前的否定词
							}
							else negPos1.add(j);//记录“比”之后否定词
						
					}
					}
					//比较词
					else if(ImportSentimentDictionary.compara_words.containsKey(subSentence.get(j).getSrcWord())&&!foundD)
					{
						if(subSentence.get(j).getSrcWord().equals(COMPAREWORD_BI_A)){
							foundC = true;
							c = ImportSentimentDictionary.compara_words.get(subSentence.get(j).getSrcWord());
							pos = j;
						}
					}
					else if(subSentence.get(j).getSrcWord().contains(Punctuation.COMMA_FULL)&&foundC&&!foundD)
					{
						foundD = true;
					
					}
					//情感词
					else if(ImportSentimentDictionary.sentiment_words.containsKey(subSentence.get(j).getSrcWord())&&foundT)
					{	//比较词之后到逗号之前的分句中的情感词
						if(foundC&&!foundD){
						ts.add(sent[j]);
						s = ImportSentimentDictionary.sentiment_words.get(subSentence.get(j).getSrcWord());
						if(s>0&&!foundN)s = 1;
						else if(s>0&&foundN) s = 2;
						else if(s<0) s = -3;						
						sentiword = subSentence.get(j).getSrcWord();
						}
					}
				
				}
				if(hasDun)
				{
//					review = review.replaceFirst(".+、", ts.get(0)+Punctuation.PAUSE);
					review = review.replaceAll("、.+、", Punctuation.EMPTY);
				}
				for(int neg = 0; neg < negPos.size(); neg++)
				{
					//否定词与“比”的距离在5以内，否则不考虑
					if((Math.abs(pos - negPos.get(neg)) < 5)){
						foundN = true;
						negcount++;
					}
				}
				for(int neg = 0; neg < negPos1.size(); neg++)
				{//否定词与“比”后的距离在5以内，否则不考虑
					if(Math.abs(pos - negPos1.get(neg)) < 5){
						negcount1++;
						s = -1*s;
					}
				}
				//比较客体为负面且情感词正面
				if(foundT&&p<0&&s>0)
				{	//比较主体为正面
					if(k>=0)
					{
						//没有否定意义
						if(c>0&&!foundN||negcount%2==0&&foundN){
						r.add(new SentiResult(topic,sentiword,2*s*(negcount+negcount1+1)));}
						//有否定意义
						else if(negcount%2==1||(c<0&&!foundN))r.add(new SentiResult(topic,sentiword,(-4)*s*(negcount+negcount1+1)));
					}
					//比较主体为负面
					else if(k<0){
						//没有否定意义
						if((c>0&&!foundN)||(negcount%2==0&&foundN)){
						r.add(new SentiResult(topic,sentiword,(-4)*s*(negcount+negcount1+1)));}
						//有否定意义
						else if((negcount%2==1)||(c<0&&!foundN))r.add(new SentiResult(topic,sentiword,s*(negcount+negcount1+1)));
					}
				}
				//找到主题词且比较客体为正面且情感词正面
				else if (foundT&&p>=0&&s>0)
				{
					//比较主体为正面
					if(k>=0)
					{
						if(c>0&&!foundN||negcount%2==0&&foundN){
						r.add(new SentiResult(topic,sentiword,s*(negcount+negcount1+1)));}
						else if(negcount%2==1||c<0&&!foundN)r.add(new SentiResult(topic,sentiword,(-1)*s*(negcount+negcount1+1)));
					}
					else if(k<0){
						if(c>0&&!foundN||negcount%2==0&&foundN){
						r.add(new SentiResult(topic,sentiword,(-4)*s*(negcount+negcount1+1)));}
						else if(negcount%2==1||c<0&&!foundN)r.add(new SentiResult(topic,sentiword,s*(negcount+negcount1+1)));
					}
				}
				//找到主题词且比较客体为负面且情感词负面
				else if(foundT&&p<0&&s<0)
				{
					if(k>=0){
						if(c>0&&!foundN||negcount%2==0&&foundN){
						r.add(new SentiResult(topic,sentiword,4*s*(negcount+negcount1+1)));}
						else if(negcount%2==1||c<0&&!foundN)r.add(new SentiResult(topic,sentiword,(-1)*s*(negcount+negcount1+1)));
					}
					else if(k<0){
						if(c>0&&!foundN||negcount%2==0&&foundN){
						r.add(new SentiResult(topic,sentiword,(-2)*s*(negcount+negcount1+1)));}
						else if(negcount%2==1||c<0&&!foundN)r.add(new SentiResult(topic,sentiword,s*(negcount+negcount1+1)));
					}
				}
				//找到主题词且比较客体为正面且情感词负面
				else if(foundT&&(p>=0)&&(s<0))
				{
					if(k>=0){
						if( c>0&&!foundN||negcount%2==0&&foundN){
						r.add(new SentiResult(topic,sentiword,2*s*(negcount+negcount1+1)));}
						else if((negcount%2==1)||(c<0&&!foundN))r.add(new SentiResult(topic,sentiword,(-1)*s*(negcount+negcount1+1)));
						}
					else if(k<0){
						if(c>0&&!foundN||negcount%2==0&&foundN){
						r.add(new SentiResult(topic,sentiword,(-1)*s*(negcount+negcount1+1)));}
						else if(negcount%2==1||c<0&&!foundN)r.add(new SentiResult(topic,sentiword,1*s*(negcount+negcount1+1)));
						}
				}
				
			}
		catch(Exception e)
		{
			log.info("biAnalyze() ERROR!");
			e.printStackTrace();
		}
		return r;
	}
}
