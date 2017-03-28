
package exceptions;

/**
 * Class for signaling an assembling error
 * 
 * @author Boris
 *
 */
public class AssemblerException extends RuntimeException
{

	private static final long	serialVersionUID	= -3354399774996988932L;

	// super.message will have the reason

	private int					line;
	private String				problematicString;

	/**
	 * Constructs the exception with the given values. Use
	 * {@code AssemblerExceptionBuilder} instead to construct exceptions for
	 * readability and helper functions when creating the message.
	 * 
	 * @param line
	 *            - the line number of where the problem happened in the source
	 *            text.
	 * @param problematicString
	 *            - the string that caused the exception.
	 * @param message
	 *            - the reason for the exception.
	 * 
	 * @see AssemblerExceptionBuilder
	 */
	public AssemblerException(int line, String problematicString, String message)
	{
		super(message);
		this.line = line;
		this.problematicString = problematicString;
	}

	/**
	 * @return the line
	 */
	public int getLine()
	{
		return line;
	}

	/**
	 * @return the problematicString
	 */
	public String getProblematicString()
	{
		return problematicString;
	}

	/**
	 * @return A formatted string for displaying the error to the user. Use this instead of {@link #toString()}.
	 */
	public String parseFailDescriptor()
	{
		return String.format("Line %d: %s\n\tin \"%s\"", line, getMessage(), problematicString);
	}
}
