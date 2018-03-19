package com.keydap.sparrow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Details of a registered application.
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
@Resource(schemaId = "urn:keydap:params:scim:schemas:core:2.0:Application", endpoint = "/Applications", desc = "Application")
public class RegisteredApp {
    @ReadOnly
    private String id;
    private String name;
    private String descritpion;
    private String secret;
    private String redirectUri;
    private boolean consentRequired;
    private boolean hasQueryInUri;
    private List<Attribute> attributes;
    private boolean enableSAML;

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

    public void setEnableSAML(boolean enableSAML) {
        this.enableSAML = enableSAML;
    }

    public boolean isEnableSAML() {
        return enableSAML;
    }

    public void add(Attribute attr) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }

        attributes.add(attr);
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    @ComplexType(multival = true)
    public static class Attribute implements Serializable {
        private String name;
        private String scimExpr;
        private String staticVal;
        private String staticMultiValDelim;

        public Attribute() {
        }

        public Attribute(String name, String scimExpr) {
            this.name = name;
            this.scimExpr = scimExpr;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setScimExpr(String scimExpr) {
            this.scimExpr = scimExpr;
        }

        public String getScimExpr() {
            return scimExpr;
        }

        public void setStaticVal(String staticVal) {
            this.staticVal = staticVal;
        }

        public String getStaticVal() {
            return staticVal;
        }

        public void setStaticMultiValDelim(String staticMultiValDelim) {
            this.staticMultiValDelim = staticMultiValDelim;
        }

        public String getStaticMultiValDelim() {
            return staticMultiValDelim;
        }
    }
}
