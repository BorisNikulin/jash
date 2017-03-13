
//Author info here
//TODO: don't forget to document each method in all classes!
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

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
		int romAddress, ramAddress;

		// // get input file name from command line or console input
		// if (args.length == 1)
		// {
		// System.out.println("command line arg = " + args[0]);
		// inputFileName = args[0];
		// }
		// else
		// {
		// Scanner keyboard = new Scanner(System.in);
		//
		// System.out.println("Please enter assembly file name you would like to
		// assemble.");
		// System.out.println("Don't forget the .asm extension: ");
		// inputFileName = keyboard.nextLine();
		//
		// keyboard.close();
		// }
		//
		// outputFileName = inputFileName.substring(0,
		// inputFileName.lastIndexOf('.')) + ".hack";
		//
		// try
		// {
		// outputFile = new PrintWriter(new FileOutputStream(outputFileName));
		// }
		// catch (FileNotFoundException ex)
		// {
		// System.err.println("Could not open output file " + outputFileName);
		// System.err.println("Run program again, make sure you have write
		// permissions, etc.");
		// System.exit(0);
		// }

		try
		{
			symbolTable = new SymbolTable();
			firstPass(null, symbolTable);
			secondPass(null, symbolTable, new PrintWriter("out.hack"));
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// TODO: march through the source code without generating any code
	// for each label declaration (LABEL) that appears in the source code,
	// add the pair <LABEL, n> to the symbol table
	// n = romAddress which you should keep track of as you go through each line
	// HINT: when should rom address increase? What kind of commands?
	private static void firstPass(String inputFileName, SymbolTable symbolTable)
	{
		Parser parser = new Parser("C:\\Users\\Boris\\Desktop\\nand2tetris\\projects\\06\\pong\\Pong.asm");
		int curROM = 0;

		while (parser.hasMoreCommands())
		{
			parser.advance();
			switch (parser.getCommandType())
			{
				case L:
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
		Parser parser = new Parser("C:\\Users\\Boris\\Desktop\\nand2tetris\\projects\\06\\pong\\Pong.asm");
		Code code = Code.getInstance();
		int nextRAM = 16;

		while (parser.hasMoreCommands())
		{
			parser.advance();
			switch (parser.getCommandType())
			{
				case A:
					// many ways to check for a number (all are annoying)
					// instead ill use the fact that a number is never a key in
					// the symbol table
					// i have to check the table before retrieving a symbol
					// anyway
					if (symbolTable.contains(parser.getSymbol()))
					{
						outputFile.write(Code.decimalToBinary(symbolTable.getAddress(parser.getSymbol())));
					}
					else
					{
						if (parser.getSymbol().chars().allMatch(Character::isDigit))
						{
							// TODO test num for 15 bit width
							outputFile.write(Code.decimalToBinary(Integer.parseInt(parser.getSymbol())));
						}
						else
						{
							// TODO check if inserted and respond accordingly
							// (proceed or signal exception)
							symbolTable.addEntry(parser.getSymbol(), nextRAM);
							outputFile.write(Code.decimalToBinary(nextRAM));
							nextRAM++;

						}

					}
					outputFile.write('\n');
					break;
				case C:
					outputFile.write("111");
					outputFile.write(code.getComp(parser.getCompMnemonic()));
					outputFile.write(code.getDest(parser.getDestMnemonic()));
					outputFile.write(code.getJump(parser.getJumpMnemonic()));
					outputFile.write('\n');
					break;
				default:
					break;
			}
		}

		outputFile.close();
	}

}