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

	/**
	 * Non-thread safe constructor for the singleton that loads the predefined
	 * mnemonics and their translation.
	 */
	protected Code()
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
	 * Non-Thread Safe instance getter for the {@link Code} singleton.
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

	/**
	 * Translates the given mnemonic.
	 * 
	 * @param mnemonic
	 *            - a computation mnemonic
	 * @return the binary translation or null if no such translation exists.
	 */
	public String getComp(String mnemonic)
	{
		return compCodes.getProperty(mnemonic, null);
	}

	/**
	 * Translates the given mnemonic.
	 * 
	 * @param mnemonic
	 *            - a destination mnemonic
	 * @return the binary translation or null if no such translation exists.
	 */
	public String getDest(String mnemonic)
	{
		return destCodes.getProperty(mnemonic, null);
	}

	/**
	 * Translates the given mnemonic.
	 * 
	 * @param mnemonic
	 *            - a jump mnemonic
	 * @return the binary translation or null if no such translation exists.
	 */
	public String getJump(String mnemonic)
	{
		return jumpCodes.getProperty(mnemonic, null);
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

	// version three optimized for 15 bits and auto pads to 15 bits (big endian)
	/**
	 * Converts the given decimal number into a 15 bit number with padded 0's in
	 * big endian. If the number is larger than 15 bits, then only the lower 15
	 * bits are represented.
	 * 
	 * @param dec
	 *            - decimal number
	 * @return big endian 15 bit representation
	 */
	public static String decimalToBinary(int dec)
	{
		StringBuilder bin = new StringBuilder(15);

		for (short pos = 14; pos >= 0; pos--)
		{
			bin.append((char) (((dec >>> pos) & 1) + '0'));
		}

		return bin.toString();
	}
}
