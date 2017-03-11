import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Singleton for translating mnemonics to corresponding hack machine bit strings
 * 
 * @author Boris
 *
 */
public class Code
{
	private static Code	instance	= null;

	private Properties	compCodes	= new Properties();
	private Properties	destCodes	= new Properties();
	private Properties	jumpCodes	= new Properties();

	protected Code ()
	{
		try
		{
			compCodes.load(Files.newInputStream(Paths.get("codes/compCodes.properties")));
			destCodes.load(Files.newInputStream(Paths.get("codes/destCodes.properties")));
			jumpCodes.load(Files.newInputStream(Paths.get("codes/jumpCodes.properties")));
		}
		catch (IOException e)
		{
			System.err.println("Translation Codes Not Found.");
			System.exit(1); // 1 = codes not found
		}
	}

	/**
	 * Not Thread Safe
	 * 
	 * @return The singleton instance
	 */
	public static Code getInstance()
	{
		if (instance == null)
		{
			instance = new Code();
		}

		return instance;
	}

	public String getComp(String mnemonic)
	{
		return this.getComp(mnemonic);
	}

	public String getDest(String mnemonic)
	{
		return this. getDest(mnemonic);
	}

	public String getJump(String mnemonic)
	{
		return this. getJump(mnemonic);
	}

	/*	@formatter:off
    public static String decimalToBinary(int decimal)
	{
	  return decimalToBinary(decimal, new StringBuilder());
	}

	private static String decimalToBinary(int num, StringBuilder binary)
	{
	    if(num > 0)
	    {
	        if(num % 2 == 0)
	        {
	            binary.insert(0, "0");
	        }
	        else
	        {
	            num -= 1;
	            binary.insert(0, "1");
	        }
	          
	        decimalToBinary(num / 2, binary);
	    }
	    else if(num == 0)
	    {
	        binary.insert(0, "0");
	    }
	    
	  return binary.toString();
	} */

/*	// version two using bit magic
	public static String decimalToBinary(int decimal)
	{
		// give it 64 capacity (long is 64 bits) and be done with it
		StringBuilder binary = new StringBuilder(64);
		while (decimal > 0)
		{
			binary = binary.append((char) ((decimal & 1) + '0'));
			decimal = decimal >>> 1;
		}

		// append and 1 reverse other than constant bit shuffling
		return binary.reverse().toString();
	} 	@formatter:on */
	
	//version three optimized for 15 bits and auto pads to 15 bits (big endian)
	public static String decimalToBinary(int dec)
	{
		StringBuilder bin = new StringBuilder();
		short pos = 15;
		
		while(pos >= 0)
		{
			bin = bin.append((char) (((dec >>> pos) & 1) + '0'));
			pos--;
		}
		
		return bin.toString();
	}
}
