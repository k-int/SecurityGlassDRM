package com.k_int.sgdrm

class RepositoryMonitorException extends RuntimeException
{
	public RepositoryMonitorException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public RepositoryMonitorException(String message)
	{
		super(message);
	}

	public RepositoryMonitorException(Throwable cause)
	{
		super(cause);
	}
}
