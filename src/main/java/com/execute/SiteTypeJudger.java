package com.execute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.init.State;
import com.pojo.Site;
import com.service.SiteServ;
import com.util.MathUtil;

/**
 * 给定一串数字，根据数字的变化规律将站点归为若干类中的一类
 * 
 * @author daniel
 *
 */
@Component
public class SiteTypeJudger {

//	0.8 0.6 0.5 0.4 0.1 0.0 0.1 0.6 4.9 3.5 3.3 2.8 2.6 3.9 4.8 4.5 3.6 2.9 0.9 1.0 0.6 0.5 0.1 0.5 38
//	0.8 0.6 0.5 0.4 0.1 0.0 0.1 0.6 4.9 3.5 3.3 2.8 2.6 3.9 4.8 4.5 3.6 2.9 0.9 1.0 0.6 0.5 0.1 0.5 38
//	0.8 0.6 0.5 0.4 0.1 0.0 0.1 0.6 4.9 3.5 3.3 2.8 2.6 3.9 4.8 4.5 3.6 2.9 0.9 1.0 0.6 0.5 0.1 0.5 38
//	5.3 5.0 4.9 6.0 6.0 5.5 6.0 5.6 2.3 1.5 1.3 1.0 0.8 1.0 0.0 0.0 0.4 2.3 2.0 3.0 3.5 4.0 4.5 4.6 38
//	1.6 1.7 0.8 1.2 0.0 1.6 1.7 2.1 1.7 0.8 1.5 2.1 1.3 2.8 6.2 4.8 6.5 5.5 4.5 3.5 3.1 2.3 1.7 0.6 38
//	
	
	@Autowired
	private SiteServ siteServ;
	
	@Autowired
	private SiteAnalyze analyze;
	@Autowired
	private SiteTypeJudger judger;
	public int start = 0;
	public int end = 23;

	public int MOUNTAIN_VALLLY_DIVIDE = 4;

	
	public static final int MOUNTAIN_ONE_TOP = 11;// 山峰类型,单峰
	public static final int MOUNTAIN_TWO_TOP = 12;// 山峰类型，双峰
	public static final int MOUNTAIN_THREE_TOP = 13;// 山峰类型，三峰及以上

	public static final int ERASE_LEFT = 41;// 山峰类型，三峰及以上
	public static final int ERASE_RIGHT = 42;// 山峰类型，三峰及以上
	public static final int ERASE_NONE = 43;// 山峰类型，三峰及以上

	public static final double ONE_TOP_SECOND_RATIO = 0.5;// 判断单峰是否有大于这个比例的亚峰存在
	public static final double ERASE_RATIO = 0.4;// 比较两个山峰时，用来抹除一个山峰的比例
	public static final double MERGE_RATIO_HIGH = 0.96;// 对于两个离的很近，把很近的一个占比很大的峰合并掉
	public static final double MERGE_RATIO_LOW = 0.9;// 对于两个离的很近，把很近的一个占比很大的峰合并掉
	public static final int MAX_ITER_ERASE = 5;// 比较两个山峰时，用来抹除一个山峰的比例

	public final int VALLY_STANDARD = 21;// 山谷类型
	
	public static final double BUMP_LIMIT_RATE = 0.5;// 计算7-19时隆起率超过这个比例算作隆起块

	public static void main(String[] args) {
		SiteTypeJudger judger = new SiteTypeJudger();
		double[] list = new double[] { 1.6, 1.7, 0.8, 1.2, 0.0, 1.6, 1.7, 2.1, 1.7, 0.8, 1.5, 2.1, 1.3, 2.8, 6.2, 4.8,
				6.5, 5.5, 4.5, 3.5, 3.1, 2.3, 1.7, 0.6 };
		judger.getBump(list);
//		judger.filterData(list);
//		int firstType = judger.mountainOrVally(list);
//		judger.mountainTops(list, firstType);
//
//		int[] tops = judger.getTops(list);
//		System.out.println(Arrays.toString(tops));
//
//		judger.eraseTops(list, tops);
//		System.out.println(Arrays.toString(tops));
	}
	
