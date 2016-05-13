package de.digitalcollections.rosetta.vpp.openwayback.service;

import de.digitalcollections.rosetta.vpp.openwayback.service.MetadataService;
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
public class MetadataServiceTest {

  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  
  private MetadataService metadataService;
  
  @Before
  public void setUp() {
    metadataService = new MetadataService();
  }

  @Test
  public void parseHarvestDateShouldParseDate() throws Exception {
    Calendar  calendar = new GregorianCalendar();
    calendar.set(2012, 10, 1, 23, 11, 5);
    Date date = calendar.getTime();
    assertThat(metadataService.parseHarvestDate(FORMAT.format(date)).toString(), is(date.toString()));
  }
  
}
