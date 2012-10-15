package com.k_int.sgdrm

class OAIException extends RuntimeException
{
	public OAIException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public OAIException(String message)
	{
		super(message);
	}

	public OAIException(Throwable cause)
	{
		super(cause);
	}
}
