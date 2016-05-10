package org.mdz.dldc.rosetta.viewer.wayback.urn.vpp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Marcus Bitzl <marcus.bitzl@bsb-muenchen.de>
 */
public class MetadataService {

  private static final SimpleDateFormat harvestDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
  public Date parseHarvestDate(String source) throws ParseException {
    return harvestDateFormat.parse(source);
  }
  
}
