package com.bupt.utility;
	import java.util.*;
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileNotFoundException;
	import java.io.IOException;
	import java.io.BufferedReader;
	import java.io.InputStreamReader;
/**
 * 读取词典
 * @author BUPT
 *
 */
	public class TextToDict {
		private static ArrayList<String> gettedArrayList;
		private static LinkedList<String> gettedLinkList;
		private static HashMap <String, Integer> gettedDic;
		
	/**
	 * 将词表读取到hashmap中
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
		public static HashMap<String,Integer> getHashMap(String filepath) throws IOException
		{ 

			try {   
				gettedDic= new HashMap<String,Integer>();
				FileInputStream	infile = new FileInputStream(new File(filepath));
				BufferedReader readStream=new BufferedReader(new InputStreamReader(infile));
		        String temp;
		        while((temp=readStream.readLine())!=null)
		        {     
		        	String result[]=temp.split("\\s{1,}");	
		        	int b=Integer.parseInt(result[1]);
		        	gettedDic.put(result[0],b);   
		        	
				    	
				}	       
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			return gettedDic;
			
		}	
		
		/**
		 * 将词表读取到Linklist中
		 * @param filepath
		 * @return
		 * @throws IOException
		 */
		public static LinkedList<String> getLinkList(String filepath) throws IOException
		{   gettedLinkList= new LinkedList<String>();
			try {
				FileInputStream	infile = new FileInputStream(new File(filepath));
				BufferedReader readStream=new BufferedReader(new InputStreamReader(infile));
		        String temp;
		        while((temp=readStream.readLine())!=null)	            
		        	gettedLinkList.add(temp);      	
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			
			return gettedLinkList;
		}

		
		/**
		 * 将词表读取到Arraylist中
		 * @param filepath
		 * @return
		 * @throws IOException
		 */
		public static ArrayList<String> getArrayList(String filepath) throws IOException
		{ gettedArrayList = new ArrayList<String>();
			try {
				FileInputStream	infile = new FileInputStream(new File(filepath));
				BufferedReader readStream=new BufferedReader(new InputStreamReader(infile));
		        String temp;
		        while((temp=readStream.readLine())!=null)	            
		        	gettedArrayList.add(temp);      	
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			
			return gettedArrayList;
		}
	}

