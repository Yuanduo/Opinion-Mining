package com.bupt.document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.bupt.sentimentgui.ProcessTask;


/**
 * 多文档情感分析批处理精确度测试入口
 * @author BUPT
 *
 */
public class TestDocuments {

	private int countPositive = 0;
	private int countNegative = 0;
	private int nPositiveYes = 0; // 运行属于正面，实际为正面
	private int nPositiveNo = 0; // 运行属于正面，实际为负面
	private int nNegativeYes = 0;// 运行属于负面，实际为负面
	private int nNegativeNo = 0;// 运行属于负面，且为正面
	
	
	private int posLvl1=0;
	private int posLvl2=0;
	private int zerLvl=0;
	private int negLvl1=0;
	private int negLvl2=0;
	private int correctNum=0;
	private LinkedList<String> docuPositive = new LinkedList<String>();//正面文章总数
	private LinkedList<String> docuNegative = new LinkedList<String>();//负面文章总数
	
	private LinkedList<String> posLvl1_set = new LinkedList<String>();
	private LinkedList<String> posLvl2_set = new LinkedList<String>();
	private LinkedList<String> zerLvl_set = new LinkedList<String>();
	private LinkedList<String> negLvl1_set = new LinkedList<String>();
	private LinkedList<String> negLvl2_set = new LinkedList<String>();
	
	ProcessDocument pd = new ProcessDocument();//篇章级情感分析实例
	private static Log log = LogFactory.getLog(TestDocuments.class); //错误日志
	
	
	/**
	 * 多文档的基于语义指向的情感分析测试
	 * 
	 * @param inputFilePath
	 *            输入文件目录
	 * @param outputFile
	 *            输出文件
	 */
	public void documentsTest(String inputFilePath, String outputFile){
		Date time1 = new Date();
		
		// 多文档批处理进行文档级别的情感分析
		processDocuments(inputFilePath, outputFile);
		Date time2 = new Date();
		//int count = countPositive + countNegative;
		  int count = posLvl2+posLvl1+zerLvl+negLvl1+negLvl2;

		// 结果显示
		/*System.out
				.println("#########################################################################");
		System.out.println("Negative documents: ");
		for (int i = 0; i < docuNegative.size(); ++i) {
			System.out.println(docuNegative.get(i));
		}
		System.out
				.println("=========================================================================");
		System.out.println("Not negative documents: ");
		for (int i = 0; i < docuPositive.size(); ++i) {
			System.out.println(docuPositive.get(i));
		}
		System.out
				.println("========================================================================");
				*/
		  System.out.println("negative level 2 documents:"+negLvl2);
		  System.out.println("negative level 1 documents:"+negLvl1);
		  System.out.println("neutrality documents:"+zerLvl);
		  System.out.println("positive level 1 documents:"+posLvl1);
		  System.out.println("positive level 2 documents:"+posLvl2);
		  
		StringBuilder builder = new StringBuilder();
		builder.append("Total documents: ").append(count).append("\n");
		builder.append("Negative level 2 documents: ").append(negLvl2).append("\n");
		builder.append("Negative level 1 documents: ").append(negLvl1).append("\n");
		builder.append("Neutrality documents: ").append(zerLvl).append("\n");
		builder.append("Positive level 1 documents: ").append(posLvl1).append("\n");
		builder.append("positive level 2 documents: ").append(posLvl2).append("\n");
		builder.append("rate of correction: ").append((double)correctNum/count).append("\n");
		//builder.append("Not negative documents(FALSE): ").append(nPositiveNo).append("\n");
		builder.append("Time spent is " + (time2.getTime() - time1.getTime())
				/ 1000);
		ProcessTask.result = builder.toString();//用户界面输出
		System.out.println(builder.toString());//console输出
		FileWriter writer;
		try {
			writer = new FileWriter(outputFile, true);
//用户指定文件输出
		writer.write("#########################################################################");
		writer.write('\n');
		writer.write("Negative documents: ");
		writer.write('\n');
		for (int i = 0; i < docuNegative.size(); ++i) {
			writer.write(docuNegative.get(i));
			writer.write('\n');
		}
		writer
				.write("========================================================================");
		writer.write('\n');
		writer.write("Not negative documents: ");
		writer.write('\n');
		for (int i = 0; i < docuPositive.size(); ++i) {
			writer.write(docuPositive.get(i));
			writer.write('\n');
		}

		writer
				.write("========================================================================");
		writer.write('\n');
		writer.write("Total documents: " + count);
		writer.write('\n');
		writer.write("Negative documents: " + countNegative);
		writer.write('\n');
		writer.write("Negative documents(TRUE): " + nNegativeYes);
		writer.write('\n');
		writer.write("Negative documents(FALSE): " + nNegativeNo);
		writer.write('\n');
		writer.write("Not negative documents: " + countPositive);
		writer.write('\n');
		writer.write("Not negative documents(TRUE): " + nPositiveYes);
		writer.write('\n');
		writer.write("Not negative documents(FALSE): " + nPositiveNo);
		writer.write('\n');
		
		// sender.sendMessage("Time spent is "+(time2.getTime()-time1.getTime())/1000);
		//System.out.println("Time spent is "
				//+ (time2.getTime() - time1.getTime()) / 1000);
		writer.write("Time spent is " + (time2.getTime() - time1.getTime())
				/ 1000);
		writer.write('\n');

		writer.close();
		} catch (IOException e) {
			log.info("FILE WRITTER ERROR!");
			e.printStackTrace();
		}
	}

