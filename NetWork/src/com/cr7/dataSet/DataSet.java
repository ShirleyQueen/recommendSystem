package com.cr7.dataSet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cr7.util.Util;

public class DataSet {
	int ITEMNUM=1682;	//ֱ�Ӹ�����Ҳ�ɴ��ļ���ȡ���ǱȽ��鷳
	int USERNUM=943;
	public int testUserNum = 0;
	byte [][] rateMatrix;//�û��������ݣ�����>=3����Ϊ1��������Ϊ0
	
	public DataSet(String file,String str){
		rateMatrix = new byte[USERNUM+1][ITEMNUM+1];
		Util.initMatrix(rateMatrix);
		if(str.equals("train")) this.initTrainRecord(file);
		else this.initProbeRecord(file);
	}
	
	//Ϊ�������ݼ���ʼ��rateMatrix
	public byte [][] initProbeRecord(String scoreFile){
		testUserNum=0;
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(scoreFile));
			String line = "";
			int u=0;	//user
			int i=0;	//item
			byte s=0;	//score
			while((line=bf.readLine())!=null){
				Pattern p = Pattern.compile("[\\w]+");
				Matcher m = p.matcher(line);
				if(m.find()) u=Integer.parseInt(m.group());
				if(m.find()) i=Integer.parseInt(m.group());
				if(m.find()) s=Byte.parseByte(m.group());
				rateMatrix[u][i]=s;
				if(u>testUserNum)	testUserNum=u;
			}
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return rateMatrix;
	}
	
	
	//Ϊѵ�����ݼ���ʼ��rateMatrix
	public void initTrainRecord(String scoreFile){
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(scoreFile));
			String line = "";
			int u=0;	//user
			int i=0;	//item
			byte s=0;	//score
			while((line=bf.readLine())!=null){
				Pattern p = Pattern.compile("[\\w]+");
				Matcher m = p.matcher(line);
				if(m.find()) u=Integer.parseInt(m.group());
				if(m.find()) i=Integer.parseInt(m.group());
				if(m.find()) s=Byte.parseByte(m.group());
				if(s>=3) rateMatrix[u][i]=1;		//���ִ������ı�
				else if(s<=1) rateMatrix[u][i]=-1;			//����С��3�ı�
			}
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	public byte [][] getRateMatrix(){
		return this.rateMatrix;
	}
	public int getITEMNUM() {
		return ITEMNUM;
	}
	public int getUSERNUM() {
		return USERNUM;
	}
}