	public List<Integer> anayAllSites(List<Site> sites) {
		List<Integer> siteTypes=new ArrayList<>();
		int count=0;
		for(Site s:sites) {
			double[] list= analyze.analyzeSiteChange(s.getId());
			int type=judger.getSiteType(list);
			siteTypes.add(type);
			if(type==State.MOUNTAIN) {
				count++;
			}
		}
//		System.out.println("共有站点"+sites.size());
//		System.out.println("峰站点"+count);
		return siteTypes;
	}
	
	/**
	 * 获取7-20点的数据，然后对其淤积程度进行计算
	 * @param list
	 * @return bumpRate--double 时间跨度淤积率
	 * 		   bumpDegree--int(State.BUMP)根据时间跨度淤积率得出的淤积类型
	 * 		   maxBumpSpan--int[] 最大淤积的时间段
	 * 		   bumpDegree--淤积的程度
	 */
	public Map<String, Object> getBump(double[] list) {
		if(list.length!=24) {
			return null;
		}
		double[] dayList=Arrays.copyOfRange(list, 7,20);
		double max=dayList[0];
		double min=dayList[0];
		
		Map<String, Object> bumpMap=new HashMap<>();
		for(int i=0;i<dayList.length;i++) {
			if(dayList[i]>max) {
				max=dayList[i];
			}
			if(dayList[i]<min) {
				min=dayList[i];
			}
		}
		double maxMinLag=max-min;
		int maxBumpBegin=-1;
		int maxBumpEnd=-1;
		int bumpBegin=-1;
		int bumpEnd=-1;
		for(int i=0;i<dayList.length;i++) {
			if(((dayList[i]-min)/maxMinLag-BUMP_LIMIT_RATE)>0.0001) {
				if(bumpBegin==-1) {
					bumpBegin=i;
					bumpEnd=i;
				}else {
					bumpEnd++;
					if((i==dayList.length-1)&&(bumpEnd-bumpBegin)>(maxBumpEnd-maxBumpBegin)) {
						maxBumpBegin=bumpBegin;
						maxBumpEnd=bumpEnd;
					}
				}
			}else {
				if(maxBumpBegin==-1||(bumpEnd-bumpBegin)>(maxBumpEnd-maxBumpBegin)) {
					maxBumpBegin=bumpBegin;
					maxBumpEnd=bumpEnd;
					bumpBegin=-1;
					bumpEnd=-1;
				}
			}
		}
		int bumpSpan=maxBumpEnd-maxBumpBegin;
		double bumpRate=(double)bumpSpan/dayList.length;
		
		bumpMap.put("bumpRate", bumpRate);
		bumpMap.put("bumpDegree", maxMinLag);
		bumpMap.put("maxBumpSpan", new int[] {maxBumpBegin,maxBumpEnd});
	
		if(bumpSpan>0&&bumpSpan<=12) {
			//System.out.println(bumpRate+"--- "+maxBumpBegin+"---"+maxBumpEnd);
			if(bumpRate>=0.6) {
				bumpMap.put("bumpType", State.BUMP_BIG);
				//System.out.println("淤积类型---强");
			}else if(bumpRate>=0.2&&bumpRate<0.6){
				bumpMap.put("bumpType", State.BUMP_MIDDLE);
				//System.out.println("淤积类型---中");
			}else {
				bumpMap.put("bumpType", State.BUMP_SMALL);
				//System.out.println("淤积类型---弱");
			}
		}else {
			bumpMap.put("bumpType", State.BUMP_NONE);
			//System.out.println("非淤积类型");
		}
		
		return bumpMap;
	}
 
	public int getSiteType(double[] list) {
		int firstType = mountainOrVally(list);

		int secondtype = 0;
		if (firstType == State.VALLY) {
			//System.out.println("类型：谷");
			double[] turnTop = Arrays.copyOf(list, list.length);
			turnDownTops(turnTop);
			mountainTops(turnTop, firstType);

			int[] downs = getTops(turnTop);

			int downCount = countTops(downs);

			eraseTops(turnTop, downs);
			getVallyFeatures(downs);
			return firstType;
		} else {
			//System.out.println("类型：峰");
			mountainTops(list, firstType);

			int[] tops = getTops(list);

			int[] beforeErase = Arrays.copyOf(tops, tops.length);
			eraseTops(list, tops);
			mergeTops(list, tops);
			int topCount = countTops(tops);
			if (topCount == 1) {
				secondtype = MOUNTAIN_ONE_TOP;
				int second = getSecondSingle(beforeErase, tops, list);
				if (second != -1) {
					//System.out.println("存在亚峰" + second);
				}
				//System.out.println("单峰");
			} else if (topCount == 2) {
				secondtype = MOUNTAIN_TWO_TOP;
				//System.out.println("双峰");
			} else {
				secondtype = MOUNTAIN_THREE_TOP;
				//System.out.println("多峰");
			}
			//System.out.println(Arrays.toString(tops));
			return firstType;

		}
	}

