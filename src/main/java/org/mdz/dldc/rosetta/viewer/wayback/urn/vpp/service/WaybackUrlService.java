package org.mdz.dldc.rosetta.viewer.wayback.urn.vpp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Marcus Bitzl <marcus.bitzl@bsb-muenchen.de>
 */
public class WaybackUrlService {
  
  private static final SimpleDateFormat harvestDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  
  private static final SimpleDateFormat waybackDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
  
  public Date parseHarvestDate(String source) throws ParseException {
    return harvestDateFormat.parse(source);
  }
  
  public String urlDateString(Date date) {
    return waybackDateFormat.format(date);
  } 
  
  public String createUrlPath(String seed, Date harvestDate) {
    return "/" + urlDateString(harvestDate) + "/" + seed;
  }
  
}
