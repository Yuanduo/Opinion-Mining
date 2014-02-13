package com.bupt.utility;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.bupt.utility.Step;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

/**
 * 依赖关系树句法路径处理类
 * @author BUPT
 * @version 1.0
 */
public class PathProcessor {
	private HashMap<String,Integer> sentiment_words; //情感词集合
	private HashMap<String,Integer> topics; //主题词集合
	private LexicalizedParser lp; //stanford词汇分析处理器实例
	private Tree root; //依赖关系树实例
	//private boolean foundTopic; //是否找到主题词flag
	//private boolean foundSentiment; //是否找到情感词flag
	private LinkedList<Step> way1 = new LinkedList<Step>(); //句法路径1
	private LinkedList<Step> way2 = new LinkedList<Step>(); //句法路径2
	
	String relativelyPath=System.getProperty("user.dir"); 
	public PathProcessor( )
	{
		lp = new LexicalizedParser(relativelyPath+"\\xinhuaPCFG.ser.gz");
	}
	
	public PathProcessor(HashMap<String,Integer> S, HashMap<String,Integer> T )
	{
		sentiment_words = S;
		topics = T;
		lp = new LexicalizedParser(relativelyPath+"\\xinhuaPCFG.ser.gz");
	}
	public void setSenti(HashMap<String,Integer> S)
	{
		sentiment_words = S;
	}
	public void setTopic(HashMap<String,Integer> T)
	{
		topics = T;
	}
	
	public LinkedList<Step> getPath(String r)
	{
		LinkedList<Step> result = new LinkedList<Step>();
		String reviewTagged = r.trim();
		way1.clear();
		way2.clear();
		String[] sent =  reviewTagged.split(" ");
		Tree parse = lp.apply(Arrays.asList(sent));
		 root = parse.preOrderNodeList().get(0);
		//parse.pennPrint();
		//foundTopic = getPath1(parse,topics);
		//foundSentiment = getPath2(parse,sentiment_words);
		
		int i = way1.size()-1;
		int j = way2.size()-1;
		for(;i>0&&j>0;i--,j--)
		{
			if(way1.get(i).id!=way2.get(j).id)
			{
				i++;
				
				for(int k = 0 ; k <= i ; k++)
				{
					result.add(way1.get(k));
				}
				for( ; j>=0 ; j--)
				{
					result.add(way2.get(j));
				}
				break;
			}
		}	
		return result;
	}
	
	/**
	 * 检查句法模板中是否包含配对的主题词和情感词
	 * @param r 分句好的文本块
	 * @param t 主题词
	 * @param s 情感词
	 * @return 包含主题词和情感词的句法模板路径
	 */
	public String getPathOfCertainWords(String r,String t,String s)
	{
		LinkedList<Step> result = new LinkedList<Step>();
		String reviewTagged = r.trim();
		String[] sent =  reviewTagged.split(" ");
		//生成依赖关系树
		Tree parse = lp.apply(Arrays.asList(sent));
		//先序遍历得到根节点
		 root = parse.preOrderNodeList().get(0);
		 //遍历依赖关系树得到主题词
		//foundTopic = getPath1(parse,t);
		//遍历依赖关系树得到情感词
		//foundSentiment = getPath2(parse,s);
		
		//从倒数第二项开始，过滤掉sentiment
		int i = way1.size()-1;
		int j = way2.size()-1;
		for(;i>0&&j>0;i--,j--)
		{
			//连接从主题词到情感词的路径
			if(way1.get(i).id!=way2.get(j).id)
			{
				i++;
				
				//从第二项开始，过滤掉topic
				for(int k = 1 ; k <= i ; k++)
				{
					result.add(way1.get(k));
				}
				for( ; j>0 ; j--)
				{
					result.add(way2.get(j));
				}
				break;
			}
		}	
		way1.clear();
		way2.clear();
		
		return patternToString(result);
	}
	
	/**
	 * 遍历依赖关系树得到主题词
	 * @param t 依赖关系树实例
	 * @param h 主题词值对
	 * @return 是否找到主题词
	 */
	public boolean getPath1( Tree t,HashMap<String,Integer> h )//topics
	{
		if(h.containsKey(t.value()))
		{
			//System.out.println(way1);
			way1.add(new Step("TOPIC",t.value(),t.nodeNumber(root)));
			return true;
		}
		else if( t.isLeaf() )
		{
			return false;
		}
		else
		{
			boolean ret = false;
			List <Tree>lk = t.getChildrenAsList();
			for(int wl = 0;wl<lk.size();wl++)
			{
				if(getPath1(lk.get(wl),h))
					ret = true;
			}
			if(ret)
			{
				way1.add(new Step("UP",t.value(),t.nodeNumber(root)));
				return true;
			}
			else
				return false;
		}
	}
	
