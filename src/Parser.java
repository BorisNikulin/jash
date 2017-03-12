import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Parser
{
	enum CommandType
	{
		NONE, A, C, L,
	}

	Scanner		inputFile;
	int			lineNumber;
	String		rawLine;

	String		cleanLine;
	CommandType	commandType;
	String		symbol;
	String		destMnemonic;
	String		compMnemonic;
	String		jumpMnemonic;

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

	public String cleanLine(String raw)
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
	
	public CommandType parseCommand(String clean)
	{
	    if(clean == null || clean.isEmpty())
	    {
	        return CommandType.NONE;
	    }
	    
	    switch (clean.charAt(0))
	    {
	        case '@': return CommandType.A;
	        case '(': return CommandType.L;
	        default : return CommandType.C;
	    }
	}
}
