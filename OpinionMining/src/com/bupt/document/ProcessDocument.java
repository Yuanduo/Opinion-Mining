package com.bupt.document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ICTCLAS.kevin.zhang.SentenceSplitter;
import ICTCLAS.kevin.zhang.WordSplitter;
import com.bupt.document.comparative.IdentifyComparative;
import com.bupt.document.comparative.ProcessComparativeSentence;
import com.bupt.document.complex.ProcessComplexSentence;
import com.bupt.document.complex.IdentifyComplex;
import com.bupt.document.simple.ProcessSimpleSentence;
import com.bupt.utility.ImportSentimentDictionary;
import com.bupt.utility.Punctuation;
import com.bupt.utility.SVMResult;
import com.bupt.utility.SearchTopic;
import com.bupt.utility.SentiResult;
import com.bupt.utility.XMLResult;
import com.bupt.zconfigfactory.ConfigFactory;
/**
 * 篇章级情感分析
 * @author BUPT
 *
 */
public class ProcessDocument {
	private ProcessSimpleSentence ps = new ProcessSimpleSentence(); // 必须提到外面来做成员变量
	private IdentifyComparative search = new IdentifyComparative();
	private IdentifyComplex search1 = new IdentifyComplex();
	static WordSplitter wsplitter;
	private boolean first = false;
	private static Log log = LogFactory.getLog(ProcessDocument.class); //写日志
	//初始化
	ProcessDocument(){
		ConfigFactory.init("AnalysisConfig.xml");//配置文件初始化
		System.out.println("已初始化配置文件！");
		ImportSentimentDictionary.init();//导入词典
		System.out.println("已导入情感词词典！");

		try {
			wsplitter = new WordSplitter();
			//导入用户分词词典                           
		    int n = wsplitter.importUserDict(ConfigFactory.getString("UserDic.name"));
		    System.out.println("用户词典数量："+n);
		} catch (IOException e) {
			log.info("分词工具导入用户词典失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 基于语义指向的文本级情感分析
	 * @param inputFile
	 *            输入文件
	 * @param outputFile
	 *            输出文件
	 * @return 该文档的情感得分
	 */
	public  int documentSemanticProcess(String inputFile, String outputFile){
	
		int nLine = 0; // 句子切分前的行数
		int countSenti = 0; // 情感度综合计数
		int sentiNeg=0;
		int sentiPos=0;
		int sentiZer=0;
		int sentiClass=-3;
		String rev = "";
		LinkedList<SentiResult> sresult = new LinkedList<SentiResult>();//简单句情感分析结果
		LinkedList<SentiResult> cresult = new LinkedList<SentiResult>();//比较句情感分析结果
		LinkedList<XMLResult> res = new LinkedList<XMLResult>(); //复合句识别结果
		 
		// 文件读写操作
		BufferedWriter outf;
		try {
		if (first) {
		
				outf = new BufferedWriter(new FileWriter(outputFile, true));
			
		} else {
			outf = new BufferedWriter(new FileWriter(outputFile));
			first = true;// 第一次运行重新写文件
		}
		File dir = new File(inputFile);
		FileReader fr = new FileReader(dir);		
		BufferedReader br = new BufferedReader(fr);
		String tmp="";

		while ((rev = br.readLine()) != null) {
			// 清除文本中的空格，换行符
			if (rev.trim().equals(Punctuation.EMPTY) || rev.trim().equals(Punctuation.LINEFEED)) {
				continue;
				}

			// 做篇章级别的切分,大于10000行退出
			nLine++;
			if (nLine > 10000) {
				break;
			}
			// 句子切分
			// 分为两步，第一步以整个句子的完整意思为单位切分
			
			
			String[] ReviewAll = SentenceSplitter.splitSentence(rev);
			//System.out.println("ReviewAll.length"+ReviewAll.length);
	
			

			for (int nLength = 0; nLength < ReviewAll.length; nLength++) {
				ReviewAll[0]=ReviewAll[0]+tmp;
				if(ReviewAll.length>1) 
					tmp=ReviewAll[ReviewAll.length-1];				
				else
					tmp="";
				

				// 分词
				String newstr1 = wsplitter.split(ReviewAll[nLength], 1);
				// 先看一句话中是否有主题词,有主题词就继续做处理
				boolean haveTopic = SearchTopic.haveTopic(newstr1);
				if (haveTopic) {
					// 复杂句型处理
					res = search1.identifyComplex(newstr1, ConfigFactory.getString("Complex"));
					if (res.size() > 0) {
						for (int i = 0; i < res.size(); i++) {
							newstr1 = (String) ProcessComplexSentence
							
									.processComplexSentence(newstr1, res
											.get(i).type);
						}
					}
					// 第二步，以逗号等分句块为单位切分
                     
					String[] Review = SentenceSplitter.splitSubSentence(newstr1);
					for (int counter = 0; counter < Review.length; counter++) {
						// 如果分块句子长度小于2则过滤掉/
						if (Review[counter].length() <= 2) {
							continue;
						}
						// 分块句子长度处理,如果句子长度大于50，取前50
						else if (Review[counter].length() > 100)
							Review[counter] = Review[counter].substring(0, 99);
				

						// 是否含有比较关键字
						 
						boolean haveKeyword = search.haveComparativeKeyword(Review[counter]);
						
						if (haveKeyword) {
							
                            
							// 判定是否为比较句
						LinkedList<SVMResult> iscomparative  = search.identifyComparativeByXML(Review[counter], 
									ConfigFactory.getString("XMLComparative"));//XML方法
					
							if (iscomparative.size() > 0) {
							
								for (int i = 0; i < iscomparative.size(); i++) {
									// 对是比较句的句子进行情感分析
									cresult = ProcessComparativeSentence
											.processcomparativesentence(
													iscomparative.get(i).type,
													newstr1);
									// 结果输出
									if (cresult.size() > 0) {
										for (int j = 0; j < cresult.size(); j++) {
											outf.write("<content>"
													+ Review[counter]
													+ "</content>");
											outf.newLine();										
											outf.write("<sentiment>"
															+ cresult.get(j).topic
															+ " "
															+ cresult.get(j).sentiment_word
															+ " "
															+ cresult.get(j).polarity
															+ "</sentiment>");
											outf.newLine();
                                            if(cresult.get(j).polarity>0)
                                            	sentiPos++;
                                            else if(cresult.get(j).polarity==0)
                                            	sentiZer++;
                                            else
                                                sentiNeg++;
											//countSenti += cresult.get(j).polarity;											
										//	outf.write("<countSenti>" + " "
											//		+ countSenti);
											//outf.newLine();
										}
									}
									cresult.clear();
								}
							} else
								continue;
						} else {

							// 不是比较句的情况
							// 情感分析,基于语义指向的情感分析模型
							sresult = ps.syntaxAnalyzeForSemantic(Review[counter]);
							// 结果输出
							if (sresult.size() > 0) {
								for (int i = 0; i < sresult.size(); i++) {
									outf.write("<content>" + Review[counter]
											+ "</content>");
									outf.newLine();
									outf.write("<sentiment>"
											+ sresult.get(i).topic + " "
											+ sresult.get(i).sentiment_word
											+ " " + sresult.get(i).polarity
											+ "</sentiment>");
									outf.newLine();
                            if(sresult.get(i).polarity>0)
                            	sentiPos++;
                            else if(sresult.get(i).polarity==0)
                            	sentiZer++;
                            else
                            	sentiNeg++;
									//countSenti += sresult.get(i).polarity;
									//outf.write("<countSenti>" + " "
									//		+ countSenti);
									//outf.newLine();
								}
							}

							sresult.clear();
						}
					}
				}
			}

			// 文本量和情感度比率的子模块

			// 情感度判断输出
			outf.write("========================================================================");
			outf.newLine();
			/*outf.write("Total count sentiment: " + " " + countSenti);
		    outf.newLine();
			
			if (countSenti < 0) // 负面
			{
				outf.write(dir.getName() + " is negative.");
				outf.newLine();
			} else // 非负面
			{
				outf.write(dir.getName() + " isn't negative.");
				outf.newLine();
			}
			outf.write("========================================================================");
			outf.newLine();*/
			if(sentiPos==0&&sentiNeg==0)
				sentiClass=0;
			else if(sentiPos==0)
				sentiClass=-2;
			else if(sentiNeg==0)
				sentiClass=2;
			else if(sentiPos>sentiNeg)
				sentiClass=1;
			else 
				sentiClass=-1;
			outf.write(dir.getName() + " is level "+sentiClass+".");
			outf.newLine();
			
			/*
			outf.write("********************");
			outf.newLine();
			outf.write("<result>"+dir.getName()+" "+sentiClass);
			outf.newLine();
			outf.write("********************");
			outf.newLine();
			*/
			outf.flush();

		}
		} catch (IOException e) {
			log.info("ProcessDoucument ERROR!");
			e.printStackTrace();

		}
		return sentiClass;
	}
}