	/**
	 * 遍历依赖关系树得到主题词
	 * @param t 依赖关系树实例
	 * @param top 主题词
	 * @return 是否找到主题词
	 */
	public boolean getPath1( Tree t,String top )//topics
	{
		//找到主题词
		if(t.value().equals(top))
		{
			way1.add(new Step("TOPIC",t.value(),t.nodeNumber(root)));
			return true;
		}
		//叶子节点
		else if( t.isLeaf() )
		{
			return false;
		}
		else
		{
			boolean ret = false;
			List <Tree>lk = t.getChildrenAsList();
			for(int wl = 0;wl<lk.size();wl++)
			{
				//循环遍历每个子节点
				if(getPath1(lk.get(wl),top))
					ret = true;
			}
			if(ret)
			{
				//设置向上的节点
				way1.add(new Step("UP",t.value(),t.nodeNumber(root)));
				return true;
			}
			else
				return false;
		}
	}
	
	/**
	 * 遍历依赖关系树得到情感词
	 * @param t 依赖关系树实例
	 * @param h 情感词值对
	 * @return 是否找到情感词
	 */
	public boolean getPath2( Tree t,HashMap<String,Integer> h )//sentiment
	{
		if(h.containsKey(t.value()))
		{
			//System.out.println(t.value());
			way2.add( new Step("SENTIMENT",t.value(),t.nodeNumber(root)) );
			return true;
		}
		else if( t.isLeaf() )
		{
			return false;
		}
		else if(t.value().equals("ADVP"))
		{
			return false;
		}
		else
		{
			boolean ret = false;
			List <Tree>lk = t.getChildrenAsList();
			for(int wl = 0;wl<lk.size();wl++)
			{
				if(getPath2(lk.get(wl),h))
					ret = true;
			}
			if(ret)
			{
				way2.add(new Step("DOWN",t.value(),t.nodeNumber(root)));
				return true;
			}
			else
				return false;
		}
	}
	
	/**
	 * 遍历依赖关系树得到情感词
	 * @param t 依赖关系树实例
	 * @param s 情感词
	 * @return 是否找到情感词
	 */
	public boolean getPath2( Tree t,String s )//sentiment
	{
		//找到情感词
		if(t.value().equals(s))
		{
			way2.add( new Step("SENTIMENT",t.value(),t.nodeNumber(root)) );
			return true;
		}
		//叶子节点
		else if( t.isLeaf() )
		{
			return false;
		}
		//排除副词
		else if(t.value().equals("ADVP"))
		{
			return false;
		}
		else
		{
			boolean ret = false;
			List <Tree>lk = t.getChildrenAsList();
			for(int wl = 0;wl<lk.size();wl++)
			{
				//循环遍历每个子节点
				if(getPath2(lk.get(wl),s))
					ret = true;
			}
			if(ret)
			{
				//设置向下的节点
				way2.add(new Step("DOWN",t.value(),t.nodeNumber(root)));
				return true;
			}
			else
				return false;
		}
	}
	
	/**
	 * 把节点路径转换成字符串
	 * @param 输入节点路径列表
	 * @return 返回包含路径的字符串
	 */
	public String patternToString(LinkedList<Step> s)
	{
		String ret = "";
		for(int i = 0 ; i < s.size() ; i++)
		{
			if(i==0)
			{
				ret+=s.get(i).dir+"@"+s.get(i).value;
			}
			else
			{
				ret+="#"+s.get(i).dir+"@"+s.get(i).value;
			}
		}
		
		
		return ret;
	}
	
	/**
	 * 把字符串转换成节点路径
	 * @param patt 输入包含路径的字符串
	 * @return 返回路径节点列表
	 */
	public LinkedList<Step> stringToPattern(String patt)
	{
		LinkedList<Step> steps = new LinkedList<Step>();
		String[] stps = patt.split("#");
		for(int i = 0 ; i < stps.length ; i++)
		{
			String[] temp  = stps[i].split("@");
			
			steps.add(new Step(temp[0],temp[1],0));
		}
		return steps;
	}
}
