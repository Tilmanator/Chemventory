/**
 * The ChemventoryFrame sets up the GUI
 * and handles interaction
 * @author Will, Tilman, Shaunak
 * @version April 20 2016
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class ChemventoryFrame extends JPanel {
	private static final long serialVersionUID = -4424635930877588515L;
	// Set up some initial constants
	private Dimension FRAME_SIZE = new Dimension(1000, 700);
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEE MMM dd yyyy 'at' hh:mm a");
	private static final DateFormat BACKUP_DATE_FORMAT = new SimpleDateFormat(
			"MMM'-'dd'-'yyyy");

	// Declare final fonts for the Help tab
	private static final Font BODY = new Font("Verdana", Font.PLAIN, 20);

	// Set up arrays of constants to be used for display
	private static final String[] UNITS = { "g", "mL", "pc" };
	// private static final String[] SHOPPING_CHOICES = {
	// "Add item to database",
	// "Add item to shopping list" };
	private static final int CONSUMABLE = 0, EQUIPMENT = 1;
	private static final int yesNoOption = JOptionPane.YES_NO_OPTION;

	// Make tables global so they can be updated easily
	private static JTable table, orderTable, searchTable;

	// Keep track of the messages to be displayed
	private static Object[][] reminders;
	private static JTextArea reminderMssgs;
	private static JTextArea historyMssgs;
	private static String[] messages = new String[20];
	private static String[] history = new String[30];
	private static String historyMessage;
	private static Date currentDate;

	// Boolean variables used for checking for edits
	private static boolean changedStatus;
	private static boolean changedDetails;

	// Table variables
	private static int selectedRow = -1, searchQueueSelectedRow = -1,
			orderQueueSelectedRow = -1, currentStatus;
	private static Queue allQueue, orderQueue = null, searchQueue;

	JScrollPane helpScroll;
	JPanel sideBySide;

	/**
	 * Initialize the ChemventoryFrame object
	 */
	ChemventoryFrame() {
		// Create a new JPanel that holds everything
		super(new GridLayout(1, 0));
		this.setBackground(new Color(204, 255, 255));
		setPreferredSize(FRAME_SIZE);
		// Reads in the data from the 3 text files
		readData();

		// Create the panels to be used
		JPanel bulletinPanel = createBulletinPanel();
		JPanel orderPanel = createOrderPanel();
		JPanel databasePanel = createDatabasePanel();
		JPanel addPanel = createAddPanel();
		JPanel searchPanel = createSearchPanel();
		JPanel historyPanel = createHistoryPanel();
		JPanel helpPanel = createHelpPanel();

		// Add tabs
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Bulletin", null, bulletinPanel);
		tabbedPane.addTab("Orders", null, orderPanel);
		tabbedPane.addTab("Database", null, databasePanel);
		tabbedPane.addTab("Add", null, addPanel);
		tabbedPane.addTab("Search", null, searchPanel);
		tabbedPane.addTab("History", null, historyPanel);
		tabbedPane.addTab("Help", null, helpPanel);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

		// Customise the pane and we're off
		customise(tabbedPane);
		add(tabbedPane);
		this.setVisible(true);
	}

	/**
	 * Creates the panel for the bulletin tab
	 * 
	 * @return the bulletin panel
	 */
	public JPanel createBulletinPanel() {
		// Declare the panel and set its layout
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		// Create Reminders and Messages Labels
		JLabel reminder = new JLabel("Reminders");
		reminder.setFont(new Font("Verdana", 1, 25));
		reminder.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel message = new JLabel("Messages");
		message.setFont(new Font("Verdana", 1, 25));
		message.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Create reminders text area
		reminderMssgs = new JTextArea();
		reminderMssgs.setEditable(false);
		reminderMssgs.setFont(new Font("Serif", Font.BOLD, 24));

		// Stores all items that require a reminder in an array
		// length, 4 col(name, quantity, units, DateAcquired)
		// Display reminders
		reminders = allQueue.remindToOrder(new Date());
		if (reminders.length == 0)
			reminderMssgs.append("No Reminders");
		else {
			for (int reminderMssg = 0; reminderMssg < reminders.length; reminderMssg++) {
				reminderMssgs.append(String.format(
						"It has been %d days since %s was acquired.%n",
						(new Date().getTime() / 1000 / 60 / 60 / 24)
								- ((Date) reminders[reminderMssg][3]).getTime()
								/ 1000 / 60 / 60 / 24,
						(String) reminders[reminderMssg][0]));
			}

		}

		// Create the messages text area
		JTextArea mssgs = new JTextArea();
		mssgs.setEditable(false);
		mssgs.setFont(new Font("Serif", Font.BOLD, 24));

		// Read the arrays from the text file and add the messages to the
		// messages textarea
		try {
			readArrays();

			for (int i = 0; i < messages.length; i++) {
				if (messages[i] != null && !messages[i].equals("null"))
					mssgs.append(messages[i] + "\n");
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Warning",
					"There is a reading error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		// Create 2 scroll panes for the two textareas
		JScrollPane reminderMessages = new JScrollPane(reminderMssgs);
		reminderMessages.setPreferredSize(new Dimension(2000, 700));
		JScrollPane bulletinMessages = new JScrollPane(mssgs);
		bulletinMessages.setPreferredSize(new Dimension(2000, 700));

		// Add the labels and scroll panes to the panel
		panel.add(reminder);
		panel.add(reminderMessages);
		panel.add(message);
		panel.add(bulletinMessages);

		// Way to add a message to the messages text area
		JButton addMessage = new JButton("Add Message");
		panel.add(addMessage);
		addMessage.requestFocus();
		addMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String message = JOptionPane
						.showInputDialog("Enter your message: ");
				messages = updateArray(messages, message);
				writeToTextFile(messages, "Bulletin");
				mssgs.setText("");
				for (int i = 0; i < messages.length; i++) {
					if (messages[i] != null && !messages[i].equals("null"))
						mssgs.append(messages[i] + "\n");
				}
				mssgs.setCaretPosition(0);
				bulletinMessages.revalidate();
			}
		});
		return panel;
	}

	/**
	 * Creates the panel for the history tab
	 * 
	 * @return the history panel
	 */
	public JPanel createHistoryPanel() {

		// Create the panel
		JPanel panel = new JPanel();

		// Create the title label
		panel.setLayout(new BorderLayout());
		JLabel title = new JLabel("CHEMVENTORY HISTORY", JLabel.CENTER);
		title.setFont(new Font("Verdana", 1, 20));
		panel.add(title, BorderLayout.PAGE_START);

		// Create the text area
		historyMssgs = new JTextArea();
		historyMssgs.setEditable(false);
		historyMssgs.setFont(new Font("Serif", Font.BOLD, 24));

		// Read the messages from the textfile and add them to the textarea
		try {
			readArrays();

			for (int i = 0; i < history.length; i++) {
				if (history[i] != null && !history[i].equals("null"))
					historyMssgs.append(history[i] + "\n");
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Warning",
					"There is a reading error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		// Create a scroll pane for the text area and add it to the panel
		JScrollPane historyMessages = new JScrollPane(historyMssgs);
		panel.add(historyMessages);

		return panel;
	}

	/**
	 * Makes the panel with helping guides
	 * 
	 * @return the help panel
	 */
	public JPanel createHelpPanel() {
		JPanel help = new JPanel();
		// help.setLayout(new BoxLayout(help, BoxLayout.Y_AXIS));
		help.setLayout(new BorderLayout());

		sideBySide = new JPanel();
		sideBySide.setLayout(new BoxLayout(sideBySide, BoxLayout.Y_AXIS));
		sideBySide.setAlignmentX(LEFT_ALIGNMENT);
		sideBySide.setAlignmentY(LEFT_ALIGNMENT);
		helpScroll = new JScrollPane(sideBySide);

		JPanel buttons = new JPanel();
		JLabel click = new JLabel("<<< Click on a button for help");

		// Where directions will be written
		JTextArea display = new JTextArea();
		display.setFont(new Font("Sans-serif", Font.BOLD, 15));
		display.setAlignmentY(CENTER_ALIGNMENT);
		display.setText("Click on a button to read a tutorial");

		// Guides
		JButton bulletinHelp = new JButton("   Bulletin  ");
		bulletinHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Remove all previous panels
				sideBySide.removeAll();
				JTextArea display1 = new JTextArea();

				// Sets the text
				display1.setText("The Bulletin contains two areas. The top area contains reminders\n");
				display1.append("to order certain items. The bottom area contains messages that are\n");
				display1.append(" displayed.\n");
				display1.append("\nReminder Area\n\n");
				display1.setFont(BODY);
				display1.append("When each item is created in the Add tab, the reminder field indicates\n");
				display1.append("the number of days until a reminder will be posted in this area.\n");

				// Sets the image
				JLabel image1 = new JLabel(new ImageIcon("Resources/HelpImages/1b.png"));
				image1.setAlignmentX(Component.RIGHT_ALIGNMENT);

				// Sets the text
				JTextArea display2 = new JTextArea();
				display2.setEditable(false);
				display2.setFont(BODY);
				display2.append("\nAfter this amount of days has elapsed since the item’s creation,\n");
				display2.append("a reminder will be posted here. The reminder will show the number\n");
				display2.append("of days that have passed since the item was created. To postpone a\n");
				display2.append("reminder, select the item in the Database tab (or search it in the\n");
				display2.append("Search tab). Then, press the “Edit” button and enter the number of\n");
				display2.append("days from today until the next reminder is needed.\n");

				// Sets the image
				JLabel image2 = new JLabel(new ImageIcon("Resources/HelpImages/2b.png"));
				image2.setAlignmentX(Component.RIGHT_ALIGNMENT);

				// Sets the text
				JTextArea display3 = new JTextArea();
				display3.setEditable(false);
				display3.append("\nMessage Area\n\n");
				display3.setFont(BODY);
				display3.append("The \"Add Message\" button pops up a box that allows the user to post\n");
				display3.append("a message onto the message area. The messages\' creation dates will\n");
				display3.append("be displayed beside them. In the message area, the most recent posts\n");
				display3.append("are displayed near the top.\n");

				// Sets the image
				JLabel image3 = new JLabel(new ImageIcon("Resources/HelpImages/3b.png"));
				image3.setAlignmentX(Component.RIGHT_ALIGNMENT);

				// Adds the panels to the help panel
				sideBySide.add(display1);
				sideBySide.add(image1);
				sideBySide.add(display2);
				sideBySide.add(image2);
				sideBySide.add(display3);
				sideBySide.add(image3);
				helpScroll.revalidate();
			}
		});
		JButton ordersHelp = new JButton("  Orders ");
		ordersHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sideBySide.removeAll();
				JTextArea display1 = new JTextArea();
				display1.setEditable(false);
				display1.setFont(BODY);
				display1.setText("In the Orders Tab, items that need to be ordered and items that\n");
				display1.append("have already been ordered are displayed. Red names indicate items\n");
				display1.append("that need to be ordered. Yellow names indicate items that have already\n");
				display1.append("been ordered, but not yet received. These items can still be found in \n");
				display1.append("the Database, so the Orders Tab allows the user to see clearly \n");
				display1.append("all the immediate items that are needed or ordered. Clicking an item\n");
				display1.append("on the list will show its information on the display area to the right.\n");

				JLabel image1 = new JLabel(new ImageIcon("Resources/HelpImages/1o.png"));
				image1.setAlignmentX(Component.RIGHT_ALIGNMENT);

				JTextArea display2 = new JTextArea();
				display2.setEditable(false);
				display2.setFont(BODY);
				display2.append("\nThe order status is shown in the table and in the display area next\n");
				display2.append("to Availability. This status can be changed using the three buttons\n");
				display2.append("at the bottom of the display area. When an item is added directly to\n");
				display2.append("the shopping list, its order status is set to \"Needed\" as a default with\n");
				display2.append("the color red. By pressing the \"Change to \'Ordered\'\" button, the user\n");
				display2.append("indicates that the item is already ordered and the name changes to\n");
				display2.append("yellow. Pressing the \"Change to \'Received\'\" will remove the item from the \n");
				display2.append("Orders tab. The item can still be found in the Database tab.\n");

				JLabel image2 = new JLabel(new ImageIcon("Resources/HelpImages/2o.png"));
				image2.setAlignmentX(Component.RIGHT_ALIGNMENT);

				JTextArea display3 = new JTextArea();
				display3.setEditable(false);
				display3.setFont(BODY);
				display3.append("\nThe order status can also be changed by editing an item in the Database.\n");
				display3.append("Once the user has selected the item in the Database or have found the\n");
				display3.append("the item using the Search tab, the “Edit” button allows the user to change\n");
				display3.append("the status of the item directly without opening the Order tab. Pressing\n");
				display3.append("\"Order\" will change the shopping status from received to needed,\n");
				display3.append("\"Set ordered\" will change the status from needed to ordered, and \n");
				display3.append("\"Set received\" will change the status from ordered to received. Both methods\n");
				display3.append("of changing the order status result in the same outcome.\n");

				JLabel image3 = new JLabel(new ImageIcon("Resources/HelpImages/3o.png"));
				image3.setAlignmentX(Component.RIGHT_ALIGNMENT);

				sideBySide.add(display1);
				sideBySide.add(image1);
				sideBySide.add(display2);
				sideBySide.add(image2);
				sideBySide.add(display3);
				sideBySide.add(image3);
				helpScroll.revalidate();
			}
		});
		JButton databaseHelp = new JButton("   Database  ");
		databaseHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sideBySide.removeAll();
				JTextArea display1 = new JTextArea();
				display1.setEditable(false);
				display1.setFont(BODY);
				display1.setText("The Database tab displays all the items in the current database. All\n");
				display1.append("information on the database can be searched using the Search tab. The\n");
				display1.append("Database tab can be sorted alphabetically or by date by clicking on the\n");
				display1.append("corresponding table heading \"Name\" or \"Date Acquired\". Clicking on an item\n");
				display1.append("in the list will display more information on the right.\n");

				JLabel image1 = new JLabel(new ImageIcon("Resources/HelpImages/1d.png"));
				image1.setAlignmentX(Component.RIGHT_ALIGNMENT);

				JTextArea display2 = new JTextArea();
				display2.setEditable(false);
				display2.setFont(BODY);
				display2.append("\nItems can be edited or removed using the two buttons at the bottom of the\n");
				display2.append("display area. Pressing the “Remove” button will permanently delete the item\n");
				display2.append("from both the database and the spreadsheet file. Pressing the \"Edit\" button\n");
				display2.append("will pop up a window that allows the user to edit the item’s information. The\n");
				display2.append("information is saved only when the \"Ok\" button is pressed. Clicking on \"Cancel\"\n");
				display2.append("or closing the pop-up window will not make any changes to the item.\n");

				JLabel image2 = new JLabel(new ImageIcon("Resources/HelpImages/2d.png"));
				image2.setAlignmentX(Component.RIGHT_ALIGNMENT);

				JTextArea display3 = new JTextArea();
				display3.setEditable(false);
				display3.setFont(BODY);
				display3.append("\nAll the information in the Database is stored and updated in a spreadsheet file.\n");
				display3.append("(.csv)\n");
				display3.append("\nThis file can be found in the Resources folder.\n");

				JLabel image3 = new JLabel(new ImageIcon("Resources/HelpImages/3d.png"));
				image3.setAlignmentX(Component.RIGHT_ALIGNMENT);

				sideBySide.add(display1);
				sideBySide.add(image1);
				sideBySide.add(display2);
				sideBySide.add(image2);
				sideBySide.add(display3);
				sideBySide.add(image3);
				helpScroll.revalidate();
			}
		});
		JButton addHelp = new JButton("Add");
		addHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sideBySide.removeAll();
				JTextArea display1 = new JTextArea();
				display1.setEditable(false);
				display1.setFont(BODY);
				display1.setText("The Add tab allows users to add items to the database. Note: Adding an\n");
				display1.append("item to the database will allow the item to be seen in the Database tab\n");
				display1.append("only, while adding an item to the Orders tab will allow the item to be found\n");
				display1.append("in both tabs.");

				JLabel image1 = new JLabel(new ImageIcon("Resources/HelpImages/1a.png"));
				image1.setAlignmentX(Component.RIGHT_ALIGNMENT);

				sideBySide.add(display1);
				sideBySide.add(image1);
				helpScroll.revalidate();
			}
		});
		JButton searchHelp = new JButton(" Search");
		searchHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sideBySide.removeAll();
				JTextArea display1 = new JTextArea();
				display1.setEditable(false);
				display1.setFont(BODY);
				display1.setText("The Search tab allows the user to search for items in the database using\n");
				display1.append("key words. The items found can then be removed or edited from the database\n");
				display1.append("by using the buttons at the bottom of the display panel.\n");

				JLabel image1 = new JLabel(new ImageIcon("Resources/HelpImages/1s.png"));
				image1.setAlignmentX(Component.RIGHT_ALIGNMENT);

				sideBySide.add(display1);
				sideBySide.add(image1);
				helpScroll.revalidate();
			}
		});
		JButton historyHelp = new JButton("  History  ");
		historyHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sideBySide.removeAll();
				JTextArea display1 = new JTextArea();
				display1.setEditable(false);
				display1.setFont(BODY);
				display1.setText("All actions performed are recorded in the History tab.\n");
				JLabel image1 = new JLabel(new ImageIcon("Resources/HelpImages/1h.png"));
				image1.setAlignmentX(Component.RIGHT_ALIGNMENT);
				sideBySide.add(display1);
				sideBySide.add(image1);
				helpScroll.revalidate();
			}
		});
		JButton helpHelp = new JButton("Help");
		helpHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sideBySide.removeAll();
				JTextArea display1 = new JTextArea();
				display1.setEditable(false);
				display1.setFont(BODY);
				display1.setText("The Help tab contains descriptions of all the tabs as well as information\n");
				display1.append("on how to use the program.\n");

				JLabel image1 = new JLabel(
						new ImageIcon("Resources/HelpImages/1help.png"));
				image1.setAlignmentX(Component.RIGHT_ALIGNMENT);

				sideBySide.add(display1);
				sideBySide.add(image1);
				helpScroll.revalidate();
			}
		});

		// Setting up the look of the panel
		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));
		buttons.add(bulletinHelp);
		buttons.add(ordersHelp);
		buttons.add(databaseHelp);
		buttons.add(addHelp);
		buttons.add(searchHelp);
		buttons.add(historyHelp);
		buttons.add(helpHelp);
		buttons.add(click);

		// Add the buttons and the display information to the help panel
		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));
		//buttonsPanel.add(buttons);
		help.add(buttons, BorderLayout.NORTH);
		help.add(helpScroll, BorderLayout.CENTER);

		return help;
	}

	/**
	 * Creates the panel that displays items that need to or have been ordered
	 * 
	 * @return the order panel
	 */
	public JPanel createOrderPanel() {
		// Initialize and set up the order panel
		JPanel order = new JPanel();
		order.setLayout(new BoxLayout(order, SwingConstants.HORIZONTAL));

		// Receive the orders from the allQueue
		orderQueue = allQueue.getOrders();
		orderTable = makeDatabaseTable(orderQueue.convertToArray());

		// Add the created table to the JScrollPane
		JScrollPane scrollPaneTable = new JScrollPane(orderTable);
		scrollPaneTable.setPreferredSize(new Dimension(700, 300));

		// Create the JTextArea where the detailed information for each item
		// will be displayed
		JTextArea display = new JTextArea(null, 0, 0);
		display.setLineWrap(true);
		display.setMinimumSize(new Dimension(320, 300));
		display.setEditable(false);

		/**
		 * Create the three buttons that change the shopping status of the
		 * Database item
		 */
		// Create the "Change to Needed" button
		JButton shopButton = new JButton();
		shopButton.setLayout(new BorderLayout());
		JLabel label1 = new JLabel("Change to");
		JLabel label2 = new JLabel("\"Needed\"");
		shopButton.add(BorderLayout.NORTH, label1);
		shopButton.add(BorderLayout.SOUTH, label2);
		shopButton.requestFocus();
		// Listen to actions from the "Change to Needed" button
		shopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// If an item is selected
				if (orderQueueSelectedRow != -1) {
					// Get the item's reference from the orderQueue
					Item selection = orderQueue.get(orderQueueSelectedRow);
					// Check to see if the item is already the same shopping
					// status
					if (selection.getShoppingStatus() == 1)
						JOptionPane.showMessageDialog(null, "The item "
								+ selection.getName()
								+ " is already set to \"Needed\".",
								"Attention", JOptionPane.INFORMATION_MESSAGE);
					else if (JOptionPane.showConfirmDialog(
							null,
							"Are you sure you want to set "
									+ selection.getName() + " to \"Needed\"?",
							"Are you really sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						// Change item's shoppingStatus to "Needed"
						orderQueue.get(orderQueueSelectedRow)
								.setShoppingStatus(1);
						allQueue.get(allQueue.indexOf(selection))
								.setShoppingStatus(1);
						allQueue.writeToFile("Resources/Spreadsheet.csv");
						backup();
						updateDatabase(orderTable, orderQueue);
						// Update the display text on the right side of the
						// panel
						setDisplay(display, selection);

						// Write to history
						historyMessage = selection.getName()
								+ " was set to Needed.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
					}
				} else
					// If an item is not selected
					JOptionPane.showMessageDialog(null, "No item selected.",
							"Attention", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		// Create the "Change to Ordered" Button
		JButton orderButton = new JButton();
		orderButton.setLayout(new BorderLayout());
		JLabel label3 = new JLabel("Change to");
		JLabel label4 = new JLabel("\"Ordered\"");
		orderButton.add(BorderLayout.NORTH, label3);
		orderButton.add(BorderLayout.SOUTH, label4);
		orderButton.requestFocus();
		// Listen to actions from the "Change to Ordered" button
		orderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// If an item is selected
				if (orderQueueSelectedRow != -1) {
					// Get the item's reference from the orderQueue
					Item selection = orderQueue.get(orderQueueSelectedRow);
					// Check to see if the item is already the same shopping
					// status
					if (selection.getShoppingStatus() == 2)
						JOptionPane.showMessageDialog(null, "The item "
								+ selection.getName()
								+ " is already set to \"Ordered\".",
								"Attention", JOptionPane.INFORMATION_MESSAGE);
					else if (JOptionPane.showConfirmDialog(
							null,
							"Are you sure you want to set "
									+ selection.getName() + " to \"Ordered\"?",
							"Are you really sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						// Update the item's status in both the orderQueue and
						// the allQueue
						allQueue.get(allQueue.indexOf(selection))
								.setShoppingStatus(2);
						allQueue.writeToFile("Resources/Spreadsheet.csv");
						backup();
						orderQueue.get(orderQueueSelectedRow)
								.setShoppingStatus(2);
						updateDatabase(orderTable, orderQueue);

						// Update the display text on the right side of the
						// panel
						setDisplay(display, selection);

						// Write to history
						historyMessage = selection.getName()
								+ " was set to Ordered.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
					}
				} else
					// If an item is not selected
					JOptionPane.showMessageDialog(null, "No item selected.",
							"Attention", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		// Create the "Change to Received" button
		JButton databaseButton = new JButton();
		databaseButton.setLayout(new BorderLayout());
		JLabel label5 = new JLabel("Change to");
		JLabel label6 = new JLabel("\"Received\"");
		databaseButton.add(BorderLayout.NORTH, label5);
		databaseButton.add(BorderLayout.SOUTH, label6);
		databaseButton.requestFocus();
		// Listen to actions from the "Change to Received" button
		databaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// If an item is selected
				if (orderQueueSelectedRow != -1) {
					// Get the item's reference from the orderQueue
					Item selection = orderQueue.get(orderQueueSelectedRow);
					if (JOptionPane
							.showConfirmDialog(
									null,
									"Are you sure you want to move "
											+ selection.getName()
											+ " from the "
											+ ((selection.getShoppingStatus() == 1) ? "\"Needed\""
													: "\"Ordered\"")
											+ " list to the Database?",
									"Are you really sure?", yesNoOption) == JOptionPane.YES_OPTION) {
						// Set the item to OK and remove it from the orderQueue
						allQueue.get(allQueue.indexOf(selection))
								.setShoppingStatus(0);
						allQueue.writeToFile("Resources/Spreadsheet.csv");
						backup();
						orderQueue.remove(orderQueueSelectedRow);

						// Update the database and reset the display
						updateDatabase(orderTable, orderQueue);
						display.setText("");
						orderQueueSelectedRow = -1;

						// Write to history
						historyMessage = selection.getName()
								+ " was moved to the Database.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
					}
				} else
					// If an item is not selected
					JOptionPane.showMessageDialog(null, "No item selected.",
							"Attention", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// Create the JPanel that holds the buttons
		JPanel buttons = new JPanel(new GridLayout(1, 3));
		buttons.add(shopButton);
		buttons.add(orderButton);
		buttons.add(databaseButton);

		// Add the Table to the order JPanel
		order.add(scrollPaneTable);

		// Sets the layouts
		JPanel right = new JPanel();
		GroupLayout layout = new GroupLayout(right);
		right.setLayout(layout);

		// Sets the horizontal and vertical settings of the add panel
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(display).addComponent(buttons)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(display).addComponent(buttons, 50, 50, 50));
		order.add(right);

		// Allows selections to be tracked and displayed
		ListSelectionModel rowSM = orderTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// Don't allow data to be changed manually
				if (orderTable.isEditing())
					orderTable.getCellEditor().stopCellEditing();

				// Get the selected row
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					display.removeAll();
				} else {
					// Display the information beside the table
					orderQueueSelectedRow = lsm.getMinSelectionIndex();
					Item curr = orderQueue.get(orderQueueSelectedRow);
					setDisplay(display, curr);
				}
			}
		});
		return order;
	}

	/**
	 * Creates the panel that contains all items
	 * 
	 * @return the database panel
	 */
	public JPanel createDatabasePanel() {
		// Initialize and set up the database panel
		JPanel database = new JPanel();
		database.setLayout(new BoxLayout(database, SwingConstants.HORIZONTAL));

		// Create the Database JTable
		table = makeDatabaseTable(allQueue.convertToArray());

		// Add the created table to the JScrollPane
		JScrollPane scrollPaneTable = new JScrollPane(table);
		scrollPaneTable.setPreferredSize(new Dimension(700, 300));

		// Create the JTextArea where the detailed information for each item
		// will be displayed (attractively)
		JTextArea display = new JTextArea(null, 0, 0);
		display.setEditable(false);
		display.setLineWrap(true);
		display.setMinimumSize(new Dimension(320, 300));

		// Removing an item
		JButton removeButton = new JButton("Remove");
		removeButton.requestFocus();
		// When the "Remove" button is pressed
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				// Needs to be a valid item
				if (selectedRow != -1) {
					if (JOptionPane.showConfirmDialog(null,
							"Are you sure you want to remove this item?",
							"Are you really sure?", yesNoOption) == JOptionPane.YES_OPTION) {
						clearDisplay(display);

						// Check if this item is in the search queue
						Item curr = allQueue.get(selectedRow);
						if (searchQueue != null) {
							int indexInSearch = searchQueue.indexOf(curr);
							if (indexInSearch != -1) {
								DefaultTableModel m = (DefaultTableModel) searchTable
										.getModel();
								m.removeRow(indexInSearch);
								m.fireTableDataChanged();
								searchQueue.remove(indexInSearch);
								searchQueueSelectedRow = -1;
							}
						}
						// Check if this item is in the order queue
						if (orderQueue != null) {
							int indexInSearch = orderQueue.indexOf(curr);
							if (indexInSearch != -1) {
								DefaultTableModel m = (DefaultTableModel) orderTable
										.getModel();
								m.removeRow(indexInSearch);
								m.fireTableDataChanged();
								orderQueue.remove(indexInSearch);
							}
						}

						// Remove the item from the database
						Item removed = allQueue
								.removeAndWriteToFile(selectedRow);
						backup();
						DefaultTableModel m = (DefaultTableModel) table
								.getModel();
						m.removeRow(selectedRow);
						m.fireTableDataChanged();

						// Reset selected row
						selectedRow = -1;

						// add to history
						historyMessage = removed.getName()
								+ " was removed from the database.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
					}
					return;
				} else
					// if no item is selected
					JOptionPane.showMessageDialog(null, "No item selected!",
							"Attention", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// When editing, call the edit method to display a pop-up window
		// containing all the editable fields
		JButton editButton = new JButton("Edit");
		editButton.requestFocus();
		// When the edit button is pressed
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedRow != -1) {
					// Get the item's reference from the allQueue
					Item curr = allQueue.get(selectedRow);

					// Display the edit pop-up window
					editItem(curr);

					// Update the display
					setDisplay(display, curr);
					updateDatabase(table, allQueue);

					// Check if this item is in the other queue as well
					if (searchQueue != null && searchQueue.indexOf(curr) != -1)
						updateDatabase(searchTable, searchQueue);

				} else
					// if no item is selected
					JOptionPane.showMessageDialog(null, "No item selected!",
							"Attention", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// Layout the buttons attractively
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.add(removeButton);
		buttons.add(editButton);
		database.add(scrollPaneTable);

		// Add the display with a custom layout
		JPanel right = new JPanel();
		GroupLayout layout = new GroupLayout(right);
		right.setLayout(layout);

		// Set the horizontal and vertical settings for the GroupLayout
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(display).addComponent(buttons)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(display).addComponent(buttons, 50, 50, 50));
		database.add(right);

		// Allows selections to be tracked and displayed
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// Don't allow data to be changed manually
				if (table.isEditing())
					table.getCellEditor().stopCellEditing();

				// Get the selected row
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					display.removeAll();
				} else {
					// Display the information beside the table
					selectedRow = lsm.getMinSelectionIndex();
					Item currentItem = allQueue.get(selectedRow);
					setDisplay(display, currentItem);
				}
			}
		});
		return database;
	}

	/**
	 * Creates the panel on which new items can be added
	 * 
	 * @return the add panel
	 */
	public JPanel createAddPanel() {
		// Initialize the add panel and set background colours
		JPanel addPanel = new JPanel();
		addPanel.setBackground(new Color(204, 255, 204));

		/**
		 * First column of Labels and Fields
		 */
		// Create JLabel and JTextField for the Name
		JLabel nameLabel = new JLabel("Enter name: ", SwingConstants.LEFT);
		nameLabel.setFont(new Font("Verdana", 1, 14));
		JTextField nameText = new JTextField(20);
		nameText.setHorizontalAlignment(SwingConstants.LEFT);

		// Create JLabel and JComboBox for the type of shopping or database
		// addition
		JLabel shoppingTypeLabel = new JLabel("Enter type: ",
				SwingConstants.LEFT);
		shoppingTypeLabel.setFont(new Font("Verdana", 1, 14));
		String[] shoppingChoices = { "Add item to database",
				"Add item to shopping list" };
		JComboBox<String> shoppingChoice = new JComboBox<String>(
				shoppingChoices);
		shoppingChoice.setSelectedIndex(-1);

		// Create JLabel and JComboBox for item type
		JLabel itemTypeLabel = new JLabel("Type of item: ", SwingConstants.LEFT);
		itemTypeLabel.setFont(new Font("Verdana", 1, 14));
		String[] itemNames = { "Consumable", "Equipment" };
		JComboBox<String> itemChoice = new JComboBox<String>(itemNames);
		itemChoice.setSelectedIndex(-1);

		// Create JLabel and JComboBox for the department
		JLabel departmentLabel = new JLabel("Department: ", SwingConstants.LEFT);
		departmentLabel.setFont(new Font("Verdana", 1, 14));
		String[] departmentNames = { "General", "Biology" };
		JComboBox<String> departmentChoice = new JComboBox<String>(
				departmentNames);
		departmentChoice.setSelectedIndex(0);

		// Create JLabel and JTextField for the Quantity
		JLabel quantityLabel = new JLabel("Select amount:  ",
				SwingConstants.LEFT);
		quantityLabel.setFont(new Font("Verdana", 1, 14));
		quantityLabel.setBounds(0, 0, 400, 500);
		quantityLabel.setAlignmentY(SwingConstants.LEFT);
		JTextField quantityText = new JTextField(5);
		quantityText.setText("0");
		quantityText.setHorizontalAlignment(SwingConstants.RIGHT);

		// Create JLabel and JTextField for the Remind Date
		JLabel remindLabel = new JLabel("Remind in number of days: ",
				SwingConstants.LEFT);
		remindLabel.setFont(new Font("Verdana", 1, 14));
		remindLabel.setAlignmentY(SwingConstants.LEFT);
		JTextField remindText = new JTextField(4);
		remindText.setText("30");
		remindText.setHorizontalAlignment(SwingConstants.LEFT);

		// Create JLabel and JTextField for the Notes
		JLabel notesLabel = new JLabel("Additional Notes: ",
				SwingConstants.LEFT);
		notesLabel.setFont(new Font("Verdana", 1, 14));
		notesLabel.setBounds(0, 0, 400, 500);
		notesLabel.setAlignmentY(SwingConstants.TOP);
		JTextField notesText = new JTextField(20);
		notesText.setText("Notes");

		/**
		 * Second column of Labels and Fields
		 */
		// Create JLabel and JComboBox for the The item Status
		JLabel statusLabel = new JLabel("Item status: ", SwingConstants.LEFT);
		statusLabel.setFont(new Font("Verdana", 1, 14));
		String[] statusNames = { "Ok", "Empty" };
		JComboBox<String> statusChoice = new JComboBox<String>(statusNames);
		statusChoice.setSelectedIndex(0);

		// Create JLabel and JTextField for the Company
		JLabel companyLabel = new JLabel("Company Name: ", SwingConstants.LEFT);
		companyLabel.setFont(new Font("Verdana", 1, 14));
		companyLabel.setAlignmentY(SwingConstants.LEFT);
		JTextField companyText = new JTextField(5);
		companyText.setText("Unknown");
		companyText.setHorizontalAlignment(SwingConstants.LEFT);

		// Create JLabel and JTextField for the Catalogue
		JLabel catalogLabel = new JLabel("Catalog Number: ",
				SwingConstants.LEFT);
		catalogLabel.setFont(new Font("Verdana", 1, 14));
		catalogLabel.setAlignmentY(SwingConstants.LEFT);
		JTextField catalogText = new JTextField(5);
		catalogText.setText("Unknown");
		catalogText.setHorizontalAlignment(SwingConstants.LEFT);

		// Create JLabel and JTextField for the MSDS
		JLabel msdsLabel = new JLabel("MSDS Number: ", SwingConstants.LEFT);
		msdsLabel.setFont(new Font("Verdana", 1, 14));
		msdsLabel.setBounds(0, 0, 400, 500);
		msdsLabel.setAlignmentY(SwingConstants.LEFT);
		JTextField msdsText = new JTextField(4);
		msdsText.setText("Unknown");
		msdsText.setHorizontalAlignment(SwingConstants.LEFT);

		// Create JComboBox for the Units
		JComboBox<String> unitChoices = new JComboBox<String>(UNITS);
		unitChoices.setSelectedIndex(0);

		// Create JLabel and JTextField for the Storage Area
		JLabel storageLabel = new JLabel("Storage Area: ", SwingConstants.LEFT);
		storageLabel.setFont(new Font("Verdana", 1, 14));
		storageLabel.setBounds(0, 0, 400, 500);
		storageLabel.setAlignmentY(SwingConstants.LEFT);
		JTextField storageText = new JTextField(4);
		storageText.setText("Unknown");
		storageText.setHorizontalAlignment(SwingConstants.LEFT);

		// Create JLabel and JTextField for the Room Number
		JLabel roomLabel = new JLabel("Room Number: ", SwingConstants.LEFT);
		roomLabel.setFont(new Font("Verdana", 1, 14));
		roomLabel.setBounds(0, 0, 400, 500);
		roomLabel.setAlignmentY(SwingConstants.LEFT);
		JTextField roomText = new JTextField(4);
		roomText.setText("1044");
		roomText.setHorizontalAlignment(SwingConstants.LEFT);

		// Create Submit button to add to database
		JButton submitButton = new JButton("Submit");
		submitButton.requestFocus();
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get first Column of information
				String name = nameText.getText();
				int typeOfShopping = shoppingChoice.getSelectedIndex();
				int itemType = itemChoice.getSelectedIndex();
				int department = departmentChoice.getSelectedIndex();
				String quantity = removeZeroes(quantityText.getText());
				int daysToRemind = Integer.parseInt(removeZeroes(remindText
						.getText()));
				String notes = notesText.getText();

				// Get second Column of information
				int status = statusChoice.getSelectedIndex();
				String company = companyText.getText();
				String catalogueNum = catalogText.getText();
				String msds = msdsText.getText();
				int unitsIndex = unitChoices.getSelectedIndex();
				String storage = storageText.getText();
				String roomNum = roomText.getText();

				// Get today's date
				Date date = new Date();

				// Combine all fields to detect invalid characters
				String allEntries = name + quantity + notes + company
						+ catalogueNum + msds + storage + roomNum;
				// Prevents commas from being entered
				if (containsComma(allEntries)) {
					JOptionPane.showMessageDialog(addPanel,
							"Please remove any commas in any of the fields.",
							"No Commas Please", JOptionPane.ERROR_MESSAGE);
				}
				// Check for invalid fields in the first column of data
				else if (name == null || name.equals("")) {
					nameText.setForeground(Color.red);
					nameText.setText("Invalid");
					JOptionPane.showMessageDialog(addPanel,
							"Please fix the name.", "Invalid Field",
							JOptionPane.ERROR_MESSAGE);
				} else if (typeOfShopping == -1) {
					JOptionPane.showMessageDialog(addPanel,
							"Please selected a list to add to.",
							"Invalid Field", JOptionPane.ERROR_MESSAGE);
				} else if (itemType == -1) {
					JOptionPane.showMessageDialog(addPanel,
							"Please choose a type of item.", "Invalid Field",
							JOptionPane.ERROR_MESSAGE);
				} else if (!isNumber(quantity)) {
					// Change the text color if invalid
					quantityText.setForeground(Color.red);
					quantityText.setText("Invalid");
					JOptionPane.showMessageDialog(addPanel,
							"Please fix quantity.", "Invalid Field",
							JOptionPane.ERROR_MESSAGE);
				} else if (daysToRemind <= 0) {
					JOptionPane.showMessageDialog(addPanel,
							"Please fix the days until next reminder.",
							"Invalid Field", JOptionPane.ERROR_MESSAGE);
				} else if (notes == null || notes.equals("")) {
					JOptionPane.showMessageDialog(addPanel,
							"Please fix the notes.", "Invalid Field",
							JOptionPane.ERROR_MESSAGE);
				}
				// Check for invalid fields in the second column of data
				else if (company == null || company.equals("")) {
					companyText.setForeground(Color.red);
					companyText.setText("Invalid");
					JOptionPane.showMessageDialog(addPanel,
							"Please fix the company.", "Invalid Field",
							JOptionPane.ERROR_MESSAGE);
				} else if (catalogueNum == null || catalogueNum.equals("")) {
					catalogText.setForeground(Color.red);
					catalogText.setText("Invalid");
					JOptionPane.showMessageDialog(addPanel,
							"Please fix the catalogue number.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else if (msds == null || msds.equals("")) {
					JOptionPane.showMessageDialog(addPanel,
							"Please fix the MSDS field", "Invalid Field",
							JOptionPane.ERROR_MESSAGE);
				} else if (storage == null || storage.equals("")) {
					JOptionPane.showMessageDialog(addPanel,
							"Please fix the storage area.", "Invalid Field",
							JOptionPane.ERROR_MESSAGE);
				} else if (!isNumber(roomNum)) {
					roomText.setForeground(Color.red);
					roomText.setText("Invalid");
					JOptionPane.showMessageDialog(addPanel,
							"Please fix the room number.", "Invalid Field",
							JOptionPane.ERROR_MESSAGE);
				} else
				// if nothing is wrong
				{
					// Display the confirmation dialog box
					int option = JOptionPane.showConfirmDialog(null,
							"Are you sure you want to add this item?",
							"Confirm", yesNoOption);
					if (option != JOptionPane.YES_OPTION)
						return;
					quantityText.setForeground(Color.BLACK);

					// Clearing all fields so they are ready for the next time
					nameText.setText("");
					shoppingChoice.setSelectedIndex(-1);
					itemChoice.setSelectedIndex(-1);
					departmentChoice.setSelectedIndex(0);
					quantityText.setText("0");
					remindText.setText("30");
					notesText.setText("Notes");
					statusChoice.setSelectedIndex(0);
					companyText.setText("Unknown");
					catalogText.setText("Unknown");
					msdsText.setText("Unknown");
					unitChoices.setSelectedIndex(0);
					storageText.setText("Unknown");
					roomText.setText("1044");

					// Add a new Consumable item
					if (itemType == CONSUMABLE) {

						allQueue.addNewItem(new Consumable(name,/* type */
						typeOfShopping, department, quantity, daysToRemind,
								notes, status, company, catalogueNum, msds,
								unitsIndex, storage, roomNum, date));
						updateDatabase(table, allQueue);

						// Update the history
						if (typeOfShopping == 0)
							historyMessage = quantity + UNITS[unitsIndex]
									+ " of " + name
									+ " was added to the Database.";
						else
							historyMessage = quantity + UNITS[unitsIndex]
									+ " of " + name + " was added to Orders.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
					}
					// Add a new Equipment item
					else if (itemType == EQUIPMENT) {
						allQueue.addNewItem(new Equipment(name,/* type */
						typeOfShopping, department, quantity, daysToRemind,
								notes, status, company, catalogueNum, msds,
								unitsIndex, storage, roomNum, date));
						updateDatabase(table, allQueue);

						// Update the history
						if (typeOfShopping == 0)
							historyMessage = quantity + UNITS[unitsIndex]
									+ " of " + name
									+ " was added to the Database.";
						else
							historyMessage = quantity + UNITS[unitsIndex]
									+ " of " + name + " was added to Orders.";

						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
					}

					// Update the ordered list
					orderQueue = allQueue.getOrders();
					updateDatabase(orderTable, orderQueue);
				}
			}

		});

		// Set the layout of the edit page
		GroupLayout layout = new GroupLayout(addPanel);
		addPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		// Add each component into four columns
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(nameLabel)
								.addComponent(shoppingTypeLabel)
								.addComponent(itemTypeLabel)
								.addComponent(departmentLabel)
								.addComponent(quantityLabel)
								.addComponent(remindLabel)
								.addComponent(notesLabel)
								.addComponent(submitButton))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(nameText)
								.addComponent(shoppingChoice)
								.addComponent(itemChoice)
								.addComponent(departmentChoice)
								.addComponent(quantityText)
								.addComponent(remindText)
								.addComponent(notesText))
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(statusLabel)
								.addComponent(companyLabel)
								.addComponent(catalogLabel)
								.addComponent(msdsLabel)
								.addComponent(unitChoices,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(storageLabel)
								.addComponent(roomLabel))

				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addComponent(statusChoice)
								.addComponent(companyText)
								.addComponent(catalogText)
								.addComponent(msdsText)
								.addComponent(storageText)
								.addComponent(roomText)));

		// Set the format of the vertical aspects of the GroupLayout
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)

								.addComponent(nameLabel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(nameText)

								.addComponent(statusLabel)
								.addComponent(statusChoice, 25, 25, 25))

				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, 20)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(shoppingTypeLabel)
								.addComponent(shoppingChoice)

								.addComponent(companyLabel)
								.addComponent(companyText))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, 20)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(itemTypeLabel)
								.addComponent(itemChoice)

								.addComponent(catalogLabel)
								.addComponent(catalogText))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, 20)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(departmentLabel)
								.addComponent(departmentChoice, 25, 25, 25)

								.addComponent(msdsLabel).addComponent(msdsText))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, 20)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(quantityLabel)
								.addComponent(quantityText,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(unitChoices,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, 20)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(remindLabel)
								.addComponent(remindText)
								.addComponent(storageLabel)
								.addComponent(storageText))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, 20)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addComponent(notesLabel)
								.addComponent(notesText)
								.addComponent(roomLabel).addComponent(roomText))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, 20)
				.addComponent(submitButton));

		return addPanel;
	}

	/**
	 * Creates the panel on which users can search for items
	 * 
	 * @return the search panel
	 */
	public JPanel createSearchPanel() {
		// Initialize the search panel and set the color and other information
		JPanel search = new JPanel();
		search.setBackground(new Color(204, 229, 255));
		search.setLayout(new BoxLayout(search, BoxLayout.PAGE_AXIS));

		// Panel to organize the top portion
		// Includes a label and search field
		JPanel top = new JPanel();
		top.setBackground(new Color(204, 229, 255));
		top.setMaximumSize(new Dimension(1000, 50));
		JLabel jlabel = new JLabel("Search for:  ");
		jlabel.setFont(new Font("Verdana", 1, 17));
		jlabel.setAlignmentX(CENTER_ALIGNMENT);
		jlabel.setBounds(0, 0, 400, 100);
		jlabel.setAlignmentY(SwingConstants.LEFT);
		JTextField text = new JTextField(30);
		text.setHorizontalAlignment(SwingConstants.LEFT);
		top.add(jlabel);
		top.add(text);

		search.add(top);

		// Where results will be displayed
		JPanel results = new JPanel();
		results.setBackground(new Color(204, 229, 255));
		results.setLayout(new BoxLayout(results, SwingConstants.HORIZONTAL));

		// Create the Database JTable initially empty so nothing shows up
		searchTable = new JTable();
		searchQueue = new Queue();

		// Add the created table to the JScrollPane
		JScrollPane scrollPaneTable = new JScrollPane(searchTable);
		scrollPaneTable.setPreferredSize(new Dimension(700, 300));
		scrollPaneTable.setBackground(new Color(229, 255, 204));

		// Create the JTextArea where the detailed information for each item
		// will be displayed
		JTextArea display = new JTextArea(null, 0, 0);
		display.setBorder(BorderFactory.createLineBorder(Color.black));
		display.setLineWrap(true);
		display.setMinimumSize(new Dimension(320, 300));
		display.setEditable(false);

		// Same as for the database remove except that any items removed are
		// guaranteed to be in the queue that holds all items
		JButton removeButton = new JButton("Remove");
		removeButton.requestFocus();
		// Listens for actions of the "Remove" button
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// If an item is selected
				if (searchQueueSelectedRow != -1) {
					// If the user proceeds to remove the item
					if (JOptionPane.showConfirmDialog(null,
							"Are you sure you want to remove this item?",
							"Are you really sure?", yesNoOption) == JOptionPane.YES_OPTION) {
						// Reset the display and remove the item from the
						// allQueue
						clearDisplay(display);
						int indexToRemove = allQueue.indexOf(searchQueue
								.get(searchQueueSelectedRow));

						// Update the spreadsheet file
						Item removed = allQueue
								.removeAndWriteToFile(indexToRemove);
						backup();

						// Update the table
						DefaultTableModel m = (DefaultTableModel) table
								.getModel();
						m.removeRow(indexToRemove);
						m.fireTableDataChanged();

						// Find index of the selected item
						indexToRemove = orderQueue.indexOf(searchQueue
								.get(searchQueueSelectedRow));
						if (indexToRemove != -1) {
							m = (DefaultTableModel) orderTable.getModel();
							m.removeRow(indexToRemove);
							m.fireTableDataChanged();
							orderQueue.remove(indexToRemove);
						}

						// Remove the item from the searchQueue
						searchQueue.remove(searchQueueSelectedRow);
						m = (DefaultTableModel) searchTable.getModel();
						m.removeRow(searchQueueSelectedRow);
						m.fireTableDataChanged();
						searchQueueSelectedRow = -1;

						// Update the history with a remove message
						historyMessage = removed.getName()
								+ " was removed from the database.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
					}
					return;
				} else
					// if no item is selected when the "Remove" button is
					// pressed
					JOptionPane.showMessageDialog(null, "No item selected!",
							"Attention", JOptionPane.INFORMATION_MESSAGE);
			}

		});

		// Same as previous edit
		JButton editButton = new JButton("Edit");
		editButton.requestFocus();
		// Listens to the "Edit" button
		editButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (searchQueueSelectedRow != -1) {
					// Get the item's reference from the searchQueue
					Item curr = searchQueue.get(searchQueueSelectedRow);

					// Displays the edit pop-up box
					editItem(curr);
					setDisplay(display, curr);
					updateDatabase(searchTable, searchQueue);
					// allQueue.sortList(0);
					updateDatabase(table, allQueue);
				} else
					JOptionPane.showMessageDialog(null, "No item selected!",
							"Attention", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// Set the remove button and the edit button in the search panel
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.add(removeButton);
		buttons.add(editButton);
		results.add(scrollPaneTable);

		// Set up the panel that displays item information on the right
		JPanel right = new JPanel();
		GroupLayout layout = new GroupLayout(right);
		right.setLayout(layout);

		// Set the horizonatal and vertical settings for the text display
		// information
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(display).addComponent(buttons)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(display).addComponent(buttons, 50, 50, 50));

		results.add(right);
		search.add(results);

		// If something is searched for, we make a queue of related stuff
		text.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Clear the existing search results
				searchQueueSelectedRow = -1;
				clearDisplay(display);
				String s = text.getText();
				searchTable = search(s);

				if (searchTable.getRowCount() != 0) {
					// Allows selections to be tracked and displayed
					ListSelectionModel rowSM = searchTable.getSelectionModel();
					rowSM.addListSelectionListener(new ListSelectionListener() {
						public void valueChanged(ListSelectionEvent e) {
							// Don't allow data to be changed manually
							ListSelectionModel lsm = (ListSelectionModel) e
									.getSource();
							if (lsm.isSelectionEmpty()) {
								display.removeAll();
							} else {
								// Display the information beside the table
								searchQueueSelectedRow = lsm
										.getMinSelectionIndex();
								Item curr = searchQueue
										.get(searchQueueSelectedRow);
								setDisplay(display, curr);
							}
						}
					});

					// It only works properly when i remove and re-add
					// everything in the results panel
					JScrollPane temp = new JScrollPane(searchTable);
					temp.setPreferredSize(new Dimension(700, 300));
					results.removeAll();
					results.add(temp);
					results.add(right);
					results.revalidate();
					search.revalidate();
				} else {
					// Clear anything so nothing out of context can be displayed
					clearDisplay(display);
					searchTable = new JTable();
					JScrollPane temp = new JScrollPane(searchTable);
					temp.setPreferredSize(new Dimension(700, 300));
					results.removeAll();
					results.add(temp);
					results.add(right);
					search.revalidate();
					JOptionPane.showMessageDialog(search, "No results.");
				}
				// Since we switch up the scrollpane we might as well delete the
				// old one
				System.gc();
			}
		});

		return search;
	}

	/**
	 * Customize the tabs and add hover information
	 * 
	 * @param tabbedPane
	 *            the given JTabbedPane object
	 */
	public void customise(JTabbedPane tabbedPane) {
		// Set the default information for the tabbed pane
		tabbedPane.setBackground(Color.cyan);
		tabbedPane.setSelectedIndex(0);
		tabbedPane.setFont(new Font("Sans-Serif", Font.BOLD, 20));

		// Set a minimum height
		JLabel bulletin = new JLabel("Bulletin");
		bulletin.setFont(new Font("Sans-Serif", Font.BOLD, 20));
		bulletin.setPreferredSize(new Dimension(77, 35));

		tabbedPane.setTabComponentAt(0, bulletin);

		// Add tips in html format
		String bulletinTip = new String(
				"<html>Main menu.<br>&nbsp;&nbsp;</html>");
		String orderTip = new String(
				"<html>Click here to view requested or current orders.<br>&nbsp;&nbsp;</html>");
		String databaseTip = new String(
				"<html>The entire database can be viewed here.<br>&nbsp;&nbsp;</html>");
		String addTip = new String(
				"<html>Add to the database here.<br>&nbsp;&nbsp;</html>");
		String searchTip = new String(
				"<html>Search the database here.<br>&nbsp;&nbsp;</html>");
		String historyTip = new String(
				"<html>View recent history here.<br>&nbsp;&nbsp;</html>");
		String helpTip = new String(
				"<html>View help tutorials here.<br>&nbsp;&nbsp;</html>");

		// Adds hover information for each of the tabs
		tabbedPane.setToolTipTextAt(0, bulletinTip);
		tabbedPane.setToolTipTextAt(1, orderTip);
		tabbedPane.setToolTipTextAt(2, databaseTip);
		tabbedPane.setToolTipTextAt(3, addTip);
		tabbedPane.setToolTipTextAt(4, searchTip);
		tabbedPane.setToolTipTextAt(5, historyTip);
		tabbedPane.setToolTipTextAt(6, helpTip);
	}

	/**
	 * Searches the allQueue for items containing the given string s
	 * @param s the given String search query
	 * @return the JTable containing the search results
	 */
	public JTable search(String s) {
		// Nothing should be displayed, only point towards help tab
		if (s.toLowerCase().equals("help")) {
			help();
			return new JTable();
		}

		// Search for any results and put them into a queue
		searchQueue = allQueue.search(s.toLowerCase());

		JTable table = makeDatabaseTable(searchQueue.convertToArray());
		return table;

	}

	/**
	 * Reads data from the spreadsheet file and store it in the allQueue
	 */
	public void readData() {
		//Initialize the BufferedReader and the Queue
		BufferedReader br;
		allQueue = new Queue();
		try {
			br = new BufferedReader(new FileReader("Resources/Spreadsheet.csv"));
			StringTokenizer st;
			// Read in the first 8 lines of header
			final int HEADER_SIZE = 8;
			String[] header = new String[HEADER_SIZE];
			for (int headerRow = 0; headerRow < HEADER_SIZE; headerRow++) {
				if(headerRow == 3)
				{
					st = new StringTokenizer(br.readLine(), ",");
					st.nextToken();
					currentDate = DATE_FORMAT.parse(st.nextToken());
					header[headerRow] = "DATE COMPLETED:," + DATE_FORMAT.format(currentDate);
				}
				else
					header[headerRow] = br.readLine();
			}
			//Keeps track of the header
			allQueue.setHeader(header);
			//Read in all the data
			while (br.ready()) {
				//Separate values by commas
				st = new StringTokenizer(br.readLine(), ",");

				// First Column of information is read in
				String name = st.nextToken();
				int type = st.nextToken().equals("Consumable") ? 0 : 1;
				int shoppingStatus;
				{
					String readShopping = st.nextToken();
					if (readShopping.equals("No"))
						shoppingStatus = 0;
					else if (readShopping.equals("Needed"))
						shoppingStatus = 1;
					else
						shoppingStatus = 2;
				}
				int department = st.nextToken().equals("General") ? 0 : 1;
				String quantity = st.nextToken();
				int daysToReminder = Integer.parseInt(st.nextToken());
				String notes = st.nextToken();

				// Second Column of information is read in
				int status = st.nextToken().equals("Ok") ? 0 : 1;
				String company = st.nextToken();
				String catalog = st.nextToken();
				String msds = st.nextToken();
				int units = Integer.parseInt(st.nextToken());
				String storage = st.nextToken();
				String roomNum = st.nextToken();

				Date dateAcquired = DATE_FORMAT.parse(st.nextToken());

				//Add a new Consumable Item to the allQueue
				if (type == CONSUMABLE) {
					allQueue.add(new Consumable(name, shoppingStatus,
							department, quantity, daysToReminder, notes,
							status, company, catalog, msds, units, storage,
							roomNum, dateAcquired));
				} 
				//Add a new Equipment Item to the allQueue
				else if (type == EQUIPMENT) {
					allQueue.add(new Equipment(name, shoppingStatus,
							department, quantity, daysToReminder, notes,
							status, company, catalog, msds, units, storage,
							roomNum, dateAcquired));
				}
			}
			br.close();
		} 
		//If there is no spreadsheet
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Warning",
					"There must be a text file named \"Spreadsheet.csv\" in the Resources folder",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		//If the BufferedReader is unable to read the file
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Warning",
					"There is a reading error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		//If the date is unreadable
		catch (ParseException e) {
			JOptionPane.showMessageDialog(null, "Warning",
					"There is a date problemn", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		//Sort the list alphabetically
		allQueue.sortList(0);
	}

	/**
	 * Check to see if the given string is a positive number
	 * @param check the given String to be checked
	 * @return the status of the String
	 */
	public static boolean isNumber(String check) {
		//If the String is empty, it is not a number
		if (check.length() == 0 || check == null)
			return false;
		//Check each character to see if it is a number
		for (int i = 0; i < check.length(); i++) {
			if (!Character.isDigit(check.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * Check to see if the given string contains any commas
	 * @param check the given String to be checked
	 * @return the status of the String
	 */
	private boolean containsComma(String check) {
		//Check each character to see if there is a comma
		for (int character = 0; character < check.length(); character++)
			if (check.charAt(character) == ',')
				return true;
		return false;
	}

	/**
	 * Create the database table containing the name, quantity, and date acquired of all given items
	 * The table can be sorted by name or date by clicking on the table headers
	 * @param data the 2D Object array of data
	 * @return the JTable of item information
	 */
	@SuppressWarnings("serial")
	public static JTable makeDatabaseTable(Object[][] data) {
		String[] columnNames = { "Name", "Quantity", "Date Acquired" };

		// Ensure the new table cannot be edited directly
		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
			public boolean isCellEditable(int row, int column) {
				return false;
			};
		};
		
		//Initialize and set up the JTable
		JTable newTable = new JTable(tableModel);
		newTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//Set the color of requested items in the order queue
		if (orderQueue == null || orderTable == null) {
			newTable = new JTable(tableModel) {
				public Component prepareRenderer(TableCellRenderer renderer,
						int row, int column) {
					Component c = super.prepareRenderer(renderer, row, column);
					//Set Needed items to Red
					if (orderQueue.get(row).getShoppingStatus() == 1)
						c.setBackground(new Color(255, 100, 100));
					//Set Ordered items to Yellow
					else
						c.setBackground(new Color(255, 255, 100));
					return c;
				}
			};
		}
		//Set scroll information for the table
		newTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		newTable.setBackground(new Color(249, 245, 230));
		JTableHeader header = newTable.getTableHeader();
		//Sort the table alphabetically or by date depending on which header is clicked
		header.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int col = header.columnAtPoint(e.getPoint());
				if (col == 0) {
					if (searchQueue != null) {
						searchQueue.sortList(0);
						updateDatabase(searchTable, searchQueue);
					}
					if (orderQueue != null) {
						orderQueue.sortList(0);
						updateDatabase(orderTable, orderQueue);
					}
					allQueue.sortList(0);
					updateDatabase(table, allQueue);
				} else if (col == 2) {
					if (searchQueue != null) {
						searchQueue.sortList(0);
						updateDatabase(searchTable, searchQueue);
					}
					if (orderQueue != null) {
						orderQueue.sortList(0);
						updateDatabase(orderTable, orderQueue);
					}
					allQueue.sortList(1);
					updateDatabase(table, allQueue);
				}
				return;
			}
		});

		//Set the information of the table header
		newTable.getTableHeader().setBackground(new Color(204, 255, 255));
		newTable.getTableHeader()
				.setFont(new Font("Sans-Serif", Font.BOLD, 18));
		newTable.setFont(new Font(newTable.getFont().getFontName(), Font.PLAIN,
				17));
		newTable.setRowHeight(22);

		// Change the units to be displayed in the center of the column
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		newTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

		// Set a minimum width for each column
		for (int i = 0; i < tableModel.getColumnCount(); i++)
			newTable.getColumnModel().getColumn(i).setMinWidth(100);
		newTable.getColumnModel().getColumn(1).setMaxWidth(300);
		newTable.getColumnModel().getColumn(1).setResizable(false);
		// newTable.getColumnModel().getColumn(0).setMinWidth(200);
		newTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return newTable;
	}

	/**
	 * Displays a dialogue for the user to seek help
	 */
	public void help() {
		JOptionPane.showMessageDialog(null,
				"Click on the \"Help\" tab at the top", "Help",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Reads the two text files containing strings of messages (Bulletin and
	 * History) and stores the messages in their respective arrays
	 * 
	 * @throws IOException
	 *             if the file is not found
	 */
	public static void readArrays() throws IOException {
		BufferedReader br;
		try {
			// Read the bulletin messages
			br = new BufferedReader(new FileReader("Resources/Bulletin"));
			int index = 0;
			while (br.ready() && index < messages.length) {
				messages[index] = br.readLine();
				index++;
			}
			br.close();

			// Read the history messages
			br = new BufferedReader(new FileReader("Resources/History"));
			index = 0;
			while (br.ready() && index < history.length) {
				history[index] = br.readLine();
				index++;
			}
			br.close();
		}
		//If there is no spreadsheet file
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Warning",
					"There must be text files named \"Bulletin\" and \"History\" in the resources folder",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		//If the BufferedReader is reading wrong
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Warning",
					"There is a reading error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Updates an array by adding a new message at the start
	 * 
	 * @param array
	 *            the initial array
	 * @param newMssg
	 *            the message to add
	 * @return a modified array that includes the message
	 */
	public static String[] updateArray(String[] array, String newMssg) {
		for (int i = array.length - 1; i >= 1; i--) {
			array[i] = array[i - 1];
		}
		array[0] = newMssg;
		return array;
	}

	/**
	 * Writes an array of strings to a given text file
	 * 
	 * @param array
	 *            the array of strings
	 * @param fileName
	 *            the given name of the text file
	 */
	public static void writeToTextFile(String[] array, String fileName) {
		PrintWriter output;
		//Prints out the file
		try {
			output = new PrintWriter(new File("Resources/" + fileName));
			for (int i = 0; i < array.length; i++) {
				output.println(array[i]);
			}
			// Close the PrintWriter
			output.close();
		}
		//If there are problems writing to the file
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Error",
					"Error writing to the file.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Fills a table with information from a specific queue
	 * 
	 * @param t
	 *            the table that will contain the information
	 * @param q
	 *            the queue from which the data comes
	 */
	public static void updateDatabase(JTable t, Queue q) {
		DefaultTableModel tableModel = (DefaultTableModel) t.getModel();
		// Clear the table and then re-add all the old plus any new information
		tableModel.setRowCount(0);
		for (int i = 0; i < q.getSize(); i++) {
			Item curr = q.get(i);
			String s = curr.getQuantity() + " " + UNITS[curr.getUnits()];
			Object[] stuff = { curr.getName(), s,
					DATE_FORMAT.format(curr.getDateAcquired()) };
			tableModel.addRow(stuff);
		}
		tableModel.fireTableDataChanged();
	}

	/**
	 * Performs changes on an item if the user decides to make any
	 * 
	 * @param i
	 *            the item that will be edited
	 */
	public static void editItem(Item i) {
		// Refer to the add tab
		// This is simply a replica of that with the exception that all fields
		// initially contain the current data of the item
		JPanel popup = new JPanel(new GridLayout(16, 2));
		JTextField nameText = new JTextField(25);

		//Set up the name field
		nameText.setText(i.getName());
		popup.add(new JLabel("Edit Name: "));
		popup.add(nameText);

		//Set up the status field
		String[] statusNames = { "Ok", "Empty" };
		JComboBox<String> statusChoice = new JComboBox<String>(statusNames);
		statusChoice.setSelectedIndex(i.getStatus());
		popup.add(new JLabel("Edit status: "));
		popup.add(statusChoice);

		//Set up the field containing the item type
		String[] itemNames = { "Consumable", "Equipment" };
		JComboBox<String> itemChoice = new JComboBox<String>(itemNames);
		itemChoice.setSelectedIndex(i instanceof Consumable ? 0 : 1);
		popup.add(new JLabel("Edit item type: "));
		popup.add(itemChoice);

		//Set up the department field
		String[] departmentNames = { "General", "Biology" };
		JComboBox<String> departmentChoice = new JComboBox<String>(
				departmentNames);
		departmentChoice.setSelectedIndex(i.getDepartment());
		popup.add(new JLabel("Edit Department: "));
		popup.add(departmentChoice);

		//Set up the quantity field
		JTextField quantityText = new JTextField(20);
		quantityText.setText(i.getQuantity());
		popup.add(new JLabel("Edit Quantity: "));
		popup.add(quantityText);

		//Set up the units field
		String[] unitNames = { "g", "mL", "pc" };
		JComboBox<String> unitChoice = new JComboBox<String>(unitNames);
		unitChoice.setSelectedIndex(i.getUnits());
		popup.add(new JLabel("Edit quantity units: "));
		popup.add(unitChoice);

		//Set up the company field
		JTextField companyText = new JTextField(25);
		companyText.setText(i.getCompany());
		popup.add(new JLabel("Edit Company: "));
		popup.add(companyText);

		//Set up the catalog field
		JTextField catalogueText = new JTextField(25);
		catalogueText.setText(i.getCatalog());
		popup.add(new JLabel("Edit Catalog: "));
		popup.add(catalogueText);

		//Set up the msds field
		JTextField msdsText = new JTextField(25);
		msdsText.setText(i.getMSDS());
		popup.add(new JLabel("Edit MSDS: "));
		popup.add(msdsText);

		//Set up the storage field
		JTextField storageText = new JTextField(25);
		storageText.setText(i.getStorage());
		popup.add(new JLabel("Edit Storage: "));
		popup.add(storageText);

		///Set up the room number field
		JTextField roomText = new JTextField(25);
		roomText.setText(i.getRoomNumber());
		popup.add(new JLabel("Edit Room #: "));
		popup.add(roomText);

		//Set up the notes field
		JTextField notesText = new JTextField(25);
		notesText.setText(i.getNotes());
		popup.add(new JLabel("Edit Notes: "));
		popup.add(notesText);

		//Set up the remind field
		JTextField remindText = new JTextField(25);
		remindText.setText("Do not postpone");
		popup.add(new JLabel("Current reminder date: "));
		popup.add(new JLabel(DATE_FORMAT.format(new Date(i.getDateAcquired()
				.getTime()
				+ ((long) i.getDaysToReminder() * 24 * 60 * 60 * 1000)))));
		popup.add(new JLabel("Postpone reminder date from today: "));
		popup.add(remindText);

		//Update the status of the shoppingStatus button
		currentStatus = i.getShoppingStatus();
		changedStatus = false;
		changedDetails = false;

		// This was a confusing problem with whether the item was requested to
		// be ordered, actually ordered or fine
		String s = "";
		if (currentStatus == 0)
			s = "Order";
		if (currentStatus == 1)
			s = "Set Ordered";
		if (currentStatus == 2)
			s = "Set Received";
		
		//Create the shoppingStatus button
		JButton statusButton = new JButton(s);
		popup.add(statusButton);
		statusButton.requestFocus();
		statusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (currentStatus == 0) {// 0-Fine 1-needed 2-ordered
					JOptionPane.showMessageDialog(null, "Set to needed",
							"Changed Order Status",
							JOptionPane.INFORMATION_MESSAGE);
					statusButton.setText("Set Ordered");
					currentStatus = 1;
					popup.revalidate();
					// If currently requested
				} else if (currentStatus == 1) {
					statusButton.setText("Set Received");
					JOptionPane.showMessageDialog(null, "Set to ordered",
							"Changed Order Status",
							JOptionPane.INFORMATION_MESSAGE);
					currentStatus = 2;
					popup.revalidate();
					// If currently ordered
				} else if (currentStatus == 2) {
					JOptionPane.showMessageDialog(null, "Set to received",
							"Changed Order Status",
							JOptionPane.INFORMATION_MESSAGE);
					statusButton.setText("Order new");
					currentStatus = 0;
					popup.revalidate();
				}
				changedStatus = true;
			}
		});

		// Confirms submit is legit
		if (JOptionPane.showConfirmDialog(null, popup,
				"Please edit any fields", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			// perform a check on valid fields and then change them in all
			// queues
			String name = nameText.getText();
			String quantity = removeZeroes(quantityText.getText());
			int units = unitChoice.getSelectedIndex();
			String company = companyText.getText();
			int itemType = itemChoice.getSelectedIndex();
			int status = statusChoice.getSelectedIndex();
			int department = departmentChoice.getSelectedIndex();
			String catalogueNum = catalogueText.getText();
			String msds = msdsText.getText();
			String roomNum = roomText.getText();
			String storage = storageText.getText();
			String notes = notesText.getText();
			String remind = remindText.getText().toLowerCase();

			// Check if any fields are changed
			if (i.getName().equals(name) && i.getQuantity().equals(quantity)
					&& i.getUnits() == units && i.getCompany().equals(company)
					&& i.getStatus() == status
					&& i.getDepartment() == department
					&& i.getCatalog().equals(catalogueNum)
					&& i.getMSDS().equals(msds)
					&& i.getRoomNumber().equals(roomNum)
					&& i.getStorage().equals(storage)
					&& i.getNotes().equals(notes)
					&& remind.equals("do not postpone")) {
				changedDetails = false;
			} else
				changedDetails = true;

			// If reminder is invalid, display a popup box
			if ((!isNumber(remind) && !remind.equals("do not postpone")))
				JOptionPane
						.showMessageDialog(
								null,
								"The postpone reminder box should either contain a non-negative number or the text \"Do not postpone\".",
								"Edit Cancelled", JOptionPane.ERROR_MESSAGE);
			// Record information if the fields are accurate
			else if (name != null && isNumber(quantity) && company != null
					&& catalogueNum != null && msds != null && roomNum != null
					&& storage != null && notes != null) {
				// Postpone the reminder date and update daysToReminder to store
				// the number of days after the date acquired
				int daysToReminder = i.getDaysToReminder();
				if (!remind.equals("do not postpone")) {
					daysToReminder = (int) (new Date().getTime() / 1000.0 / 60
							/ 60 / 24 + Integer.parseInt(remind) - i
							.getDateAcquired().getTime()
							/ 1000.0
							/ 60
							/ 60
							/ 24);
					i.setDaysToReminder(daysToReminder);
					updateReminders();
				}

				//Change the item type from Consumable to Equipment
				if (i instanceof Consumable && itemType == EQUIPMENT) {
					boolean existsOrder = false;
					if (orderQueue.indexOf(i) != -1) {
						existsOrder = true;
						orderQueue.remove(orderQueue.indexOf(i));
					}
					boolean existsSearch = false;
					if (searchQueue.indexOf(i) != -1) {
						existsSearch = true;
						searchQueue.remove(searchQueue.indexOf(i));
					}

					allQueue.remove(allQueue.indexOf(i));

					i = new Equipment(name, currentStatus, department,
							quantity, daysToReminder, notes, status, company,
							catalogueNum, msds, units, storage, roomNum,
							i.getDateAcquired());
					allQueue.add(i);
					allQueue.sortList(0);
					updateDatabase(table, allQueue);

					if (existsOrder) {
						orderQueue.add(i);
						orderQueue.sortList(0);
						updateDatabase(orderTable, orderQueue);
					}
					if (existsSearch) {
						searchQueue.add(i);
						searchQueue.sortList(0);
						updateDatabase(searchTable, searchQueue);
					}

				}
				//Change the item type from Equipment to Consumable
				else if (i instanceof Equipment && itemType == CONSUMABLE) {

					boolean existsOrder = false;
					if (orderQueue.indexOf(i) != -1) {
						existsOrder = true;
						orderQueue.remove(orderQueue.indexOf(i));
					}
					boolean existsSearch = false;
					if (searchQueue.indexOf(i) != -1) {
						existsSearch = true;
						searchQueue.remove(searchQueue.indexOf(i));
					}

					allQueue.remove(allQueue.indexOf(i));
					i = new Consumable(name, currentStatus, department,
							quantity, daysToReminder, notes, status, company,
							catalogueNum, msds, units, storage, roomNum,
							i.getDateAcquired());
					allQueue.add(i);
					allQueue.sortList(0);
					updateDatabase(table, allQueue);

					if (existsOrder) {
						orderQueue.add(i);
						orderQueue.sortList(0);
						updateDatabase(orderTable, orderQueue);
					}
					if (existsSearch) {
						searchQueue.add(i);
						searchQueue.sortList(0);
						updateDatabase(searchTable, searchQueue);
					}

				} else {
					//Change the information if the item type remains the same
					i.setName(name);
					i.setQuantity(quantity);
					i.setUnits(units);
					i.setCompany(company);
					i.setStatus(status);
					i.setDepartment(department);
					i.setCatalog(catalogueNum);
					i.setMSDS(msds);
					i.setRoomNumber(roomNum);
					i.setStorage(storage);
					i.setNotes(notes);

					//Update the history
					if (changedDetails) {
						historyMessage = "Details of " + i.getName()
								+ " were edited.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
					}
				}

				// If it is needed or ordered, check if it is already in the
				// ordered queue and if not put it there

				if (currentStatus == 0) {// 0 No 1 needed 2 ordered
					if (i.getShoppingStatus() != 0)
						orderQueue.remove(orderQueue.indexOf(i));

					allQueue.get(allQueue.indexOf(i)).setShoppingStatus(0);

					if (changedStatus) {
						historyMessage = i.getName() + " was set to Received.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
						popup.revalidate();
					}

				} else if (currentStatus == 1) { // If currently
													// needed,
					// If it was not on the list
					if (i.getShoppingStatus() == 0)
						orderQueue.add(i);

					allQueue.get(allQueue.indexOf(i)).setShoppingStatus(1);

					if (changedStatus) {
						historyMessage = i.getName() + " was set to Needed.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
						popup.revalidate();
					}

				} else if (currentStatus == 2) { // If currently
													// ordered,
					if (i.getShoppingStatus() == 0)
						orderQueue.add(i);

					allQueue.get(allQueue.indexOf(i)).setShoppingStatus(2);

					if (changedStatus) {
						historyMessage = i.getName() + " was set to Ordered.";
						updateArray(history, historyMessage);
						writeToTextFile(history, "History");
						updateHistory();
						popup.revalidate();
					}
				}
				updateDatabase(orderTable, orderQueue);

				allQueue.writeToFile("Resources/Spreadsheet.csv");
				backup();
				
			}

			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			tableModel.fireTableDataChanged();
			tableModel = (DefaultTableModel) searchTable.getModel();
			tableModel.fireTableDataChanged();
		}
	}
	
	
	/**
	 * Create a new backup if there is no backup for the current day
	 */
	private static void backup()
	{
		
		if(new Date().getTime()/1000/60/60/24 > currentDate.getTime()/1000/60/60/24)
		{
			currentDate = new Date();
			String[] header = allQueue.getHeader();
			header[3] = "DATE COMPLETED:," + DATE_FORMAT.format(currentDate);
			allQueue.setHeader(header);
			allQueue.writeToFile("Resources/Backups/" + BACKUP_DATE_FORMAT.format(currentDate) + ".csv");
		}
		
	}

	/**
	 * Displays all the information of the selected item in a JTextArea
	 * 
	 * @param area
	 *            the JTextArea where the information will be displayed
	 * @param i
	 *            the selected item
	 */
	private void setDisplay(JTextArea area, Item i) {
		Font defaultFont = area.getFont();
		Font boldFont = new Font(defaultFont.getFontName(), Font.BOLD, 19);
		area.setFont(boldFont);
		
		area.setText("Name: " + i.getName());
		area.append("\nType of item: "
				+ (i instanceof Consumable ? "Consumable" : "Equipment"));
		int shoppingStatus = i.getShoppingStatus();
		String displayShoppingStatus;
		if (shoppingStatus == 0)
			displayShoppingStatus = "Yes";
		else if (shoppingStatus == 1)
			displayShoppingStatus = "Needed";
		else
			displayShoppingStatus = "Ordered";
		area.append("\nAvailability: " + displayShoppingStatus);
		area.append("\nDepartment: "
				+ (i.getDepartment() == 0 ? "General" : "Biology"));
		area.append("\nStatus: " + (i.getStatus() == 0 ? "Ok" : "Empty"));
		area.append("\nQuantity: " + i.getQuantity() + " "
				+ UNITS[i.getUnits()]);
		area.append("\nDate Acquired: "
				+ DATE_FORMAT.format(i.getDateAcquired()));
		area.append("\nRemind date: "
				+ DATE_FORMAT.format(new Date(i.getDateAcquired()
						.getTime()
						+ ((long) i.getDaysToReminder() * 24 * 60 * 60 * 1000))));
		area.append("\nCompany: " + i.getCompany());
		area.append("\nCatalogue ID: " + i.getCatalog());
		area.append("\nMSDS: " + i.getMSDS());
		area.append("\nRoom Number: " + i.getRoomNumber());
		area.append("\nStorage: " + i.getStorage());
		area.append("\nNotes: " + i.getNotes());
		area.getParent().revalidate();
	}

	/**
	 * Clears the JTextArea display
	 * @param area the JTextArea containing the item information
	 */
	private static void clearDisplay(JTextArea area) {
		area.setText("");
		area.getParent().revalidate();
	}

	/**
	 * Removes all zeros from a given string
	 * @param check String the given String
	 * @return the formatted String
	 */
	private static String removeZeroes(String check) {
		while (check.length() > 1 && check.charAt(0) == '0')
			check = check.substring(1);
		return check;
	}

	/**
	 * Updates the array of reminders and appends it to its text area
	 */
	private static void updateReminders() {
		reminderMssgs.setText("");
		reminders = allQueue.remindToOrder(new Date());
		if (reminders.length == 0)
			reminderMssgs.append("No Reminders");
		else {
			for (int reminderMssg = 0; reminderMssg < reminders.length; reminderMssg++) {
				reminderMssgs.append(String.format(
						"It has been %d days since %s was acquired.%n",
						(new Date().getTime() / 1000 / 60 / 60 / 24)
								- ((Date) reminders[reminderMssg][3]).getTime()
								/ 1000 / 60 / 60 / 24,
						(String) reminders[reminderMssg][0]));

			}
		}
		reminderMssgs.setCaretPosition(0);
		reminderMssgs.getParent().revalidate();
		reminderMssgs.getParent().getParent().revalidate();
	}

	/**
	 * Updates the History tab by updating its textarea
	 */
	private static void updateHistory() {
		historyMssgs.setText("");
		try {
			readArrays();

			for (int i = 0; i < history.length; i++) {
				if (history[i] != null && !history[i].equals("null"))
					historyMssgs.append(history[i] + "\n");
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Warning",
					"There is a reading error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		historyMssgs.setCaretPosition(0);
		historyMssgs.getParent().revalidate();
		historyMssgs.getParent().getParent().revalidate();
	}
}