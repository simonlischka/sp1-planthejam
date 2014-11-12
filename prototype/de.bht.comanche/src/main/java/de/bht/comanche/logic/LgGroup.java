package de.bht.comanche.logic;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.bht.comanche.persistence.DaObject;

@Entity
@Table(name = "group")
public class LgGroup extends DaObject{
	
	private static final long serialVersionUID = 1L;
	
	@NotNull
	@Column
	private String name;

	@NotNull
	@ManyToOne
	private LgUser user;
		
	@OneToMany(mappedBy="group", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<LgMember> member;
	
	public String getGroupName() {
		return name;
	}

	public LgGroup setName(String name) {
		this.name = name;
		return this;
	}
	
	public LgGroup setUser(LgUser user) {
		this.user = user;
		return this;
	}
	
	public long getGroupOid() {
		return this.getOid();
	}
	
	public LgGroup save() {
		return pool.save(this);
	}
	
	public void delete() {
		user.removeGroup(this);
		pool.delete(this); //throw exc when delete errror
	}
	
	public List<LgMember> getMembers() {
		return member;
	}

	public LgGroup setMembers(List<LgMember> member) {
		this.member = member;
		return this;
	}
	
//	public void setMember(long groupOid){
//		user.save(user.getGroups().).saveMember
//		
//	}
	
//	TODO How to find group by id - pool.find-getOid??
//	public LgGroup getLgGroup(User , long group_oid){
//		return null;
//	}
	

}
