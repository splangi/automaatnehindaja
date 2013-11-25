package config;
// Configuration file exception class
// A.Greensted
// http://www.labbookpages.co.uk
// Version 1.0

public class ConfigException extends Exception
{
	public ConfigException(Throwable t)
	{
		super(t);
	}

	public ConfigException(String msg)
	{
		super(msg);
	}
}
