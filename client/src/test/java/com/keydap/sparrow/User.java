package com.keydap.sparrow;

import com.keydap.sparrow.ComplexType;
import com.keydap.sparrow.Extension;
import com.keydap.sparrow.ReadOnly;
import com.keydap.sparrow.Resource;
import java.util.List;
import java.util.Date;
import java.io.Serializable;

/*
 * Generated on Tue Dec 05 18:59:01 IST 2017 using Keydap Sparrow's json2java plugin 
 */

@Resource(schemaId="urn:ietf:params:scim:schemas:core:2.0:User", endpoint="/Users", desc="User Account")
public  class User implements Serializable
{


      @ReadOnly
      private String id;
      private String externalId;


      @ReadOnly
      private Meta meta;
      private String userName;
      private Name name;
      private String displayName;
      private String nickName;
      private String profileUrl;
      private String title;
      private String userType;
      private String preferredLanguage;
      private String locale;
      private String timezone;
      private boolean active;
      private String password;
      private List<Email> emails;
      private List<PhoneNumber> phoneNumbers;
      private List<Im> ims;
      private List<Photo> photos;
      private List<Address> addresses;


      @ReadOnly
      private List<Group> groups;
      private List<Entitlement> entitlements;
      private List<Role> roles;
      private List<X509Certificate> x509Certificates;


      @Extension("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User")
      private EnterpriseUser enterpriseUser;
   
   // a readonly field that gets populated only while reading reasources
   @ReadOnly
   private String[] schemas;
   
    public static String SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:User";
    
    public User()
    {
    }
   
      
       public String getId()
       {
          return id;
       }
       
       
       public void setExternalId( String externalId )
       {      
          this.externalId = externalId;
       }
      
       public String getExternalId()
       {
          return externalId;
       }
       
         
       public Meta getMeta()
       {
          return meta;
       }
       
       
       public void setUserName( String userName )
       {      
          this.userName = userName;
       }
      
       public String getUserName()
       {
          return userName;
       }
       
       
       public void setName( Name name )
       {      
          this.name = name;
       }
      
       public Name getName()
       {
          return name;
       }
       
       
       public void setDisplayName( String displayName )
       {      
          this.displayName = displayName;
       }
      
       public String getDisplayName()
       {
          return displayName;
       }
       
       
       public void setNickName( String nickName )
       {      
          this.nickName = nickName;
       }
      
       public String getNickName()
       {
          return nickName;
       }
       
       
       public void setProfileUrl( String profileUrl )
       {      
          this.profileUrl = profileUrl;
       }
      
       public String getProfileUrl()
       {
          return profileUrl;
       }
       
       
       public void setTitle( String title )
       {      
          this.title = title;
       }
      
       public String getTitle()
       {
          return title;
       }
       
       
       public void setUserType( String userType )
       {      
          this.userType = userType;
       }
      
       public String getUserType()
       {
          return userType;
       }
       
       
       public void setPreferredLanguage( String preferredLanguage )
       {      
          this.preferredLanguage = preferredLanguage;
       }
      
       public String getPreferredLanguage()
       {
          return preferredLanguage;
       }
       
       
       public void setLocale( String locale )
       {      
          this.locale = locale;
       }
      
       public String getLocale()
       {
          return locale;
       }
       
       
       public void setTimezone( String timezone )
       {      
          this.timezone = timezone;
       }
      
       public String getTimezone()
       {
          return timezone;
       }
       
       
       public void setActive( boolean active )
       {      
          this.active = active;
       }
      
       public boolean isActive()
       {
          return active;
       }
       
       
       public void setPassword( String password )
       {      
          this.password = password;
       }
      
       public String getPassword()
       {
          return password;
       }
       
       
       public void setEmails( List<Email> emails )
       {      
          this.emails = emails;
       }
      
       public List<Email> getEmails()
       {
          return emails;
       }
       
       
       public void setPhoneNumbers( List<PhoneNumber> phoneNumbers )
       {      
          this.phoneNumbers = phoneNumbers;
       }
      
