package com.keydap.sparrow;

/**
 * Details of a registered application.
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
@Resource(schemaId="urn:keydap:params:scim:schemas:core:2.0:Application", endpoint="/Applications", desc="Application")
public class RegisteredApp {
    @ReadOnly
    private String id;
    private String name;
    private String descritpion;
    private String secret;
    private String redirectUri;
    private boolean consentRequired;
    private boolean hasQueryInUri;

    @ReadOnly
    private String[] schemas;
    public static String SCHEMA = "urn:keydap:params:scim:schemas:core:2.0:Application";

     public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSecret() {
        return secret;
    }

    public String getDescritpion() {
        return descritpion;
    }

    public void setDescritpion(String descritpion) {
        this.descritpion = descritpion;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public boolean isConsentRequired() {
        return consentRequired;
    }

    public void setConsentRequired(boolean consentRequired) {
        this.consentRequired = consentRequired;
    }

    public boolean isHasQueryInUri() {
        return hasQueryInUri;
    }

    public void setHasQueryInUri(boolean hasQueryInUri) {
        this.hasQueryInUri = hasQueryInUri;
    }

    public void setName(String name) {
        this.name = name;
    }
}
