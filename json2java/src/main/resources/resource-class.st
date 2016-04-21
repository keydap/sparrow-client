$if(package)$

package $package$;

import com.keydap.sparrow.*;
import java.util.List;

/*
 * Generated on $date$ using Keydap Sparrow's json2java plugin 
 */
/*
//$endpoint$
//$resourceDesc$
*/
$endif$
$visibility$ $static$ class $className$
{

   $allAttrs: { attr |
   $if(attr.extension)$
   @Extension("$attr.schema$")
   $endif$ 
   private $attr.type$ $attr.name$;}; separator="\n\n"$

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
    
    
    $allInnerClasses: { innerClass | $innerClass$ }; separator="\n\n"$
}