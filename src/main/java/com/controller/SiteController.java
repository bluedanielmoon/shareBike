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

import com.execute.SiteAnalyze;
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
	
	
	
	@GetMapping(value = "/all")
	@ResponseBody
	public List<Site> getAll() {
		return siteServ.getAllSites();
	}
	
	@GetMapping(value = "/score")
	@ResponseBody
	public Map<Double, Lnglat> getSiteScore(@RequestParam int rate,@RequestParam double flucSca,@RequestParam double countSca,
			@RequestParam double poiSca) {
		
		return siteServ.getScores(rate, flucSca, countSca, poiSca);
	}
	
	@GetMapping(value = "/cluster")
	@ResponseBody
	public List<Point> getClusterScore(@RequestParam int rate,@RequestParam double flucSca,@RequestParam double countSca,
			@RequestParam double poiSca,@RequestParam int clusterDist) {
		return siteServ.mergeSites(rate, flucSca, countSca, poiSca, clusterDist);
	}
	
	@GetMapping(value = "/submit")
	@ResponseBody
	public boolean submitSites(@RequestParam int rate,@RequestParam double flucSca,@RequestParam double countSca,
			@RequestParam double poiSca,@RequestParam int clusterDist) {
		return siteServ.writeBase(rate, flucSca, countSca, poiSca, clusterDist);
		
	}
	
	@GetMapping(value = "/change")
	@ResponseBody
	public double[] getSite(@RequestParam int siteID) {
		
		return siteServ.getSiteChange(siteID);
		
	}
	
	@GetMapping(value = "/update")
	@ResponseBody
	public boolean updateSitePos(@RequestParam int id,@RequestParam double lng,@RequestParam double lat) {
		return siteServ.updateSite(id,lng,lat);
	}
	
	@GetMapping(value = "/updateInfo")
	@ResponseBody
	public boolean updateSiteInfo(@RequestParam int id,@RequestParam String siteName,@RequestParam int siteLimit,
			@RequestParam int siteType) {
		return siteServ.updateSite(id,siteName,siteLimit,siteType);
	}

	@GetMapping(value = "/add")
	@ResponseBody
	public boolean getSite(@RequestParam String name,@RequestParam int volume,@RequestParam int type,
			@RequestParam double lng,@RequestParam  double lat) {
		return siteServ.addSite(name, volume, type, lng, lat);
	}

	@PostMapping(value = "/delete")
	@ResponseBody
	public boolean postSite(@RequestParam List<Integer> ids) {
		return siteServ.patchDeleteSites(ids);
	}


}