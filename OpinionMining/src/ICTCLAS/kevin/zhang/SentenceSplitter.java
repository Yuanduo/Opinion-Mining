package ICTCLAS.kevin.zhang;

import com.bupt.utility.Punctuation;

public class SentenceSplitter {

	
	/**
	 * 文档级情感分析的整句切分
	 * @param str
	 *        待分句的字符串
	 * @return
	 */
	public static String[] splitSentence(String str){
		
		str = str.replace(Punctuation.SPACE, Punctuation.EMPTY);
		str = str.replace(Punctuation.COMMA_FULL,Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.QUOTATION_LEFT, Punctuation.QUOTATION);
		str = str.replace(Punctuation.QUOTATION_RIGHT, Punctuation.QUOTATION);
		str = str.replace(Punctuation.QUESTION_HALF, Punctuation.EXCLAMATION_HALF);
		str = str.replace(Punctuation.QUESTION_FULL, Punctuation.EXCLAMATION_HALF);
		str = str.replace(Punctuation.STOP_HALF, Punctuation.EXCLAMATION_HALF);
		str = str .replace(Punctuation.STOP_FULL, Punctuation.EXCLAMATION_HALF);
	    str = str .replace(Punctuation.EXCLAMATION_FULL, Punctuation.EXCLAMATION_HALF);
	    String[] Review = str.split(Punctuation.EXCLAMATION_HALF); // 英文中的”.“不行
	    return Review;
	}
	
	/**
	 * 文档级情感分析的分句块切分
	 * @param  str
	 *         待切分为分句块的字符串
	 * @return
	 */
	public static String[] splitSubSentence(String str){
		str = str.replace(Punctuation.SEMICOLON_HALF, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.SEMICOLON_FULL, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.DASH_HALF, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.DASH_FULL, Punctuation.COMMA_HALF);
		String[] Review = str.split(Punctuation.COMMA_A);
		return Review;
	}
	
	
	/**
	 * 句子级情感分析的分句块切分
	 * @param str
	 *        待切分为分句块的字符串
	 * @return
	 */
	public static String[] ssplitSentence(String str){
		str = str.replace(Punctuation.QUOTATION_LEFT, Punctuation.QUOTATION);
		str = str.replace(Punctuation.QUOTATION_RIGHT, Punctuation.QUOTATION);
		str = str.replace(Punctuation.COMMA_FULL,Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.STOP_HALF, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.STOP_FULL, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.QUESTION_HALF, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.QUESTION_FULL, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.SEMICOLON_HALF, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.SEMICOLON_FULL, Punctuation.COMMA_HALF);
	    str = str .replace(Punctuation.EXCLAMATION_FULL, Punctuation.COMMA_HALF);
	    str = str .replace(Punctuation.EXCLAMATION_HALF, Punctuation.COMMA_HALF);
	    str = str.replace(Punctuation.SPACE,Punctuation.COMMA_HALF);
	    str = str.replace(Punctuation.SPACE_FULL,Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.DASH_HALF, Punctuation.COMMA_HALF);
		str = str.replace(Punctuation.DASH_FULL, Punctuation.COMMA_HALF);
		String []Review = str.split(Punctuation.COMMA_HALF);
	    return Review;
	}
}
