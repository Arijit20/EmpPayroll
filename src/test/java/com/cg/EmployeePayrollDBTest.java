package com.cg;

import java.time.LocalDate;
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
	
	@Test
	public void givenDateRangeWhenRetrieved_ShouldReturnEmpCount() throws EmpPayrollException {
		EmpPayrollService empPayRollService = new EmpPayrollService();
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> empPayrollList = empPayRollService.getEmployeePayrollDataForDateRange(startDate, endDate);
		Assert.assertEquals(3, empPayrollList.size());
	}
}
