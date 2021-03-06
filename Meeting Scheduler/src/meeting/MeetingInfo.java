package meeting;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MeetingInfo {

	Interval meetingInterval;
	int organizerId;
	String meetingId;
	public static DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
	
	public MeetingInfo() {
		super();
	}
	public MeetingInfo(Interval meetingInterval, int organizerId, String meetingId) {
		this.meetingInterval = meetingInterval;
		this.organizerId = organizerId;
		this.meetingId = meetingId;
	}
	public Interval getMeetingInterval() {
		return meetingInterval;
	}
	public void setMeetingInterval(Interval meetingInterval) {
		this.meetingInterval = meetingInterval;
	}
	public int getOrganizerId() {
		return organizerId;
	}
	public void setOrganizerId(int organizerId) {
		this.organizerId = organizerId;
	}
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	@Override
	public String toString() {
		return "MeetingInfo [meetingInterval= " + intervalString() + ", organizerId= " 
				+ organizerId + ", meetingId= " + meetingId + "]";
	}
	/**
	 * Beautifies the String representation of the meeting time interval.
	 * @param meetingInterval
	 * @return
	 */
	public String intervalString() {
		return meetingInterval.getStart().toString(dtf) + "to"
				+ meetingInterval.getEnd().toString(dtf);
	}
}
