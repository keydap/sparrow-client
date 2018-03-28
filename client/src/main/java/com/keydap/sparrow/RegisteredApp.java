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
    private List<OauthAttribute> oauthAttributes;
    private String acsUrl;
    private String sloUrl;
    private String metaUrl;
    private List<SamlAttribute> samlAttributes;
    private int assertionValidity;

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

    public void add(OauthAttribute attr) {
        if (this.oauthAttributes == null) {
            this.oauthAttributes = new ArrayList();
        }

        this.oauthAttributes.add(attr);
    }

    public void add(SamlAttribute attr) {
        if (samlAttributes == null) {
            samlAttributes = new ArrayList<>();
        }

        samlAttributes.add(attr);
    }

    public void setOauthAttributes(List<OauthAttribute> attributes) {
        this.oauthAttributes = attributes;
    }

    public List<OauthAttribute> getOauthAttributes() {
        return oauthAttributes;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }

    public String getSloUrl() {
        return sloUrl;
    }

    public void setSloUrl(String sloUrl) {
        this.sloUrl = sloUrl;
    }

    public String getMetaUrl() {
        return metaUrl;
    }

    public void setMetaUrl(String metaUrl) {
        this.metaUrl = metaUrl;
    }

    public List<SamlAttribute> getSamlAttributes() {
        return samlAttributes;
    }

    public void setSamlAttributes(List<SamlAttribute> samlAttributes) {
        this.samlAttributes = samlAttributes;
    }

    public int getAssertionValidity() {
        return assertionValidity;
    }

    public void setAssertionValidity(int assertionValidity) {
        this.assertionValidity = assertionValidity;
    }

    @ComplexType(multival = true)
    public static class OauthAttribute implements Serializable {
        private String name;
        private String scimExpr;
        private String staticVal;
        private String staticMultiValDelim;

        public OauthAttribute() {
        }
        
        public OauthAttribute(String name, String scimExpr) {
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

    @ComplexType(multival = true)
    public static class SamlAttribute extends OauthAttribute implements Serializable {
        private String format = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri"; // default format

        public SamlAttribute() {
        }

        public SamlAttribute(String name, String scimExpr) {
            super(name, scimExpr);
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }
}
