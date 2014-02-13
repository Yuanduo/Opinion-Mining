package com.bupt.sentence.simple;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;

import ICTCLAS.kevin.zhang.SentenceSplitter;

import com.bupt.utility.Punctuation;
import com.bupt.utility.SentiResult;


/**
 * 句子级别的情感分析系统精确度测试
 * @author BUPT
 * @version 1.0
 */
public class TestSentences {
	ProcessSentence ps = new ProcessSentence();
	
	public void sentenceTest(String inputFilePath, String outputFile) throws IOException{
		//文件读写操作
		//file test
		Date time1 = new Date(); 
		String rev = "";
		PrintWriter outf = new PrintWriter(new FileWriter(outputFile));
		File dir = new File(inputFilePath);
		FileReader fr = new FileReader(dir);
		BufferedReader br = new BufferedReader(fr);
		int total = 0;
		while((rev = br.readLine())!=null)
		{
			outf.flush();
			//清除文本中的空格，换行符
			if(rev.trim().equals(Punctuation.EMPTY)||rev.trim().equals(Punctuation.LINEFEED))
			{
				continue;
			}
			//句子切分
			String []Review = SentenceSplitter.ssplitSentence(rev);
			LinkedList<SentiResult> result1 = new LinkedList<SentiResult>();
			
			for(int counter = 0 ; counter < Review.length ; counter++)
			{
				total++;
				//分块句子长度处理
				if(Review[counter].length()>50)
					Review[counter] = Review[counter].substring(0, 49);
				
				//情感分析
				//ps.syntaxAnalyze 为句法分析方法
				//ps.simpleAnalyze 为暴力方法
				//ps.windowAnalyze 为按距离分析方法
				result1 = ps.syntaxAnalyze(Review[counter]);
				
				System.out.print(total+"<content>"+Review[counter]+"</content>");
				outf.print("<content>"+Review[counter]+"</content>");
				//结果输出
				if(result1.size()>0)
				{
					for(int i = 0 ; i < result1.size() ; i++)
					{
						System.out.print("<sentiment>"+result1.get(i).topic+" "+result1.get(i).polarity+"</sentiment>");
						outf.print("<sentiment>"+result1.get(i).topic+" "+result1.get(i).polarity+"</sentiment>");
					}
					System.out.println();
					outf.println();
				}
				else
				{
					System.out.println("<sentiment>"+"unknown"+"</sentiment>");
					outf.println("<sentiment>"+"unknown"+"</sentiment>");
				}
				result1.clear();
			}
		}		
		Date time2 = new Date();
		System.out.println("Time spent is "+(time2.getTime()-time1.getTime())/1000);
	}
	public static void main(String[] args) throws Exception
	{
		TestSentences sp = new TestSentences();
	//	sp.sentenceTest(args[0],args[1]);
		sp.sentenceTest("D://input//1n.txt","D://output1.txt");

		
	}
}
