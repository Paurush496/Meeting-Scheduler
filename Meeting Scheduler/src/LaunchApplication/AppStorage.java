package LaunchApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import employees.Employee;
import meeting.MeetingRoom;

/*
 * Abstract class for main storage.
 */
public abstract class AppStorage {

	protected static Employee[] allEmployees;
	protected static MeetingRoom[] allMeetingRooms;
	
	public AppStorage() {
		super();
	}
	public Employee[] getAllEmployees() {
		return allEmployees;
	}
	public void setAllEmployees(Employee[] allEmployees) {
		AppStorage.allEmployees = allEmployees;
	}
	public MeetingRoom[] getAllMeetingRooms() {
		return allMeetingRooms;
	}
	public void setAllMeetingRooms(MeetingRoom[] allMeetingRooms) {
		AppStorage.allMeetingRooms = allMeetingRooms;
	}
	/**
	 * From all storage employees, extract subset of given employees.
	 * @param employeesIds
	 * @return
	 */
	public List<Employee> getEmployeesSubset(int[] employeesIds) {
		List<Employee> searchEmployees = new ArrayList<Employee>();
		for (int i=0; i<employeesIds.length; i++) {
			for (int j=0; j<getAllEmployees().length; j++) {
				if (employeesIds[i] == getAllEmployees()[j].getEmployeeId()) {
					searchEmployees.add(getAllEmployees()[j]);
					break;
				}
			}
		}
		return searchEmployees;
	}
	@Override
	public String toString() {
		return "AppStorage [allEmployees=" + Arrays.toString(allEmployees) + ", allMeetingRooms="
				+ Arrays.toString(allMeetingRooms) + "]";
	}
	
}
