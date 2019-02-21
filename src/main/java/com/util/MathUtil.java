package com.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MathUtil {

	public static int getListMax(List<Integer> list) {
		int max = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) > max) {
				max = list.get(i);
			}
		}
		return max;

	}
	
	public static double minVal(double[] list) {
		if(list.length==0) {
			return 0;
		}
		double min=list[0];
		for(int i=0;i<list.length;i++) {
			if(list[i]<min) {
				min=list[i];
			}
		}
		return min;
	}
	
	public static double maxVal(double[] list) {
		if(list.length==0) {
			return 0;
		}
		double max=list[0];
		for(int i=0;i<list.length;i++) {
			if(list[i]>max) {
				max=list[i];
			}
		}
		return max;
	}

	/**
	 * 对两个数组进行归一化评分，数组必须数量相等　
	 * boolean 为正，数值越大越好，boolean为负，数值越小越好
	 * @param countList
	 * @param distList
	 * @param big1 代表countList
	 * @param big2 代表distList
	 * @param ratio1
	 * @return
	 */
	public static double[] calcuMaxScore(List<Integer> countList, List<Integer> distList, boolean big1, boolean big2,
			double ratio1) {
		if(countList.size()==0||countList.size()!=distList.size()) {
			return null;
		}
		int maxCount = countList.get(0);
		int maxDist = distList.get(0);
		int minCount = countList.get(0);
		int minDist = distList.get(0);
		for (int i = 0; i < countList.size(); i++) {
			if (countList.get(i) > maxCount) {
				maxCount = countList.get(i);
			}
			if (distList.get(i) > maxDist) {
				maxDist = distList.get(i);
			}
			if (countList.get(i) < minCount) {
				minCount = countList.get(i);
			}
			if (distList.get(i) < minDist) {
				minDist = distList.get(i);
			}
		}

		List<Double> scoreList=new ArrayList<>();
		for (int i = 0; i < countList.size(); i++) {
			double score1 = 0;
			double score2 = 0;
			if (big1) {
				score1 = (double) (countList.get(i) - minCount) / (maxCount - minCount);
			} else {
				score1 = 1 - (double) (countList.get(i) - minCount) / (maxCount - minCount);
			}
			if (big2) {
				score2 = (double) (distList.get(i) - minDist) / (maxDist - minDist);
			} else {
				score2 = 1 - (double) (distList.get(i) - minDist) / (maxDist - minDist);
			}

			double score = score1 * ratio1 + score2 * (1 - ratio1);
			scoreList.add(score);
		}

		int maxIndex = 0;
		double maxScore = 0;
		double temp=0;
		for(int i=0;i<scoreList.size();i++) {
			temp=scoreList.get(i);
			if (temp > maxScore) {
				maxIndex = i;
				maxScore = temp;
			}
		}
		return new double[] {maxIndex,maxScore};
	}
	
	
	public List<Integer> getItemInOneNotTwo(List<Integer> l1,List<Integer> l2){
		l1.sort(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return Integer.compare(o1, o2);
			}
		});
		l2.sort(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				// TODO Auto-generated method stub
				return Integer.compare(o1, o2);
			}
		});
		int jCount=0;
		List<Integer> result=new ArrayList<>();
		for(int i=0;i<l1.size();i++) {
			int item=l1.get(i);
			for(int j=jCount;j<l2.size();j++) {
				if(l2.get(j)>item) {
					result.add(item);
					jCount=j;
					break;
				}else if(l2.get(j)==item) {
					j++;
					jCount=j;
					break;
				}
			}
			if(jCount==l2.size()) {
				for(int k=i+1;k<l1.size();k++) {
					result.add(l1.get(k));
				}
				break;
			}
		}
		return result;
	}
	public static void main(String[] args) {
		List<Integer> l1 = new ArrayList<>();
		l1.add(1);
		l1.add(4);
		l1.add(8);
		List<Integer> l2 = new ArrayList<>();
		l2.add(9);
		l2.add(1);
		l2.add(3);
		MathUtil mathUtil = new MathUtil();

		System.out.println(mathUtil.calcuMaxScore(l1, l2,true,false, 0.1));

	}

}
