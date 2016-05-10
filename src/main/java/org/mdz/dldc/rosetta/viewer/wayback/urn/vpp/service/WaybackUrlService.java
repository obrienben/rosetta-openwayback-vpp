package org.mdz.dldc.rosetta.viewer.wayback.urn.vpp.service;

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
  
  public String createUrlPath(String seed, Date harvestDate) {
    return "/" + urlDateString(harvestDate) + "/" + seed;
  }
  
}
