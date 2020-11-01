package com.cg;



public class EmpPayrollException extends Exception{

	enum ExceptionType{
		CONNECTION_ERROR, INCORRECT_INFO, THREAD_INTERRUPTION
	}

	ExceptionType type;
	
	public EmpPayrollException(ExceptionType type, String message) {
		super(message);
		this.type = type;
	}
}
