package org.ccci.gto.android.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpHeaderUtilsTest {
    @Test
    public void testParseChallenge() throws Exception {
        // test parsing a CAS Auth challenge
        final HttpHeaderUtils.Challenge challenge = HttpHeaderUtils.parseChallenge(
                "CAS realm=\"test\", casUrl=\"https://thekey.me/cas/\", service=\"https://www.example/api/service\"");
        assertEquals("CAS", challenge.getScheme());
        assertEquals(3, challenge.getParameters().size());
        assertEquals("test", challenge.getParameterValue("realm"));
        assertEquals("https://thekey.me/cas/", challenge.getParameterValue("casUrl"));
        assertEquals("https://www.example/api/service", challenge.getParameterValue("service"));
    }
}
