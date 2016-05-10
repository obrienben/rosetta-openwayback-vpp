package org.mdz.dldc.rosetta.viewer.wayback.urn.vpp;

import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.dps.sdk.delivery.AbstractViewerPreProcessor;
import java.text.ParseException;
import org.mdz.dldc.rosetta.viewer.wayback.urn.vpp.service.MetadataService;
import org.mdz.dldc.rosetta.viewer.wayback.urn.vpp.service.WaybackUrlService;

/**
 *
 * @author Marcus Bitzl <marcus.bitzl@bsb-muenchen.de>
 */
public class WaybackUrnVpp extends AbstractViewerPreProcessor {
  
  private final WaybackUrlService waybackUrlService;
  
  private final MetadataService metadataService;
  
  private String additionalParameters;

  public WaybackUrnVpp() {
    this.waybackUrlService = new WaybackUrlService();
    this.metadataService = new MetadataService();
  }
  
  @Override
  public void execute() throws Exception {
    DnxDocumentHelper documentHelper = new DnxDocumentHelper(getDnx());
    additionalParameters = createUrlPath(documentHelper.getWebHarvesting());
  }

  public String createUrlPath(DnxDocumentHelper.WebHarvesting webHarvesting) throws ParseException {
    String marker = getMarker();
    StringBuilder builder = new StringBuilder();
    builder.append(marker);
    builder.append(waybackUrlService.createUrlPath(webHarvesting.getPrimarySeedURL(), metadataService.parseHarvestDate(webHarvesting.getHarvestDate())));
    builder.append(marker);
    return builder.toString();
  }

  @Override
  public String getAdditionalParameters() {
    return additionalParameters;
  }

  public String getMarker() {
    return "###";
  }
  
}
