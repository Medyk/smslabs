/**
 * History:
 *
 * 1 (2018-01-12) - Initial release.
 */
package pl.waw.medynski.smslabs;




/**
 * SMSLabsException class.
 *
 * @author Maciej Medyński
 * @version 1
 */
public class SMSLabsException extends RuntimeException
{
    /**
     * {@inheritDoc }
     */
    public SMSLabsException()
    {
        super();
    }
    
    
    /**
     * {@inheritDoc }
     * 
     * @param message
     */
    public SMSLabsException(final String message)
    {
        super(message);
    }
    
    
    /**
     * {@inheritDoc }
     * 
     * @param message
     * @param cause
     */
    public SMSLabsException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    
    /**
     * {@inheritDoc }
     * 
     * @param cause
     */
    public SMSLabsException(final Throwable cause)
    {
        super(cause);
    }
}
