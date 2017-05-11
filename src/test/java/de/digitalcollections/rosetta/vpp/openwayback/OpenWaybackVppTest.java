package de.digitalcollections.rosetta.vpp.openwayback;

import com.exlibris.digitool.common.dnx.DnxDocumentHelper;
import com.exlibris.dps.sdk.access.AccessException;
import java.text.ParseException;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import com.exlibris.dps.sdk.delivery.AbstractViewerPreProcessor;
import com.google.gson.Gson;
import de.digitalcollections.rosetta.vpp.openwayback.service.HttpConnectionService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class OpenWaybackVppTest {
  
  private OpenWaybackVpp vpp;
  
  private DnxDocumentHelper.WebHarvesting webHarvesting;
  private HttpConnectionService resourceStoreConn;
  
  @Before
  public void setUp() throws AccessException {
    vpp = spy(new OpenWaybackVpp());
    resourceStoreConn = new HttpConnectionService();
    webHarvesting = mock(DnxDocumentHelper.WebHarvesting.class);
    when(webHarvesting.getHarvestDate()).thenReturn("12/03/2014 13:57:04");
    when(webHarvesting.getPrimarySeedURL()).thenReturn("http://wwww.bahn.de");
//    when(resourceStoreConn.post(new byte[])).thenReturn("http://wwww.bahn.de");
    doNothing().when(vpp).extractHarvestFilePaths();
    doNothing().when(vpp).updateResourceStore();
  }

  @Test
  public void createUrlPathResultShouldStartAndEndWithMarkers() throws ParseException {
    String marker = vpp.getMarker(emptyMap());
    String path = vpp.createUrlPath(webHarvesting, emptyMap());
    assertThat(path, allOf(startsWith(marker), endsWith(marker)));
  }

  @Test
  public void createUrlPathResultShouldContainUrlPath() throws ParseException {
    String path = vpp.createUrlPath(webHarvesting, emptyMap());
    assertThat(path, containsString("/20140312135704/http://wwww.bahn.de")); 
  }

  @Test
  public void overviewQueryShouldSetQuery() throws ParseException {
    String query = vpp.createOverviewQuery(webHarvesting);
    assertThat(query, is("url=http://wwww.bahn.de")); 
  }  
  @Test
  public void hasRequestedDetailShouldReturnTrueIfDetailIsRequested() {
    Map<String, String> viewContext = new HashMap<>();
    viewContext.put("detail", "true");
    assertThat(vpp.hasRequestedDetail(viewContext), is(true));
  }
  
  @Test
  public void hasRequestedDetailShouldReturnFalseIfDetailIsNotRequested() {
    Map<String, String> viewContext = new HashMap<>();
    viewContext.put("detail", "false");
    assertThat(vpp.hasRequestedDetail(viewContext), is(false));
  }
      
  @Test
  public void hasRequestedDetailShouldReturnFalseIfDetailIsNotSpecified() {
    assertThat(vpp.hasRequestedDetail(emptyMap()), is(false));
  }
  
  @Test
  public void executeShouldRunDetailIfRequested() throws ParseException {
    DnxDocumentHelper documentHelper = mock(DnxDocumentHelper.class);
    when(documentHelper.getWebHarvesting()).thenReturn(webHarvesting);
    
    Map<String, String> viewContext = new HashMap<>();
    viewContext.put("detail", "true");
    vpp.execute(documentHelper, viewContext);
    assertThat(vpp.getAdditionalParameters(), containsString("20140312135704"));
  }
  
  @Test
  public void executeShouldRunOverviewIfDetailIsNotRequested() throws ParseException {
    DnxDocumentHelper documentHelper = mock(DnxDocumentHelper.class);
    when(documentHelper.getWebHarvesting()).thenReturn(webHarvesting);
    vpp.execute(documentHelper, emptyMap());
    assertThat(vpp.getAdditionalParameters(), containsString("url"));
  }
  
  @Test
  public void getMarkerShouldBeConfigurable() {
    Map<String, String> viewContext = new HashMap<>();
    viewContext.put("marker", "ABC");
    assertThat(vpp.getMarker(viewContext), is("ABC"));
  }
  
  @Test
  public void getMarkerShouldHaveDefaultValue() {
    assertThat(vpp.getMarker(emptyMap()), is("@"));
  }

  @Ignore("Only needed when testing against a running Resource Store")
  @Test
  public void postFilePathsToResourceStore() throws ParseException {
    HashMap<String, String> filePathsTest = new HashMap<>();
    filePathsTest.put("V1-FL888888.warc", "/vendor/some_storage/doc_02/file_1/V1-FL888888.warc");
    filePathsTest.put("V1-FL777777.warc", "/vendor/some_storage/doc_02/file_1/V1-FL777777.warc");
    filePathsTest.put("V1-FL666666.warc", "/vendor/some_storage/doc_02/file_1/V1-FL666666.warc");
    filePathsTest.put("V1-FL555555.warc", "/vendor/some_storage/doc_02/file_1/V1-FL555555.warc");
    byte[] filePathsJSON = new Gson().toJson(filePathsTest).getBytes();
    String result = null;
    try {
      result = resourceStoreConn.post(filePathsJSON);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //TODO write assertion for result
    assertThat(result, containsString("20140312135704"));
  }

  @Ignore("Only needed when testing against a running Resource Store")
  @Test
  public void postTestFilePathsToResourceStore() throws ParseException {
    HashMap<String, String> filePaths = new HashMap<>();
    filePaths.put("NLNZ-TI92930263-20151108060042-00000-kaiwae-z4.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\NLNZ-TI92930263-20151108060042-00000-kaiwae-z4.warc");
    filePaths.put("NLNZ-TI92930263-20151108060054-00001-kaiwae-z4.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\NLNZ-TI92930263-20151108060054-00001-kaiwae-z4.warc");
    filePaths.put("NLNZ-TI92930263-20151108111900-00002-kaiwae-z4.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\NLNZ-TI92930263-20151108111900-00002-kaiwae-z4.warc");
    filePaths.put("NLNZ-TI92930263-20151108112503-00003-kaiwae-z4.warc", "C:\\\\wct\\\\openwayback2.2\\\\store\\\\oversixty\\\\NLNZ-TI92930263-20151108112503-00003-kaiwae-z4.warc");
    byte[] filePathsJSON = new Gson().toJson(filePaths).getBytes();
    String result = null;
    try {
      result = resourceStoreConn.post(filePathsJSON);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //TODO write assertion for result
//    assertThat(result, containsString("20140312135704"));
  }
}
