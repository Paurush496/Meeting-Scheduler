package meetManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import LaunchApplication.AppStorage;
import employees.Employee;
import meeting.MeetingInfo;
import meeting.MeetingRoom;

/**
 * @author Paurush Choudhary
 * Manages all Meeting Operations
 */
public class MeetScheduler extends AppStorage {

	public MeetScheduler() {
		super();
	}

	/**
	 * Organize a new meeting.
	 * @param employeeId
	 * @param foStartTime
	 * @param foEndTime
	 * @return output - relevant String output
	 */
	public String organizeNewMeet(int employeeId, DateTime foStartTime, DateTime foEndTime) {
		Employee employee = null;
		String output = null;
		Interval meetingInterval = new Interval(foStartTime, foEndTime);
		//Fetch Employee from storage
		for(int i=0; i<getAllEmployees().length; i++) {
			if (employeeId == getAllEmployees()[i].getEmployeeId()) {
				employee = getAllEmployees()[i];
				break;
			}
		}
		if (employee == null) {
			output = "No employee found with id " + employeeId + "\r\n";
			return output;
		}
		//Confirm whether employee has a conflicting meeting during the given interval.
		String overlapMeetingId = employee.findOverlappingMeet(meetingInterval);
		if (overlapMeetingId != null && !overlapMeetingId.isEmpty()) {
			output = "Conflicting with meeting " + overlapMeetingId + "\r\n";
			return output;
		}
		for(int i=0; i<getAllMeetingRooms().length; i++) {
			MeetingRoom meetingRoom = getAllMeetingRooms()[i];
			//Book new meeting in the next available meeting room.
			if (!meetingRoom.verifyIntervalOverlap(meetingInterval)) {
				MeetingInfo meeting = meetingRoom.addNewMeeting(meetingInterval, employeeId);
				employee.addNewMeeting(meeting);
				output = "Meeting booked successfully with meeting id " + meeting.getMeetingId();
				sortMeetingRoomUp(i);
				break;
			}
		}
		//If conflict not found and meeting room also not booked. Only possibility
		//is that all meeting rooms are busy for the given interval.
		if(output == null) {
			output = "All rooms busy for the given time interval.";
		}		
		return output.concat("\r\n");
	}

	/**
	 * This method ensures that the MeetingRoom array in the storage remains 
	 * sorted in the ascending order of increasing total no of meets/room. This 
	 * is done to evenly spread the new bookings throughout all rooms, and also
	 * facilitate quick search. Bubbles busiest room up to the end of the array.
	 * @param index
	 */
	private void sortMeetingRoomUp(int index) {
		while(index+1 < getAllMeetingRooms().length) {
			MeetingRoom modifiedMeetingRoom = getAllMeetingRooms()[index];
			MeetingRoom nextMeetingRoom = getAllMeetingRooms()[index+1];
			int totalRoomMeets = modifiedMeetingRoom.getMeetingRoomInfo().size();
			int totalNextRoomMeets = nextMeetingRoom.getMeetingRoomInfo().size();
			if (totalNextRoomMeets < totalRoomMeets) {
				getAllMeetingRooms()[index] = nextMeetingRoom;
				getAllMeetingRooms()[index+1] = modifiedMeetingRoom;
			} else break;
			index++;
		}
	}

	/**
	 * For the given employees, find their mutual available time slots in the given interval.
	 * @param employeeIds
	 * @param foStartTime
	 * @param foEndTime
	 * @return List<Free time slots>
	 */
	public List<String> findFreeTimeSlots(int[] employeeIds, DateTime foStartTime, DateTime foEndTime) {
		Interval freeInterval = new Interval(foStartTime, foEndTime);
		Iterator<Employee> searchEmployees = getSortedSearchEmployeesList(employeeIds);
		Iterator<Interval> freeTimeSlots = null;
		List<String> freeSlots = new ArrayList<>();
		
		for (int count=0; searchEmployees.hasNext(); count++) {
			Employee currentSearchEmployee = searchEmployees.next();
			List<Interval> mutualFreeTimeSlot = new ArrayList<>();
			//For all found freeTimeSlots till now, iteratively find mutually free
			//time slots with the next employee.
			while(freeTimeSlots != null && freeTimeSlots.hasNext()) {
				freeInterval = freeTimeSlots.next();
				mutualFreeTimeSlot.addAll(getFreeTimeSlots(currentSearchEmployee,freeInterval));
			}
			if (!mutualFreeTimeSlot.isEmpty())
				//Use this List of mutually free time slots to find mutually free
				//time slots with the next employee in the next iteration.
				freeTimeSlots = mutualFreeTimeSlot.iterator();
			else if (mutualFreeTimeSlot.isEmpty() && count>0) {
				//No mutually free time slots found.
				freeSlots.add("No free time slots found. \r\n");
				break;
			}
			//Only executed in the 1st loop to get free time slots for the first search employee.
			else freeTimeSlots = getFreeTimeSlots(currentSearchEmployee,freeInterval).iterator();			
		}	
		//String output format
		while(freeTimeSlots != null && freeTimeSlots.hasNext()) {
			freeSlots.add(intervalString(freeTimeSlots.next()));
		}
		return freeSlots;
	}
	
