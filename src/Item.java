/**
 * Class that defines an item object
 * @author Tilman Lindig, Will Wei, Shaunak Rajadhyaksha
 * @version April 20, 2016
 */

import java.util.Date;

public class Item {

	// Declare all required attributes of an Item
	// First Column
	private String name;
	private int shoppingType;
	private int department;
	private String quantity;
	private int daysToReminder;
	private String notes;

	// Second Column
	private int status;
	private String company;
	private String catalogNum;
	private String msds;
	private int units;
	private String storage;
	private String roomNum;

	private Date dateAcquired;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            item name
	 * @param shoppingType
	 *            its availability
	 * @param department
	 *            which department it belongs to
	 * @param quantity
	 *            how much there is
	 * @param daysToReminder
	 *            self-explanatory
	 * @param notes
	 *            any notes
	 * @param status
	 *            whether it's ok or empty
	 * @param company
	 *            which company it was ordered from
	 * @param catalog
	 *            catalog id
	 * @param msds
	 *            msds identification number
	 * @param unitType
	 *            whether it's measured in grams
	 * @param storage
	 *            where it is stored
	 * @param room
	 *            the room number where it is stored
	 * @param date
	 *            the date acquired
	 */
	Item(String name, int shoppingType, int department, String quantity,
			int daysToReminder, String notes, int status, String company,
			String catalog, String msds, int unitType, String storage,
			String room, Date date) {
		// First column
		this.name = name;
		this.shoppingType = shoppingType;
		this.department = department;
		this.quantity = quantity;
		this.daysToReminder = daysToReminder;
		this.notes = notes;

		// Second Column
		this.status = status;
		this.company = company;
		this.catalogNum = catalog;
		this.msds = msds;
		this.units = unitType;
		this.storage = storage;
		this.roomNum = room;
		this.dateAcquired = date;

	}

	/**
	 * Sets the name of the item object	
	 * @param name the given name
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the item
	 * @return the item's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the type of the item
	 * @param status the given type (status)
	 */
	void setShoppingStatus(int status) {
		shoppingType = status;
	}

	/**
	 * Returns the type of item
	 * @return the type (status) of the item
	 */
	int getShoppingStatus() {
		return shoppingType;
	}

	/**
	 * Sets the quantity of the item to a given quantity
	 * @param quantity the given quantity
	 */
	void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	/**
	 * Returns the quantity of the item
	 * @return the item's quantity
	 */
	String getQuantity() {
		return quantity;
	}

	/**
	 * Sets the type of units of the item
	 * @param units the given integer corresponding to the type of units
	 */
	void setUnits(int units) {
		this.units = units;
	}

	/**
	 * Returns the integer corresponding to the type of units
	 * @return the integer corresponding to the type of units
	 */
	int getUnits() {
		return units;
	}

	/**
	 * Sets the status of the item (Ok or Empty)
	 * @param status the given integer corresponding to the status
	 */
	void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Returns the integer corresponding to the status (Ok or Empty)
	 * @return the integer corresponding to the status of the item
	 */
	int getStatus() {
		return status;
	}

	/**
	 * Sets the date acquired of the item given a date
	 * @param date the given date object
	 */
	void setDateAcquired(Date date) {
		dateAcquired = date;
	}

	/**
	 * Returns the date acquired of the item being referred to
	 * @return the date object indicating the date the item was acquired
	 */
	Date getDateAcquired() {
		return dateAcquired;
	}

	/**
	 * Sets the name of the company from which the item was purchased
	 * @param company the given name of the company
	 */
	void setCompany(String company) {
		this.company = company;
	}

	/**
	 * Returns the name of the company from which the item was purchased
	 * @return the name of the item's company
	 */
	String getCompany() {
		return company;
	}

	/**
	 * Sets the name of the catalog number of the item being referred to
	 * @param catalog the given catalog number (as a String)
	 */
	void setCatalog(String catalog) {
		this.catalogNum = catalog;
	}

	/**
	 * Returns the catalog number of the item
	 * @return the item's catalog number (as a String)
	 */
	String getCatalog() {
		return catalogNum;
	}

	/**
	 * Sets the room number in which the item is stored
	 * @param room the given room number (as a String)
	 */
	void setRoomNumber(String room) {
		roomNum = room;
	}

	/**
	 * Returns the room number in which the item is stored
	 * @return the room number of the item (as a String)
	 */
	String getRoomNumber() {
		return roomNum;
	}

	/**
	 * Sets the miscellaneous notes of an item given a string of notes
	 * @param notes the given string of notes
	 */
	void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Returns the miscellaneous notes of the item being referred to
	 * @return the item's notes
	 */
	String getNotes() {
		return notes;
	}

	/**
	 * Sets the days from the current date when users are reminded of an item
	 * @param remind the given number of days from today after which a reminder is created
	 */
	void setDaysToReminder(int remind) {
		this.daysToReminder = remind;
	}

	/**
	 * Returns the integer corresponding to the number of days after which a reminder is made
	 * @return the number of days after which a reminder is made
	 */
	int getDaysToReminder() {
		return daysToReminder;
	}

	/**
	 * Sets the integer corresponding to the department to which the item belongs
	 * @param department the given integer corresponding to the item's department (General or Biology)
	 */
	void setDepartment(int department) {
		this.department = department;
	}

	/**
	 * Returns the integer corresponding to the department to which the item belongs
	 * @return the integer corresponding to the item's department (General or Biology)
	 */
	int getDepartment() {
		return department;
	}

	/**
	 * Sets the MSDS number of the item being referred to
	 * @param msds the given MSDS number of the item (as a String)
	 */
	void setMSDS(String msds) {
		this.msds = msds;
	}

	/**
	 * Returns the MSDS number for the item being referred to
	 * @return the item's MSDS number (as a String)
	 */
	String getMSDS() {
		return msds;
	}

	/**
	 * Sets the storage location of the item being referred to
	 * @param storage the given storage location of the item
	 */
	void setStorage(String storage) {
		this.storage = storage;
	}

	/**
	 * Returns the storage location of the item
	 * @return the storage location of the item being referred to (as a String)
	 */
	String getStorage() {
		return storage;
	}
	
}
