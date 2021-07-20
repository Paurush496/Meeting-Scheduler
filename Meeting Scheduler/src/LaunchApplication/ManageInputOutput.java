package LaunchApplication;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import employees.Employee;
import meetManager.MeetScheduler;
import meeting.MeetingInfo;
import meeting.MeetingRoom;

public class ManageInputOutput {
	
	static AppStorage storage;
	static String consoleInput;
	static String consoleOutput;
	static java.io.BufferedReader stndin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
	static DateTime startTime;
	static DateTime endTime;

	//Entry method of the class.
	public static void processInput(String input, AppStorage data) throws IOException {
		storage = data;	
		switch(input) {
		case "1":
			bookNewMeeting();
			break;
		case "2":
			findFreeTimeSlots();
			break;
		case "3":
		case "4":
			manageEmployeesInAMeeting(input);
			break;
		case "5":
			printAllEmployees();
			break;
		case "6":
			printAllMeetingRooms();
			break;
		default: 
			consoleOutput = "Invalid Input.";
			break;
		}
		System.out.println(consoleOutput);	
	}
	
	//Process input details of new meeting.
	private static void bookNewMeeting() {
		int employeeId = 0;
		try {
			System.out.println("Please Enter Employee Id of the Organizer: ");
			System.out.flush();
			if ((consoleInput = stndin.readLine()) != null) {
				employeeId = Integer.valueOf(consoleInput).intValue();
			}
			System.out.println("Please Enter Start Time of the Meeting (in HH:mm): ");
			System.out.flush();
			if ((consoleInput = stndin.readLine()) != null) {
				//Format the date in given format
				startTime = MeetingInfo.dtf.parseDateTime(consoleInput);
			}
			System.out.println("Please Enter End Time of the Meeting (in HH:mm): ");
			System.out.flush();
			if ((consoleInput = stndin.readLine()) != null) {
				//Format the date in given format
				endTime = MeetingInfo.dtf.parseDateTime(consoleInput);
			}
		} catch(Exception ex) {
			consoleOutput = "Invalid input.. " + ex.getMessage();
			return;
		}
		//New meeting Id
		consoleOutput = ((MeetScheduler) storage).organizeNewMeet(employeeId, startTime, endTime);
	}
	
	//Process input details for finding free time slots.
	private static void findFreeTimeSlots() {
		int employeeIds[] = null;
		try {
			System.out.println("Please Enter comma separated Employee Ids: ");
			System.out.flush();
			if ((consoleInput = stndin.readLine()) != null) {
				String[] stringEmpIds = consoleInput.split(",");
				employeeIds = new int[stringEmpIds.length];
				for (int i=0; i<stringEmpIds.length; i++) {
					employeeIds[i] = Integer.valueOf(stringEmpIds[i]).intValue();
				}
			}
			System.out.println("Please Enter Start Time of the Interval (in HH:mm): ");
			System.out.flush();
			if ((consoleInput = stndin.readLine()) != null) {
				//Format the date in given format
				startTime = MeetingInfo.dtf.parseDateTime(consoleInput);
			}
			System.out.println("Please Enter End Time of the Interval (in HH:mm): ");
			System.out.flush();
			if ((consoleInput = stndin.readLine()) != null) {
				//Format the date in given format
				endTime = MeetingInfo.dtf.parseDateTime(consoleInput);
			}
		} catch(Exception ex) {
			consoleOutput = "Invalid input.. " + ex.getMessage();
			return;
		}
		//Free Time Slots.
		List<String> freeTimeSlots = ((MeetScheduler) storage).findFreeTimeSlots(employeeIds, startTime, endTime);	
		consoleOutput = "Free Time Slots: " + freeTimeSlots.toString();
	}

	//Process input details for adding/removing employees to/from a meeting.
	private static void manageEmployeesInAMeeting(String addOrRemove) {
		int employeeIds[] = null;
		try {
			System.out.println("Please Enter comma separated Employee Ids: ");
			System.out.flush();
			if ((consoleInput = stndin.readLine()) != null) {
				String[] stringEmpIds = consoleInput.split(",");
				employeeIds = new int[stringEmpIds.length];
				//Extract all employee Ids from the input.
				for (int i=0; i<stringEmpIds.length; i++) {
					employeeIds[i] = Integer.valueOf(stringEmpIds[i]).intValue();
				}
			}
			System.out.println("Please Enter Meeting Id (case sensitive): ");
			System.out.flush();
			if ((consoleInput = stndin.readLine()) != null) {
				consoleOutput = addOrRemove.equals("3") ? 
						((MeetScheduler) storage).addEmployeesToMeeting(employeeIds, consoleInput)
						: ((MeetScheduler) storage).removeEmployeesFromMeeting(employeeIds, consoleInput);
			}
		} catch(Exception ex) {
			consoleOutput = "Invalid input.. " + ex.getMessage();
		}
	}
	
	//Helper method to print all storage employees as String.
	private static void printAllEmployees() {
		//Fetch all employees.
		Employee[] allEmployees = storage.getAllEmployees();
		String employeeString = "";
		for (int i=0; i<allEmployees.length; i++) {
			String employee = "[Employee " + allEmployees[i].getEmployeeId() + ": [";
			List<MeetingInfo> meetings = allEmployees[i].getEmployeeMeetings();
			for (int j=0; j<meetings.size(); j++) {
				employee += meetings.get(j).toString();
			}
			employee += "]]" + "\r\n";
			employeeString += employee;
		}
		consoleOutput = "All Employees: " + employeeString;
	}

	//Helper method to print all storage meeting rooms as String.
	private static void printAllMeetingRooms() {
		//Fetch all meeting rooms.
		MeetingRoom[] allMeetings = storage.getAllMeetingRooms();
		String meetingString = "";
		for (int i=0; i<allMeetings.length; i++) {
			String meetRoom = "[Meeting Room " + allMeetings[i].getMeetingRoomId() + ": [";
			List<MeetingInfo> meetings = allMeetings[i].getMeetingRoomInfo();
			for (int j=0; j<meetings.size(); j++) {
				meetRoom += meetings.get(j).toString();
			}
			meetRoom += "]]" + "\r\n";
			meetingString += meetRoom;
		}
		consoleOutput = "All Meeting Rooms: " + meetingString;
	}
}
