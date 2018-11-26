package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.execute.SiteChooser;
import com.execute.SiteChooser.MaxScore;
import com.pojo.Lnglat;
import com.pojo.Point;
import com.pojo.Site;
import com.service.SiteServ;

/**
 * 列举所有的controller的标准形式
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/site")
public class SiteController {
	
	@Autowired
	private SiteServ siteServ;
	
	@Autowired
	private SiteChooser SiteChooser;
	
	@GetMapping(value = "/all")
	@ResponseBody
	public List<Site> getAll() {
		return siteServ.getAllSites();
	}
	
	@GetMapping(value = "/score")
	@ResponseBody
	public Map<Double, Lnglat> getSiteScore(@RequestParam int rate,@RequestParam double flucSca,@RequestParam double countSca,
			@RequestParam double poiSca) {
		
		MaxScore maxScore=SiteChooser.new MaxScore();
		Map<String, Map<String, Object>> totalScore =SiteChooser.judgeScore(maxScore,flucSca,countSca,poiSca);
		
		return SiteChooser.chooseSite(totalScore,maxScore,rate,true);
	}
	
	@GetMapping(value = "/cluster")
	@ResponseBody
	public List<Point> getClusterScore(@RequestParam int rate,@RequestParam double flucSca,@RequestParam double countSca,
			@RequestParam double poiSca) {
		
		int divideDist=50;
		int minDist=200;
		MaxScore maxScore=SiteChooser.new MaxScore();
		Map<String, Map<String, Object>> totalScore =SiteChooser.judgeScore(maxScore,flucSca,countSca,poiSca);
		
		Map<Double, Lnglat> choosed=SiteChooser.chooseSite(totalScore,maxScore,rate,false);
		
		return SiteChooser.mergeSites(choosed, minDist, divideDist);
	}
	
	@GetMapping(value = "/submit")
	@ResponseBody
	public boolean submitSites(@RequestParam int rate,@RequestParam double flucSca,@RequestParam double countSca,
			@RequestParam double poiSca) {
		
		int divideDist=50;
		int minDist=200;
		MaxScore maxScore=SiteChooser.new MaxScore();
		Map<String, Map<String, Object>> totalScore =SiteChooser.judgeScore(maxScore,flucSca,countSca,poiSca);
		
		Map<Double, Lnglat> choosed=SiteChooser.chooseSite(totalScore,maxScore,rate,false);
		List<Point> sites=SiteChooser.mergeSites(choosed, minDist, divideDist);
		
		return SiteChooser.writeToDatabase(sites);
	}

	@GetMapping(value = "/add")
	@ResponseBody
	public boolean getSite(@RequestParam String name,@RequestParam int volume,@RequestParam int type,
			@RequestParam double lng,@RequestParam  double lat) {
		return siteServ.addSite(name, volume, type, lng, lat);
	}

	@PostMapping(value = "/delete")
	@ResponseBody
	public boolean postSite(@RequestParam List<String> names) {
		return siteServ.patchDeleteSites(names);
	}


}