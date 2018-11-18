package brightspark.runemagic.util;

public class RuneMagicException extends RuntimeException
{
	public RuneMagicException(String message, Object... args)
	{
		super(String.format(message, args));
	}

	public RuneMagicException(Throwable cause, String message, Object... args)
	{
		super(String.format(message, args), cause);
	}

	public RuneMagicException(Throwable cause)
	{
		super(cause);
	}
}
