package com.bupt.document;

import java.io.File;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * 单篇文档的文档级情感分析测试入口
 * @author BUPT
 *
 */
public class TestDocument {
	ProcessDocument pd = new ProcessDocument();
	private static Log log = LogFactory.getLog(TestDocument.class);

	/**
	 * 单篇文档的文档级情感分析
	 * @param inputFilePath
	 *       文件名
	 * @param outputFile
	 *      输出结果保存至文件名
	 */
	public void documentTest(String inputFilePath, String outputFile){
		int countSenti = 0;
		File file = new File(inputFilePath);

		// 判断是否是文件
		if ((!file.isDirectory()) && (file.getPath().endsWith(".txt"))) {

			// 调用基于语义指向处理的情感分析模型
			countSenti = pd.documentSemanticProcess(file.getPath(), outputFile);
			System.out.println(countSenti);
		}
		else log.info("INPUT FILE ERROR!");
	}	
	
	public static void main(String[] args) throws Exception
	{
		TestDocument sp = new TestDocument();
		//sp.documentTest("D:\\input\\1n.txt", "D:\\output1.txt");
		sp.documentTest("D:\\1.txt", "D:\\1n.txt");
	
	}
	
	
}
