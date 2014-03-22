package com.belsofto.vet.test.util;

import com.belsofto.vet.util.MessageUtils;
import org.junit.Assert;
import org.junit.Test;

public class MessageUtilsTest {
    @Test
    public void testMainFrameTitle() {
        String title = MessageUtils.getMessage("ui.mainFrame.title");
        Assert.assertNotNull(title);
        Assert.assertFalse(title.contains("???"));
    }

    @Test
    public void testMissKeyTemplate() {
        String missedMessage = MessageUtils.getMessage("123");
        Assert.assertTrue("??? 123 ???".equals(missedMessage));
    }
}