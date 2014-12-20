package de.bht.comanche.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.bht.comanche.persistence.DaObject;

/**
 * This entity class represents a group and serve methods for working with 
 * LgMember and LgGroup objects.
 * 
 * @author Maxim Novichkov
 *
 */

@Entity
@Table(name = "group")
public class LgGroup extends DaObject{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Column for a group name. Must not be null.
	 */
	@NotNull
	@Column
	private String name;
	
	/**
	 * Column for a LgUser representation. Must not be null.
	 */
	@NotNull
	@ManyToOne
	private LgUser user;
		
	private String iconurl;

	@OneToMany(mappedBy="group", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)

	private List<LgMember> members;
	
	/**
	 * Construct a new LgGroup with a list of members.
	 */
	public LgGroup() {
		this.members = new ArrayList<LgMember>();
	}
	
	/**
	 * Returns the LgMember object by oid.
	 * 
	 * @param oid The LgMember oid.
	 * @return Return serched LgMember.
	 */
	public LgMember getMember(final long oid) {
		return search(this.members, oid);
	}
	
	/**
	 * Delete LgMember by oid.
	 * @param oid The LgMember oid.
	 */
	public void deleteMember(final long oid) {
		search(this.members, oid).delete();
	}

	/**
	 * Delete LgMember by LgUser oid.
	 * @param userOid The LgUser oid.
	 */
	public void deleteUser(final long userOid) {
		for (final LgMember member : this.members) {
			final LgUser user = member.getUser();
			if (user.getOid() == userOid) {
				user.remove(this);
				this.members.remove(member);
			}
		}
	}
	
	/**
	 * Sets specified LgGroup for LgMembers.
	 * @param group The LgGroup to set.
	 * @return Returns the LgGroup.
	 */
	public LgGroup setForMember(LgGroup group){
			for (final LgMember member : group.getMembers()) {
				member.setGroup(group);
			}
			return this;
	}
	
//	//entry 5,6 -> delete 5, than add 7 = as a result 6 will be resaved as 7, 7 as 8
//	public LgGroup setForMember(){
//		for (final LgMember member : this.getMembers()) {
//			member.setGroup(this);
//		}
////		this.save();
//		return this;
//	}
	
	/**
	 * Returns a list of LgUsers for specified group.
	 * @return The list of LgUsers.
	 */
	public List<LgUser> getUsers() {
		final List<LgUser> users = new LinkedList<LgUser>();
		for (final LgMember member : this.members) {
			users.add(member.getUser());
		}
		return users;
	}
	
	/**
	 * Gets string name for this group.
	 * @return The name of this group.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets a  name for this group.
	 * @param name Name to set.
	 * @return The name of this group.
	 */
	public LgGroup setName(final String name) {
		this.name = name;
		final LgGravatarUtils utils = new LgGravatarUtils();
		this.setIconurl(utils.getGroupUrl(name));
		return this;
	}
	
	/**
	 * Sets LgUser for this LgGroup.
	 * @param user LgUser object to set.
	 * @return LgGroup with specified LgUser object.
	 */
	public LgGroup setUser(final LgUser user) {
		this.user = user;
		return this;
	}
	
	/**
	 * Returns LgMember list for specified LgGroup. 
	 * @return The list with LgMembers.
	 */
	public List<LgMember> getMembers() {
		return this.members;
	}

	@Override
	public String toString() {
		return String.format(
				"LgGroup [name=%s, user=%s, members=%s, oid=%s, pool=%s]",
				name, user, members, oid, pool);
	}

	public String getIconurl() {
		final LgGravatarUtils utils = new LgGravatarUtils();
		iconurl = utils.getGroupUrl(name);
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
}
