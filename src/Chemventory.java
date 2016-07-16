/**
 * The main method that starts everything off
 * @author Will Wei, Shaunak Rajadhyaksha, Tilman Lindig
 * @version April 20 , 2016
 */
//Import needed packages
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class Chemventory{
	private static Dimension MIN_SIZE = new Dimension(615, 400);

	public Chemventory() {
		// Create a new frame
		JFrame frame = new JFrame("Chemventory");
		frame.setIconImage(Toolkit.getDefaultToolkit()
				.getImage("Resources/ChemFlask.png"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the content pane
		ChemventoryFrame contentPane = new ChemventoryFrame();
		contentPane.setOpaque(true);
		frame.setContentPane(contentPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(MIN_SIZE);

		// frame should adapt to the panel size
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Start 'er up
		new Chemventory();
	}
}