package com.cg;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.cg.EmpPayrollService.IOService;

public class EmpPayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmpPayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmpPayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = new ArrayList<>(employeePayrollList);
	}

	public void readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			Scanner consoleInputReader = new Scanner(System.in);
			System.out.print("Enter Employee ID: ");
			int id = consoleInputReader.nextInt();
			System.out.print("Enter Employee Name: ");
			String name = consoleInputReader.next();
			System.out.print("Enter Employee Salary: ");
			double salary = consoleInputReader.nextDouble();
			consoleInputReader.close();
			employeePayrollList.add(new EmployeePayrollData(id, name, salary));
		} else if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().readData();
		}
	}

	public List<EmployeePayrollData> readEmpPayrollData(IOService ioService) throws EmpPayrollException {
		if (ioService.equals(IOService.DB_IO))
			employeePayrollList = employeePayrollDBService.readData();
		return employeePayrollList;
	}

	public void writeEmpPayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO))
			System.out.println("\nWriting Payroll to Console\n" + employeePayrollList);
		else if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().writeData(employeePayrollList);

	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().printData();
		else
			System.out.println(employeePayrollList);
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			return new EmployeePayrollFileIOService().countEntries();
		return employeePayrollList.size();
	}
	
	public void updateEmployeeSalary(String name, double salary, IOService ioService) throws EmpPayrollException{
		if(ioService.equals(IOService.DB_IO)) {
			this.updateEmployeeSalary(name, salary);
		}
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null) employeePayrollData.setSalary(salary);
	}

	public void updateEmployeeSalary(String name, double salary) throws EmpPayrollException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
	}

	public EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream().filter(employee -> employee.getName().contentEquals(name)).findFirst()
				.orElse(null);

	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmpPayrollException {
		// TODO Auto-generated method stub
		EmployeePayrollData employeePayrollData = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollData.getSalary().equals(getEmployeePayrollData(name).getSalary());
	}

	public List<EmployeePayrollData> getEmployeePayrollDataForDateRange(LocalDate startDate, LocalDate endDate)
			throws EmpPayrollException {
		// TODO Auto-generated method stub
		return employeePayrollDBService.getEmployeePayrollDataForDateRange(startDate, endDate);
	}

	public double getSumByGender(IOService ioService, String c) throws EmpPayrollException {
		double sum = 0.0;
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getSumByGender(c);
		return sum;
	}

	public double getEmpDataGroupedByGender(IOService ioService, String column, String operation, String gender)
			throws EmpPayrollException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getEmpDataGroupedByGender(column, operation, gender);
		return 0.0;
	}

	public Map<String, Double> readAvgSalary(IOService ioService) throws EmpPayrollException {
		// TODO Auto-generated method stub
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getAvgSalaryByGender();
		return null;
	}
	
	public void addEmployeeToPayroll(EmployeePayrollData employeePayrollData, IOService ioService) throws EmpPayrollException {
		if(ioService.equals(IOService.DB_IO))
			this.addEmpToPayroll(employeePayrollData.getName(), employeePayrollData.getSalary(), employeePayrollData.getStartDate(),
					employeePayrollData.getGender(), employeePayrollData.getDeptList());
		else employeePayrollList.add(employeePayrollData);
	}

	public void addEmpToPayroll(String name, double salary, LocalDate start, String gender, List<String> deptList)
			throws EmpPayrollException {
		try {
			employeePayrollList.add(employeePayrollDBService.addEmpToPayroll(name, salary, start, gender, deptList));
		} catch (EmpPayrollException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void remove(String name) throws EmpPayrollException {
		employeePayrollDBService.remove(name);
	}

	public void addEmpToPayroll(List<EmployeePayrollData> empPayrollDataList) {
		empPayrollDataList.forEach(empPayrollData -> {
			System.out.println("Emp being Added : " + empPayrollData.getName());
			try {
				this.addEmpToPayroll(empPayrollData.getName(), empPayrollData.getSalary(),
						empPayrollData.getStartDate(), empPayrollData.getGender(), empPayrollData.getDeptList());
			} catch (EmpPayrollException e) {
				e.printStackTrace();
			}
			System.out.println("Emp Added : " + empPayrollData.getName());
		});
		System.out.println(this.employeePayrollList);
	}

	public void addEmpToPayrollWithThreads(List<EmployeePayrollData> empPayrollDataList) throws EmpPayrollException {
		Map<Integer, Boolean> employeeStatusMap = new HashMap<Integer, Boolean>();
		empPayrollDataList.forEach(employeePayrollData -> {
			employeeStatusMap.put(employeePayrollData.hashCode(), false);
			Runnable task = () -> {
				System.out.println("Employee Being Added : " + Thread.currentThread().getName());
				try {
					this.addEmpToPayroll(employeePayrollData.getName(), employeePayrollData.getSalary(),
							employeePayrollData.getStartDate(), employeePayrollData.getGender(),
							employeePayrollData.getDeptList());
				} catch (EmpPayrollException e) {
					e.printStackTrace();
				}
				employeeStatusMap.put(employeePayrollData.hashCode(), true);
				System.out.println("Employee Added : " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employeePayrollData.getName());
			thread.start();
		});
		while (employeeStatusMap.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new EmpPayrollException(EmpPayrollException.ExceptionType.THREAD_INTERRUPTION, e.getMessage());
			}
		}
		System.out.println(this.employeePayrollList);
	}

	public boolean updateEmployeeSalaryInDB(List<EmployeePayrollData> empPayrollDataList) throws EmpPayrollException {
		Map<Integer, Boolean> empUpdateStatus = new HashMap<Integer, Boolean>();
		empPayrollDataList.forEach(employeePayrollData -> {
			empUpdateStatus.put(employeePayrollData.hashCode(), false);
			Runnable task = () -> {
				try {
					employeePayrollDBService.updateEmployeeSalaryInDB(employeePayrollData.getName(),
							                                          employeePayrollData.getSalary());
				} catch (EmpPayrollException e) {
					e.printStackTrace();
				}
				empUpdateStatus.put(employeePayrollData.hashCode(), true);
			};
			Thread thread = new Thread(task, employeePayrollData.getName());
			thread.start();
		});
		while (empUpdateStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new EmpPayrollException(EmpPayrollException.ExceptionType.THREAD_INTERRUPTION, e.getMessage());
			}
		}
		if(empUpdateStatus.containsValue(false))return false;
		return true;
	}
}
