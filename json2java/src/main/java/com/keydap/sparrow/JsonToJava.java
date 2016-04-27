package com.keydap.sparrow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.keydap.sparrow.ResourceType.Extension;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JsonToJava extends AbstractMojo {
    @Parameter(defaultValue = "com")
    private String generatePackage;

    @Parameter
    private String baseUrl;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;
    
    @Parameter(defaultValue = "${project.build.directory}")
    protected File targetDirectory;

    private static StringTemplateGroup stg = new StringTemplateGroup("schema");

    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();

        log.info("Starting json2java");

        String packageDirPath = generatePackage.replace(".", "/");

        File srcRoot = new File(targetDirectory, "generated-sources/json2java");
        File srcDir = new File( srcRoot, packageDirPath);

        log.debug("Creating directory " + srcDir.getAbsolutePath() + " for storing generated source files");

        if (!srcDir.exists()) {
            boolean created = srcDir.mkdirs();
            if (!created) {
                String msg = "Failed to create the directory " + srcDir.getAbsolutePath();
                log.warn(msg);
                throw new MojoFailureException(msg);
            }
        }

        project.addCompileSourceRoot(srcRoot.getAbsolutePath());
        
        try {
            generateAndSave(srcDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String fetch(String url) throws MojoExecutionException {
        BufferedReader br = null;
        try {
            getLog().debug("Fetching data from " + url);
            URL u = new URL(url);
            br = new BufferedReader(new InputStreamReader(u.openStream()));

            String s;

            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            return sb.toString();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read data from " + url,
                    e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public void generateAndSave(File srcDir) throws MojoExecutionException {
        Gson gson = new Gson();

        getLog().info("Fetching ResourceTypes...");
        String rtJson = fetch(baseUrl + "/ResourceTypes");

        Type rtt = new TypeToken<List<ResourceType>>() {
        }.getType();

        List<ResourceType> resTypes = gson.fromJson(rtJson, rtt);

        getLog().info("Fetching Schemas...");
        String scJson = fetch(baseUrl + "/Schemas");

        Type st = new TypeToken<List<Schema>>() {
        }.getType();
        List<Schema> schemas = gson.fromJson(scJson, st);

        Map<String, Schema> scMap = new HashMap<String, Schema>();

        for (Schema s : schemas) {
            scMap.put(s.id, s);
        }

        for (ResourceType rt : resTypes) {
            _generateAndSave(rt, scMap, srcDir);
        }
    }

    public void _generateAndSave(ResourceType rt, Map<String, Schema> schemas,
            File srcDir) throws MojoExecutionException {
        getLog().info("Generating model for resource type " + rt.name);

        String className = makeClassName(rt.name);

        StringTemplate template = generateClass(rt, schemas);

        File javaFile = new File(srcDir, className + ".java");

        FileWriter fw = null;

        try {
            fw = new FileWriter(javaFile);
            fw.write(template.toString());
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to store the generated source file for resource type " + rt.name,  e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private StringTemplate generateClass(ResourceType rt, Map<String, Schema> schemas) {
        StringTemplate template = stg.getInstanceOf("resource");

        template.setAttribute("schemaId", rt.schema);
        template.setAttribute("date", new Date());
        template.setAttribute("package", generatePackage);
        template.setAttribute("visibility", "public");
        template.setAttribute("resourceDesc", rt.description);
        template.setAttribute("className", rt.name);
        template.setAttribute("endpoint", rt.endpoint);

        List<String> innerClasses = new ArrayList<String>();

        Schema coreSchema = schemas.get(rt.schema);

        addCommonAtTypes(coreSchema);
        
        for (AttributeType at : coreSchema.attributes) {
            if (!at.isComplex()) {
                prepareSimpleAttribute(at, template);
            } else {
                innerClasses.add(addComplexAttribute(rt.name, at));
            }
        }

        List<AttributeType> allAttrs = new ArrayList<AttributeType>();
        allAttrs.addAll(coreSchema.attributes);
        
        if (rt.schemaExtensions != null) {
            // these are not real attribute's of schema but the java fields associated with
            // the extended types
            List<AttributeType> extensions = new ArrayList<AttributeType>();

            for(Extension et : rt.schemaExtensions) {
                Schema sc = schemas.get(et.schema);
                StringTemplate extTemplate = stg.getInstanceOf("resource");
                
                String extClassName = makeClassName(sc.name);
                extTemplate.setAttribute("visibility", "public");
                extTemplate.setAttribute("static", "static");
                extTemplate.setAttribute("className", extClassName);
                
                List<String> extInnerClasses = new ArrayList<String>();
                
                for (AttributeType at : sc.attributes) {
                    if (!at.isComplex()) {
                        prepareSimpleAttribute(at, extTemplate);
                    } else {
                        innerClasses.add(addComplexAttribute(sc.name, at));
                    }
                }
                
                extTemplate.setAttribute("allAttrs", sc.attributes);
                extTemplate.setAttribute("allInnerClasses", extInnerClasses);
                extTemplate.setAttribute("schemaId", sc.id);
                
                innerClasses.add(extTemplate.toString());
                AttributeType extAt = new AttributeType();
                extAt.name = makeFieldName(sc.name);
                extAt.type = extClassName;
                extAt.extension = true;
                extAt.schema = sc.id;
                extensions.add(extAt);
            }
            
            allAttrs.addAll(extensions);
        }
        
        template.setAttribute("allAttrs", allAttrs);
        template.setAttribute("allInnerClasses", innerClasses);

        return template;
    }

    private void prepareSimpleAttribute(AttributeType at, StringTemplate template) {
        if (at.type.equalsIgnoreCase("dateTime")) {
            at.type = "Date";
        } else if (at.type.equalsIgnoreCase("reference")
                || at.type.equalsIgnoreCase("string")) {
            at.type = "String";
        } else if (at.type.equalsIgnoreCase("binary")) {
            at.type = "byte[]";
        }
        
        if (at.multiValued) {
            at.type = "List<" + at.type + ">";
        }
    }

    private String addComplexAttribute(String parentClassName, AttributeType at) {
        StringTemplate template = stg.getInstanceOf("resource");
        
        String className = makeClassName(at.name);
        
        if (className.endsWith("s")) {
            int endPos = className.length() - 1;
            
            // special case for Address'es'
            if (className.endsWith("Addresses")) {
                endPos -= 1;
            }
            
            className = className.substring(0, endPos);
        }
        
        
        template.setAttribute("visibility", "public");
        template.setAttribute("static", "static");
        template.setAttribute("className", className);
        
        //at.type = parentClassName + "." + className;
        at.type = className;
        
        if (at.multiValued) {
            at.type = "List<" + at.type + ">";
        }
        
        for(AttributeType subAt : at.subAttributes) {
            prepareSimpleAttribute(subAt, template);
        }
        
        template.setAttribute("allAttrs", at.subAttributes);

        return template.toString();
    }

    /**
     * @param generatePackage
     *            the generatePackage to set
     */
    public void setGeneratePackage(String generatePackage) {
        this.generatePackage = generatePackage;
    }

    /**
     * @param baseUrl
     *            the schemaBaseUrl to set
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String makeClassName(String name) {
        String className = Character.toUpperCase(name.charAt(0))
                + name.substring(1);
        return className;
    }

    private String makeFieldName(String name) {
        String fieldName = Character.toLowerCase(name.charAt(0))
                + name.substring(1);
        return fieldName;
    }

    private void addCommonAtTypes(Schema schema) {
        List<AttributeType> atList = new ArrayList<AttributeType>();
        
        // common attribute types
        AttributeType id = new AttributeType();
        id.multiValued = false;
        id.description = "Identifier of the resource";
        id.mutability = "readOnly";
        id.type = "string";
        id.name = "id";
        atList.add(id);

        AttributeType externalId = new AttributeType();
        externalId.multiValued = false;
        externalId.description = "External identifier of the resource";
        externalId.mutability = "readWrite";
        externalId.type = "string";
        externalId.name = "externalId";
        atList.add(externalId);

        AttributeType meta = new AttributeType();
        meta.multiValued = false;
        meta.description = "Metadata of the resource";
        meta.mutability = "readOnly";
        meta.type = "complex";
        meta.name = "meta";
        atList.add(meta);
        
        meta.subAttributes = new ArrayList<AttributeType>();
        
        AttributeType resourceType = new AttributeType();
        resourceType.multiValued = false;
        resourceType.description = "The name of the resource type of the resource";
        resourceType.mutability = "readOnly";
        resourceType.type = "string";
        resourceType.name = "resourceType";
        meta.subAttributes.add(resourceType);

        AttributeType created = new AttributeType();
        created.multiValued = false;
        created.description = "The date and time when the resource was added";
        created.mutability = "readOnly";
        created.type = "datetime";
        created.name = "created";
        meta.subAttributes.add(created);

        AttributeType lastModified = new AttributeType();
        lastModified.multiValued = false;
        lastModified.description = "The date and time when the resource was last modified";
        lastModified.mutability = "readOnly";
        lastModified.type = "datetime";
        lastModified.name = "lastModified";
        meta.subAttributes.add(lastModified);

        AttributeType location = new AttributeType();
        location.multiValued = false;
        location.description = "The location of the resource";
        location.mutability = "readOnly";
        location.type = "string";
        location.name = "location";
        meta.subAttributes.add(location);

        AttributeType version = new AttributeType();
        version.multiValued = false;
        version.description = "The version of the resource";
        version.mutability = "readOnly";
        version.type = "string";
        version.name = "version";
        meta.subAttributes.add(version);

        for(AttributeType at : schema.attributes) {
            String name = at.name;
            // if the
            if(name.equalsIgnoreCase(id.name) 
                    || name.equalsIgnoreCase(externalId.name)
                    || name.equalsIgnoreCase(meta.name)) {
                continue;
            }
            
            atList.add(at);
        }
        
        schema.attributes = atList;
    }
    
    public static void main(String[] args) throws Exception {
        JsonToJava jj = new JsonToJava();
        jj.baseUrl = "http://localhost:9090/v2";
        jj.generatePackage = "com";
        jj.targetDirectory = new File("/Users/dbugger/projects/sparrow-client/json2java/target");
        jj.execute();
    }
}
