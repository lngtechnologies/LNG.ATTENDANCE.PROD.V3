package com.lng.attendancecompanyservice.entity.masters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tmempblock")
public class EmployeeBlock {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="empBlkId")
	private Integer empBlkId;
	@ManyToOne
	@JoinColumn(name = "refEmpId")
	private Employee employee;
	@ManyToOne
	@JoinColumn(name = "refBlkId")
	private  Block block;
	public Integer getEmpBlkId() {
		return empBlkId;
	}
	public void setEmpBlkId(Integer empBlkId) {
		this.empBlkId = empBlkId;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Block getBlock() {
		return block;
	}
	public void setBlock(Block block) {
		this.block = block;
	}


}
