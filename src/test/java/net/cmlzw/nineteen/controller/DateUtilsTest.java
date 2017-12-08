package net.cmlzw.nineteen.controller;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class DateUtilsTest {
    @Test
    public void getToday() throws Exception {
        Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        System.out.println(DateFormatUtils.format(today, "yyyy-MM-dd HH:mm:ss.ssssss"));
        assertEquals(0, today.getHours());
        assertEquals(0, today.getMinutes());
        assertEquals(0, today.getSeconds());
        Date yesterday = DateUtils.addDays(today, -1);
        assertEquals(today.getTime(), yesterday.getTime() + 24 * 3600 * 1000);
    }

    @Test
    public void userAgentMatch() throws Exception {
        String userAgent = "MicroMessenger v8";
        assertTrue(userAgent.matches(".*MicroMessenger.*"));
        Pattern wx = Pattern.compile(".*MicroMessenger.*", Pattern.CASE_INSENSITIVE);
        assertTrue(wx.matcher(userAgent).matches());
    }
}
