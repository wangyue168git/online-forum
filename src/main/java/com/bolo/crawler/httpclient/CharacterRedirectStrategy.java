package com.bolo.crawler.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.net.URI;

/**
 * @Author wangyue
 * @Date 15:33
 */
@Slf4j
public class CharacterRedirectStrategy extends LaxRedirectStrategy {
    private String[][] chars;
    public CharacterRedirectStrategy(String[][] replaceCharsWhenRedirect) {
        this.chars = chars;
    }
    protected URI createLocationURI(final String location) throws ProtocolException {
        String loc = location;
        if (chars != null && chars.length > 0) {
            for(String[] c : chars) {
                loc = loc.replaceAll(c[0], c[1]);
            }
        }
        if(loc != null) {
            log.info("location:" + loc);
        }
        return super.createLocationURI(loc);
    }
}
