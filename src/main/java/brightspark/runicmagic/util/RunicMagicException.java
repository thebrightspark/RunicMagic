package brightspark.runicmagic.util;

public class RunicMagicException extends RuntimeException
{
	public RunicMagicException(String message, Object... args)
	{
		super(String.format(message, args));
	}

	public RunicMagicException(Throwable cause, String message, Object... args)
	{
		super(String.format(message, args), cause);
	}

	public RunicMagicException(Throwable cause)
	{
		super(cause);
	}
}
