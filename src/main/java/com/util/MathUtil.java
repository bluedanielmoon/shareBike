package com.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
		if (list.length == 0) {
			return 0;
		}
		double min = list[0];
		for (int i = 0; i < list.length; i++) {
			if (list[i] < min) {
				min = list[i];
			}
		}
		return min;
	}

	public static double maxVal(double[] list) {
		if (list.length == 0) {
			return 0;
		}
		double max = list[0];
		for (int i = 0; i < list.length; i++) {
			if (list[i] > max) {
				max = list[i];
			}
		}
		return max;
	}

	public static double[] calcuMaxSco(List<Integer> countList, List<Integer> distList, boolean big1, boolean big2,
			double ratio1,double bestRatio) {
		if (countList.size() == 0 || countList.size() != distList.size()) {
			return null;
		}
		int count = countList.size();
		double[][] scoreIndex = new double[count][2];
		
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
		
		for (int i = 0; i < count; i++) {
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
			System.out.println(score1+"=="+score2);
			System.out.println();
			scoreIndex[i][0] = score;
			scoreIndex[i][1] = i;
		}
		for(double[] dd:scoreIndex) {
			System.out.println(dd[0]+"--"+dd[1]);
		}
		System.out.println("++++++");
		quickSort(scoreIndex);
		for(double[] dd:scoreIndex) {
			System.out.println(dd[0]+"--"+dd[1]);
		}
		System.out.println("!!!!!");
		double[] choose=chooseByDescentRatio(scoreIndex,bestRatio);
		System.out.println("----------");
		System.out.println(Arrays.toString(choose));
		int chooseIndex=(int) choose[1];
		double[] finalAnswer=new double[] {chooseIndex,scoreIndex[chooseIndex][0]};
		System.out.println(Arrays.toString(finalAnswer));
		return finalAnswer;
	}
	private static double[] chooseByDescentRatio(double[][] scoreIndex,double initRatio) {
		double[] choose=new double[2];
		int count=scoreIndex.length-1;
		Random random=new Random(System.currentTimeMillis());
		if (initRatio>1||initRatio<0) {
			choose[0]=scoreIndex[count][0];
			choose[1]=scoreIndex[count][1];
			return choose;
		}
		if (initRatio==1) {
			choose[0]=scoreIndex[count][0];
			choose[1]=scoreIndex[count][1];
		}else if (initRatio==0) {
			choose[0]=scoreIndex[0][0];
			choose[1]=scoreIndex[0][1];
		}else {
			
			for(int i=scoreIndex.length-1;i>=0;i--) {
				
				double randSeed=random.nextDouble();
				if (i==0) {
					choose[0]=scoreIndex[i][0];
					choose[1]=scoreIndex[i][1];
					return choose;
				}
				//如果大于生成数，就选择，否则，继续
				if (initRatio-randSeed>0.0001) {
					choose[0]=scoreIndex[i][0];
					choose[1]=scoreIndex[i][1];
					return choose;
				}else {
					initRatio=initRatio*initRatio;
					continue;
				}
			}
		}
		return choose;
	}
	/**
	 * 从小到大排列，大的在后面
	 * @param numbers
	 */
	public static void quickSort(double[][] numbers) {
		sortOrder(numbers, 0, numbers.length - 1);
	}

	private static void sortOrder(double[][] numbers, int start, int end) {
		if (start < end) {
			double base = numbers[start][0];
			double temp;
			double tempIndex;
			int i = start, j = end;
			do {
				while ((numbers[i][0] < base) && (i < end))
					i++;
				while ((numbers[j][0] > base) && (j > start))
					j--;
				if (i <= j) {
					temp = numbers[i][0];
					tempIndex = numbers[i][1];
					numbers[i][0] = numbers[j][0];
					numbers[i][1] = numbers[j][1];
					numbers[j][0] = temp;
					numbers[j][1] = tempIndex;
					i++;
					j--;
				}
			} while (i <= j);
			if (start < j)
				sortOrder(numbers, start, j);
			if (end > i)
				sortOrder(numbers, i, end);
		}
	}

	/**
	 * 对两个数组进行归一化评分，数组必须数量相等 boolean 为正，数值越大越好，boolean为负，数值越小越好
	 * 
	 * @param countList
	 * @param distList
	 * @param big1      代表countList
	 * @param big2      代表distList
	 * @param ratio1
	 * @return
	 */
	public static double[] calcuxScore(List<Integer> countList, List<Integer> distList, boolean big1, boolean big2,
			double ratio1) {
		if (countList.size() == 0 || countList.size() != distList.size()) {
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

		List<Double> scoreList = new ArrayList<>();
		for (int i = 0; i < countList.size(); i++) {
			double score1 = 0;
			double score2 = 0;
			if (maxCount==minCount) {
				score1=1;
			}else {
				if (big1) {
					score1 = (double) (countList.get(i) - minCount) / (maxCount - minCount);
				} else {
					score1 = 1 - (double) (countList.get(i) - minCount) / (maxCount - minCount);
				}
			}
			if (maxDist==minDist) {
				score2=1;
			}else {
				if (big2) {
					score2 = (double) (distList.get(i) - minDist) / (maxDist - minDist);
				} else {
					score2 = 1 - (double) (distList.get(i) - minDist) / (maxDist - minDist);
				}
			}
			
			double score = score1 * ratio1 + score2 * (1 - ratio1);
			scoreList.add(score);
		}

		int maxIndex = 0;
		double maxScore = 0;
		double temp = 0;
		for (int i = 0; i < scoreList.size(); i++) {
			temp = scoreList.get(i);
			if (temp > maxScore) {
				maxIndex = i;
				maxScore = temp;
			}
		}
		return new double[] { maxIndex, maxScore };
	}

	public List<Integer> getItemInOneNotTwo(List<Integer> l1, List<Integer> l2) {
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
		int jCount = 0;
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < l1.size(); i++) {
			int item = l1.get(i);
			for (int j = jCount; j < l2.size(); j++) {
				if (l2.get(j) > item) {
					result.add(item);
					jCount = j;
					break;
				} else if (l2.get(j) == item) {
					j++;
					jCount = j;
					break;
				}
			}
			if (jCount == l2.size()) {
				for (int k = i + 1; k < l1.size(); k++) {
					result.add(l1.get(k));
				}
				break;
			}
		}
		return result;
	}
	
	private static void randomSelect() {
		Random random=new Random(System.currentTimeMillis());
		System.out.println(random.nextDouble());
	}

	public static void main(String[] args) {
		List<Integer> l1 = new ArrayList<>();
		l1.add(1);
		l1.add(4);
		l1.add(8);
		List<Integer> l2 = new ArrayList<>();
		l2.add(9);
		l2.add(4);
		l2.add(3);
		MathUtil mathUtil = new MathUtil();

//		double[] x=calcuMaxScore2(l1, l2,true,true, 0.7,0.1);
//		System.out.println(l1);
//		System.out.println(l2);
//		System.out.println(Arrays.toString(x));
		
		
//		double[][] xx = new double[][] { { 3.2, 1.0 }, { 2.8, 2.0 }, { 2.8, 7.0 }, { 4.2, 3.0 }, { 10.2, 5.0 }, { 1.8, 4.0 },
//				{ 4.0, 6.0 } };
//		quickSort(xx);
//		for (double[] dd : xx) {
//			System.out.println(dd[0] + "  " + dd[1]);
//		}
//		System.out.println("----");
//		
//		for(int i=0;i<10;i++) {
//			double[] result=chooseByDescentRatio(xx,0.5);
//			System.out.println(result[0]+"  "+result[1]);
//		}
		
//		randomSelect();
	}

}
