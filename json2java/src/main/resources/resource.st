$if(package)$

package $package$;

import com.keydap.sparrow.Extension;
import com.keydap.sparrow.Resource;
import java.util.List;

/*
 * Generated on $date$ using Keydap Sparrow's json2java plugin 
 */

@Resource(schemaId="$schemaId$", endpoint="$endpoint$", desc="$resourceDesc$")$\n$
$endif$
$visibility$ $static$ class $className$
{
   $allAttrs: { attr |
   $if(attr.extension)$
   $\n$
   @Extension("$attr.schema$")
   $endif$
   private $attr.type$ $attr.name$;}; separator="\n"$
   
   $if(package)$
   // a readonly field that gets populated only while reading reasources
   private String[] schemas;
   $endif$
   
   $if(schemaId)$
    public static String SCHEMA = "$schemaId$";
   $endif$
    
    public $className$()
    {
    }
   
   $allAttrs: { attr |
   $if(!attr.readOnly)$ 
    public void set$attr.methodName$( $attr.type$ $attr.name$ )
    {      
       this.$attr.name$ = $attr.name$;
    }
   $endif$
   
    $if(attr.boolean)$
    public $attr.type$ is$attr.methodName$()
    $else$
    public $attr.type$ get$attr.methodName$()
    $endif$
    {
       return $attr.name$;
    }
    
   }$
   
  $if(package)$
   public String[] getSchemas() {
     return schemas;
   }
  $endif$
   
   $allInnerClasses: { innerClass | $innerClass$ }; separator="\n"$
}