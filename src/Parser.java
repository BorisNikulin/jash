import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import exceptions.ParserExceptionBuilder;

public class Parser
{
	enum CommandType
	{
		NONE, A, C, LABEL,
	}

	private Scanner		inputFile;
	private int			lineNumber;
	private String		rawLine;

	private String		cleanLine;
	private CommandType	commandType;
	private String		symbol;
	private String		destMnemonic;
	private String		compMnemonic;
	private String		jumpMnemonic;

	public Parser(String inputFilePath)
	{
		lineNumber = 0;

		try
		{
			inputFile = new Scanner(Paths.get(inputFilePath));
		}
		catch (IOException e)
		{
			System.err.println("Could not open file \"" + inputFilePath + "\".");
			System.exit(2); // 2 = could not open file
		}
	}

	public boolean hasMoreCommands()
	{
		return inputFile.hasNextLine();
	}

	public void advance()
	{
		lineNumber++;
		rawLine = inputFile.nextLine();
		cleanLine = cleanLine(rawLine);

		clearParsedValues();
		parse();
	}

	private String cleanLine(String raw)
	{
		// pardon the small regex :D
		// I just don't want to spam lots of replaceAll s
		String clean = raw.replaceAll("\\s+", "");

		int commentIndex = raw.indexOf("//");
		if (commentIndex >= 0)
		{
			return clean.substring(0, commentIndex);
		}
		else
		{
			return clean;
		}
	}

	private void parseCommandType()
	{
		if (cleanLine == null || cleanLine.isEmpty())
		{
			commandType = CommandType.NONE;
			return;
		}

		switch (cleanLine.charAt(0))
		{
			case '@':
				commandType = CommandType.A;
				return;
			case '(':
				commandType = CommandType.LABEL;
				return;
			default:
				commandType = CommandType.C;
				return;
		}
	}

	private void clearParsedValues()
	{
		// command type is always set so no need to clear it
		symbol = null;
		destMnemonic = null;
		compMnemonic = null;
		jumpMnemonic = null;
	}

	private void parse()
	{
		parseCommandType();
		switch (commandType)
		{
			case A:
			case LABEL:
				parseSymbol();
				break;
			case C:
				parseDest();
				parseComp();
				parseJump();
				break;
			default:
				break;
		}
	}

	private void parseSymbol()
	{
		switch (commandType)
		{
			case A: // clean line must have at least length 1 (the '@')
				symbol = cleanLine.substring(1, cleanLine.length());
				if (symbol.isEmpty())
				{
					throw ParserExceptionBuilder.start()
							.at(lineNumber)
							.in(cleanLine)
							.as("A instruciton needs a symbol or a number")
							.build();
				}
				break;
			case LABEL:
				if (cleanLine.indexOf(')') == (cleanLine.length() - 1))
				{
					symbol = cleanLine.substring(1, cleanLine.length() - 1);
				}
				else
				{
					throw ParserExceptionBuilder.start()
							.at(lineNumber)
							.in(cleanLine)
							.expected(")")
							.build();
				}
				break;
			default:
				break;
		}
	}

	private void parseDest()
	{
		int destEndIndex = cleanLine.indexOf('=');
		if (destEndIndex >= 0)
		{
			destMnemonic = cleanLine.substring(0, destEndIndex);

			if (destMnemonic.length() < 1 || destMnemonic.length() > 3)
			{
				throw ParserExceptionBuilder.start()
						.at(lineNumber)
						.in(cleanLine)
						.as("Expected 1 to 3 letter destination mnemonic")
						.build();
			}
		}
		else
		{
			destMnemonic = "null";
		}
	}

	private void parseComp()
	{
		int compStartIndex = cleanLine.indexOf('=');
		compStartIndex = compStartIndex >= 0
				? compStartIndex + 1 : 0;

		int compEndIndex = cleanLine.indexOf(';');
		compEndIndex = compEndIndex >= 0
				? compEndIndex : cleanLine.length();

		compMnemonic = cleanLine.substring(compStartIndex, compEndIndex);
	}

	private void parseJump()
	{
		int jumpStartIndex = cleanLine.indexOf(';');
		jumpStartIndex += 1;

		if (jumpStartIndex > 0)
		{
			jumpMnemonic = cleanLine.substring(jumpStartIndex, cleanLine.length());

			if (jumpMnemonic.length() != 3)
			{
				throw ParserExceptionBuilder.start()
						.at(lineNumber)
						.in(cleanLine)
						.as("Expected 3 letter jump mnemonic")
						.build();
			}
		}
		else
		{
			jumpMnemonic = "null";
		}
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * @return the rawLine
	 */
	public String getRawLine()
	{
		return rawLine;
	}

	/**
	 * @return the cleanLine
	 */
	public String getCleanLine()
	{
		return cleanLine;
	}

	/**
	 * @return the commandType
	 */
	public CommandType getCommandType()
	{
		return commandType;
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol()
	{
		return symbol;
	}

	/**
	 * @return the destMnemonic
	 */
	public String getDestMnemonic()
	{
		return destMnemonic;
	}

	/**
	 * @return the compMnemonic
	 */
	public String getCompMnemonic()
	{
		return compMnemonic;
	}

	/**
	 * @return the jumpMnemonic
	 */
	public String getJumpMnemonic()
	{
		return jumpMnemonic;
	}
}