	private int getSecondSingle(int[] beforeErase, int[] tops, double[] list) {
		double secondMax = 0;
		double max = 0;
		int maxIndex = 0;
		for (int i = 0; i < beforeErase.length; i++) {
			if (tops[i] == 1) {
				maxIndex = i;
				max = list[i];
			}
		}
		int secondIndex = -1;
		for (int i = 0; i < beforeErase.length; i++) {
			if (beforeErase[i] == 1 && Math.abs(maxIndex - i) >= 4 && list[i] > secondMax) {
				secondMax = list[i];
				if ((secondMax / max - 0.4) > 0.0001) {
					secondIndex = i;
				}

			}
		}
		return secondIndex;
	}

	private void mergeTops(double[] list, int[] tops) {
		double oneTop = 0;
		int maxIndex = 0;
		for (int i = 0; i < tops.length; i++) {
			if (tops[i] == 1) {
				oneTop = list[i];
				maxIndex = i;

				if (maxIndex >= 2 && maxIndex <= (tops.length - 3)) {
					int other = maxIndex - 2;
					if (tops[other] == 1) {
						double temp = list[other];
						double low = list[other + 1];
						if (temp <= oneTop) {
							double ratio = temp / oneTop;
							double lowratio = low / oneTop;
							if ((ratio - MERGE_RATIO_HIGH) > 0.00001 && (MERGE_RATIO_LOW - lowratio) > 0.00001) {
								tops[other] = 0;
							}
						}
					}
					other = maxIndex + 2;
					if (tops[other] == 1) {
						double temp = list[other];
						double low = list[other - 1];
						if (temp <= oneTop) {
							double ratio = temp / oneTop;
							double lowratio = low / oneTop;
							if ((ratio - MERGE_RATIO_HIGH) > 0.00001 && (lowratio - MERGE_RATIO_LOW) > 0.00001) {
								tops[other] = 0;
							}
						}
					}
				}

			}
		}

	}

	private void getVallyFeatures(int[] downs) {
		int leftCount = 0;
		for (int i = 7; i <= 11; i++) {
			if (downs[i] == 1) {
				leftCount++;
			}
		}
		int rightCount = 0;
		for (int i = 13; i <= 17; i++) {
			if (downs[i] == 1) {
				rightCount++;
			}
		}
		//System.out.println(leftCount + "---" + rightCount);
		if (leftCount == 1 && rightCount == 1) {
			//System.out.println("标准谷");
		}

	}

	public int mountainTops(double[] list, int type) {
		if (type != State.MOUNTAIN) {
			return -1;
		}

		return -1;
	}

	public int[] getTops(double[] list) {
		if (list.length < 3) {
			return null;
		}
		int[] tops = new int[list.length];
		for (int i = 1; i < list.length - 1; i++) {
			if (list[i] > list[i - 1] && list[i] > list[i + 1]) {
				tops[i] = 1;
			} else if (list[i] > list[i - 1] && list[i] == list[i + 1]) {
				tops[i] = 1;
				i++;
			} else if (list[i] == list[i - 1] && list[i] > list[i + 1]) {
				tops[i] = 1;
			}
		}
		return tops;
	}

	public int countTops(int[] list) {

		int count = 0;
		for (int i = 0; i < list.length; i++) {
			if (list[i] == 1) {
				count++;
			}
		}
		return count;
	}

	public void turnDownTops(double[] list) {
		double max = MathUtil.maxVal(list);
		for (int i = 0; i < list.length; i++) {
			list[i] = max - list[i];
		}
	}

