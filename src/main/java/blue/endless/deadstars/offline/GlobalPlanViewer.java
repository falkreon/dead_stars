package blue.endless.deadstars.offline;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class GlobalPlanViewer extends JFrame {
	public GlobalPlanViewer() {
		this.setMinimumSize(new Dimension(300, 300));
		this.rootPane.setLayout(new BorderLayout());
		this.rootPane.add(new GlobalPlanPanel(), BorderLayout.CENTER);
	}
	
	public static void main(String... args) {
		GlobalPlanViewer viewer = new GlobalPlanViewer();
		viewer.setVisible(true);
	}
}
