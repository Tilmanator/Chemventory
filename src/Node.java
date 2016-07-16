/**
 * A general node to be used by a data structure
 * 
 * @author William Wei, Shaunak R, Tilman Lindig
 * @version April 14, 2016
 */
public class Node {
	private Node next; // the next in line
	private Node previous; // the previous node
	private Item data; // the data contained in this node

	/**
	 * Constructor
	 * 
	 * @param object
	 *            the data to be stored
	 */
	Node(Item object) {
		data = object;
	}

	/**
	 * Returns stored data
	 * 
	 * @return the data
	 */
	public Item getData() {
		return data;
	}

	/**
	 * Set the data in the node
	 * 
	 * @param data
	 *            the data to be stored
	 */
	public void setData(Item data) {
		this.data = data;
	}

	/**
	 * Get the next node in line
	 * 
	 * @return the next node
	 */
	public Node getNext() {
		return next;
	}

	/**
	 * Sets the next node
	 * 
	 * @param next
	 *            the next node
	 */
	public void setNext(Node next) {
		this.next = next;
	}

	/**
	 * Get the previous node in line
	 * 
	 * @return the previous node
	 */
	public Node getPrevious() {
		return previous;
	}

	/**
	 * Sets the previous node
	 * 
	 * @param previous
	 *            the node to be set
	 */
	public void setPrevious(Node previous) {
		this.previous = previous;
	}

	/**
	 * Compares two nodes based on a type
	 * 
	 * @param node
	 *            the node to be compared to
	 * @param sortType
	 *            the type of sort
	 * @return the result of the coparison numerically
	 */
	public int compareTo(Node node, int sortType) {
		// Compare the names regardless of case
		if (sortType == 0)
			return data.getName().toLowerCase()
					.compareTo(node.getData().getName().toLowerCase());
		else {
			if (data.getDateAcquired().after(node.getData().getDateAcquired()))
				return 1;
			else
				return -1;
		}

	}

}
