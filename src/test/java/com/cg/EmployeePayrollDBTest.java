package com.cg;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cg.EmpPayrollService.IOService;

public class EmployeePayrollDBTest {
	
	

	@Test
	public void givenEmployeePayrollDB_shouldReturnCount() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		List<EmployeePayrollData> empPayrollList = empPayRollService.readEmpPayrollData(IOService.DB_IO);
		Assert.assertEquals(3, empPayrollList.size());
	}
	
	@Test
	public void givenNewSalary_whenUpdatedShouldMatch() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		List<EmployeePayrollData> empPayrollList = empPayRollService.readEmpPayrollData(IOService.DB_IO);
		empPayRollService.updateEmployeeSalary("Bill", 150.00);
		boolean result = empPayRollService.checkEmployeePayrollInSyncWithDB("Bill");
		Assert.assertTrue(result);
	}
}