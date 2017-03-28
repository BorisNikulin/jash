import java.util.HashMap;
import java.util.function.IntPredicate;

/**
 * Class for storing RAM and ROM address of symbols in a hack asm file.
 * 
 * @author Boris
 *
 */
public class SymbolTable
{
	final public IntPredicate			FIRST_CHAR;
	final public IntPredicate			REST_CHAR;

	private HashMap<String, Integer>	symbolTable;

	/**
	 * Initializes {@code SymbolTable} with predefined symbols.
	 */
	public SymbolTable()
	{
		// R0 to R15 the aliases + kbd and screen + 10 for initial symbols
		symbolTable = new HashMap<>(15 + 7 + 10);

		for (int i = 0; i < 16; ++i)
		{
			symbolTable.put("R" + Integer.toString(i), i);
		}

		symbolTable.put("SP", 0);
		symbolTable.put("LCL", 1);
		symbolTable.put("ARG", 2);
		symbolTable.put("THIS", 3);
		symbolTable.put("THAT", 4);

		symbolTable.put("SCREEN", 16384);
		symbolTable.put("KBD", 24576);

		FIRST_CHAR = ((IntPredicate) Character::isLetter).or(c -> "_.$:".indexOf(c) != -1);
		REST_CHAR = FIRST_CHAR.or(Character::isDigit);
	}

	/**
	 * Adds the symbol address pair to the symbol table as long as the symbol is
	 * a valid symbol. Check {@code #contains(String)} before hand to avoid
	 * mutating entries since entries should be immutable once added.
	 * 
	 * @param symbol
	 *            - symbol of A or LABEl instruction.
	 * @param address
	 *            - address (RAM or ROM) to associate the symbol with.
	 * @return true if the symbol was first added else false if an entry was
	 *         mutated.
	 */
	public boolean addEntry(String symbol, int address)
	{
		if (!isvalidName(symbol))
		{
			return false;
		}

		// only works if no null key allowed which is true
		// also should always return true (added something new)
		// contains should be checked before hand
		return symbolTable.put(symbol, address) == null;
	}

	/**
	 * Test the {@link SymbolTable} for containment of the symbol in the table.
	 * 
	 * @param symbol
	 *            - the symbol to check.
	 * @return
	 */
	public boolean contains(String symbol)
	{
		return symbolTable.containsKey(symbol);
	}

	/**
	 * Get the address of the symbol or null.
	 * 
	 * @param symbol
	 *            - the symbol to get the address for.
	 * @return the address associated with the symbol or null if there is no
	 *         entry for that symbol.
	 */
	public int getAddress(String symbol)
	{
		return symbolTable.get(symbol);
	}

	/**
	 * Tests the A or LABEL symbol for validity according to hack asm rules.
	 * 
	 * @param symbol
	 *            - symbol to test.
	 * @return
	 */
	private boolean isvalidName(String symbol)
	{
		if (symbol == null || symbol.isEmpty())
		{
			return false;
		}

		// at least one char needed here for the substr which is true
		return FIRST_CHAR.test(symbol.charAt(0))
				&& symbol.substring(1, symbol.length()).chars()
						.allMatch(REST_CHAR);

		//TODO consider @formatter:off
//		return FIRST_CHAR.test(symbol.charAt(0))
//				&& symbol.chars()
//						.skip(1)
//						.allMatch(REST_CHAR);
//		@formatter:on
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 * 
	 * Convenience method mainly for debugging.
	 */
	public String toString()
	{
		return symbolTable.toString();
	}
}
