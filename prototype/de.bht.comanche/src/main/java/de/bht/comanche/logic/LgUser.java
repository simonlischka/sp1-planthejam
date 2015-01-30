package de.bht.comanche.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import static multex.MultexUtil.create;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.bht.comanche.persistence.DaObject;
import de.bht.comanche.rest.ReUserService.RestLoginUserFailure;

/**
 * This entity class represents a user and serve methods for working with all
 * objects LgClasses.
 *
 * @author Simon Lischka
 *
 */
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class LgUser extends DaObject {
	private static final long serialVersionUID = 1L;
	/**
	 * Column for a user name. Must not be null.
	 */
	@Column(unique = true, nullable = false)
	private String name;
	/**
	 * Column for a user's telephone.
	 */
	private String tel;
	/**
	 * Column for a user's email.
	 */
	private String email;
	/**
	 * Column for a user's password.
	 */
	private String password;
	/**
	 * URL to gravatar icon
	 */
	private String iconurl;
	/**
	 * Representation of a foreign key in a LgInvite entity. Provide a list of
	 * invites.
	 */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<LgInvite> invites;

	/**
	 * General availability of a user
	 */
	@ElementCollection(targetClass = LgTimePeriod.class, fetch = FetchType.EAGER)
	@Column(name = "general_availability")
	private Set<LgTimePeriod> generalAvailability;

	/**
	 * Messages of this user, currently not used
	 */
	@ElementCollection(targetClass = LgMessage.class, fetch = FetchType.EAGER)
	@Column(name = "messages")
	private Set<LgMessage> messages;

	/**
	 * Used to generate Gravatar urls by email or user name
	 */
	@Transient
	final LgGravatarUtils gravUtils;

	public LgUser() {
		this.invites = new ArrayList<LgInvite>();
		this.gravUtils = new LgGravatarUtils();
	}

	public LgInvite getInviteBySurveyName(final String name) {
		for (LgInvite invite : invites) {
			if (invite.getSurvey().getName() == name) {
				return invite;
			}
		}
		throw create(InviteNotFoundByNameExc.class, name, this.getName());
	}
	/**
	 * Could not find invite with name {0} of user {1}.
	 */
	@SuppressWarnings("serial")
	public static final class InviteNotFoundByNameExc extends multex.Exc {}

	public LgSurvey getSurveyByName(final String name) {
		for (LgInvite invite : invites) {
			final LgSurvey survey = invite.getSurvey();
			if (survey.getName() == name) {
				return survey;
			}
		}
		throw create(SurveyNotFoundByNameExc.class, name, this.getName());
	}
	/**
	 * Could not find survey with name {0} of user {1}.
	 */
	@SuppressWarnings("serial")
	public static final class SurveyNotFoundByNameExc extends multex.Exc {}
	
	public void deleteOtherUserAccount(final LgUser user) {
		this.findOneByKey(LgUser.class, "oid", user.getOid()).delete();
	}

	/**
	 * Delete LgInvite by provided oid.
	 *
	 * @param inviteOid
	 *            The LgInvite oid.
	 */
	public void deleteInvite(final long inviteOid) {
		getInvite(inviteOid).delete();
	}
	
	/**
	 * Save LgGroup for current user.
	 * @param group The LgGroup to save.
	 * @return The saved LgGroup.
	 */
	public LgGroup save(final LgGroup group) {
		group.setUser(this).setForMember(group);
		return saveUnattached(group);
	}

	/**
	 * Delete LgGroup by provided oid.
	 * @param groupOid The LgGroup oid.
	 */
	public void deleteGroup(final long groupOid) {
		this.getGroup(groupOid).delete();
	}

	/**
	 * Returns LgGroup by provided oid.
	 * @param groupOid The LgGroup oid.
	 * @return The found LgGroup
	 */
	public LgGroup getGroup(final long groupOid) {
		return this.findOneByKey(LgGroup.class, "OID", groupOid);
	}
	
	@JsonIgnore
	public List<LgGroup> getGroups() {
		return findManyByKey(LgGroup.class, "USER_OID", this.getOid());
	}

	public LgGroup updateGroup(final LgGroup other){
		if (other.getOid() <= 0) {
			throw create(UpdateWithUnpersistedGroupExc.class, other.getOid());
		}
		final LgGroup group = findOneByKey(LgGroup.class, "OID", other.getOid());
		group.updateWith(other);
		return saveUnattached(other);
	}

	@SuppressWarnings("serial")
	public static final class UpdateWithUnpersistedGroupExc extends multex.Failure {}

	/**
	 * Search LgMember object by group oid and user oid.
	 * @param groupId The LgGroup oid.
	 * @param userId The LgUser oid.
	 * @return The found list with LgMember.
	 */
	public List<LgMember> search(final long groupId, final long userId) {
		return search(LgMember.class, "GROUP_OID", groupId, "USER_OID", userId);
	}

	/**
	 * Generates icon url from classes internal email Gravatar will deliver a
	 * default icon if no email given.
	 */
	public String getIconurl() {

		if (email != null) {
			iconurl = gravUtils.getUserUrl(email);
		} else {
			iconurl = gravUtils.getUserUrl(name);
		}
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
	
	/**
	 * Proof key and value of user name and password.
	 *
	 * @param user
	 *            The LgUser to proof.
	 * @return If the key and value match - true.
	 */
	public boolean passwordMatchWith(final LgUser user) {
		if (this.password == null) {
			return false; 
		}
		return this.password.equals(user.getPassword());
	}

	/**
	 * Returns LgTimePeriods list for current user.
	 *
	 * @return The list with LgTimePeriods.
	 */

	public Set<LgTimePeriod> getGeneralAvailability() {
		return this.generalAvailability;
	}

	/**
	 * Returns LgTimePeriods list for current user.
	 *
	 * @return The list with LgTimePeriods.
	 */
	public LgUser setGeneralAvailability(Set<LgTimePeriod> generalAvailability) {
		this.generalAvailability = generalAvailability;
		return this;
	}

	/**
	 * Remove invite object from the list of invites.
	 *
	 * @param invite
	 *            The LgInvite to remove.
	 */
	public void remove(final LgInvite invite) {
		this.invites.remove(invite);
	}

	public LgSurvey getSurvey(final Long oid) {
		final LgSurvey survey = findOneByKey(LgSurvey.class, "OID", oid);
		if (survey != null) {
			return attachPoolFor(survey);
		}
		return null; // TODO: Throw MULTEX exception
	}

    @JsonIgnore
	public List<LgSurvey> getSurveys() {
		List<LgSurvey> surveys = new ArrayList<LgSurvey>();
		for (LgInvite invite : this.invites) {
			if (invite.getIsHost()) {
				surveys.add(invite.getSurvey());
			}
		}
		return surveys;
	}

    @JsonIgnore
	public List<LgInvite> getInvites() {
		return this.invites;
	}

	public List<LgInvite> getInvitesForSurvey(final Long oid) {
		return findManyByKey(LgInvite.class, "SURVEY_OID", oid);
	}

	 /**
     * JSON Object from Server doesn't send host invite.
     *
     * @param survey
     * @return
     */
    public LgSurvey saveSurvey(final LgSurvey survey) {
            final LgSurvey persistedSurvey = saveUnattached(
            		addSurveyForHost(survey)
            		);
            return persistedSurvey;
    }
    
    public LgSurvey addSurveyForHost(final LgSurvey survey) {
        survey.addHost(this);
        for (int i = 0; i < survey.getInvites().size(); i++) {
                final LgInvite invite = survey.getInviteAt(i);
                // Set survey reference
                invite.setSurvey(survey);
                // Write back
                survey.setInvite(i, invite);
        }
        return survey;
    }
    
	public LgSurvey updateSurvey(final LgSurvey other) {
		if (other.getOid() <= 0) {
			throw create(UpdateWithUnpersistedSurveyExc.class, other.getOid());
		}
		final LgSurvey survey = findOneByKey(LgSurvey.class, "OID", other.getOid());
		survey.updateWith(other);
		return saveUnattached(other);
	}

	/**
	 *  The survey with oid "{0}" seems to be unpersisted. You can only
	 *  update surveys you have retrieved from the server before.
	 */
	@SuppressWarnings("serial")
	public static final class UpdateWithUnpersistedSurveyExc extends multex.Failure {}

	public void deleteSurvey(final long oid) {
		List<LgInvite> invites = new ArrayList<LgInvite>();
		invites.addAll(this.findManyByKey(LgInvite.class, "survey_oid", oid));
		for (LgInvite invite : invites) {
			invite.setSurvey(null);
			invite.setUser(null);
			invite.delete();
		}
		this.findOneByKey(LgSurvey.class, "oid", oid).delete();
	}
	

	/**
	 * Complete delete of user account.
	 *
	 * Invites have to be delete first in order to comply
	 * with foreign key constraint.
	 *
	 */
	public void deleteThisAccount() {
		for (final LgInvite invite : this.getInvites()) {
			invite.setUser(null);
		}
		// Do the same with Groups!
		// Child surveys + invites are not cascaded!
		delete();
		
	}
	
//	public void deleteAllInvites() {
//		List<LgInvite> invites = new ArrayList<LgInvite>();
//		invites.addAll(this.findManyByKey(LgInvite.class, "user_oid", this.getOid()));
//		for (LgInvite invite : invites) {
//			invite.setSurvey(null);
//			invite.setUser(null);
//			invite.delete();
//		}
//	}
	
	@JsonIgnore
	public List<LgInvite> getInvitesAsParticipant() {
		List<LgInvite> filteredInvites = new ArrayList<LgInvite>();
		for (LgInvite invite : this.invites) {
			if (!invite.getIsHost()) {
				filteredInvites.add(invite);
			}
		}
		return filteredInvites;
	}

	/**
	 * Saves an invite for the user.
//	 * 
	 * @param invite to be saved
	 * @return invite with OID
	 */
	public LgInvite saveInvite(final LgInvite invite) {
		return saveUnattached(invite);
	}

	/**
	 * Update an existing invite.
	 * 
	 * Currently forwards the request to saveInvite. Prepared
	 * to treat update and save operations seperately .
	 * 
	 * @param invite to be updated
	 * @return Updated invite
	 */
	public LgInvite updateInvite(LgInvite invite) {
		return saveInvite(invite);
	}

	/**
	 * Evaluates all surveys of this user and and notifies host about outcome.
	 *
	 * Should be triggered by rest path before calling getSurveys().
	 */
    public void evaluateAllSurveys() {
    	final List<LgSurvey> surveysOfThisUser = getSurveys();
    	for (final LgSurvey survey : surveysOfThisUser) {
    		if (survey.shouldBeEvaluated()) {
    			survey.evaluate();
    			saveUnattached(survey);
    			// notifyHost(survey);
    			saveUnattached(this);
    		}
    	}
    }

    /**
     * Sends host a message about determined time period if
     * existent.
     *
     * @param survey Surevey to be used for notifying host.
     */
    private void notifyHost(LgSurvey survey) {
    	if (survey.getDeterminedTimePeriod().isNull()) {
    		this.addMessage("Sorry, we couldn't determine a common date for the survey '" + survey.getName() + "'");
    	} else {
    		this.addMessage("We determined a date for the survey '" + survey.getName() + "' from "
    				+ survey.getDeterminedTimePeriod().getStartTime() + "to " +  survey.getDeterminedTimePeriod().getEndTime());
    	}
    }

    /**
     * Notifies all participants of a survey of the outcome by
     * sending them a message.
     *
     * Should be triggered by rest path.
     *
     * @param surveyOid Survey to be used for notifying participants.
     */
    public void notifyParticipants(long surveyOid) {
    	final LgSurvey survey = this.getSurvey(surveyOid);
    	if (!survey.isAlgoChecked()) {
    		return;
    	}
    	if (survey.getSuccess() == LgStatus.YES) {
    		final String yesMsg = survey.getName() + " is going to happen from "
					+ survey.getDeterminedTimePeriod().getStartTime() + " to "
					+ survey.getDeterminedTimePeriod().getEndTime();
			sendMessageToAllParticipants(survey.getParticipants(), yesMsg);
    	}
    	if (survey.getSuccess() == LgStatus.NO) {
    		final String noMsg = survey.getName() + " got cancelled. Blame " + survey.getHost();
			sendMessageToAllParticipants(survey.getParticipants(), noMsg);
    	}
    }

    public void sendMessageToAllParticipants(final List<LgUser> participants, final String text) {
    	for (LgUser user: participants) {
    		user.addMessage(text);
        	saveUnattached(user);
    	}
    }

    /**
     * Sends this user a string message.
     *
     * Convenience method, constructs message object and adds to local
     * list.
     *
     * @param message Message text as string
     */
    public void addMessage(String message) {
    	this.messages.add(new LgMessage().setMessage(message));
    }

    /**
     * For testing: Add invites manually
     * @param invite
     * @return Invite added to collection
     */
    public LgUser addInvite(final LgInvite invite) {
    	this.invites.add(invite);
    	return this;
    }

    /**
     * Searches invite by oid. Doesn't execute owner check!
     *
     * @param oid OID of invite to be retrieved
     * @return Found invite
     */
	public LgInvite getInvite(final long oid) {
		return findOneByKey(LgInvite.class, "oid", oid);
	}


    public LgUser updateWith(LgUser other) {
		this.email = other.email;
		this.generalAvailability =  other.generalAvailability;
		this.iconurl = other.iconurl;
		this.invites = other.invites;
		this.messages = other.messages;
		this.name = other.name;
		this.password = other.password;
		this.tel = other.tel;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public LgUser setName(final String name) {
		this.name = name;
		return this;
	}

	public String getTel() {
		return this.tel;
	}

	public LgUser setTel(final String tel) {
		this.tel = tel;
		return this;
	}

	public String getEmail() {
		return this.email;
	}

	public LgUser setEmail(final String email) {
		this.email = email;
		return this;
	}

	public String getPassword() {
		return this.password;
	}

	public LgUser setPassword(final String password) {
		this.password = password;
		return this;
	}

	 public Set<LgMessage> getMessages() {
		 return this.messages;
	 }

	 public void setMessages(Set<LgMessage> messages) {
		 this.messages = messages;
	 }

	//Removed invites from toString method (Causes stack overflow error)
	@Override
	public String toString() {
		return String
				.format("LgUser [name=%s, tel=%s, email=%s, password=%s]",
						name, tel, email, password);
	}
}