/**
 * History:
 *
 * 1 (2018-01-12) - Initial release.
 */
package pl.waw.medynski.smslabs;




/**
 * Imports.
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;




/**
 * SMSLabsParam class.
 *
 * @author Maciej Medyński
 * @version 1
 */
/* package */ class SMSLabsParam
{
    /**
     * Constructor.
     * 
     * @param key
     * @param value 
     */
    public SMSLabsParam(final String key, final Object value)
    {
        this(key, value, false);
    }
    
    
    /**
     * Constructor.
     * 
     * @param key
     * @param value
     * @param optional 
     */
    public SMSLabsParam(final String key, final Object value, final boolean optional)
    {
        this.key = key;
        this.value = value;
        this.optional = optional;
    }
    
    
    /**
     * Query string representation of a phone number param.
     * 
     * @param value
     * @return 
     */
    public static String toPhoneNumber(final String value)
    {
        try
        {
            return "phone_number[]=" + URLEncoder.encode(value, "UTF-8");
        }
        catch (final UnsupportedEncodingException ex)
        {
            throw new SMSLabsException("Failed to encode phone number", ex);
        }
    }
    
    
    /**
     * Query string representation of a parameter.
     * 
     * @return 
     */
    public String toQueryString()
    {
        if (optional && null == value) return null;
        if (null == value)
        {
            throw new SMSLabsException("Parameter '" + key + "' is not set");
        }
        try
        {
            return URLEncoder.encode(key, "UTF-8") + '=' + URLEncoder.encode(value.toString(), "UTF-8");
        }
        catch (final UnsupportedEncodingException ex)
        {
            throw new SMSLabsException("Failed to encode param: " + key, ex);
        }
    }
    
    
    /**
     * Parameter key.
     */
    public String key;
    
    
    /**
     * Parameter value.
     */
    public Object value;
    
    
    /**
     * Is parameter optional.
     */
    public boolean optional;
}
