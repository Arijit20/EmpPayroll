package com.cg;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class EmployeePayrollData {
	private int id;
	private String name;
	private double salary;
	private LocalDate startDate;
	private String gender;
	private List<String> deptList;

	public EmployeePayrollData(Integer id, String name, Double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayrollData(Integer id, String name, Double salary, LocalDate startDate) {
		this(id,name,salary);
		this.setStartDate(startDate);
	}
	
	public EmployeePayrollData(Integer id, String name, Double salary, LocalDate startDate,String gender) {
		this(id,name,salary, startDate);
		this.setGender(gender);
	}
	
	public EmployeePayrollData(Integer id, String name, Double salary, LocalDate startDate,String gender, List<String> deptList) {
		this(id,name,salary, startDate, gender);
		this.setDeptList(deptList);
	}
	public Double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getGender() {
		return gender;
	}
	
	
	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<String> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<String> deptList) {
		this.deptList = deptList;
	}
	
	@Override
	public String toString() {
		return "id="+id+", name="+name+", salary="+salary+", startDate="+startDate;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, gender, salary, startDate);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		EmployeePayrollData that = (EmployeePayrollData)o;
		return id == that.id && 
				Double.compare(that.salary, salary) == 0 &&
				name.contentEquals(that.name) && this.gender.contentEquals(that.gender) && this.deptList.equals(that.deptList);
	}

}
