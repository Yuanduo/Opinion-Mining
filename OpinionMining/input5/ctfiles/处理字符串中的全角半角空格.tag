问题/n 描述/v :/wp 
去掉/v 字符串/n 中/b 首尾/n 的/ude1 全/a 角/n 半/m 角/q 空格/n 。/wj 

解决/v 方案/n ：/wp 全/a 角/q 空格/n 两/m 位/q 值/n 都/d 是/vshi -/wp 95/m ,/wd 只要/c 把/pba 这个/rz 值/n 改/vd 成/v 32/m 这个/rz 半/m 角/q 的/ude1 空格/n 值/n ，/wd 然后/c 重新/d 创建/v 字符串/n 。/wj 




String/x  a/x  =/m  "/n 　 市/n  1/m "/n ;/wf 
//n //n 首/m 部/q 是/vshi 全/a 角/q 空格/n ，/wd 中间/f 为/v 半/m 角/q 空格/n  

byte/x [/wkz ]/wky  bytes/x  =/m  a.getBytes/x (/wkz )/wky ;/wf 
for/x  (/wkz int/x  i/x  =/m  0/m ;/wf  i/x  </n  bytes.length/x ;/wf  i++/x )/wky  {/n 
	if/x  (/wkz bytes/x [/wkz i/x ]/wky  ==/m  -/wp 95/m )/wky  
		 bytes/x [/wkz i/x ]/wky  =/m  32/m ;/wf 
		 
}/n 
a=/x  new/x  String/x (/wkz bytes/x )/wky ./wj trim/x (/wkz )/wky  ;/wf 
System.out.println/x (/wkz a/x )/wky ;/wf 





import/x  java.io.BufferedReader/x ;/wf 
import/x  java.io.File/x ;/wf 
import/x  java.io.FileReader/x ;/wf 
import/x  java.io.IOException/x ;/wf 

public/x  class/x  tiqu/x  {/n 

	//n */n */n 
	 */n  @param/x  args/x 
	 */n //n 
	public/x  static/x  void/x  main/x (/wkz String/x [/wkz ]/wky  args/x )/wky  {/n 
		        
				File/x  file/x  =/m  new/x  File/x (/wkz "/n 1.txt/x "/n )/wky ;/wf 
		        BufferedReader/x  reader/x  =/m  null/x ;/wf 
		        try/x  {/n 
		            System.out.println/x (/wkz "/n 以/p 行为/n 单位/n 读取/vn 文件/n 内容/n ，/wd 一/m 次/qv 读/v 一/m 整/m 行/q ：/wp "/n )/wky ;/wf 
		            reader/x  =/m  new/x  BufferedReader/x (/wkz new/x  FileReader/x (/wkz file/x )/wky )/wky ;/wf 
		            String/x  tempString/x  =/m  null/x ;/wf 
		            //n //n  一/m 次/qv 读/v 入/v 一/m 行/q ，/wd 直到/v 读入/v null/x 为/p 文件/n 结束/v 
		            while/x  (/wkz (/wkz tempString/x  =/m  reader.readLine/x (/wkz )/wky )/wky  !/wt =/m  null/x )/wky  {/n 
		                //n //n  显示/v 行/ng 号/n 
		                System.out.println/x (/wkz tempString/x )/wky ;/wf 
		                String/x  abs/x  =/m  tempString.trim/x (/wkz )/wky ;/wf 
		        		String/x  str2/x  =/m  abs.replaceAll/x (/wkz "/n  "/n ,/wd  "/n \/n \/n |/n "/n )/wky ;/wf 
		        		String/x [/wkz ]/wky  ary/x  =/m  str2.split/x (/wkz "/n \/n \/n |/n "/n )/wky ;/wf 
		        		if/x  (/wkz ary/x [/wkz 0/m ]/wky ./wj equals/x (/wkz "/n 　"/n )/wky )/wky 
		        		{/n 
		        		String/x  s1/x  =/m  ary/x [/wkz 1/m ]/wky ;/wf 
		        		String/x  s2/x  =/m  ary/x [/wkz 2/m ]/wky ;/wf 
		        		System.out.println/x (/wkz "/n s1/x  =/m "/n  +/m  s1/x )/wky ;/wf 
		        		System.out.println/x (/wkz "/n s2/x  =/m "/n  +/m  s2/x )/wky ;/wf 
		        		}/n 
		        		else/x 
		        		{/n 
		        			String/x  s1/x  =/m  ary/x [/wkz 0/m ]/wky ;/wf 
			        		String/x  s2/x  =/m  ary/x [/wkz 1/m ]/wky ;/wf 
			        		System.out.println/x (/wkz "/n s1/x  =/m "/n  +/m  s1/x )/wky ;/wf 
			        		System.out.println/x (/wkz "/n s2/x  =/m "/n  +/m  s2/x )/wky ;/wf 
		        		}/n 
		        		

		            }/n 
		            reader.close/x (/wkz )/wky ;/wf 
		        }/n  catch/x  (/wkz IOException/x  e/x )/wky  {/n 
		            e.printStackTrace/x (/wkz )/wky ;/wf 
		        }/n  finally/x  {/n 
		            if/x  (/wkz reader/x  !/wt =/m  null/x )/wky  {/n 
		                try/x  {/n 
		                    reader.close/x (/wkz )/wky ;/wf 
		                }/n  catch/x  (/wkz IOException/x  e1/x )/wky  {/n 
		                }/n 
		            }/n 
		        }/n 
		

	}/n 
}/n 


