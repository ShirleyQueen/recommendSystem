package com.cr7.dataSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cr7.util.Util;

public class DataSet {
	int ITEMNUM=1682;	//ֱ�Ӹ�����Ҳ�ɴ��ļ���ȡ���ǱȽ��鷳
	int USERNUM=943;
//	int ITEMNUM=2000;	//ֱ�Ӹ�����Ҳ�ɴ��ļ���ȡ���ǱȽ��鷳
//	int USERNUM=3118;
	public int testUserNum = 0;
	byte [][] rateMatrix;//�û��������ݣ�����>=3����Ϊ1��������Ϊ0
	private double ave[];	//�û������־�ֵ
	
	public DataSet(String file){
		rateMatrix = new byte[USERNUM+1][ITEMNUM+1];
		Util.initMatrix(rateMatrix);
		readRecord(file);
	}
	
	//��ȡ���־���
	private void readRecord(String rateFile){
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(rateFile));
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
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	
	public double [] getAverage(){
		ave = new double[USERNUM+1];
		Util.init(ave);
		for(int u=1;u<=USERNUM;u++){
			int count=0;
			for(int i=1;i<=ITEMNUM;i++){
				if(rateMatrix[u][i]!=0){
					ave[u]+=rateMatrix[u][i];
					count++;
				}
			}
			ave[u]/=((double)count);
		}
		return ave;
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