	/**
	 * 批处理读取测试文件
	 * 
	 * @param inputFilePath
	 *            输入文件目录
	 * @param outputFile
	 *            输出文件
	 * @throws Exception
	 */
	public void processDocuments(String inputFilePath, String outputFile) {
 
		int countSenti = 0;
		File file = new File(inputFilePath);

		// 判断是否是文件
		if ((!file.isDirectory()) && (file.getPath().endsWith(".txt"))) {
			// 调用基于语义指向处理的情感分析模型
			countSenti = pd.documentSemanticProcess(file.getPath(), outputFile);
		/*	if (countSenti < 0) {
				++countNegative;
				docuNegative.add(file.getAbsolutePath());

				if ((file.getPath().endsWith("n.txt"))
						|| (file.getPath().endsWith("n-.txt"))
						|| (file.getPath().endsWith("n+.txt"))) {
					++nNegativeYes;
				} else {
					++nNegativeNo;
				}
			} else {
				++countPositive;
				docuPositive.add(file.getAbsolutePath());

				if ((file.getPath().endsWith("p.txt"))
						|| (file.getPath().endsWith("p-.txt"))
						|| (file.getPath().endsWith("p+.txt"))
						|| (file.getPath().endsWith("e.txt"))) {
					++nPositiveYes;
				} else {
					++nPositiveNo;
				}
			}*/
			
			switch(countSenti)
			{
			    case -2: { negLvl2++; negLvl2_set.add(file.getAbsolutePath()); break;}
			    case -1: { negLvl1++; negLvl1_set.add(file.getAbsolutePath()); break;}
			    case 0:  { zerLvl++; zerLvl_set.add(file.getAbsolutePath()); break;}
			    case 1:  { posLvl1++; posLvl1_set.add(file.getAbsolutePath()); break;}
			    case 2:  { posLvl2++; posLvl2_set.add(file.getAbsolutePath()); break;}
			    
			}
		}
		// 是文件目录，一次遍历
		else if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(inputFilePath + "\\" + filelist[i]);
				if ((!readfile.isDirectory())
						&& (readfile.getPath().endsWith(".txt"))) {
					countSenti = pd.documentSemanticProcess(readfile.getPath(),
							outputFile);
					if(Integer.valueOf(filelist[i].substring(filelist[i].indexOf("_")+1, filelist[i].indexOf(".txt"))).equals(Integer.valueOf(countSenti)))
						correctNum++;
					
					
					
					
				/*	if (countSenti < 0) {
						++countNegative;
						docuNegative.add(readfile.getAbsolutePath());

						if ((readfile.getPath().endsWith("n.txt"))
								|| (readfile.getPath().endsWith("n-.txt"))
								|| (readfile.getPath().endsWith("n+.txt"))) {
							++nNegativeYes;
						} else {
							++nNegativeNo;
						}
					} else {
						++countPositive;
						docuPositive.add(readfile.getAbsolutePath());

						if ((readfile.getPath().endsWith("p.txt"))
								|| (readfile.getPath().endsWith("p-.txt"))
								|| (readfile.getPath().endsWith("p+.txt"))
								|| (readfile.getPath().endsWith("e.txt"))) {
							++nPositiveYes;
						} else {
							++nPositiveNo;
						}
					}*/
					switch(countSenti)
					{
					    case -2: { negLvl2++; negLvl2_set.add(readfile.getAbsolutePath()); break;}
					    case -1: { negLvl1++; negLvl1_set.add(readfile.getAbsolutePath()); break;}
					    case 0:  { zerLvl++; zerLvl_set.add(readfile.getAbsolutePath()); break;}
					    case 1:  { posLvl1++; posLvl1_set.add(readfile.getAbsolutePath()); break;}
					    case 2:  { posLvl2++; posLvl2_set.add(readfile.getAbsolutePath()); break;}
					    
					}
					
					
					
				} else if (readfile.isDirectory()) {
					processDocuments(inputFilePath + "\\" + filelist[i], outputFile);
				}
			}
		}

	}	
	
	
	public static void main(String[] args) throws Exception
	{
		//long startTime = System.currentTimeMillis();
		TestDocuments sp = new TestDocuments();
		sp.documentsTest("input1", "D:\\output11.txt");
		//long endTime = System.currentTimeMillis();
		//System.out.println("用时： " + (endTime-startTime)/1000.0 + "秒");
	
	}
}