	/**
	 * Searched employees are sorted in an increasing order of their availability.
	 * First employee is the busiest - this is done to try and start with min no of 
	 * free time slots while extracting mutually free time slots for these search 
	 * employees, hence reducing the mutually free time slots while moving up the list 
	 * towards the least busy employee.
	 * @param searchEmployeesIds
	 * @return
	 */
	public Iterator<Employee> getSortedSearchEmployeesList(int[] searchEmployeesIds) {
		//Fetch required Employees from storage
		List<Employee> searchEmployees = getEmployeesSubset(searchEmployeesIds);
		Collections.sort(searchEmployees, (e1,e2) -> e2.getEmployeeMeetings().size() 
				- e1.getEmployeeMeetings().size());
		return searchEmployees.iterator();
	}
	
	/**
	 * Find mutually free time slots for the given interval.
	 * @param employee
	 * @param freeInterval
	 * @return List<Mutually free time slots>
	 */
	public List<Interval> getFreeTimeSlots(Employee employee, Interval freeInterval) {
		List<Interval> gaps = new ArrayList<>();
		//Meeting intervals of the given employee in the provided time interval.
		List<Interval> overlappingIntervals = getSortedOverlappingIntervals(employee,freeInterval);
		if (overlappingIntervals == null) {
			//Employee entirely free during the provided interval.
			gaps.add(freeInterval);
		} else if (!overlappingIntervals.isEmpty()) {
			//Find gaps (mutually free time slots) for this employee in the provided interval.
			gaps = getIntervalGaps(overlappingIntervals,freeInterval.getStart(),freeInterval.getEnd()); 
		}
		return gaps;
	}
	
	/**
	 * Meeting intervals of the given employee in the provided time interval. Sorting is done
	 * just to arrange the time intervals in occurrence order.
	 * @param searchEmployee
	 * @param searchInterval
	 * @return
	 */
	public List<Interval> getSortedOverlappingIntervals(Employee searchEmployee, Interval searchInterval) {
		List<MeetingInfo> employeeMeetings = searchEmployee.getEmployeeMeetings();
		List<Interval> overlappingIntervals = new ArrayList<Interval>();
		if (employeeMeetings.isEmpty())
			//Employee doesn't has any scheduled meetings.
			return null;
		employeeMeetings.forEach(meeting -> {
			Interval overlap = searchInterval.overlap(meeting.getMeetingInterval());
			if (overlap != null) {
				overlappingIntervals.add(overlap);
			}
		});
		if (overlappingIntervals.isEmpty())
			//Employee doesn't has any meetings scheduled in the provided time interval.
			return null;
		//Sorting done to arrange the time intervals in occurrence order.
		Collections.sort(overlappingIntervals, new Comparator<Interval>() {
			@Override
			public int compare(Interval i1, Interval i2) {
				if (i1.isAfter(i2)) return 1;
				else return -1;
			}
		});
		return overlappingIntervals;
	}
	
	/**
	 * For all the meeting overlaps found, get the in-between gaps (i.e free time slots). 
	 * @param overlapsList
	 * @param foStartTime
	 * @param foEndTime
	 * @return List<Free time slots within the given time interval>
	 */
	public List<Interval> getIntervalGaps(List<Interval> overlapsList, DateTime foStartTime, DateTime foEndTime) {
		List<Interval> gaps = new ArrayList<>();
		//To keep track of previous overlap interval.
		Interval prevOverlap = null;
		Iterator<Interval> overlaps = overlapsList.iterator();
		while (overlaps.hasNext()) {
			Interval overlap = overlaps.next();
			DateTime startTime = overlap.getStart();
			DateTime endTime = overlap.getEnd();
			//Existing meeting overlaps at the beginning of the specified interval.
			if (startTime.isEqual(foStartTime) && endTime.isBefore(foEndTime)) {
				//Add the first gap.
				if (overlaps.hasNext()) {
					Interval nextOverlap = overlaps.next();
					if (endTime.isBefore(nextOverlap.getStart())) {
						startTime = nextOverlap.getStart();
						gaps.add(new Interval(endTime,startTime));
					}
					endTime = nextOverlap.getEnd();
					//nextOverlap is actually where the iterator cursor is currently present.
					prevOverlap = nextOverlap;
				}
			} 
			//Existing meeting overlaps somewhere in the middle of the specified interval.
			else if (prevOverlap == null && startTime.isAfter(foStartTime)) { 
				//Add the first gap (since prevOverlap == null).
				gaps.add(new Interval(foStartTime,startTime));
				prevOverlap = overlap;
			}
			//Account for gaps between consecutive overlapping meetings.
			else if (prevOverlap != null && prevOverlap.getEnd().isBefore(startTime)) {
				gaps.add(new Interval(prevOverlap.getEnd(),startTime));
				prevOverlap = overlap;
			}
			
			//Account for the last gap which might get overlooked - last available 
			//time slot after all overlapping meetings.
			if (!overlaps.hasNext() && endTime.isBefore(foEndTime)) {
				gaps.add(new Interval(endTime, foEndTime));
			} 
		}
		return gaps;
	}

