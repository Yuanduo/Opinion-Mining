package ICTCLAS.kevin.zhang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

//import ate.util.Constant;

/**
 * 分词
 * @author BUPT
 *
 */
public class WordSplitter {
//	private static final int ICT_POS_MAP_FIRST =1; //计算所一级标注集
	private static final int ICT_POS_MAP_SECOND = 0; //计算所二级标注集
//	private static final int PKU_POS_MAP_SECOND =2;  //北大二级标注集						                
//	private static final int PKU_POS_MAP_FIRST =3;  //北大一级标注集
	//创建类的对象  
    private ICTCLAS2010 ictclas = null;
    private boolean available ;
   
    public WordSplitter() throws IOException {
       this.ictclas = new ICTCLAS2010();
       this.available = init();
    }
    
/**
 * 初始化
 * @return
 */
    private boolean init() {
       String argu = ".";
       boolean result = false;
       try {
		if (ictclas.ICTCLAS_Init(argu.getBytes("GB2312")) == false) {
		       System.out.println("Init Fail!");
//		       return false;
		   } else
			   {ictclas.ICTCLAS_SetPOSmap(ICT_POS_MAP_SECOND);// 设置词性标注集
			   result = true;}
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      return result;
    }
    
/**
 *  导入用户字典
 * @param userDict
 *         用户字典路径
 * @return
 * @throws UnsupportedEncodingException
 */
    public int importUserDict(String userDict) {
    	   int nCount = 0;
    	try {
    		if (available) {
    		// 返回导入用户词语个数。参数为用户字典的编码类型
			nCount = ictclas.ICTCLAS_ImportUserDict(userDict
			          .getBytes("GB2312"));
           ictclas.ICTCLAS_SaveTheUsrDic();
           }
       else
    	   nCount=0;
    }
     catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return nCount;
    }
/**
 * 分词
 * @param str
 *        带分词字符串
 * @param tag
 *        设置输出的形式
 * @return
 * @throws IOException
 */
    public String split(String str, int tag) throws IOException {
       if (available) {
           String string;
          // ictclas.ICTCLAS_ImportUserDict(Constant.USERDICT.getBytes());
           byte[] bytes = ictclas.ICTCLAS_ParagraphProcess(str
                  .getBytes("GB2312"), tag);
           string = new String(bytes, 0, bytes.length, "GB2312");
           return string.trim();
           
       } else
           return null;
    }
    //退出
    public void exit() {
       ictclas.ICTCLAS_Exit();
    }
   
}