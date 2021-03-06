
//Author info here
//TODO: don't forget to document each method in all classes!
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import exceptions.AssemblerException;
import exceptions.AssemblerExceptionBuilder;

public class Assembler
{

	// ALGORITHM:
	// get input file name
	// create output file name and stream

	// create symbol table
	// do first pass to build symbol table (no output yet!)
	// do second pass to output translated ASM to HACK code

	// print out "done" message to user
	// close output file stream
	public static void main(String[] args)
	{
		String inputFileName, outputFileName;
		PrintWriter outputFile = null; // keep compiler happy
		SymbolTable symbolTable;
		// TODO remove following line?
		// int romAddress, ramAddress;

		// get input file name from command line or console input
		if (args.length == 1)
		{
			System.out.println("command line arg = " + args[0]);
			inputFileName = args[0];
		}
		else
		{
			Scanner keyboard = new Scanner(System.in);

			System.out.println("Please enter assembly file name you would like to assemble.");
			System.out.println("Don't forget the .asm extension: ");
			inputFileName = keyboard.nextLine();

			keyboard.close();
		}

		// TODO check if inputFile even exists

		String[] pathSections = dissectPath(inputFileName);

		outputFileName = pathSections[0] + pathSections[1] + ".hack";

		try
		{
			outputFile = new PrintWriter(outputFileName);
		}
		catch (FileNotFoundException ex)
		{
			System.err.println("Could not open output file " + outputFileName);
			System.err.println("Run program again, make sure you have write permissions, etc.");
			System.exit(0);
		}

		symbolTable = new SymbolTable();
		if (firstPass(inputFileName, symbolTable))
		{
			secondPass(inputFileName, symbolTable, outputFile);
		}

		outputFile.close();
	}

	// TODO: march through the source code without generating any code
	// for each label declaration (LABEL) that appears in the source code,
	// add the pair <LABEL, n> to the symbol table
	// n = romAddress which you should keep track of as you go through each line
	// HINT: when should rom address increase? What kind of commands?
	private static boolean firstPass(String inputFileName, SymbolTable symbolTable)
	{
		Parser parser = new Parser(inputFileName);
		int curROM = 0;

		boolean noParseErrors = true;

		while (parser.hasMoreCommands())
		{
			try
			{
				parser.advance();

				switch (parser.getCommandType())
				{
					case LABEL:
						if (!symbolTable.contains(parser.getSymbol()))
						{
							symbolTable.addEntry(parser.getSymbol(), curROM);
						}
						break;
					case A:
					case C:
						curROM++;
						break;
					default:
						break;
				}
			}
			catch (AssemblerException e)
			{
				System.err.println(e.parseFailDescriptor());
				noParseErrors = false;
			}
		}

		return noParseErrors;
	}

