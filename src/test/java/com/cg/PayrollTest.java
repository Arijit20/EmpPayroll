package com.cg;

import org.junit.Test;

import com.cg.EmpPayrollService;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Assert;

public class PayrollTest {
	
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchNumberOfEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmployees = { 
				new EmployeePayrollData(1, "Arijit dey", 100.0),
				new EmployeePayrollData(2, "Kalyan Arigela", 110.0),
				new EmployeePayrollData(3, "Anand Kumar", 150.0) };
		EmpPayrollService empPayrollService; 
		empPayrollService = new EmpPayrollService(Arrays.asList(arrayOfEmployees));
		empPayrollService.writeEmpPayrollData(EmpPayrollService.IOService.FILE_IO);
		empPayrollService.printData(EmpPayrollService.IOService.FILE_IO);
		Assert.assertEquals(3, empPayrollService.countEntries(EmpPayrollService.IOService.FILE_IO));
	}
	
	@Test
	public void givenEmpDataInFileShouldRead() {
		EmpPayrollService empPayrollService = new EmpPayrollService();
		empPayrollService.readEmployeePayrollData(EmpPayrollService.IOService.FILE_IO);
		long entries = empPayrollService.countEntries(EmpPayrollService.IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}
}
