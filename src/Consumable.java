/**
 * A consumable item, such as a chemical
 * @version April 15, 2016
 */

import java.util.Date;

public class Consumable extends Item {

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
	Consumable(String name, int shoppingType, int department, String quantity,
			int daysToReminder, String notes, int status, String company,
			String catalog, String msds, int unitType, String storage,
			String room, Date date) {
		super(name, shoppingType, department, quantity, daysToReminder, notes,
				status, company, catalog, msds, unitType, storage, room, date);
	}

}
