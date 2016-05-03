package org.mdz.dldc.rosetta.viewer.wayback.urn.vpp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
  
  private Date createDate(int year, int month, int day, int hour, int minute, int second) {
    Calendar  calendar = new GregorianCalendar();
    calendar.set(year, month, day, hour, minute, second);
    return calendar.getTime();
  }

  @Before
  public void setUp() {
    service = new WaybackUrlService();
  }

  @Test
  public void parseHarvestDateShouldParseDate() throws Exception {
    Calendar  calendar = new GregorianCalendar();
    calendar.set(2012, 10, 1, 23, 11, 5);
    Date date = calendar.getTime();
    assertThat(service.parseHarvestDate(FORMAT.format(date)).toString(), is(date.toString()));
  }

  @Test
  public void testUrlDateString() throws ParseException {
    Date date = FORMAT.parse("01/08/2012 23:07:05");
    assertThat(service.urlDateString(date), is("20120801230705"));
  }

  @Test
  public void testCreateUrlPath() throws ParseException {
    Date date = FORMAT.parse("01/08/2012 23:07:05");
    assertThat(service.createUrlPath("http://example.com", date), is("/20120801230705/http://example.com"));
  }
  
}
