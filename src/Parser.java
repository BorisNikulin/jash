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
		String clean = raw.replaceAll("\\s", " ");

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

	private CommandType parseCommandType()
	{
		if (cleanLine == null || cleanLine.isEmpty())
		{
			return CommandType.NONE;
		}

		switch (cleanLine.charAt(0))
		{
			case '@':
				return CommandType.A;
			case '(':
				return CommandType.L;
			default:
				return CommandType.C;
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
				parseSymbol();
			case C:
				parseDest();
				parseComp();
				parseJump();
			default:
				return;
		}
	}

	private void parseSymbol()
	{
		// clean line must have at least length 1 (the '@')
		symbol = cleanLine.substring(1, cleanLine.length());
	}

	private void parseDest()
	{
		int destEndIndex = cleanLine.indexOf('=');
		if (destEndIndex >= 0)
		{
			destMnemonic = cleanLine.substring(0, destEndIndex);
		}
	}

	private void parseComp()
	{
		int compStartIndex = cleanLine.indexOf('=');
		compStartIndex = compStartIndex >= 0 ? compStartIndex + 1 : 0;

		int compEndIndex = cleanLine.indexOf(';');
		compEndIndex = compEndIndex >= 0 ? compEndIndex : cleanLine.length();

		compMnemonic = cleanLine.substring(compStartIndex, compEndIndex);
	}

	private void parseJump()
	{
		int jumpStartIndex = cleanLine.indexOf(';');
		jumpStartIndex += 1;

		if (jumpStartIndex >= 0)
		{
			jumpMnemonic = cleanLine.substring(jumpStartIndex, cleanLine.length());
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
	 * @param lineNumber
	 *            the lineNumber to set
	 */
	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	/**
	 * @return the rawLine
	 */
	public String getRawLine()
	{
		return rawLine;
	}

	/**
	 * @param rawLine
	 *            the rawLine to set
	 */
	public void setRawLine(String rawLine)
	{
		this.rawLine = rawLine;
	}

	/**
	 * @return the cleanLine
	 */
	public String getCleanLine()
	{
		return cleanLine;
	}

	/**
	 * @param cleanLine
	 *            the cleanLine to set
	 */
	public void setCleanLine(String cleanLine)
	{
		this.cleanLine = cleanLine;
	}

	/**
	 * @return the commandType
	 */
	public CommandType getCommandType()
	{
		return commandType;
	}

	/**
	 * @param commandType
	 *            the commandType to set
	 */
	public void setCommandType(CommandType commandType)
	{
		this.commandType = commandType;
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol()
	{
		return symbol;
	}

	/**
	 * @param symbol
	 *            the symbol to set
	 */
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}

	/**
	 * @return the destMnemonic
	 */
	public String getDestMnemonic()
	{
		return destMnemonic;
	}

	/**
	 * @param destMnemonic
	 *            the destMnemonic to set
	 */
	public void setDestMnemonic(String destMnemonic)
	{
		this.destMnemonic = destMnemonic;
	}

	/**
	 * @return the compMnemonic
	 */
	public String getCompMnemonic()
	{
		return compMnemonic;
	}

	/**
	 * @param compMnemonic
	 *            the compMnemonic to set
	 */
	public void setCompMnemonic(String compMnemonic)
	{
		this.compMnemonic = compMnemonic;
	}

	/**
	 * @return the jumpMnemonic
	 */
	public String getJumpMnemonic()
	{
		return jumpMnemonic;
	}

	/**
	 * @param jumpMnemonic
	 *            the jumpMnemonic to set
	 */
	public void setJumpMnemonic(String jumpMnemonic)
	{
		this.jumpMnemonic = jumpMnemonic;
	}
}
