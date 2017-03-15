
package exceptions;

import java.util.Optional;

public class ParserExceptionBuilder
{
	private Integer				lineNum				= null;
	private String				problematicString	= null;

	// at least one of the following is needed for a successful build
	private Optional<String>	expected			= Optional.empty();
	private Optional<String>	why					= Optional.empty();

	// public or not? ... i already have a static instance getter so.. probably
	// not? ¯\_(ツ)_/¯
	protected ParserExceptionBuilder()
	{
	}

	/**
	 * Starts the builder.
	 * 
	 * @return A new instance of the builder
	 */
	public static ParserExceptionBuilder start()
	{
		return new ParserExceptionBuilder();
	}

	public ParserExceptionBuilder at(int lineNum)
	{
		this.lineNum = lineNum;
		return this;
	}

	public ParserExceptionBuilder in(String problematicString)
	{
		this.problematicString = problematicString;
		return this;
	}

	public ParserExceptionBuilder expected(String expectedChars)
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

	public ParserExceptionBuilder as(String why)
	{
		this.why = Optional.of(why);
		return this;
	}

	public ParserException build()
	{
		if (!canBuild())
		{
			throw new IllegalStateException(
					"not enough variables provided to build (need to call at, in, and one of expected and or as)");
		}

		StringBuilder message = new StringBuilder(expected.map(String::length).orElse(0)
				+ why.map(String::length).orElse(0));
		
		message.append(expected.orElse(""));
		
		if(expected.isPresent() && why.isPresent())
		{
			message.append(" as ");
		}
		
		message.append(why.orElse(""));
		
		return new ParserException(lineNum, problematicString, message.toString());
	}

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
