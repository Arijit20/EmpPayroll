package com.cg;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeePayrollDBService {
	
	private int connectionCounter = 0;
	private static EmployeePayrollDBService employeePayrollDBService;
	private PreparedStatement employeePayrollDataStatement;
	
	private EmployeePayrollDBService() {
	}
	
	public static EmployeePayrollDBService getInstance() {
		if(employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private synchronized Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?allowPublicKeyRetrieval=true&&useSSL=false";
		String userName = "root";
		String password = "arijit123dey";
		Connection connection;
		System.out.println("Processing Thread : " + Thread.currentThread().getName()+
				           "Connecting to database with id : "+ connectionCounter);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Processing Thread : " + Thread.currentThread().getName()+
		                   "Id : "+ connectionCounter + "Connection successful");
		return connection;
	}
	
	public List<EmployeePayrollData> readData() throws EmpPayrollException{
		String sql = "SELECT * FROM employee_data WHERE status = 'active';";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			employeePayrollList = getEmployeePayrollData(result);
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(String name, double salary) throws EmpPayrollException {
		// TODO Auto-generated method stub
		return this.updateEmployeeDataUsingStatement(name, salary);
	}

	private int updateEmployeeDataUsingStatement(String name, double salary) throws EmpPayrollException {
		// TODO Auto-generated method stub
		String sql = String.format("update employee_data set salary = %.2f where name ='%s' AND status = 'active';", salary, name);
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollDataFromDB(String name) throws EmpPayrollException{
		List<EmployeePayrollData> employeePayrollList = null;
		if(this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
		return 	employeePayrollList;
		}
	public EmployeePayrollData getEmployeePayrollData(String name) throws EmpPayrollException {
		// TODO Auto-generated method stub
		
			List<EmployeePayrollData> employeePayrollList = this.readData();
			EmployeePayrollData employeeData = employeePayrollList.stream()
					.filter(employee -> employee.getName().contentEquals(name))
					.findFirst()
					.orElse(null);
			return employeeData;
		
		
		}
	
	private void prepareStatementForEmployeeData() throws EmpPayrollException {
		// TODO Auto-generated method stub
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_data WHERE name = ? AND status = 'active'";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) throws EmpPayrollException{
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try {
			while(result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("start").toLocalDate();
				String gender = result.getString("gender");
				employeePayrollList.add(new EmployeePayrollData(id,name,salary,startDate, gender));
		}
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
		return employeePayrollList;
	}

	public List<EmployeePayrollData> getEmployeePayrollDataForDateRange(LocalDate startDate, LocalDate endDate) throws EmpPayrollException {
		// TODO Auto-generated method stub
		String sql = String.format("SELECT * FROM employee_data WHERE status = 'active' AND start BETWEEN '%s' AND '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try (Connection connection = getConnection()) {
			employeePayrollDataStatement = connection.prepareStatement(sql);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			while (resultSet.next()) {
				employeePayrollList.add(new EmployeePayrollData(resultSet.getInt("id"), resultSet.getString("name"),
						resultSet.getDouble("salary"), resultSet.getDate("start").toLocalDate()));
			}
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		return employeePayrollList;
	}

	public double getSumByGender(String c) throws EmpPayrollException {
		List<EmployeePayrollData> employeePayrollList = this.readData();
		double sum = 0.0;
		List<EmployeePayrollData> sortByGenderList = employeePayrollList.stream()
				                            .filter(employee -> employee.getGender().equals(c))
				                            .collect(Collectors.toList());
		sum = sortByGenderList.stream()
				                           .map(employee -> employee.getSalary()).reduce(0.0, Double::sum);
		return sum;
	}
	

	public double getEmpDataGroupedByGender(String column, String operation, String gender) throws EmpPayrollException {
		
		Map<String, Double> sumByGenderMap = new HashMap<>();
		String sql = String.format("SELECT gender, %s(%s) FROM employee_data WHERE status = 'active' GROUP BY gender;", operation, column);
		try (Connection connection = getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				sumByGenderMap.put(resultSet.getString(1), resultSet.getDouble(2));
			}
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		if(gender.equals("M")) {
		return sumByGenderMap.get("M");
		}
		return sumByGenderMap.get("F");
	}
	
	public Map<String, Double> getAvgSalaryByGender() throws EmpPayrollException  {
		// TODO Auto-generated method stub
		String sql ="SELECT gender, AVG(salary) as avg_salary FROM employee_data WHERE status = 'active' GROUP BY gender;";
		Map<String, Double> genderToAvgSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("avg_salary");
				genderToAvgSalaryMap.put(gender, salary);
			}
			
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		return genderToAvgSalaryMap;
	}

	public EmployeePayrollData addEmpToPayrollTable(String name, double salary, LocalDate start, String gender) throws  EmpPayrollException {
		// TODO Auto-generated method stub
		int id = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format("INSERT INTO employee_data(name, salary, start, gender) VALUES('%s', '%s', '%s', '%s');"
				,name, salary, Date.valueOf(start), gender);
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) id = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(id, name, salary, start, gender);
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addEmpToPayroll(String name, double salary, LocalDate start, String gender, List<String> deptList) throws EmpPayrollException {
		// TODO Auto-generated method stub
		int id = -1;
		EmployeePayrollData employeePayrollData = null;
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
		try(Statement statement = connection.createStatement()){
			String sql = String.format("INSERT INTO employee_data(name, salary, start, gender) VALUES('%s', '%s', '%s', '%s');"
					,name, salary, Date.valueOf(start), gender);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) id = resultSet.getInt(1);
			}
		}catch(SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
			}
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
		
		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO employee_dept (id,dept) VALUES ('%s','%s');", id,
					deptList);
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
			}
		}
		try(Statement statement = connection.createStatement()){
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format("INSERT INTO payroll_details "+
			"(id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')"
					,id, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if(rowAffected == 1)
				employeePayrollData = new EmployeePayrollData(id, name, salary, start, gender);
		}catch(SQLException e) {
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
			}
		}
		
		try {
		connection.commit();
		}catch(SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
		}
		finally {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw new EmpPayrollException(EmpPayrollException.ExceptionType.CONNECTION_ERROR, e.getMessage());
				}
		}
		return employeePayrollData;
	}

	public void remove(String name) throws EmpPayrollException {
		String sql = String.format("UPDATE employee_data SET status = 'inactive' WHERE name = '%s';", name);
		try (Connection connection = getConnection()) {
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			throw new EmpPayrollException(EmpPayrollException.ExceptionType.INCORRECT_INFO, e.getMessage());
		}
	}

}
