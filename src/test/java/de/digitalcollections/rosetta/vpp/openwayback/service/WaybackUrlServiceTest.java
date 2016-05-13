package de.digitalcollections.rosetta.vpp.openwayback.service;

import de.digitalcollections.rosetta.vpp.openwayback.service.WaybackUrlService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Marcus Bitzl <marcus.bitzl@bsb-muenchen.de>
 */
public class WaybackUrlServiceTest {

  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  
  private WaybackUrlService service;

  @Before
  public void setUp() {
    service = new WaybackUrlService();
  }

  @Test
  public void urlDateStringShouldFormatDate() throws ParseException {
    Date date = FORMAT.parse("01/08/2012 23:07:05");
    assertThat(service.urlDateString(date), is("20120801230705"));
  }

  @Test
  public void createDetailUrlPathShouldReturnValidPath() throws ParseException {
    Date date = FORMAT.parse("01/08/2012 23:07:05");
    assertThat(service.createDetailUrlPath("http://example.com", date), is("/20120801230705/http://example.com"));
  }

  @Test
  public void createOverviewQueryStringShouldReturnValidQueryString() {
    assertThat(service.createOverviewQueryString("http://example.com"), is("url=http://example.com"));
  }
  
}
