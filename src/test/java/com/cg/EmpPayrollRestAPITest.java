package com.cg;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class EmpPayrollRestAPITest {

	@Before
	public void initialize() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}
	
	private EmployeePayrollData[] getEmpList() {
		Response response = RestAssured.get("/employees");
		EmployeePayrollData[] arrOfEmp = new Gson().fromJson(response.asString(),  EmployeePayrollData[].class);
		return arrOfEmp;
	}
	
	private Response addEmpToJsonServer(EmployeePayrollData employeePayrollData) {
		String empJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employees");
	}
	
	@Test
	public void givenEmployeeDataInJsonServer_WhenRetrived_ShouldMatchCount() {
		EmployeePayrollData[] arrOfEmp = getEmpList();
		EmpPayrollService empPayrollService;
		empPayrollService = new EmpPayrollService(Arrays.asList(arrOfEmp));
		long entries = empPayrollService.countEntries(EmpPayrollService.IOService.REST_IO);
		Assert.assertEquals(3, entries);
	}
	
	@Test
	public void givenNewEmployee_WhenAdded_ShouldMatch201ResponseAndCount() throws EmpPayrollException {
		EmployeePayrollData[] arrOfEmp = getEmpList();
		EmpPayrollService empPayrollService;
		empPayrollService = new EmpPayrollService(Arrays.asList(arrOfEmp));
		EmployeePayrollData employeePayrollData = new EmployeePayrollData(0, "partha", 6000.0);
		Response response = addEmpToJsonServer(employeePayrollData);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		empPayrollService.addEmployeeToPayroll(employeePayrollData, EmpPayrollService.IOService.REST_IO);
		long entries = empPayrollService.countEntries(EmpPayrollService.IOService.REST_IO);
		Assert.assertEquals(4, entries);
	}
}
