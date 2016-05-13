package de.digitalcollections.rosetta.vpp.openwayback.service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Marcus Bitzl <marcus.bitzl@bsb-muenchen.de>
 */
public class WaybackUrlService {
    
  private static final SimpleDateFormat waybackDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  
  public String urlDateString(Date date) {
    return waybackDateFormat.format(date);
  } 
  
  public String createDetailUrlPath(String seed, Date harvestDate) {
    return "/" + urlDateString(harvestDate) + "/" + seed;
  }
  
  public String createOverviewQueryString(String seed) {
    return "url=" + seed;
  }
  
}
