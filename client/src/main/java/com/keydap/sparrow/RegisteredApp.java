package com.keydap.sparrow;

/**
 * Details of a registered application.
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class RegisteredApp {
    private String id;
    private String name;
    private String secret;
    private String desc;
    private String redUri;
    private long time;
    private boolean consentRequired;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSecret() {
        return secret;
    }

    public String getDesc() {
        return desc;
    }

    public String getRedUri() {
        return redUri;
    }

    public long getTime() {
        return time;
    }

    public boolean isConsentRequired() {
        return consentRequired;
    }
}
