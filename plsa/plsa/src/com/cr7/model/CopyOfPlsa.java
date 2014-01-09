package com.cr7.model;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.cr7.dataSet.DataSet;
import com.cr7.evaluate.Evaluator;
import com.cr7.util.Util;

public class CopyOfPlsa {
	private int USERNUM;
	private int ITEMNUM;
	private byte rateMatrix [][];	//�û����־���
	 Set<Integer> [] userSets;	//��Ŷ�ĳ��item���ֵ������û�
	 Set<Integer> [] itemSets;
	public int CATEGORY;
	public double mean[][];	//��ѧϰ����--��ֵ
	private double deviation[][];//��ѧϰ����--����
	public double prob_uz [][];//��ѧϰ����--p(z|u)
	private double postProb [][][];//��ѧϰ����--z�ĺ������p(z|u,v,y)
	private int MAX_LOOP=15;
	public CopyOfPlsa(DataSet d,int category){
		initWithDataSet(d);
		this.CATEGORY = category;
		mean = new double [ITEMNUM+1][CATEGORY+1];
		deviation = new double [ITEMNUM+1][CATEGORY+1];
		prob_uz = new double[USERNUM+1][CATEGORY+1];
		postProb =new double[USERNUM+1][ITEMNUM][CATEGORY+1];
		initParameter();
	}
	
	private void initWithDataSet(DataSet d){
		USERNUM=d.getUSERNUM();
		ITEMNUM=d.getITEMNUM();
		rateMatrix=d.getRateMatrix();
		userSets = d.getUserSets();
		itemSets = d.getItemSets();
		
		Util.p(userSets.length);
	}
	private void initParameter(){
		//��ֵ������Ϊ3�����¸���1�֡�
		for(int i=1;i<=ITEMNUM;i++){
			for(int z=1;z<=CATEGORY;z++){
				double ran = 2*(new Random().nextDouble());
				mean[i][z]=(4-ran);
			}
		}
//		Util.writeIntoTxt(mean,"E:\\u.txt");
		//���������Ϊ1.5���¸���0.5
		for(int i=1;i<=ITEMNUM;i++){
			for(int z=1;z<=CATEGORY;z++){
				deviation[i][z]=(2-(new Random().nextDouble()));
			}
		}
//		Util.writeIntoTxt(deviation,"E:\\dev.txt");
		//����--p(z|u)����Ϊ1/category
		for(int u=1;u<=USERNUM;u++){
			for(int z=1;z<=CATEGORY;z++){
				prob_uz[u][z]=1/(double)CATEGORY;
//				Util.p(prob_uz[u][z]);
			}
		}
//		Util.writeIntoTxt(prob_uz,"E:\\puz.txt");
	}
	
	public void trainModel(){
		int count=0;
	Util.mark();
		while(count<MAX_LOOP){
			computePostProbability();
	Util.mark("computePostProbability");
			computeParameter();
	Util.mark("computeParameter");
			computeLostFunction();
	Util.mark("computeLostFunction");
			count++;
		}
	}
	private void computePostProbability2(){
		for(int u=1;u<=USERNUM;u++){
			Iterator<Integer> items = itemSets[u].iterator();
			while(items.hasNext()){
				int i = items.next();
				double total = 0;
				for(int z=1;z<=CATEGORY;z++){
					double pv = (rateMatrix[u][i]-mean[i][z])/deviation[i][z];
					total+=(prob_uz[u][z]*Util.Ni(pv));
				}
				for(int z=1;z<=CATEGORY;z++){
					double pv = (rateMatrix[u][i]-mean[i][z])/deviation[i][z];
					pv =(prob_uz[u][z]*Util.Ni(pv));
					postProb[u][i][z]=(pv/total);
				}
			}
		}
		
	}
	private void computePostProbability(){
		for(int u=1;u<=USERNUM;u++){
			Iterator<Integer> items = itemSets[u].iterator();
			while(items.hasNext()){
				int i = items.next();
				double total = 0;
				double pv [] = new double[CATEGORY+1];
				for(int z=1;z<=CATEGORY;z++){
					double p = (rateMatrix[u][i]-mean[i][z])/deviation[i][z];
					pv[z] = prob_uz[u][z]*Util.Ni(p);
					total+=(pv[z]);
				}
				for(int z=1;z<=CATEGORY;z++){
					postProb[u][i][z]=(pv[z]/total);
				}
			}
		}
		
	}
	private void computeParameter(){
		/**
		 * 
		 * ע����ܴ���useset����itemsetΪ0�����
		 */
		//�������p(z|u)
		for(int u=1;u<=USERNUM;u++){
			if(itemSets[u].size()==0) continue;	//���uû��ѡ���κ��û���ô����p(z|u)ֻ��Ϊ��ʼ�ľ��ȷֲ�
			Iterator<Integer> items = itemSets[u].iterator();
			double p[]  = new double[CATEGORY+1];
			double total=0;
			for(int z=1;z<=CATEGORY;z++){
				items = itemSets[u].iterator();
				while(items.hasNext()){
					int i = items.next();
					total+=postProb[u][i][z];
					p[z]+=postProb[u][i][z];
				}
			}
			for(int z=1;z<=CATEGORY;z++){
				prob_uz[u][z]=p[z]/total;
			}
		}
		//������� ��y,z �����õ���Uy,z������Ҫ�ȼ���
		for(int i=1;i<=ITEMNUM;i++){
			if(userSets[i].size()==0) continue;
//			Iterator<Integer> users = userSets[i].iterator();
			double numerator = 0;
			double nominator = 0;
			for(int z=1;z<=CATEGORY;z++){
				for(int u=1;u<=USERNUM;u++){
					numerator+=postProb[u][i][z]*(rateMatrix[u][i]-mean[i][z]);
					nominator+=postProb[u][i][z];
				}
				mean[i][z]=(numerator/nominator);
			}
		}
		//���������Uy,z
		for(int i=1;i<=ITEMNUM;i++){
			if(userSets[i].size()==0) continue;
//			Iterator<Integer> users = userSets[i].iterator();
			double value = 0;
			double prob = 0;
			for(int z=1;z<=CATEGORY;z++){
				for(int u=1;u<=USERNUM;u++){
					value+=postProb[u][i][z]*rateMatrix[u][i];
					prob+=postProb[u][i][z];
				}
				mean[i][z]=(value/prob);
			}
		}
		
	}
	
	//����ɱ�����
	private double computeLostFunction(){
		double lost = 0;
		for(int u=1;u<=USERNUM;u++){
			Iterator<Integer> items = itemSets[u].iterator();
			while(items.hasNext()){
				int i = items.next();
				double rst=0;
				for(int z=1;z<=CATEGORY;z++){
					//pvyֵΪNULL
					double pvy=Util.Ni((rateMatrix[u][i]-mean[i][z])/Math.sqrt(deviation[i][z]));
					double pz = prob_uz[u][z];
					rst+=(postProb[u][i][z]*(Math.log(pvy)+Math.log(pz)));
				}
//				Util.p(rst);
				lost+=rst;
			}
		}
		Util.p("��Ȼ����ֵ��"+lost);
		return lost;
	}

}
