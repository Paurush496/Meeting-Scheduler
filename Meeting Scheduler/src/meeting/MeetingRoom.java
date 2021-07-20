package meeting;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Interval;

public class MeetingRoom {

	//Will represent the meeting room no (3 letters preferably)
	String meetingRoomId;
	List<MeetingInfo> meetingRoomInfo = new ArrayList<>();
	
	public MeetingRoom(String meetingRoomId) {
		this.meetingRoomId = meetingRoomId;
	}

	/**
	 * Helper method to determine whether this meeting room is not entirely
	 * available for the provided time interval.
	 * @param meetInterval
	 * @return
	 */
	public boolean verifyIntervalOverlap(Interval meetInterval) {
		for(int i=0; i<getMeetingRoomInfo().size(); i++) {
			if (getMeetingRoomInfo().get(i).getMeetingInterval().overlaps(meetInterval))
				return true;
		}
		return false;
	}
	
	/**
	 * Book meeting room for a new meeting.
	 * @param meetInterval
	 * @param organizerId
	 * @return new meeting info
	 */
	public MeetingInfo addNewMeeting (Interval meetInterval, int organizerId) {
		String meetingId = generateMeetingId(meetInterval);
		MeetingInfo newMeeting = new MeetingInfo(meetInterval, organizerId, meetingId);
		meetingRoomInfo.add(newMeeting);
		return newMeeting;
	}
	
	public String getMeetingRoomId() {
		return meetingRoomId;
	}

	public List<MeetingInfo> getMeetingRoomInfo() {
		return meetingRoomInfo;
	}
	
	/**
	 * Generic format for all meeting Ids. 
	 * @param meetInterval
	 * @return Unique meeting Id
	 */
	public String generateMeetingId (Interval meetInterval) {
		return meetingRoomId + meetInterval.getStart().getMinuteOfDay() +
				meetInterval.getEnd().getMinuteOfDay();
	}
	
	/**
	 * Find the meeting, for which employees need to be managed (add/remove).
	 * @param meetingId
	 * @return
	 */
	public MeetingInfo findMeetingToManage (String meetingId) {
		for (MeetingInfo meeting : getMeetingRoomInfo()) {
			if(meeting.getMeetingId().equals(meetingId))
				return meeting;
		}
		return null;
	}
}
