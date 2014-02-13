package ICTCLAS.kevin.zhang;

public class ICTCLAS2010 {
   
    static {
       System.loadLibrary("ICTCLAS2010");
    }
   
    //初始化
    public native boolean ICTCLAS_Init(byte[] sPath);
    //退出
    public native boolean ICTCLAS_Exit();
    //导入用户词典
    public native int ICTCLAS_ImportUserDict(byte[] sPath);
    //获取uni概率
    public native float ICTCLAS_GetUniProb(byte[] sWord);
    //判断词典中有没有这个词
    public native boolean ICTCLAS_IsWord(byte[] sWord);
    //一段文字的分词
    public native byte[] ICTCLAS_ParagraphProcess(byte[] sSrc, int bPOSTagged);
    //一个文本文件的分词
    public native boolean ICTCLAS_FileProcess(byte[] sSrcFilename,
           byte[] sDestFilename, int bPOSTagged);
    public native byte[] nativeProcAPara(byte[] src);
    public native int ICTCLAS_AddUserWord(byte[] sWord);
    public native int ICTCLAS_SaveTheUsrDic();
    public native int ICTCLAS_DelUsrWord(byte[] sWord);
    public native int ICTCLAS_KeyWord(byte[] resultKey, int nCountKey);
    public native long ICTCLAS_FingerPrint();
    public native int ICTCLAS_SetPOSmap(int nPOSmap);
    public native int ICTCLAS_GetElemLength(int nIndex);
 
}