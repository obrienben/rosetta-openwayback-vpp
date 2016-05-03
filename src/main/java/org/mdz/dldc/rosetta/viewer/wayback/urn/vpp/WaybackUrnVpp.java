package org.mdz.dldc.rosetta.viewer.wayback.urn.vpp;

import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.dps.sdk.delivery.AbstractViewerPreProcessor;
import java.text.ParseException;
import org.mdz.dldc.rosetta.viewer.wayback.urn.vpp.service.WaybackUrlService;

/**
 *
 * @author Marcus Bitzl <marcus.bitzl@bsb-muenchen.de>
 */
public class WaybackUrnVpp extends AbstractViewerPreProcessor {
  
  private final WaybackUrlService service;
  
  private String additionalParameters;

  public WaybackUrnVpp() {
    this.service = new WaybackUrlService();
  }
  
  @Override
  public void execute() throws Exception {
    DnxDocumentHelper documentHelper = new DnxDocumentHelper(getDnx());
    additionalParameters = createUrlPath(documentHelper.getWebHarvesting());  
  }

  public String createUrlPath(DnxDocumentHelper.WebHarvesting webHarvesting) throws ParseException {
    return service.createUrlPath(webHarvesting.getPrimarySeedURL(), service.parseHarvestDate(webHarvesting.getHarvestDate()));
  }

  @Override
  public String getAdditionalParameters() {
    return additionalParameters;
  }
  
}
