package com.cr7.evaluate;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.cr7.dataSet.DataSet;
import com.cr7.model.Model;
import com.cr7.util.Util;

public class Evaluator {
	private Map<Pair,Byte> scoreMap;    //actual rating scores
	private static final int USERNUM=943;
	private int testNum;
	private int testUserNum=943;
	public Evaluator(DataSet d){
		this.setDataSet(d);
	}
	public void setDataSet(DataSet d){
		UIMap mp = new UIMap(d);
		scoreMap = mp.getUI();
		testNum = scoreMap.size(); 
		testUserNum = d.testUserNum;
Util.p(scoreMap.size());
	}
	
	public  void testR(Model m ,int u){
		m.getTopN(u, 1);
		m.getAll(1);
		/*TreeMap<Integer,Integer> recomm = m.getAll(1);	//<δ������Ŀid,����>
		Iterator<Integer> itr = recomm.keySet().iterator();
		while(itr.hasNext()){
			int id = (Integer)itr.next();
			Util.p(id+" :"+recomm.get(id));
		}*/
	}
	
	public double getR(Model m){
		//ע���û�����С��3��itemҲ����getALL����Ŀ�б����档
		double r = 0;
		for(int u=1;u<=testUserNum;u++){
			double ur = 0;
//			Util.p(u+"*********************");
			TreeMap<Integer,Integer> recomm = m.getAll(u);	//<δ������Ŀid,����>
			int size = recomm.size();
			Iterator<Pair> itr = scoreMap.keySet().iterator();
			while(itr.hasNext()){
				int loca = 0;
				Pair p = (Pair)itr.next();
				if(p.u==u){			//u�û�����������
									//���Լ��е���Ŀһ�����Ƽ���Ŀ�����Բ����ж�recomm�Ƿ���I
					if(recomm.get(p.i)==null){
//						Util.p(p.i);
					}
					else{
						loca = recomm.get(p.i);
						ur+=((double)loca/size);
					}
				}
//				Util.p("ur:"+ur);
			}
			r+=ur;
//			Util.p("r:"+r);
		}
		r/=testUserNum;
		Util.p("average r:"+r);
		return r;
	}
	
	
	public double getPrecision(Model m,int n){
		Util.p("testUserNum"+testUserNum);
		/**
		 * �����ʣ���ȷ�Ƽ���Ŀ/�Ƽ�����
		 */
		int hits=0;
		for(int u=1;u<=testUserNum;u++){
			int [] recom = m.getTopN(u, n);
//Util.p(u+"*********************");Util.printArray(recom);
			Iterator<Pair> itr = scoreMap.keySet().iterator();
			while(itr.hasNext()){
				Pair p = (Pair)itr.next();
				if(p.u==u){
					int i = p.i;
					if(Util.exists(recom,i))hits++;
				}
			}
if(u%123==0  || u>938 ) Util.p("\n"+u+"����: "+hits);	//if(u>500)
		}
		return ((double)hits/(n*testUserNum));
	}
	
	public double getHitRate(Model m,int n){
		/**
		 * �����ʣ���ȷ�Ƽ���Ŀ/�û�ʵ��������Ŀ
		 */
		int hits=0;
		for(int u=1;u<=USERNUM;u++){
			int [] recom = m.getTopN(u, n);
//Util.p(u+"*********************");Util.printArray(recom);
			Iterator<Pair> itr = scoreMap.keySet().iterator();
			while(itr.hasNext()){
				Pair p = (Pair)itr.next();
				if(p.u==u){
					int i = p.i;
					if(Util.exists(recom,i))hits++;
				}
			}
if(u%123==0) Util.p("\n"+u+"����: "+hits);	//if(u>500)
		}
		return ((double)hits/testNum);
	}
	
}
