package employees;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Interval;

import meeting.MeetingInfo;

public class Employee {
	
	int employeeId;
	List<MeetingInfo> employeeMeetings = new ArrayList<>();
	
	public Employee(int employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * Validate whether Employee already has an overlapping meeting during
	 * the provided time interval. If yes, return the meeting Id of the
	 * overlapping meeting, else return null.
	 * @param meetInterval
	 * @return
	 */
	public String findOverlappingMeet (Interval meetInterval) {
		if (!getEmployeeMeetings().isEmpty()) {
			for(int i=0; i<employeeMeetings.size(); i++) {
				MeetingInfo meeting = employeeMeetings.get(i);
				if (meeting.getMeetingInterval().overlaps(meetInterval)) {
					return meeting.getMeetingId();
				}
			}			
		}
		return null;
	}
	
	public List<MeetingInfo> getEmployeeMeetings() {
		return employeeMeetings;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	/**
	 * Adds a new meeting.
	 * @param newMeeting
	 */
	public void addNewMeeting (MeetingInfo newMeeting) {
		getEmployeeMeetings().add(newMeeting);
	}
	
	/**
	 * Removes from an existing meeting.
	 * @param oldMeeting
	 */
	public void removeMeeting (MeetingInfo oldMeeting) {
		getEmployeeMeetings().remove(oldMeeting);
	}
}
