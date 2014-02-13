package com.bupt.utility;

/**
 * 处理句法路径的路径节点类
 * @author BUPT
 * @version 1.0
 */
public class Step {
	public String dir = ""; //表示节点类型、向上向下 
	public String value = ""; //节点的词性值
	public int id=-1; //依赖关系树节点编号
	public Step(String d,String val,int i)
	{
		dir = d;
		value = val;
		id = i;
	}
	public boolean equals(Step s)
	{
		if(s.dir.equals(dir)&&s.value.equals(value))
			return true;
		else
			return false;
	}
	@Override
	public String toString()
	{
		return dir+"#"+value;
	}

}
