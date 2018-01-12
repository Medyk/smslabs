/**
 * History:
 *
 * 1 (2018-01-12) - Initial release.
 */
package pl.waw.medynski.smslabs;




/**
 * Imports.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;




/**
 * SMSLabs class.
 *
 * @author Maciej Medyński
 * @version 1
 */
public class SMSLabs
{
    /**
     * Constructor.
     */
    public SMSLabs()
    {
        this(null);
    }
    
    
    /**
     * Constructor.
     * 
     * @param senderId 
     */
    public SMSLabs(final String senderId)
    {
        _parameters = new HashMap<>();
        _phoneNumbers = new HashSet<>();
        setSenderId(senderId);
    }
    
    
    /**
     * Init SMSLabs API.
     * 
     * @param appKey
     * @param secretKey 
     */
    public static void init(final String appKey, final String secretKey)
    {
        try
        {
            String key = appKey + ':' + secretKey;
            _basicAuth = "Basic " + Base64.getEncoder().encodeToString(key.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new SMSLabsException("Failed to set SMSLabs API secret key", ex);
        }
    }
    
    
    /**
     * Add phone number to recipients list.
     * 
     * @param phoneNumbers
     * @return 
     */
    public SMSLabs addPhoneNumber(final String ... phoneNumbers)
    {
        if (null != phoneNumbers)
        {
            for (final String phoneNumber : phoneNumbers)
            {
                if (null == phoneNumber) continue;
                if (!_pattern.matcher(phoneNumber).matches())
                {
                    throw new SMSLabsException("Invalid phone number. Accepted format '+48xxxxxxxxx'");
                }
                _phoneNumbers.add(phoneNumber);
            }
        }
        return this;
    }
    
    
    /**
     * Clear builder.
     * 
     * @return 
     */
    public SMSLabs clear()
    {
        _parameters.clear();
        _phoneNumbers.clear();
        return this;
    }
    
    
    /**
     * Clear builder and set new sender ID.
     * 
     * @param senderId
     * @return 
     */
    public SMSLabs clear(final String senderId)
    {
        return this.clear().setSenderId(senderId);
    }
    
    
    /**
     * Simple debug.
     * 
     * @param out
     */
    public void debug(final PrintStream out)
    {
        out.println("SMSLabs debug");
        _phoneNumbers.stream().filter(p -> null!=p).map(p -> "phone_number[]="+p).forEach(out::println);
        _parameters.values().stream().filter(p -> null!=p).map(p -> p.key+'='+p.value).forEach(out::println);
    }
    
    
    /**
     * Remove phone number from recipients list.
     * 
     * @param phoneNumber
     * @return 
     */
    public SMSLabs removePhoneNumber(final String phoneNumber)
    {
        _phoneNumbers.remove(phoneNumber);
        return this;
    }
    
    
    /**
     * Send SMS to recipients.
     * 
     * @return 
     */
    public String sendSms()
    {
        if (_phoneNumbers.isEmpty())
        {
            throw new SMSLabsException("Required parameter 'phone_number' is not set");
        }
        if (!_parameters.containsKey("sender_id"))
        {
            throw new SMSLabsException("Required parameter 'sender_id' is not set");
        }
        if (!_parameters.containsKey("message"))
        {
            throw new SMSLabsException("Required parameter 'message' is not set");
        }
        
        try
        {
            // Build query
            final List<String> params = _phoneNumbers.stream().map(SMSLabsParam::toPhoneNumber).collect(Collectors.toList());
            _parameters.values().stream().map(SMSLabsParam::toQueryString).filter(e -> null != e).forEach(params::add);
            final String query = params.stream().collect(Collectors.joining("&"));
            
            // HTTP connection
            final URL url = new URL(HTTP.SEND_SMS_URL);
            final HttpURLConnection connection = _getConnection(HTTP.PUT, url);
            
            // Send data
            try (final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream()))
            {
                out.write(query);
            }
            
            // Get response
            return _getResponse(connection);
        }
        catch (final IOException ex)
        {
            throw new SMSLabsException("SMS send failed", ex);
        }
    }
    
    
    /**
     * Send SMS message to recipients.
     * 
     * @param message
     * @return 
     */
    public String sendSms(final String message)
    {
        return this.setMessage(message).sendSms();
    }
    
    
    /**
     * Send SMS message with senderId to recipients.
     * Clears builder before use.
     * 
     * @param senderId
     * @param message
     * @param phoneNumbers
     * @return 
     */
    public String sendSms(final String senderId, final String message, final String ... phoneNumbers)
    {
        this.clear().setSenderId(senderId).setMessage(message);
        Stream.of(phoneNumbers).forEach(this::addPhoneNumber);
        return this.sendSms();
    }
    
    
    /**
     * Set message.
     * 
     * @param message
     * @return 
     */
    public SMSLabs setMessage(final String message)
    {
        if (null != message)
        {
            _setParam("message", message, false);
        }
        return this;
    }
    
    
    /**
     * Set sender ID.
     * 
     * @param senderId
     * @return 
     */
    public final SMSLabs setSenderId(final String senderId)
    {
        if (null != senderId)
        {
            _setParam("sender_id", senderId, false);
        }
        return this;
    }
    
    
    /**
     * Get SMS status.
     * 
     * @param smsId
     * @return 
     */
    public String smsStatus(final String smsId)
    {
        try
        {
            // HTTP connection
            SMSLabsParam param = new SMSLabsParam("id", smsId, false);
            URL url = new URL(HTTP.SMS_STATUS_URL + '?' + param.toQueryString());
            final HttpURLConnection connection = _getConnection(HTTP.GET, url);
            
            // Get response
            return _getResponse(connection);
        }
        catch (final IOException ex)
        {
            throw new SMSLabsException("SMS status failed", ex);
        }
    }
    
    
    
    
    /**
     * Get HTTP connection.
     * 
     * @param url
     * @param doInput
     * @param doOutput
     * @return
     * @throws IOException 
     */
    private HttpURLConnection _getConnection(final String method, final URL url) throws IOException
    {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", _basicAuth);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
    
    
    /**
     * Get response from server.
     * 
     * @param connection
     * @return
     * @throws IOException 
     */
    private String _getResponse(final HttpURLConnection connection) throws IOException
    {
        // Check response
        final int code = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK != code)
        {
            return "{\"code\":"+code+",\"status\":\"failed\",\"message\":\""+connection.getResponseMessage()+"\"}";
        }

        // Read response
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())))
        {
            final StringBuilder sb = new StringBuilder(1024);
            String line;
            while (null != (line = in.readLine()))
            {
                sb.append(line);
            }
            return sb.toString();
        }
    }
    
    
    /**
     * Set query parameter.
     * 
     * @param key
     * @param value
     * @param optional 
     */
    private void _setParam(final String key, final Object value, final boolean optional)
    {
        _parameters.put(key, new SMSLabsParam(key, value, optional));
    }
    
    
    /**
     * SMSLabs API secret key.
     */
    private static String _basicAuth = null;
    
    
    /**
     * Phone number check pattern.
     */
    private static final Pattern _pattern = Pattern.compile("\\+48[0-9]{9}");
    
    
    /**
     * Query parameters.
     */
    private final Map<String, SMSLabsParam> _parameters;
    
    
    /**
     * Set of phone numbers in '+48xxxxxxxxx' format.
     */
    private final Set<String> _phoneNumbers;
}
