import java.util.HashMap;
import java.util.function.IntPredicate;

public class SymbolTable
{
	final public IntPredicate			FIRST_CHAR;
	final public IntPredicate			REST_CHAR;

	private HashMap<String, Integer>	symbolTable;

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

	public boolean addEntry(String symbol, int address)
	{
		if (!isvalidName(symbol))
		{
			return false;
		}

		// only works if no null key allowed which is true
		// also should always return true (contains should be checked)
		return symbolTable.put(symbol, address) != null;
	}

	public boolean contains(String symbol)
	{
		return symbolTable.containsKey(symbol);
	}

	public int getAddress(String symbol)
	{
		return symbolTable.get(symbol);
	}

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

	public String toString()
	{
		return symbolTable.toString();
	}
}
