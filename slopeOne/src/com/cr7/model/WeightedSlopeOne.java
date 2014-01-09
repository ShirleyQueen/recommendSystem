package com.cr7.model;

import java.util.Iterator;
import java.util.Set;

import com.cr7.dataset.DataSet;

public class WeightedSlopeOne implements Model{
	private int userNum;
	private int itemNum;
	private int[][] rateMatrix;
	private Set [] userSets;	//�û�����
	private Set [] itemSets;
	private int N_IJ;	//��IJ�����ֵ��û���Ŀ
	public void setDataSet(DataSet d){
		userNum = d.getUserNum();
		itemNum = d.getItemNum();
		rateMatrix = d.getRateMatrix();
		userSets = d.getUserSet();
		itemSets = d.getItemSet();
		
		//test
		Iterator itr = userSets[1170].iterator();
		while(itr.hasNext()){
			System.out.print("u"+itr.next()+" ");
		}
		System.out.print("\n"+userSets[1170].size()+"\n");
	}
	
	
	public double computeDev(int i,int j){
		double dev = 0;
		int count =0;
		Iterator itr = userSets[i].iterator();
		while(itr.hasNext()){
			int u = (Integer)itr.next();
			if(userSets[j].contains(u)){
				dev+=(rateMatrix[u][j]-rateMatrix[u][i]);
				count++;
			}
		}
		N_IJ=count;
		//rateNum=0�������������������
		if(count==0) return 0;
		return dev/count;
	}
	
	public double predict(int tarUser,int tarItem){
		int rateNum=0;
		double predictR = 0;
		int total_N_IJ=0;
		N_IJ=0;
		for(int i=1;i<=this.itemNum;i++){
			if(rateMatrix[tarUser][i]==0) continue;
			double dev = this.computeDev(i, tarItem);
			predictR+=(rateMatrix[tarUser][i]+dev)*N_IJ;
			total_N_IJ+=N_IJ;
			rateNum++;
		}
//		�ԣ�i,j��ֻ��Ŀ���û�ͬʱ���֣���û�������û�ͬʱ��I,j���ֵ����
//		�򵥵ķ����û�u���������ֵ����۷�
		if(total_N_IJ==0){	
			int count=0;
			double rst=0;
			for(int i=1;i<=this.itemNum;i++){
				if(rateMatrix[tarUser][i]==0) continue;
				rst+=rateMatrix[tarUser][i];
				count++;
			}
			return rst/count;
		}
		predictR/=total_N_IJ;
//if(predictR>5) 	System.out.println(tarUser+" "+tarItem+"predictR"+predictR);	//
		return predictR;
	}
}
