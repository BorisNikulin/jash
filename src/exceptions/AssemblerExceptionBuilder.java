
package exceptions;

import java.util.Optional;

/**
 * A builder for {@link AssemblerException} to help with readability and to help
 * with creating the message.
 * 
 * @author Boris
 *
 */
public class AssemblerExceptionBuilder
{
	private Integer				lineNum				= null;
	private String				problematicString	= null;

	// at least one of the following is needed for a successful build
	private Optional<String>	expected			= Optional.empty();
	private Optional<String>	why					= Optional.empty();

	// public or not? ... i already have a static instance getter so.. probably
	// not? ¯\_(ツ)_/¯
	protected AssemblerExceptionBuilder()
	{
	}

	/**
	 * Starts the builder.
	 * 
	 * @return A new instance of the builder
	 */
	public static AssemblerExceptionBuilder start()
	{
		return new AssemblerExceptionBuilder();
	}

	/**
	 * Sets the line number of the {@link AssemblerException}.
	 * 
	 * @param lineNum
	 * @return the {@link AssemblerExceptionBuilder} itself
	 */
	public AssemblerExceptionBuilder at(int lineNum)
	{
		this.lineNum = lineNum;
		return this;
	}

	/**
	 * Sets the problematicString (where the exception happened in) of the
	 * {@link AssemblerException}.
	 * 
	 * @param problematicString
	 *            - source code of where the exception happend.
	 * @return the {@link AssemblerExceptionBuilder} itself
	 */
	public AssemblerExceptionBuilder in(String problematicString)
	{
		this.problematicString = problematicString;
		return this;
	}

	/**
	 * Helper method for creating a message when expecting characters but not
	 * receiving them. Can be combined with {@link #as(String)}.
	 * 
	 * @param expectedChars
	 *            - a list of expected chars as a String
	 * @return the {@link AssemblerExceptionBuilder} itself
	 */
	public AssemblerExceptionBuilder expected(String expectedChars)
	{
		if (expectedChars.length() == 1)
		{
			expected = Optional.of(String.format("'%s' expected", expectedChars));
		}
		else if (expectedChars.length() == 2)
		{
			expected = Optional
					.of(String.format("'%c' or '%c' expected", expectedChars.charAt(0), expectedChars.charAt(1)));
		}
		else if (expectedChars.length() > 2)
		{
			StringBuilder expectedCharsStr = expectedChars.chars()
					.limit(expectedChars.length() - 1)
					.collect(StringBuilder::new,
							(strBuilder, charAsInt) -> strBuilder.append('\'').appendCodePoint(charAsInt).append("', "),
							StringBuilder::append);

			expectedCharsStr.append("or '")
					.append(expectedCharsStr.charAt(expectedCharsStr.length() - 1))
					.append("' expected");

			expected = Optional.of(expectedCharsStr.toString());
		}
		else
		{
			throw new IllegalArgumentException("expectedChars needs to have at least one char");
		}

		return this;
	}

	/**
	 * Helper method for creating a message when literally explain why the
	 * exception happen. Can be combined with {@link #expected(String)}.
	 * 
	 * @param why
	 *            - a reason for the exception.
	 * @return the {@link AssemblerExceptionBuilder} itself
	 */
	public AssemblerExceptionBuilder as(String why)
	{
		this.why = Optional.of(why);
		return this;
	}

	/**
	 * Builds the {@link AssemblerExceptionBuilder} if
	 * <ul>
	 * <li>{@link #at(int)}</li>
	 * <li>{@link #in(String)}</li>
	 * </ul>
	 * and one or more of
	 * <ul>
	 * <li>{@link #expected(String)}</li>
	 * <li>{@link #as(String)}</li>
	 * </ul>
	 * were called. Otherwise {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if not enough information is provided.
	 * @return an instance of {@link AssemblerException} with the provided
	 *         information.
	 */
	public AssemblerException build()
	{
		if (!canBuild())
		{
			throw new IllegalStateException(
					"not enough variables provided to build (need to call at, in, and one of expected and or as)");
		}

		StringBuilder message = new StringBuilder(expected.map(String::length).orElse(0)
				+ why.map(String::length).orElse(0));

		message.append(expected.orElse(""));

		if (expected.isPresent() && why.isPresent())
		{
			message.append(" as ");
		}

		message.append(why.orElse(""));

		return new AssemblerException(lineNum, problematicString, message.toString());
	}

	/**
	 * Helper method for checking if the builder is in a valid state.
	 * 
	 * @return true if can create an {@link AssemblerException} instance.
	 */
	private boolean canBuild()
	{
		if (lineNum == null
				|| problematicString == null
				|| (!expected.isPresent() && !why.isPresent()))
		{
			return false;
		}

		return true;
	}
}
