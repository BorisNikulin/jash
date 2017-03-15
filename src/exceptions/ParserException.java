
package exceptions;

public class ParserException extends RuntimeException
{

	private static final long	serialVersionUID	= -3354399774996988932L;

	// super.message will have the reason

	private int					line;
	private String				problematicString;

	public ParserException(int line, String problematicString, String message)
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
}