	/**
	 * Add given employees to an existing meeting.
	 * @param employeeIds
	 * @param meetingId
	 * @return Relevant String message 
	 */
	public String addEmployeesToMeeting(int[] employeeIds, String meetingId) {
		String message = "";
		MeetingInfo meeting = validateMeetId(meetingId);
		if (meeting == null) {
			return "Invalid Meeting Id " + meetingId + "\r\n";
		}
		//Fetch Employees from storage
		List<Employee> employees = getEmployeesSubset(employeeIds);
		for(Employee employee : employees) {
			String messagePrefix = "Employee " + employee.getEmployeeId();
			String conflictMeetId;
			if (!employee.getEmployeeMeetings().isEmpty() && 
					employee.getEmployeeMeetings().contains(meeting)) {
				//Employee already added.
				message += messagePrefix + " is already added. \r\n";
			} else if ((conflictMeetId = employee.findOverlappingMeet(meeting.getMeetingInterval())) != null) {
				//Employee has another conflicting meeting during this meeting. 
				message += messagePrefix + " has conflict with meeting " + conflictMeetId + "\r\n";
			} else {
				//Employee needs to be added.
				employee.addNewMeeting(meeting);
				message += messagePrefix + " added successfully. \r\n";
			}
		}
		if (message.isBlank()) {
			return "No valid employees found. \r\n";
		}
		return message;
	}

	/**
	 * Validate whether the provided meetingId actually corresponds with
	 * an existing meeting in the storage.
	 * @param meetingId
	 * @return the found meeting info.
	 */
	public MeetingInfo validateMeetId(String meetingId) {
		//First three letters of every meeting Id corresponds
		//to the meeting room Id of that meeting.
		String meetingRoomId = meetingId.substring(0, 3);
		for(int i=0; i<getAllMeetingRooms().length; i++) {
			MeetingRoom meetingRoom = getAllMeetingRooms()[i];
			if (meetingRoom.getMeetingRoomId().equals(meetingRoomId)) {
				return meetingRoom.findMeetingToManage(meetingId);
			}
		}
		return null;
	}
	
	/**
	 * Remove given employees from an existing meeting.
	 * @param employeeIds
	 * @param meetingId
	 * @return Relevant String message
	 */
	public String removeEmployeesFromMeeting(int[] employeeIds, String meetingId) {
		String message = "";
		MeetingInfo meeting = validateMeetId(meetingId);
		if (meeting == null) {
			return "Invalid Meeting Id " + meetingId + "\r\n";
		}
		//Fetch Employees from storage
		List<Employee> employees = getEmployeesSubset(employeeIds);
		for(Employee employee : employees) {
			String messagePrefix = "Employee " + employee.getEmployeeId();
			if (employee.getEmployeeId() == meeting.getOrganizerId()) {
				//Given employee is the organizer of this meeting. Hence can't be removed.
				message += messagePrefix + " is the organizer of this meeting. Cannot remove organizer. \r\n";
			} else if (!employee.getEmployeeMeetings().isEmpty() && 
					!employee.getEmployeeMeetings().contains(meeting)) {
				//Employee is already not included in this meeting.
				message += messagePrefix + " doesn't exist for this meeting. \r\n";
			} else if (!employee.getEmployeeMeetings().isEmpty()) {
				//Employee needs to be removed.
				employee.removeMeeting(meeting);
				message += messagePrefix + " successfully removed. \r\n";
			}
		}
		if (message.isBlank()) {
			return "No valid employees found. \r\n";
		}
		return message;
	}
	
	/**
	 * Beautifies the String representation of the given time interval.
	 * @param meetingInterval
	 * @return
	 */
	public String intervalString(Interval meetingInterval) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
		return meetingInterval.getStart().toString(dtf) + "to"
				+ meetingInterval.getEnd().toString(dtf);
	}

}