/**
 * The Queue 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class Queue {
	private String[] units = { "g", "mL", "pc." };
	private Node first;
	private static final int ALPHABETICAL = 0;
	private static final int DATE = 1;
	private int sortType = ALPHABETICAL;
	private static String[] header;

	DateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEE MMM dd yyyy 'at' hh:mm a");

	/**
	 * Gets a Queue of all the items with a shoppingStatus that is not Ok
	 * @return the Queue of Orders
	 */
	public Queue getOrders() {
		Node current = first;
		Queue orders = new Queue();
		while (current != null) {
			// Add the item to the Queue if it is a shopping item
			if (current.getData().getShoppingStatus() != 0)
				orders.add(current.getData());
			current = current.getNext();
		}
		return orders;
	}

	/**
	 * Sets the header of the Queue
	 * @param head the header text
	 */
	public void setHeader(String[] head) {
		header = head;
	}

	/**
	 * Returs the header text
	 * @return the header text as a String array
	 */
	String[] getHeader() {
		return header;
	}

	/**
	 * Returns the instance of the Queue
	 * @return the instance of the Queue
	 */
	Queue getQueue() {
		return this;
	}

	/**
	 * Checks to see if the Queue is empty
	 * @return the Queue's empty status
	 */
	public boolean isEmpty() {
		if (first == null)
			return true;
		return false;
	}

	/**
	 * Returns the size of the Queue
	 * @return the size of the Queue
	 */
	public int getSize() {
		return countSize(first);
	}

	/**
	 * Counts the size of the Queue
	 * @param head the first node to check
	 * @return the size of the queue
	 */
	private int countSize(Node head) {
		if (head == null)
			return 0;
		return 1 + countSize(head.getNext());
	}

	/**
	 * Add an item to the Queue
	 * @param object the given item
	 */
	
	public void add(Item object) {
		
		if (first != null)
			first = merge(first, new Node(object));
		else
			first = new Node(object);
	}

	
	/**
	 * Remove the given item
	 * @param index the index
	 * @return the Item to be removed
	 */
	public Item remove(int index) {
		if (index == 0) // If the item to be removed is the first item
		{
			Item temp = first.getData();
			first = first.getNext();
			return temp;
		}

		// If the item to be removed is not the first item
		Node current = first;// Point to first

		// Find the pointer before the one to remove
		for (int counter = 0; counter < index - 1; counter++) {
			current = current.getNext();
		}

		// Get the data to store in a temp variable
		Item temp = current.getNext().getData();
		// Point the current to the one two after
		current.setNext(current.getNext().getNext());

		return temp;
	}
	
	/**
	 * Removes the item and write to the file
	 * @param index the index of the item to be remove
	 * @return the removed Item
	 */
	public Item removeAndWriteToFile(int index)
	{
		Item temp = remove(index);
		writeToFile("Resources/Spreadsheet.csv");
		return temp;
	}

	/**
	 * Adds a new Item and write it to the file
	 * @param object the given item
	 */
	public void addNewItem(Item object) {
		add(object);
		writeToFile("Resources/Spreadsheet.csv");
	}

	/**
	 * Return the item
	 * @param index the given index
	 * @return the Item
	 */
	public Item get(int index) {
		Node current = first;
		for (int i = 0; i < index; i++) {
			current = current.getNext();
		}
		return current.getData();
	}

	/**
	 * Finds the index of a given item
	 * @param item the given item
	 * @return the index of the given item
	 */
	public int indexOf(Item item) {
		Node current = first;
		int index = 0;
		//Compare the item with the current item
		while (current != null) {
			if (current.getData().equals(item))
				return index;
			current = current.getNext();
			index++;
		}
		return -1;
	}

	/**
	 * Sorts the list given a sort type
	 * @param sortType the type of sort: alphabetical or date
	 */
	public void sortList(int sortType) {
		if (sortType == ALPHABETICAL)
			this.sortType = ALPHABETICAL;
		else this.sortType = DATE;
		first = splitList(first);
	}

	/**
	 * Sorts the Queue using a merge sort
	 * @param first the first Node
	 * @return the sorted lists of each half of the queue
	 */
	private Node splitList(Node first) {
		if (first == null || first.getNext() == null) {
			return first;
		}
		Node middle = getMiddle(first); // get the middle of the list
		Node secondHalf = middle.getNext();
		middle.setNext(null); // split the list into two halfs

		return merge(splitList(first), splitList(secondHalf)); // recurse on
																// that
	}

	/**
	 * Merges two sorted lists
	 * @param first the first node of the first list
	 * @param second the first node of the second list
	 * @return the merged lists
	 */
	private Node merge(Node first, Node second) {
		Node placeholderHead, current;
		placeholderHead = new Node(null);
		current = placeholderHead;
		// Sort from A -> Z or Old to New
		while (first != null && second != null) {
			if ((sortType == ALPHABETICAL && first.compareTo(second,
					ALPHABETICAL) < 0)
					|| (sortType == DATE && first.compareTo(second, DATE) < 0)) {
				current.setNext(first);
				first = first.getNext();
			} else {
				current.setNext(second);
				second = second.getNext();
			}
			current=current.getNext();
		}
		if (first == null)
			current.setNext(second);
		else
			current.setNext(first);
		return placeholderHead.getNext();
	}

	/**
	 * Returns the middle of the list
	 * @param head the beginning of the list
	 * @return the middle node of the list
	 */
	private Node getMiddle(Node head) {
		if (head == null) {
			return head;
		}
		Node slow, fast;
		slow = head;
		fast = head;
		while (fast.getNext() != null && fast.getNext().getNext() != null) {
			slow = slow.getNext();
			fast = fast.getNext().getNext();
		}
		return slow;
	}

	/**
	 * Writes to file
	 * @param s the name of the file
	 */
	void writeToFile(String s) {
		PrintWriter output;
		try {
			output = new PrintWriter(new File(s));
			// Print the header of the data file
			for (String line : header)
				output.println(line);

			// Print the data of the file
			Node current = first;
			while (current != null) {
				// First Col
				output.print(current.getData().getName() + ",");
				output.print((current.getData() instanceof Consumable) ? "Consumable,"
						: "Equipment,");
				{
					int shoppingStatus = current.getData().getShoppingStatus();
					if (shoppingStatus == 0)
						output.print("No,");
					else if (shoppingStatus == 1)
						output.print("Needed,");
					else
						output.print("Ordered,");
				}
				output.print(current.getData().getDepartment() == 0 ? "General,"
						: "Biology,");
				output.print(current.getData().getQuantity() + ",");
				output.print(current.getData().getDaysToReminder() + ",");
				output.print(current.getData().getNotes() + ",");

				// Second Col
				output.print(current.getData().getStatus() == 0 ? "Ok,"
						: "Empty,");
				output.print(current.getData().getCompany() + ",");
				output.print(current.getData().getCatalog() + ",");
				output.print(current.getData().getMSDS() + ",");
				output.print(current.getData().getUnits() + ",");
				output.print(current.getData().getStorage() + ",");
				output.print(current.getData().getRoomNumber() + ",");
				output.println(DATE_FORMAT.format(current.getData()
						.getDateAcquired()));
				current = current.getNext();
			}
			// Close the PrintWriter
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Error",
					"Error writing to the file.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Converts the Queue to an Object array
	 * @return the 2D Object array
	 */
	Object[][] convertToArray() {
		if (getSize() == 0)
			return null;
		Object[][] allList = new Object[getSize()][4];// ////////////////////////////////
														// Number of things
		Node current = first;

		int index = 0;
		while (current != null) {
			Item cur = current.getData();
			allList[index][0] = cur.getName();
			allList[index][1] = cur.getQuantity() + " " + units[cur.getUnits()];
			allList[index][2] = DATE_FORMAT.format(cur.getDateAcquired());
			current = current.getNext();
			index++;
		}
		return allList;
	}

	/**
	 * Searches the Queue for a String s
	 * @param s
	 * @return
	 */
	Queue search(String s) {
		Queue searchQueue = new Queue();
		Node current = first;
		Item curr;
		String attributes;

		while (current != null) {
			curr = current.getData();
			attributes = (curr.getName() + curr.getCatalog()
					+ curr.getCompany() + curr.getNotes()).toLowerCase();

			if (attributes.indexOf(s) >= 0)
				searchQueue.add(curr);
			current = current.getNext();
		}

		return searchQueue;
	}

	/**
	 * Reminds to order the items that have the passed dates
	 * @param date the current date
	 * @return the Object 2D array of item reminders
	 */
	Object[][] remindToOrder(Date date) {
		
		Node current = first;
		Queue reminders = new Queue();
		while(current != null)
		{
			if((current.getData().getDateAcquired().getTime()/1000/60/60/24 + current.getData().getDaysToReminder())
					< date.getTime()/1000/60/60/24)
				reminders.add(current.getData());
			current = current.getNext();
		}
		Object[][] reminds = new Object[reminders.getSize()][5];
		for(int item = 0; item < reminds.length; item++)
		{
			reminds[item][0] = reminders.get(item).getName();
			reminds[item][1] = reminders.get(item).getQuantity();
			reminds[item][2] = reminders.get(item).getUnits();
			reminds[item][3] = reminders.get(item).getDateAcquired();
			reminds[item][4] = reminders.get(item).getDaysToReminder();
		}
		return reminds;
	}
}
