package de.digitalcollections.rosetta.vpp.openwayback;

import com.exlibris.core.infra.common.exceptions.logging.ExLogger;
import com.exlibris.core.sdk.consts.*;
import com.exlibris.core.sdk.consts.Enum;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.dps.sdk.access.Access;
import com.exlibris.dps.sdk.delivery.AbstractViewerPreProcessor;
import com.exlibris.dps.sdk.deposit.IEParser;
import com.google.gson.Gson;
import de.digitalcollections.rosetta.vpp.openwayback.service.HttpConnectionService;
import de.digitalcollections.rosetta.vpp.openwayback.service.MetadataService;
import de.digitalcollections.rosetta.vpp.openwayback.service.WaybackUrlService;
import gov.loc.mets.*;
import org.apache.xmlbeans.XmlObject;
//import org.apache.xmlbeans.XmlObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OpenWaybackVpp extends AbstractViewerPreProcessor {

  private static final ExLogger logger = ExLogger.getExLogger(OpenWaybackVpp.class);

  private static final String DETAIL_KEY = "detail";
  
  private static final String DEFAULT_MARKER = "@";
  
  private final WaybackUrlService waybackUrlService;
  
  private final MetadataService metadataService;

  private final HttpConnectionService resourceStoreConn;
  
  private String additionalParameters;

  private HashMap<String, String> filePaths;

  public OpenWaybackVpp() {
    this.waybackUrlService = new WaybackUrlService();
    this.metadataService = new MetadataService();
    this.resourceStoreConn = new HttpConnectionService();
  }
  
  @Override
  public void execute() throws Exception {
    execute(new DnxDocumentHelper(getDnx()), getViewContext());
  }
  
  public void execute(DnxDocumentHelper documentHelper, Map<String, String> viewContext) throws ParseException {
    if (hasRequestedDetail(viewContext)) {

      extractHarvestFilePaths();

      updateResourceStore();

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
      IEParser ieparserFull = dps_access.getExtendedIeByDvs(dvs, (long) 16);
      MetsType.FileSec fileSec = ieparserFull.getFileSec();
      MetsType.FileSec.FileGrp fileSecGrp = fileSec.getFileGrpArray(0);
//      logger.info("OpenWayback VPP - fileSecGrp sizeOfFileArray: " + fileSecGrp.sizeOfFileArray());
//      logger.info("OpenWayback VPP - ieparserFull xml: " + ieparserFull.toXML());

      for(FileType fileType : fileSecGrp.getFileArray()) {
        FileType.FLocat location = fileType.getFLocatArray(0);
        String filePath = location.getHref();
        logger.info("OpenWayback VPP - found filepath: " + filePath);
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

  public void updateResourceStore() {

    byte[] filePathsJSON = new Gson().toJson(filePaths).getBytes();

    try {
      resourceStoreConn.post(filePathsJSON);
    } catch (Exception e) {
      logger.error("Error in OpenWayback Pre-Processor Plugin - cannot update resource store", e, getPid());
    }

  }

}
