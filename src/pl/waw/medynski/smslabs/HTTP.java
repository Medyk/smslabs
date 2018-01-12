/**
 * History:
 *
 * 1 (2018-01-12) - Initial release.
 */
package pl.waw.medynski.smslabs;




/**
 * HTTP class.
 *
 * @author MDK
 * @version 1
 */
/* package */ class HTTP
{
    /**
     * HTTP GET method.
     */
    public static final String GET = "GET";
    
    
    /**
     * HTTP PUT method.
     */
    public static final String PUT = "PUT";
    
    
    /**
     * SMSLabs API - send SMS URL.
     */
    public static final String SEND_SMS_URL = "https://api.smslabs.net.pl/apiSms/sendSms";
    
    
    /**
     * SMSLabs API - SMS status URL.
     */
    public static final String SMS_STATUS_URL = "https://api.smslabs.net.pl/apiSms/smsStatus";
}
