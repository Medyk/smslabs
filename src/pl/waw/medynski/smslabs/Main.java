/**
 * History:
 *
 * 1 (2018-01-12) - Initial release.
 */
package pl.waw.medynski.smslabs;




/**
 * Main class.
 *
 * @author Maciej Medyński
 * @version 1
 */
public class Main
{
    /**
     * Main method.
     * 
     * @param args 
     */
    public static void main(final String[] args)
    {
        SMSLabs.init("appKey", "secretKey"); // call once before use
        
        SMSLabs api = new SMSLabs("SMS INFO").addPhoneNumber("+48123123123").addPhoneNumber("+48456456456"); // chainable methods
        
        String result = api.sendSms("Hello world!");
        System.out.println("sendSms: " + result); // JSON string
        
        result = api.smsStatus("smsid");
        System.out.println("smsStatus: " + result); // JSON string
        
        try
        {
            api.addPhoneNumber("bad number");
        }
        catch (final SMSLabsException ex)
        {
            System.out.println("Only one unchecked exception type (masks all internal checked exceptions)");
            System.out.println(ex);
        }
        
        api.clear("SMS INFO").addPhoneNumber("+48147147147", null, "+48258258258").sendSms("Fresh start!"); // can be used multiple times
        
        api.debug(System.err); // simple debug
        
        result = api.sendSms("SMS INFO", "Message!", "+48123123123", "+48456456456"); // all-in-one send method
        System.out.println("all-in-one: " + result);
    }
}
