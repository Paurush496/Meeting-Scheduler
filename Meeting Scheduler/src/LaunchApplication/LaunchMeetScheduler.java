package LaunchApplication;

import java.io.IOException;

import employees.Employee;
import meetManager.MeetScheduler;
import meeting.MeetingRoom;

/**
 * Meeting Scheduler Main Class.
 */

/**
 * @author Paurush Choudhary
 *
 */
public class LaunchMeetScheduler {
	static AppStorage storage = new MeetScheduler();

	//Initialize the storage with some data.
	static {
		Employee employee1 = new Employee(101);
		Employee employee2 = new Employee(102);
		Employee employee3 = new Employee(103);
		Employee employee4 = new Employee(104);
		Employee employee5 = new Employee(105);
		Employee employee6 = new Employee(106);
		Employee employee7 = new Employee(107);
		Employee employee8 = new Employee(108);
		Employee employee9 = new Employee(109);
		Employee employee10 = new Employee(110);
		Employee employee11 = new Employee(111);
		Employee employee12 = new Employee(112);
		Employee employee13 = new Employee(113);
		Employee employee14 = new Employee(114);
		Employee employee15 = new Employee(115);
		
		MeetingRoom meetRoom1 = new MeetingRoom("M01");
		MeetingRoom meetRoom2 = new MeetingRoom("M02");
		MeetingRoom meetRoom3 = new MeetingRoom("M03");
		MeetingRoom meetRoom4 = new MeetingRoom("M04");
		
		Employee[] allEmployees = {employee1, employee2, employee3, employee4, employee5,
				employee6, employee7, employee8, employee9, employee10,
				employee11, employee12, employee13, employee14, employee15};
		MeetingRoom[] allMeetingRooms = {meetRoom1, meetRoom2, meetRoom3, meetRoom4};

		storage.setAllEmployees(allEmployees);
		storage.setAllMeetingRooms(allMeetingRooms);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String menuText = "Welcome to FlyFin Meeting Scheduler! What would you like to do?" + "\r\n"
				+ "Enter 1 to Book a New Meeting" + "\r\n"
				+ "Enter 2 to Find Free Time Slots of Employees in a given Interval" + "\r\n"
				+ "Enter 3 to Add Employees to a Meeting" + "\r\n"
				+ "Enter 4 to Remove Employees from a Meeting" + "\r\n"
				+ "Enter 5 to Print All Employees' Meeting Info" + "\r\n"
				+ "Enter 6 to Print All Meeting Rooms' Meeting Info" + "\r\n"
				+ "Enter 9 to terminate the application" + "\r\n";
		
		java.io.BufferedReader stndin= new 
				java.io.BufferedReader(new java.io.InputStreamReader(System.in));;
		String input = null;
		System.out.println(menuText);
		System.out.flush();
		
		try {
			if ((input = stndin.readLine()) != null) {
				while (!input.equals("9")) {
					ManageInputOutput.processInput(input, storage);
					System.out.println(menuText);
					System.out.flush();
					input = stndin.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