	public int[] eraseTops(double[] list, int[] tops) {

		int iterCount = 0;
		// 每一轮过一遍，每轮把相邻的两个山峰之间比较，看是否需要进行擦除
		while (iterCount++ < MAX_ITER_ERASE) {
			int eraseCount = 0;
			int leftIndex = 0;
			int rightIndex = 0;
			// System.out.println("擦除山峰第 :"+iterCount);
			while (leftIndex < list.length) {
				double leftTop = -1;
				double rightTop = -1;
				// 从leftIndex---list.length之间找到最左侧的两个山峰
				for (int i = leftIndex; i < list.length; i++) {
					if (tops[i] == 1) {
						if (leftTop == -1) {
							leftTop = list[i];
							leftIndex = i;
						} else {
							if (rightTop == -1) {
								rightTop = list[i];
								rightIndex = i;
							} else {
								break;
							}
						}
					}
				}
				if (leftIndex == rightIndex || leftTop == -1 || rightTop == -1) {
					break;
				}
				// 判断对于两个山峰是否应该擦除一个
				int eraser = eraseWhichTop(list, tops, leftIndex, rightIndex);
				if (eraser == ERASE_LEFT) {
					tops[leftIndex] = 0;
					eraseCount++;
				} else if (eraser == ERASE_RIGHT) {
					tops[rightIndex] = 0;
					eraseCount++;
				}
				leftIndex = rightIndex;
				rightIndex = -1;
			}
			// System.out.println(Arrays.toString(tops));
			if (eraseCount == 0) {
				break;
			}
		}
		// System.out.println(Arrays.toString(tops));
		return tops;
	}

	private int eraseWhichTop(double[] list, int[] tops, int left, int right) {
		double min = list[left];
		for (int i = left; i <= right; i++) {
			if (list[i] < min) {
				min = list[i];
			}
		}
		double leftLag = list[left] - min;
		double rightLag = list[right] - min;
		double ratio = 0;
		if (leftLag > rightLag) {
			ratio = rightLag / leftLag;
			if (ratio < ERASE_RATIO) {
				return ERASE_RIGHT;
			}
		} else if (leftLag < rightLag) {
			ratio = leftLag / rightLag;
			if (ratio < ERASE_RATIO) {
				return ERASE_LEFT;
			}
		}
		return ERASE_NONE;
	}

	public void filterData(double[] list) {
		double[] filter = new double[end - start + 1];
		int count = 0;
		for (int i = start; i <= end; i++) {
			filter[count] = list[start];
		}
		list = filter;
	}

	public int mountainOrVally(double[] list) {
		if (MOUNTAIN_VALLLY_DIVIDE < 3) {
			return -1;
		}
		double[] divide = new double[MOUNTAIN_VALLLY_DIVIDE];
		int step = list.length / MOUNTAIN_VALLLY_DIVIDE;
		int count = 0;
		for (int i = 0; i < list.length; i += step) {

			double temp = 0;
			for (int j = i; j < list.length && j < i + step; j++) {
				temp += list[j];
			}
			temp = temp / step;
			divide[count++] = ((int) (temp * 100)) / 100.0;
		}
		//System.out.println(Arrays.toString(divide));
		double side = 0;
		double middle = 0;
		for (int i = 0; i < MOUNTAIN_VALLLY_DIVIDE; i++) {
			if (i == 0 || i == (MOUNTAIN_VALLLY_DIVIDE - 1)) {
				side += divide[i];
			} else {
				middle += divide[i];
			}
		}
		side = side / 2;
		middle = middle / (MOUNTAIN_VALLLY_DIVIDE - 2);
		if (middle > side) {
			return State.MOUNTAIN;
		} else if (middle < side) {
			return State.VALLY;
		} else {
			return State.FLAT;
		}
	}

	/**
	 * 判断作为双峰类型的得分
	 * 
	 * @param list
	 * @return
	 */
	public double getDoubleHigh(double[] list) {
		if (list.length != 24) {
			return 0;
		}
		int count = list.length;
		double[] sorted = Arrays.copyOf(list, count);

		//System.out.println(Arrays.toString(list));
		Arrays.sort(sorted);
		//System.out.println(Arrays.toString(sorted));

		double firstMax = sorted[count - 1];
		double secondMax = sorted[count - 2];
		//System.out.println(firstMax);
		//System.out.println(secondMax);

		return 0;

	}

}
