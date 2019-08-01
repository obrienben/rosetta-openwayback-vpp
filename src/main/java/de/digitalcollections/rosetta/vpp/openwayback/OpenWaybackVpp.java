package de.digitalcollections.rosetta.vpp.openwayback;

import com.exlibris.core.infra.common.exceptions.logging.ExLogger;
import com.exlibris.core.sdk.consts.*;
import com.exlibris.core.sdk.consts.Enum;
import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.dps.sdk.access.Access;
import com.exlibris.dps.sdk.delivery.AbstractViewerPreProcessor;
import com.exlibris.dps.sdk.deposit.IEParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.digitalcollections.rosetta.vpp.openwayback.service.HttpConnectionService;
import de.digitalcollections.rosetta.vpp.openwayback.service.MetadataService;
import de.digitalcollections.rosetta.vpp.openwayback.service.WaybackUrlService;
import gov.loc.mets.*;
import org.apache.xmlbeans.XmlObject;
//import org.apache.xmlbeans.XmlObject;

import java.lang.reflect.Type;
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

  private Map<String, String> filePaths;

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
    logger.info("OpenWayback Pre-Processor Plugin - URL built: " + builder.toString());
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

      for(FileType fileType : fileSecGrp.getFileArray()) {
        FileType.FLocat location = fileType.getFLocatArray(0);
        String filePath = location.getHref();
//        logger.info("OpenWayback VPP - found filepath: " + filePath);
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
    String responseText = "";

    try {
      responseText = resourceStoreConn.post(filePathsJSON);
    } catch (Exception e) {
      logger.error("OpenWayback Pre-Processor Plugin - cannot update resource store", e, getPid());
    }

    Type hashMapType = new TypeToken<HashMap<String, List<String>>>(){}.getType();
    HashMap<String, List<String>> results = new Gson().fromJson(responseText, hashMapType);

    List<String> success = results.get("success");
    List<String> fail = results.get("fail");

    // Check for any failures
    if(!results.get("fail").isEmpty()){
      logger.error("OpenWayback Pre-Processor Plugin - failed updates for " + getPid() + ": " + results.get("fail").toString());
    }

    // Check for mismatch
    if((results.get("success").size() + results.get("fail").size()) != filePaths.size()){
      logger.error("OpenWayback Pre-Processor Plugin - update mismatch for " + getPid());
    }


  }


  // Uncomment for testing Resource Store functionality
//  public static void main(String[] args) {
//
////    OpenWaybackVpp vpp = new OpenWaybackVpp();
//    Map<String, String> filePaths = new HashMap<>();
//    filePaths.put("NLNZ-TI92930263-20151108060042-00000-kaiwae-z4.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\NLNZ-TI92930263-20151108060042-00000-kaiwae-z4.warc");
//    filePaths.put("NLNZ-TI92930263-20151108060054-00001-kaiwae-z4.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\NLNZ-TI92930263-20151108060054-00001-kaiwae-z4.warc");
//    filePaths.put("NLNZ-TI92930263-20151108111900-00002-kaiwae-z4.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\NLNZ-TI92930263-20151108111900-00002-kaiwae-z4.warc");
//    filePaths.put("NLNZ-TI92930263-20151108112503-00003-kaiwae-z4.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\NLNZ-TI92930263-20151108112503-00003-kaiwae-z4.warc");
////    vpp.filePaths = test_data;
////    vpp.updateResourceStore();
//    final HttpConnectionService resourceStoreConn = new HttpConnectionService();
//
//    byte[] filePathsJSON = new Gson().toJson(filePaths).getBytes();
//    String responseText = "";
//
//    try {
//      responseText = resourceStoreConn.post(filePathsJSON);
//    } catch (Exception e) {
////      logger.error("OpenWayback Pre-Processor Plugin - cannot update resource store", e, getPid());
//    }
//
//    Type hashMapType = new TypeToken<HashMap<String, List<String>>>(){}.getType();
//    HashMap<String, List<String>> results = new Gson().fromJson(responseText, hashMapType);
//
//    List<String> success = results.get("success");
//    List<String> fail = results.get("fail");
//
//    // Check for any failures
//    if(!results.get("fail").isEmpty()){
////      logger.error("OpenWayback Pre-Processor Plugin - failed updates for " + getPid() + ": " + results.get("fail").toString());
//    }
//
//    // Check for mismatch
//    if((results.get("success").size() + results.get("fail").size()) != filePaths.size()){
////      logger.error("OpenWayback Pre-Processor Plugin - update mismatch for " + getPid());
//    }
//  }
}
