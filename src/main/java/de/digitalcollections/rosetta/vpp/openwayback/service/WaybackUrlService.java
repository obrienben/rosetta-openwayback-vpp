package de.digitalcollections.rosetta.vpp.openwayback.service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Prepare data for OpenWayback.
 * 
 */
public class WaybackUrlService {
    
  private static final SimpleDateFormat waybackDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  
  public String urlDateString(Date date) {
    return waybackDateFormat.format(date);
  } 
  
  public String createDetailUrlPath(String seed, Date harvestDate) {
    if(seed.contains(" ")){
      String[] seeds = seed.split("\\s+");
      if(seeds.length > 0){
        // Only take first seed
        seed = seeds[0];
      }
    }

    if(seed.contains("?")){
        seed = seed.replace("?", "[q-mark]");
    }

    return "/" + urlDateString(harvestDate) + "/" + seed;
  }
  
  public String createOverviewQueryString(String seed) {
    return "url=" + seed;
  }

}
