package com.cr7.dataSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cr7.util.Configuration;
import com.cr7.util.Util;

public class DataSet {
	int ITEMNUM;	//ֱ�Ӹ�����Ҳ�ɴ��ļ���ȡ���ǱȽ��鷳
	int USERNUM;
	byte [][] rateMatrix;//�û��������ݣ�����>=3����Ϊ1��������Ϊ0
	Set<Integer> [] userSets;
	 Set<Integer> [] itemSets;
	public DataSet(String file){
		USERNUM=Configuration.usernum;
		ITEMNUM=Configuration.itemnum;
		rateMatrix = new byte[USERNUM+1][ITEMNUM+1];
		userSets = new Set[this.ITEMNUM+1];
		itemSets = new Set[this.USERNUM+1];
		Util.initMatrix(rateMatrix);
		for(int i=1;i<userSets.length;i++){
			 userSets[i] = new HashSet<Integer>();
		 }
		 for(int i=1;i<itemSets.length;i++){
			 itemSets[i] = new HashSet<Integer>();
		 }
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
				userSets[i].add(u);
				itemSets[u].add(i);
			}
		} catch (Exception e) { 
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

	public Set<Integer>[] getUserSets() {
		return userSets;
	}

	public Set<Integer>[] getItemSets() {
		return itemSets;
	}
}
