import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import exceptions.AssemblerExceptionBuilder;

/**
 * Class for parsing a hack asm file.
 * 
 * @author Boris
 *
 */
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

	/**
	 * Parses a hack asm file.
	 * 
	 * @param inputFilePath - path to the hack asm file.
	 */
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

	/**
	 * Tests to see if you can call {@link #advance()}. Will close the stream if
	 * there are no more lines to parse
	 * 
	 * @return True if there are more commands to process in the file
	 */
	public boolean hasMoreCommands()
	{
		if (!inputFile.hasNextLine())
		{
			// should probably provide a close method
			inputFile.close();
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Advanced the parser, parsing the line, thus mutating most of it's fields
	 * such as commandType (to represent the command type of the parsed line) to
	 * the destMnemonic (if it had one).
	 */
	public void advance()
	{
		lineNumber++;
		rawLine = inputFile.nextLine();
		cleanLine();

		clearParsedValues();
		parse();
	}

	/**
	 * Removes all whitespace and everything past a comment leaving only the
	 * command to be parsed.
	 */
	private void cleanLine()
	{
		// pardon the small regex :D
		// I just don't want to spam lots of replaceAll s
		cleanLine = rawLine.replaceAll("\\s+", "");

		int commentIndex = rawLine.indexOf("//");
		if (commentIndex >= 0)
		{
			cleanLine = cleanLine.substring(0, commentIndex);
		}
	}

	/**
	 * Determine the command type of the line.
	 */
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

	/**
	 * Clears the parsed values for the next run of parsing.
	 */
	private void clearParsedValues()
	{
		// command type is always set so no need to clear it
		symbol = null;
		destMnemonic = null;
		compMnemonic = null;
		jumpMnemonic = null;
	}

	/**
	 * Parses the line based on the command type.
	 */
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

	/**
	 * Parses the line as an A or LABEL instruction. Mutates the
	 * {@link#symbol} to become the current instruction symbol.
	 */
	private void parseSymbol()
	{
		switch (commandType)
		{
			case A: // clean line must have at least length 1 (the '@')
				symbol = cleanLine.substring(1, cleanLine.length());
				if (symbol.isEmpty())
				{
					throw AssemblerExceptionBuilder.start()
							.at(lineNumber)
							.in(rawLine.trim())
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
					throw AssemblerExceptionBuilder.start()
							.at(lineNumber)
							.in(rawLine.trim())
							.expected(")")
							.build();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Parses the line as an C instruction. Mutates the {@link#destMnemonic} to
	 * become the current instruction's mnemonic for the field which may or may
	 * no be there.
	 */
	private void parseDest()
	{
		int destEndIndex = cleanLine.indexOf('=');
		if (destEndIndex >= 0)
		{
			destMnemonic = cleanLine.substring(0, destEndIndex);

			if (destMnemonic.length() < 1 || destMnemonic.length() > 3)
			{
				throw AssemblerExceptionBuilder.start()
						.at(lineNumber)
						.in(rawLine.trim())
						.as("Expected 1 to 3 letter destination mnemonic")
						.build();
			}
		}
		else
		{
			destMnemonic = "null";
		}
	}

	/**
	 * Parses the line as an C instruction. Mutates the {@link#compMnemonic} to
	 * become the current instruction's mnemonic for the field. This field is
	 * required.
	 */
	private void parseComp()
	{
		int compStartIndex = cleanLine.indexOf('=');
		compStartIndex = compStartIndex >= 0
				? compStartIndex + 1 : 0;

		int compEndIndex = cleanLine.indexOf(';');
		compEndIndex = compEndIndex >= 0
				? compEndIndex : cleanLine.length();

		compMnemonic = cleanLine.substring(compStartIndex, compEndIndex);
		
		//TODO maybe some error checking here
	}

	/**
	 * Parses the line as an C instruction. Mutates the {@link#destMnemonic} to
	 * become the current instruction's mnemonic for the field which may or may
	 * no be there.
	 */
	private void parseJump()
	{
		int jumpStartIndex = cleanLine.indexOf(';');
		jumpStartIndex += 1;

		if (jumpStartIndex > 0)
		{
			jumpMnemonic = cleanLine.substring(jumpStartIndex, cleanLine.length());

			if (jumpMnemonic.length() != 3)
			{
				throw AssemblerExceptionBuilder.start()
						.at(lineNumber)
						.in(rawLine.trim())
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
