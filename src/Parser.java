import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Parser
{
	enum CommandType
	{
		NONE, A, C, L,
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
				commandType = CommandType.L;
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
			case L:
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
		if (commandType == CommandType.A)
		{
			// clean line must have at least length 1 (the '@')
			symbol = cleanLine.substring(1, cleanLine.length());
		}
		else
		{
			if (cleanLine.indexOf(')') == (cleanLine.length() - 1))
			{
				symbol = cleanLine.substring(1, cleanLine.length() - 1);
			}
			else
			{
				//TODO turn these errors into exceptions for proper error reporting
				System.err.println("Labels must have a closing parenthesis.");
				System.exit(3);
			}
		}
	}

	private void parseDest()
	{
		int destEndIndex = cleanLine.indexOf('=');
		if (destEndIndex >= 0)
		{
			destMnemonic = cleanLine.substring(0, destEndIndex);
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