	// TODO: march again through the source code and process each line:
	// if the line is a c-instruction, simple (translate)
	// if the line is @xxx where xxx is a number, simple (translate)
	// if the line is @xxx and xxx is a symbol, look it up in the symbol
	// table and proceed as follows:
	// If the symbol is found, replace it with its numeric value and
	// and complete the commands translation
	// If the symbol is not found, then it must represent a new variable:
	// add the pair <xxx, n> to the symbol table, where n is the next
	// available RAM address, and complete the commands translation
	// HINT: when should rom address increase? What should ram address start
	// at? When should it increase? What do you do with L commands and No
	// commands?
	private static void secondPass(String inputFileName, SymbolTable symbolTable, PrintWriter outputFile)
	{
		Parser parser = new Parser(inputFileName);
		Code code = Code.getInstance();
		int nextRAM = 16;

		try
		{
			while (parser.hasMoreCommands())
			{
				parser.advance();

				switch (parser.getCommandType())
				{
					case A:
						if (symbolTable.contains(parser.getSymbol()))
						{
							outputFile.write('0');
							outputFile.write(Code.decimalToBinary(symbolTable.getAddress(parser.getSymbol())));
						}
						else if (parser.getSymbol().chars().allMatch(Character::isDigit))
						{
							int num = Integer.parseInt(parser.getSymbol());

							if (num < 0 || num >= (1 << 16))
							{
								throw AssemblerExceptionBuilder.start()
										.at(parser.getLineNumber())
										.in(parser.getRawLine().trim())
										.as(String.format("Constant exceeded 15 bits (0 to %d inclusive)",
												(1 << 16) - 1))
										.build();
							}
							else
							{
								outputFile.write('0');
								outputFile.write(Code.decimalToBinary(num));
							}
						}
						else
						{
							if (!symbolTable.addEntry(parser.getSymbol(), nextRAM))
							{
								// charAt(0) is safe since an empty symbol
								// parsed in the first pass would stop the
								// second pass
								if (!symbolTable.FIRST_CHAR.test(parser.getSymbol().charAt(0)))
									throw AssemblerExceptionBuilder.start()
											.at(parser.getLineNumber())
											.in(parser.getRawLine().trim())
											.as("Symbol's first character mustbe a letter"
													+ " or a character from \"_.$:\"")
											.build();
								else
								{
									throw AssemblerExceptionBuilder.start()
											.at(parser.getLineNumber())
											.in(parser.getRawLine().trim())
											.as("Symbol's non first characters must be a letter"
													+ ", a character from \"_.$:\", or a digit")
											.build();
								}
							}
							else
							{
								outputFile.write('0');
								outputFile.write(Code.decimalToBinary(nextRAM));
								nextRAM++;
							}

						}

						outputFile.write('\n');
						break;
					case C:
						String compBinary = code.getComp(parser.getCompMnemonic());
						String destBinary = code.getDest(parser.getDestMnemonic());
						String jumpBinary = code.getJump(parser.getJumpMnemonic());

						if (compBinary == null)
						{
							throw AssemblerExceptionBuilder.start()
									.at(parser.getLineNumber())
									.in(parser.getRawLine().trim())
									.as("Illegal Computation mnemonic")
									.build();
						}
						else if (destBinary == null)
						{
							throw AssemblerExceptionBuilder.start()
									.at(parser.getLineNumber())
									.in(parser.getRawLine().trim())
									.as("Illegal Destination mnemonic")
									.build();
						}
						else if (jumpBinary == null)
						{
							throw AssemblerExceptionBuilder.start()
									.at(parser.getLineNumber())
									.in(parser.getRawLine().trim())
									.as("Illegal Jump mnemonic")
									.build();
						}
						else
						{
							outputFile.write("111");
							outputFile.write(compBinary);
							outputFile.write(destBinary);
							outputFile.write(jumpBinary);
							outputFile.write('\n');
						}
						break;
					default:
						break;
				}
			}
		}
		catch (AssemblerException e)
		{
			System.err.println(e.parseFailDescriptor());
		}
	}

	/**
	 * Takes a file path as a string and returns an array with the first element
	 * being the part of the string before the name, the second element with the
	 * name, and the third part of the array with the extension. Where there is
	 * no applicable part from the file path, the corresponding section will be
	 * empty. For instance if there is no extension the the third element of the
	 * returned array will have an empty string. Concatenating the array
	 * together from the first to the last element will result in the original
	 * file path.
	 * 
	 * @param filePath
	 * @return
	 */
	public static String[] dissectPath(String filePath)
	{
		int nameStartIndex;
		if ((nameStartIndex = filePath.lastIndexOf('/')) >= 0)
		{
			nameStartIndex += 1;
		}
		else if ((nameStartIndex = filePath.lastIndexOf('\\')) >= 0)
		{
			nameStartIndex += 1;
		}
		else
		{
			nameStartIndex = 0;
		}

		int nameEndIndex = filePath.indexOf('.');
		if (nameEndIndex < 0)
		{
			nameEndIndex = filePath.length();
		}

		String[] dissectedPaths = new String[3];

		dissectedPaths[0] = filePath.substring(0, nameStartIndex);
		dissectedPaths[1] = filePath.substring(nameStartIndex, nameEndIndex);
		dissectedPaths[2] = filePath.substring(nameEndIndex, filePath.length());

		return dissectedPaths;
	}

}