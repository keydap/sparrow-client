package com.keydap.sparrow;

/**
 * Request for registering a new application.
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class RegisterAppRequest {
    private String name;
    private String desc;
    private String redUri;
    private boolean consentRequired;
    
    public RegisterAppRequest(String name, String redUri) {
        this.name = name;
        this.redUri = redUri;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getRedUri() {
        return redUri;
    }

    public boolean isConsentRequired() {
        return consentRequired;
    }

    public void setConsentRequired(boolean consentRequired) {
        this.consentRequired = consentRequired;
    }
}