       public List<PhoneNumber> getPhoneNumbers()
       {
          return phoneNumbers;
       }
       
       
       public void setIms( List<Im> ims )
       {      
          this.ims = ims;
       }
      
       public List<Im> getIms()
       {
          return ims;
       }
       
       
       public void setPhotos( List<Photo> photos )
       {      
          this.photos = photos;
       }
      
       public List<Photo> getPhotos()
       {
          return photos;
       }
       
       
       public void setAddresses( List<Address> addresses )
       {      
          this.addresses = addresses;
       }
      
       public List<Address> getAddresses()
       {
          return addresses;
       }
       
         
       public List<Group> getGroups()
       {
          return groups;
       }
       
       
       public void setEntitlements( List<Entitlement> entitlements )
       {      
          this.entitlements = entitlements;
       }
      
       public List<Entitlement> getEntitlements()
       {
          return entitlements;
       }
       
       
       public void setRoles( List<Role> roles )
       {      
          this.roles = roles;
       }
      
       public List<Role> getRoles()
       {
          return roles;
       }
       
       
       public void setX509Certificates( List<X509Certificate> x509Certificates )
       {      
          this.x509Certificates = x509Certificates;
       }
      
       public List<X509Certificate> getX509Certificates()
       {
          return x509Certificates;
       }
       
       
       public void setEnterpriseUser( EnterpriseUser enterpriseUser )
       {      
          this.enterpriseUser = enterpriseUser;
       }
      
       public EnterpriseUser getEnterpriseUser()
       {
          return enterpriseUser;
       }
       
      
   
   public String[] getSchemas() {
     return schemas;
   }
   
      @ComplexType

   public static class Meta implements Serializable
   {


         @ReadOnly
         private String resourceType;


         @ReadOnly
         private Date created;


         @ReadOnly
         private Date lastModified;


         @ReadOnly
         private String location;


         @ReadOnly
         private String version;
      
      
       
       public Meta()
       {
       }
      
         
          public String getResourceType()
          {
             return resourceType;
          }
          
            
          public Date getCreated()
          {
             return created;
          }
          
            
          public Date getLastModified()
          {
             return lastModified;
          }
          
            
          public String getLocation()
          {
             return location;
          }
          
            
          public String getVersion()
          {
             return version;
          }
          
         
      
      
   } 
      @ComplexType

   public static class Name implements Serializable
   {
         private String formatted;
         private String familyName;
         private String givenName;
         private String middleName;
         private String honorificPrefix;
         private String honorificSuffix;
      
      
       
       public Name()
       {
       }
      
       
          public void setFormatted( String formatted )
          {      
             this.formatted = formatted;
          }
         
          public String getFormatted()
          {
             return formatted;
          }
          
          
          public void setFamilyName( String familyName )
          {      
             this.familyName = familyName;
          }
         
          public String getFamilyName()
          {
             return familyName;
          }
          
          
          public void setGivenName( String givenName )
          {      
             this.givenName = givenName;
          }
         
          public String getGivenName()
          {
             return givenName;
          }
          
          
          public void setMiddleName( String middleName )
          {      
             this.middleName = middleName;
          }
         
          public String getMiddleName()
          {
             return middleName;
          }
          
          
          public void setHonorificPrefix( String honorificPrefix )
          {      
             this.honorificPrefix = honorificPrefix;
          }
         
          public String getHonorificPrefix()
          {
             return honorificPrefix;
          }
          
          
          public void setHonorificSuffix( String honorificSuffix )
          {      
             this.honorificSuffix = honorificSuffix;
          }
         
