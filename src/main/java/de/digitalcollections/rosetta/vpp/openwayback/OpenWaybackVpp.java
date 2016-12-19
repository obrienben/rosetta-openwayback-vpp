package de.digitalcollections.rosetta.vpp.openwayback;

import com.exlibris.core.infra.common.exceptions.logging.ExLogger;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.dps.sdk.access.Access;
import com.exlibris.dps.sdk.delivery.AbstractViewerPreProcessor;
import com.exlibris.dps.sdk.deposit.IEParser;
import de.digitalcollections.rosetta.vpp.openwayback.service.MetadataService;
import de.digitalcollections.rosetta.vpp.openwayback.service.WaybackUrlService;
import gov.loc.mets.FileType;
import gov.loc.mets.MetsType;
//import org.apache.xmlbeans.XmlObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


public class OpenWaybackVpp extends AbstractViewerPreProcessor {

  private static final ExLogger logger = ExLogger.getExLogger(OpenWaybackVpp.class);

  private static final String DETAIL_KEY = "detail";
  
  private static final String DEFAULT_MARKER = "@";
  
  private final WaybackUrlService waybackUrlService;
  
  private final MetadataService metadataService;
  
  private String additionalParameters;

  private HashMap<String, String> filePaths;

  public OpenWaybackVpp() {
    this.waybackUrlService = new WaybackUrlService();
    this.metadataService = new MetadataService();
  }
  
  @Override
  public void execute() throws Exception {
    execute(new DnxDocumentHelper(getDnx()), getViewContext());
  }
  
  public void execute(DnxDocumentHelper documentHelper, Map<String, String> viewContext) throws ParseException {
    if (hasRequestedDetail(viewContext)) {

      extractHarvestFilePaths();

      additionalParameters = createUrlPath(documentHelper.getWebHarvesting(), viewContext);
    }
    else {
      additionalParameters = createOverviewQuery(documentHelper.getWebHarvesting());
    }    
  }

  public String createUrlPath(DnxDocumentHelper.WebHarvesting webHarvesting, Map<String, String> viewContext) throws ParseException {
    String marker = getMarker(viewContext);
    StringBuilder builder = new StringBuilder();
    builder.append(marker);
    builder.append(waybackUrlService.createDetailUrlPath(webHarvesting.getPrimarySeedURL(), metadataService.parseHarvestDate(webHarvesting.getHarvestDate())));
    builder.append(marker);
    return builder.toString();
  }

  @Override
  public String getAdditionalParameters() {
    return additionalParameters;
  }

  public String getMarker(Map<String, String> viewContext) {
    return viewContext.getOrDefault("marker", DEFAULT_MARKER);
  }

  public boolean hasRequestedDetail(Map<String, String> viewContext) {
    return viewContext.containsKey(DETAIL_KEY) && "true".equals(viewContext.get(DETAIL_KEY));
  }

  public String createOverviewQuery(DnxDocumentHelper.WebHarvesting webHarvesting) {
    return waybackUrlService.createOverviewQueryString(webHarvesting.getPrimarySeedURL());
  }

  /*
    Extract file paths for .warc and .arc files from Mets document.
   */
  public void extractHarvestFilePaths() {

    HashMap<String, String> paths = new HashMap<>();

    try {
      Access dps_access = getAccess();
      IEParser ieparser = dps_access.getIE(ieParentId, "", "");
      MetsType.FileSec fileSec = ieparser.getFileSec();
      MetsType.FileSec.FileGrp ieFiles = fileSec.getFileGrpArray(0);

      for(FileType fileType : ieFiles.getFileArray()){
        fileType.getMIMETYPE();
        FileType.FLocat location = fileType.getFLocatArray(0);
        String filePath = location.getHref();
        if(filePath.endsWith(".warc") || filePath.endsWith(".arc")){
          String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
          paths.put(fileName, filePath);
          logger.info("OpenWayback Pre-Processor Plugin - extracting path for: " + fileName);
        }
      }

    } catch (Exception e) {
      logger.error("Error in OpenWayback Pre-Processor Plugin - cannot get file paths", e, getPid());
    }

    if(paths.size() == 0){
      logger.error("Error in OpenWayback Pre-Processor Plugin - no file paths found");
    }

    filePaths = paths;
  }
  
}
