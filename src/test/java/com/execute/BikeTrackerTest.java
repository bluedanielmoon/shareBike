package com.execute;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.BikePos;
import com.pojo.LnglatTime;
import com.util.FilesUtil;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class BikeTrackerTest {
	
	@Autowired
	BikeTracker tracker;

	@Test
	public void testProduceBikeTackFile() {
		String latest=FilesUtil.checkLastestFile();
		Map<String, Object> latestBikes=FilesUtil.readFileToBikeMap(latest);
		List<BikePos> bikes=(List<BikePos>) latestBikes.get("bikes");
		tracker.trackBikesInSite(3, bikes);
	}

	@Test
	public void testTrackBikesInSite() {
		String latest=FilesUtil.checkLastestFile();
		Map<String, Object> latestBikes=FilesUtil.readFileToBikeMap(latest);
		List<BikePos> bikes=(List<BikePos>) latestBikes.get("bikes");
		int siteID=3;
		Map<String, Object> data=tracker.trackBikesInSite(siteID, bikes);
		Map<String, List<LnglatTime>> tracks=(Map<String, List<LnglatTime>>) data.get("tracks");
		
		for(String s:tracks.keySet()) {
			System.out.println(s+"---"+tracks.get(s));
			tracker.anaylyzeTrack(tracks.get(s));

		}
	}

}
