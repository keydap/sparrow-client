package com.keydap.sparrow;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keydap.sparrow.PatchRequest.PatchOperation;
import com.keydap.sparrow.User.Address;
import com.keydap.sparrow.User.Email;
import com.keydap.sparrow.User.EnterpriseUser;
import com.keydap.sparrow.User.Manager;
import com.keydap.sparrow.User.Name;

/**
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class PatchGeneratorTest {

    private User original;
    
    private static SparrowClient client;

    private PatchGenerator pg = new PatchGenerator();

    private Gson gson = new Gson();
    
    @BeforeClass
    public static void init() {
        client = new SparrowClient("");
        client.register(User.class);
    }
    
    @Before
    public void setup() {
        original = new User();
        original.setActive(true);
        Address a1 = new Address();
        a1.setCountry("IN");
        a1.setFormatted("Nilgiris, Western Ghats, India");
        a1.setLocality("Nilgiris");
        a1.setPostalCode("000000");
        a1.setRegion("West Coast");
        a1.setStreetAddress("Highway");
        a1.setType("Home");
        
        List<Address> lstAddresses = new ArrayList<>();
        lstAddresses.add(a1);
        original.setAddresses(lstAddresses);
        
        original.setDisplayName("Nilgiri Thar");
        
        Email e1 = new Email();
        e1.setPrimary(true);
        e1.setType("Office");
        e1.setValue("thar@westernghats.org");
        
        Email e2 = new Email();
        e2.setPrimary(true);
        e2.setType("Home");
        e2.setValue("thar@nilgiris.org");

        List<Email> lstEmails = new ArrayList<>();
        lstEmails.add(e1);
        lstEmails.add(e2);
        original.setEmails(lstEmails);

        original.setUserName("thar");
        
        Name name = new Name();
        name.setFamilyName("Angulate");
        name.setFormatted("Thar (Angulate)");
        name.setGivenName("Thar");
        
        original.setName(name);
    }
    
    private <T> T cloneObject(T obj) {
        // DO NOT use the SparrowClient's serialize, the output is not compatible to get an object back 
        JsonElement je = gson.toJsonTree(obj);
        return (T) gson.fromJson(je, obj.getClass());
    }
    
    @Test
    public void testAttrDiff() throws Exception {
        User modified = cloneObject(original);
        modified.setDisplayName("Westernghats Thar");
        Email e = modified.getEmails().get(1);
        e.setValue("new@mail.com");
        // modified
        modified.getName().setFormatted("Thar");
        // added but will be grouped under replace
        modified.getName().setHonorificPrefix("Mr.");

        modified.setTitle("Mr.");
        
        PatchRequest pr = pg.create("", modified, original);
        assertEquals(4, pr.getOperations().size());
    }
    
    @Test
    public void testListDiff() throws Exception {
        User modified = cloneObject(original);
        
        // same list of emails - shouldn't have any difference
        PatchRequest pr = pg.create("", modified, original);
        assertEquals(0, pr.getOperations().size());

        // modify one and add a new one - one replace and one add
        Email modE = modified.getEmails().get(1);
        modE.setValue("mod@mail.com");

        Email newE = new Email();
        newE.setValue("new@email.com");
        newE.setType("new");
        modified.getEmails().add(newE);
        
        pr = pg.create("", modified, original);
        assertEquals(2, pr.getOperations().size());
        
        // reorder the emails in the modified array and test
        // here the modified array is larger than the original array
        Email e1 = createEmail();
        Email e2 = createEmail();
        Email e3 = createEmail();
        Email e4 = createEmail();
        
        List<Email> oEmails = new ArrayList<>();
        oEmails.add(e1);
        oEmails.add(e2);
        original.setEmails(oEmails);
        
        List<Email> mEmails = new ArrayList<>();
        mEmails.add(e3);
        mEmails.add(e1);
        mEmails.add(e2);
        mEmails.add(e4);
        modified.setEmails(mEmails);
        
        pr = pg.create("", modified, original);
        List<PatchOperation> ops = pr.getOperations();
        assertEquals(3, ops.size());
        
        PatchOperation po = ops.get(0);
        assertEquals("replace", po.getOp());
        assertEquals(pg.buildPathWithFilter("emails", e1), po.getPath());
        JsonObject jo = (JsonObject)gson.toJsonTree(e3);
        jo.remove("primary");// primary values are same during diff so it won't be included in patch
        assertEquals(jo, po.getValue());
        
        po = ops.get(1);
        assertEquals("replace", po.getOp());
        assertEquals(pg.buildPathWithFilter("emails", e2), po.getPath());
        jo = (JsonObject)gson.toJsonTree(e1);
        jo.remove("primary");// primary values are same during diff so it won't be included in patch
        assertEquals(jo, po.getValue());
        
        po = ops.get(2);
        assertEquals("add", po.getOp());
        assertEquals("emails", po.getPath());
        JsonArray arr = new JsonArray();
        arr.add(gson.toJsonTree(e2));
        arr.add(gson.toJsonTree(e4));
        assertEquals(arr, po.getValue());
        
        // reorder the emails again in the modified array and test
        // here the modified array is smaller than the original array
        oEmails = new ArrayList<>();
        oEmails.add(e1);
        oEmails.add(e2);
        oEmails.add(e3);
        oEmails.add(e4);
        original.setEmails(oEmails);
        
        mEmails = new ArrayList<>();
        mEmails.add(e3);
        mEmails.add(e1);
        modified.setEmails(mEmails);

        pr = pg.create("", modified, original);
        ops = pr.getOperations();
        assertEquals(2, ops.size());
        
        po = ops.get(0);
        assertEquals("replace", po.getOp());
        assertEquals(pg.buildPathWithFilter("emails", e1), po.getPath());
        jo = (JsonObject)gson.toJsonTree(e3);
        jo.remove("primary");// primary values are same during diff so it won't be included in patch
        assertEquals(jo, po.getValue());
        
        po = ops.get(1);
        assertEquals("replace", po.getOp());
        assertEquals(pg.buildPathWithFilter("emails", e2), po.getPath());
        jo = (JsonObject)gson.toJsonTree(e1);
        jo.remove("primary");// primary values are same during diff so it won't be included in patch
        assertEquals(jo, po.getValue());
        
        // delete
        oEmails = new ArrayList<>();
        oEmails.add(e1);
        oEmails.add(e2);
        oEmails.add(e3);
        oEmails.add(e4);
        original.setEmails(oEmails);
        
        mEmails = new ArrayList<>();
        mEmails.add(e3);
        mEmails.add(e1);
        mEmails.add(null);
        mEmails.add(null);
        
        modified.setEmails(mEmails);

        pr = pg.create("", modified, original);
        ops = pr.getOperations();
        assertEquals(4, ops.size()); // 2 replace and 2 delete
        
        po = ops.get(2);
        assertEquals("remove", po.getOp());
        assertEquals(pg.buildPathWithFilter("emails", e3), po.getPath());
        
        po = ops.get(3);
        assertEquals("remove", po.getOp());
        assertEquals(pg.buildPathWithFilter("emails", e4), po.getPath());
    }

    @Test
    public void testExtensionDiff() {
        User modified = cloneObject(original);
        
        EnterpriseUser eu = new EnterpriseUser();
        eu.setCostCenter("mountain valley");
        eu.setDepartment("greenwood");
        eu.setDivision("plains");
        eu.setEmployeeNumber("1");
        Manager manager = new Manager();
        manager.setValue("ram");
        eu.setManager(manager);
        eu.setOrganization("herd");
        
        modified.setEnterpriseUser(eu);
        
        PatchRequest pr = pg.create("", modified, original);
        List<PatchOperation> ops = pr.getOperations();
        assertEquals(1, ops.size());
        
        PatchOperation po = ops.get(0);
        assertEquals("add", po.getOp());
        assertEquals("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User", po.getPath());
        JsonObject jo = (JsonObject)gson.toJsonTree(eu);
        assertEquals(jo, po.getValue());
        
        original.setEnterpriseUser(cloneObject(eu));
        eu.setCostCenter("new cost center");
        eu.getManager().setValue("new ram");
        pr = pg.create("", modified, original);
        ops = pr.getOperations();
        assertEquals(2, ops.size());
        
        po = ops.get(0);
        assertEquals("replace", po.getOp());
        assertEquals("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:costCenter", po.getPath());
        
        po = ops.get(1);
        assertEquals("replace", po.getOp());
        assertEquals("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User:manager", po.getPath());
    }
    
    @Test
    public void testJSONValueInPath() {
        String jsonEmailVal = "[{\"op\":\"read\",\"allowAttrs\":\"name\",\"filter\":\"schemas pr\"}]";
        Email e = new Email();
        e.setValue(jsonEmailVal);
        e.setType("home");
        
        User original = new User();
        original.setEmails(Collections.singletonList(e));
        User modified = cloneObject(original);
        modified.getEmails().get(0).setPrimary(true);
        
        PatchRequest pr = pg.create("", modified, original);
        String path = pr.getOperations().get(0).getPath();
        
        String expectedPath = "emails[value EQ \"[{\\\"op\\\":\\\"read\\\",\\\"allowAttrs\\\":\\\"name\\\",\\\"filter\\\":\\\"schemas pr\\\"}]\" AND type EQ \"home\" AND primary EQ false]";
        assertEquals(expectedPath, path);
    }
    
    private void dump(Object obj) {
        System.out.println(gson.toJson(obj));
    }
    
    private Email createEmail() {
        Email e = new Email();
        Random rnd = new SecureRandom();
        int v = rnd.nextInt();
        e.setDisplay("display " + v);
        e.setPrimary(false);
        e.setType(String.valueOf(v));
        e.setValue(v + "@email.com");
        
        return e;
    }
}
