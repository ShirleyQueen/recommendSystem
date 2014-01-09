package com.cr7.model;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.cr7.dataSet.DataSet;
import com.cr7.evaluate.Evaluator;
import com.cr7.util.Util;

public class Plsa {
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
	private int MAX_LOOP=20;
	public static final double NEARZERO=0.00000000001;
	public Plsa(DataSet d,int category){
		initWithDataSet(d);
		this.CATEGORY = category;
		mean = new double [ITEMNUM+1][CATEGORY+1];
		deviation = new double [ITEMNUM+1][CATEGORY+1];
		prob_uz = new double[USERNUM+1][CATEGORY+1];
		postProb =new double[USERNUM+1][ITEMNUM+1][CATEGORY+1];
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
		//���������Ϊ1.5���¸���0.5
		for(int i=1;i<=ITEMNUM;i++){
			for(int z=1;z<=CATEGORY;z++){
				deviation[i][z]=(2-(new Random().nextDouble()));
			}
		}
		//����--p(z|u)����Ϊ1/category
		for(int u=1;u<=USERNUM;u++){
			for(int z=1;z<=CATEGORY;z++){
				prob_uz[u][z]=1/(double)CATEGORY;
			}
		}
//		Util.writeIntoTxt(prob_uz,"E:\\puz.txt");
	}
	
	public void trainModel(){
		int count=0;
	Util.mark();
		while(count<MAX_LOOP){
			computePostProbability();
//	Util.mark("computePostProbability");
			computeParameter();
//	Util.mark("computeParameter");
			computeLostFunction();
//	Util.mark("computeLostFunction");
			count++;
			if(count>=28){
				for(int u=678;u<=700;u++){
					for(int i=1;i<=ITEMNUM;i++){
						Util.writeIntoTxt(postProb[u][i],"E:\\postProb"+count+".txt");
					}
				}
				Util.writeIntoTxt(mean,"E:\\mean"+count+".txt");
				Util.writeIntoTxt(deviation,"E:\\devia"+count+".txt");
				Util.writeIntoTxt(prob_uz,"E:\\puz"+count+".txt");
			}
	Util.mark("����ʱ��");
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
					if(Double.isNaN(p)){
							Util.p(deviation[i][z]);
					}
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
		//������� ��y,z ��deviation�� �����õ���Uy,z������Ҫ�ȼ���
		for(int i=1;i<=ITEMNUM;i++){
			if(userSets[i].size()==0) continue;
			for(int z=1;z<=CATEGORY;z++){
				double numerator = 0;
				double nominator = 0;
				for(int u=1;u<=USERNUM;u++){
					if(rateMatrix[u][i]==0) continue;
					double diff = (rateMatrix[u][i]-mean[i][z])*(rateMatrix[u][i]-mean[i][z]);
					numerator+=(postProb[u][i][z]*diff);
					nominator+=postProb[u][i][z];
				}
				if(nominator<NEARZERO || numerator<NEARZERO) continue; 
				deviation[i][z]=(numerator/nominator);
//				if(deviation[i][z]<(NEARZERO*NEARZERO)){
//					Util.p("rateMatrix[u][i]"+rateMatrix[u][i]+"mean[i][z]"+mean[i][z]
//					                         +"postProb[u][i][z]"+postProb[u][i][z]);
//				}
			}
		}
		//���������Uy,z
		for(int i=1;i<=ITEMNUM;i++){
			if(userSets[i].size()==0) continue;
			for(int z=1;z<=CATEGORY;z++){
				double value = 0;
				double prob = 0;
				for(int u=1;u<=USERNUM;u++){
					if(rateMatrix[u][i]==0) continue;
					value+=postProb[u][i][z]*rateMatrix[u][i];
					prob+=postProb[u][i][z];
				}
				///////////NEARZERO
				if(prob<NEARZERO)	continue;
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
					double pvy=0;
					double pz = prob_uz[u][z];
					//ע����Щ��Ŀֻ��һ�����û����˷֣�����Ϊ0
					if(deviation[i][z]<=NEARZERO || pz<=NEARZERO) continue;
					if(pz<=NEARZERO) pz+=NEARZERO;
					double normal = (rateMatrix[u][i]-mean[i][z])/Math.sqrt(deviation[i][z]);
					pvy=Util.Ni(normal);	//�˴�pvy����Ϊ0����Ϊ��׼��̬�ֲ�С��-9�ĸ��ʾ��ǻ���Ϊ0�ˡ�
					if(pvy>NEARZERO) rst+=(postProb[u][i][z]*(Math.log(pvy)+Math.log(pz)));
					
					/*if(Double.isNaN(postProb[u][i][z]*(Math.log(pvy)+Math.log(pz)))){
						Util.p(rateMatrix[u][i]+"><"+mean[i][z]+"><"+deviation[i][z]+"><"+Math.sqrt(deviation[i][z])+"<");
						Util.p(normal);
						Util.p(pz+" --"+pvy+"......");
						Util.p("log: "+Math.log(pz)+" --"+Math.log(pvy)+"......");
					}*/
				}
				lost+=rst;
			}
		}
		Util.p("��Ȼ����ֵ��"+lost);
		return lost;
	}
	
	public static void main(String[] args) {
		DataSet d = new DataSet("E:\\�����ھ�����\\ml-100k\\u_time.base");
		Plsa plsa = new Plsa(d,35);
		plsa.trainModel();
		DataSet t = new DataSet("E:\\�����ھ�����\\ml-100k\\u_time.test");
		Evaluator e = new Evaluator();
		e.setDataSet(t);
		e.getMAEAndRMSE(plsa);
	}
	

}
