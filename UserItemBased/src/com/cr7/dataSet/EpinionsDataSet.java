package com.cr7.dataSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cr7.util.Util;

public class EpinionsDataSet {
	int ITEMNUM=139738;	//ֱ�Ӹ�����Ҳ�ɴ��ļ���ȡ���ǱȽ��鷳
	int USERNUM=49290;
	
	private int userDegree[];
	private int itemDegree[];
	public EpinionsDataSet(String file){
		try {
//			filter_rate("E:\\�����ھ�����\\epinions\\filterData\\itemSelected.txt");
//			getDegree("E:\\�����ھ�����\\epinions\\filterData\\rateSelected1.txt");
//			filter_rate();
//			replaceId();
			 divide();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void filter_item(){
		Random r = new Random();
		HashSet<Integer> lineSet = new HashSet<Integer>();
		for(int i=0;i<2000;){
			if(lineSet.add(r.nextInt(11657))) i++;
		}
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader("E:\\�����ھ�����\\epinions\\ItemCandidate.txt"));
			String line = "";
			int count = 1;
			while((line=bf.readLine())!=null){
				if(lineSet.contains(count)){
					Util.writeIntoTxt(line+"\r\n", "E:\\�����ھ�����\\epinions\\itemSelected.txt");
				}
				count++;
			}
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * �滻�ɵ�ID
	 */
	private void replaceId() throws Exception{
		HashMap<Integer,Integer> usermap = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> itemmap = new HashMap<Integer,Integer>();
		//read date into maps;
		BufferedReader bf;
		bf = new BufferedReader(new FileReader("E:\\�����ھ�����\\epinions\\filterData\\userSelected.txt"));
		String line = "";
		int newId=0;	//user
		int oldId=0;	//item
		while((line=bf.readLine())!=null){
			Pattern p = Pattern.compile("[\\w]+");
			Matcher m = p.matcher(line);
			if(m.find()) oldId=Integer.parseInt(m.group());
			if(m.find()) newId=Integer.parseInt(m.group());
			usermap.put( oldId,newId);
		}
		Util.p("**"+usermap.size());
		bf = new BufferedReader(new FileReader("E:\\�����ھ�����\\epinions\\filterData\\itemSelected.txt"));
		line = "";
		newId=0;	//user
		oldId=0;	//item
		while((line=bf.readLine())!=null){
			Pattern p = Pattern.compile("[\\w]+");
			Matcher m = p.matcher(line);
			if(m.find()) oldId=Integer.parseInt(m.group());
			if(m.find()) newId=Integer.parseInt(m.group());
			itemmap.put( oldId,newId);
		}
		Util.p("**"+itemmap.size());
		//read rate
		bf = new BufferedReader(new FileReader("E:\\�����ھ�����\\epinions\\filterData\\rateSelected2.txt"));
		line = "";
		int u=0;	//user
		int i=0;	//item
		int s=0;	//rate
		while((line=bf.readLine())!=null){
			Pattern p = Pattern.compile("[\\w]+");
			Matcher m = p.matcher(line);
			if(m.find()) u=Integer.parseInt(m.group());
			if(m.find()) i=Integer.parseInt(m.group());
			if(m.find()) s=Integer.parseInt(m.group());
			String result = usermap.get(u)+"\t"+itemmap.get(i)+"\t"+s+"\r\n";
			Util.writeIntoTxt(result, "E:\\�����ھ�����\\epinions\\filterData\\rateSelected3.txt");
		}
	}
	
	
	/**
	 * ����rate����
	 * 
	 */
	private void filter_rate()throws Exception{
		HashSet<Integer> userset = new HashSet<Integer>();
		//read date into sets;
		BufferedReader bf;
		String line = "";
		bf = new BufferedReader(new FileReader("E:\\�����ھ�����\\epinions\\filterData\\userSelected.txt"));
		int newId=0;	//user
		int oldId=0;	//item
		while((line=bf.readLine())!=null){
			Pattern p = Pattern.compile("[\\w]+");
			Matcher m = p.matcher(line);
			if(m.find()) oldId=Integer.parseInt(m.group());
			if(m.find()) newId=Integer.parseInt(m.group());
			userset.add(oldId);
		}
		Util.p("**"+userset.size());
		//read rate
		bf = new BufferedReader(new FileReader("E:\\�����ھ�����\\epinions\\filterData\\rateSelected1.txt"));
		line = "";
		int u=0;	//user
		int i=0;	//item
		int s=0;	//rate
		while((line=bf.readLine())!=null){
			Pattern p = Pattern.compile("[\\w]+");
			Matcher m = p.matcher(line);
			if(m.find()) u=Integer.parseInt(m.group());
			if(m.find()) i=Integer.parseInt(m.group());
			if(m.find()) s=Integer.parseInt(m.group());
			if( userset.contains(u)){
				Util.writeIntoTxt(line+"\r\n", "E:\\�����ھ�����\\epinions\\filterData\\rateSelected2.txt");
			}
		}
	}

	private void getDegree(String rateFile){
		userDegree = new int[USERNUM+1];
		Util.init(userDegree);
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(rateFile));
			String line = "";
			int u=0;	//user
			int i=0;	//item
			Util.p("");
			while((line=bf.readLine())!=null){
				Pattern p = Pattern.compile("[\\w]+");
				Matcher m = p.matcher(line);
				if(m.find()) u=Integer.parseInt(m.group());
				if(m.find()) i=Integer.parseInt(m.group());
				userDegree[u]++;
			}
			Util.p("������");
			for(u=1;u<=USERNUM;u++){
				Util.writeIntoTxt(u+"\t"+userDegree[u]+"\r\n", "E:\\�����ھ�����\\epinions\\filterData\\�������������û���.txt");
			}
			Util.p("д����");
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������ݼ����ֳ�ѵ�����Ͳ��Լ�
	 */
	private void divide()throws Exception{
		Random r = new Random();
		HashSet<Integer> lineSet = new HashSet<Integer>();
		for(int i=0;i<6000;){
			if(lineSet.add(r.nextInt(33977))) i++;
		}
		BufferedReader bf;
		bf = new BufferedReader(new FileReader("E:\\�����ھ�����\\epinions\\filterData\\rateSelected3.txt"));
		String line = "";
		int count = 1;
		while((line=bf.readLine())!=null){
			if(lineSet.contains(count)){
				Util.writeIntoTxt(line+"\r\n", "E:\\�����ھ�����\\epinions\\filterData\\rate_test.txt");
			}else{
				Util.writeIntoTxt(line+"\r\n", "E:\\�����ھ�����\\epinions\\filterData\\rate_train.txt");
			}
			count++;
		}
	}
}