          public String getHonorificSuffix()
          {
             return honorificSuffix;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class Email implements Serializable
   {
         private String value;
         private String display;
         private String type;
         private boolean primary;
      
      
       
       public Email()
       {
       }
      
       
          public void setValue( String value )
          {      
             this.value = value;
          }
         
          public String getValue()
          {
             return value;
          }
          
          
          public void setDisplay( String display )
          {      
             this.display = display;
          }
         
          public String getDisplay()
          {
             return display;
          }
          
          
          public void setType( String type )
          {      
             this.type = type;
          }
         
          public String getType()
          {
             return type;
          }
          
          
          public void setPrimary( boolean primary )
          {      
             this.primary = primary;
          }
         
          public boolean isPrimary()
          {
             return primary;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class PhoneNumber implements Serializable
   {
         private String value;
         private String display;
         private String type;
         private boolean primary;
      
      
       
       public PhoneNumber()
       {
       }
      
       
          public void setValue( String value )
          {      
             this.value = value;
          }
         
          public String getValue()
          {
             return value;
          }
          
          
          public void setDisplay( String display )
          {      
             this.display = display;
          }
         
          public String getDisplay()
          {
             return display;
          }
          
          
          public void setType( String type )
          {      
             this.type = type;
          }
         
          public String getType()
          {
             return type;
          }
          
          
          public void setPrimary( boolean primary )
          {      
             this.primary = primary;
          }
         
          public boolean isPrimary()
          {
             return primary;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class Im implements Serializable
   {
         private String value;
         private String display;
         private String type;
         private boolean primary;
      
      
       
       public Im()
       {
       }
      
       
          public void setValue( String value )
          {      
             this.value = value;
          }
         
          public String getValue()
          {
             return value;
          }
          
          
          public void setDisplay( String display )
          {      
             this.display = display;
          }
         
          public String getDisplay()
          {
             return display;
          }
          
          
          public void setType( String type )
          {      
             this.type = type;
          }
         
          public String getType()
          {
             return type;
          }
          
          
          public void setPrimary( boolean primary )
          {      
             this.primary = primary;
          }
         
          public boolean isPrimary()
          {
             return primary;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class Photo implements Serializable
   {
         private String value;
         private String display;
         private String type;
         private boolean primary;
      
      
       
       public Photo()
       {
       }
      
       
          public void setValue( String value )
          {      
             this.value = value;
          }
         
          public String getValue()
          {
             return value;
          }
          
          
          public void setDisplay( String display )
          {      
             this.display = display;
          }
         
          public String getDisplay()
          {
             return display;
          }
          
          
          public void setType( String type )
          {      
             this.type = type;
          }
         
          public String getType()
          {
             return type;
          }
          
          
          public void setPrimary( boolean primary )
          {      
             this.primary = primary;
          }
         
          public boolean isPrimary()
          {
             return primary;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class Address implements Serializable
   {
         private String formatted;
         private String streetAddress;
         private String locality;
         private String region;
         private String postalCode;
         private String country;
         private String type;
      
      
       
       public Address()
       {
       }
      
       
          public void setFormatted( String formatted )
          {      
             this.formatted = formatted;
          }
         
          public String getFormatted()
          {
             return formatted;
          }
          
          
          public void setStreetAddress( String streetAddress )
          {      
             this.streetAddress = streetAddress;
          }
         
          public String getStreetAddress()
          {
             return streetAddress;
          }
          
          
          public void setLocality( String locality )
          {      
             this.locality = locality;
          }
         
          public String getLocality()
          {
             return locality;
          }
          
          
          public void setRegion( String region )
          {      
             this.region = region;
          }
         
          public String getRegion()
          {
             return region;
          }
          
          
          public void setPostalCode( String postalCode )
          {      
             this.postalCode = postalCode;
          }
         
          public String getPostalCode()
          {
             return postalCode;
          }
          
          
          public void setCountry( String country )
          {      
             this.country = country;
          }
         
          public String getCountry()
          {
             return country;
          }
          
          
          public void setType( String type )
          {      
             this.type = type;
          }
         
          public String getType()
          {
             return type;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class Group implements Serializable
   {


         @ReadOnly
         private String value;


         @ReadOnly
         private String $ref;


         @ReadOnly
         private String display;


         @ReadOnly
         private String type;
      
      
       
       public Group()
       {
       }
      
         
          public String getValue()
          {
             return value;
          }
          
            
          public String get$ref()
          {
             return $ref;
          }
          
            
          public String getDisplay()
          {
             return display;
          }
          
            
          public String getType()
          {
             return type;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class Entitlement implements Serializable
   {
         private String value;
         private String display;
         private String type;
         private boolean primary;
      
      
       
       public Entitlement()
       {
       }
      
       
          public void setValue( String value )
          {      
             this.value = value;
          }
         
          public String getValue()
          {
             return value;
          }
          
          
          public void setDisplay( String display )
          {      
             this.display = display;
          }
         
          public String getDisplay()
          {
             return display;
          }
          
          
          public void setType( String type )
          {      
             this.type = type;
          }
         
          public String getType()
          {
             return type;
          }
          
          
          public void setPrimary( boolean primary )
          {      
             this.primary = primary;
          }
         
          public boolean isPrimary()
          {
             return primary;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class Role implements Serializable
   {
         private String value;
         private String display;
         private String type;
         private boolean primary;
      
      
       
       public Role()
       {
       }
      
       
          public void setValue( String value )
          {      
             this.value = value;
          }
         
          public String getValue()
          {
             return value;
          }
          
          
          public void setDisplay( String display )
          {      
             this.display = display;
          }
         
          public String getDisplay()
          {
             return display;
          }
          
          
          public void setType( String type )
          {      
             this.type = type;
          }
         
          public String getType()
          {
             return type;
          }
          
          
          public void setPrimary( boolean primary )
          {      
             this.primary = primary;
          }
         
          public boolean isPrimary()
          {
             return primary;
          }
          
         
      
      
   } 
      @ComplexType(multival=true)

   public static class X509Certificate implements Serializable
   {
         private byte[] value;
         private String display;
         private String type;
         private boolean primary;
      
      
       
       public X509Certificate()
       {
       }
      
       
          public void setValue( byte[] value )
          {      
             this.value = value;
          }
         
          public byte[] getValue()
          {
             return value;
          }
          
          
          public void setDisplay( String display )
          {      
             this.display = display;
          }
         
          public String getDisplay()
          {
             return display;
          }
          
          
          public void setType( String type )
          {      
             this.type = type;
          }
         
          public String getType()
          {
             return type;
          }
          
          
          public void setPrimary( boolean primary )
          {      
             this.primary = primary;
          }
         
          public boolean isPrimary()
          {
             return primary;
          }
          
         
      
      
   } 
      @ComplexType

   public static class Manager implements Serializable
   {
         private String value;
         private String $ref;


         @ReadOnly
         private String displayName;
      
      
       
       public Manager()
       {
       }
      
       
          public void setValue( String value )
          {      
             this.value = value;
          }
         
          public String getValue()
          {
             return value;
          }
          
          
          public void set$ref( String $ref )
          {      
             this.$ref = $ref;
          }
         
          public String get$ref()
          {
             return $ref;
          }
          
            
          public String getDisplayName()
          {
             return displayName;
          }
          
         
      
      
   } 
   public static class EnterpriseUser implements Serializable
   {
         private String employeeNumber;
         private String costCenter;
         private String organization;
         private String division;
         private String department;
         private Manager manager;
      
      
       public static String SCHEMA = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User";
       
       public EnterpriseUser()
       {
       }
      
       
          public void setEmployeeNumber( String employeeNumber )
          {      
             this.employeeNumber = employeeNumber;
          }
         
          public String getEmployeeNumber()
          {
             return employeeNumber;
          }
          
          
          public void setCostCenter( String costCenter )
          {      
             this.costCenter = costCenter;
          }
         
          public String getCostCenter()
          {
             return costCenter;
          }
          
          
          public void setOrganization( String organization )
          {      
             this.organization = organization;
          }
         
          public String getOrganization()
          {
             return organization;
          }
          
          
          public void setDivision( String division )
          {      
             this.division = division;
          }
         
          public String getDivision()
          {
             return division;
          }
          
          
          public void setDepartment( String department )
          {      
             this.department = department;
          }
         
          public String getDepartment()
          {
             return department;
          }
          
          
          public void setManager( Manager manager )
          {      
             this.manager = manager;
          }
         
          public Manager getManager()
          {
             return manager;
          }
          
         
      
      
   } 
}