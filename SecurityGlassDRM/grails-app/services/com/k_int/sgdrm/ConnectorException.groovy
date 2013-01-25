package com.k_int.sgdrm

class ConnectorException extends RuntimeException
{
	public ConnectorException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ConnectorException(String message)
	{
		super(message);
	}

	public ConnectorException(Throwable cause)
	{
		super(cause);
	}
}
