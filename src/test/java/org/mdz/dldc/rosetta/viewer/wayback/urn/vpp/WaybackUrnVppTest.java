package org.mdz.dldc.rosetta.viewer.wayback.urn.vpp;

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
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Marcus Bitzl <marcus.bitzl@bsb-muenchen.de>
 */
public class WaybackUrnVppTest {
  
  private WaybackUrnVpp vpp;
  
  private DnxDocumentHelper.WebHarvesting webHarvesting;
  
  @Before
  public void setUp() throws AccessException {
    vpp = new WaybackUrnVpp();
    webHarvesting = mock(DnxDocumentHelper.WebHarvesting.class);
    when(webHarvesting.getHarvestDate()).thenReturn("12/03/2014 13:57:04");
    when(webHarvesting.getPrimarySeedURL()).thenReturn("http://wwww.bahn.de");
  }

  @Test
  public void createUrlPathResultShouldStartAndEndWithMarkers() throws ParseException {
    String marker = vpp.getMarker();
    String path = vpp.createUrlPath(webHarvesting);
    assertThat(path, allOf(startsWith(marker), endsWith(marker)));
  }

  @Test
  public void createUrlPathResultShouldContainUrlPath() throws ParseException {
    String path = vpp.createUrlPath(webHarvesting);
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
}
