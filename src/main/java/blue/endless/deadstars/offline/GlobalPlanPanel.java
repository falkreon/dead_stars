package blue.endless.deadstars.offline;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import blue.endless.deadstars.world.GlobalPlan;
import blue.endless.deadstars.world.GlobalPlan.Circle;
import blue.endless.deadstars.world.GlobalPlan.Mare;
import blue.endless.deadstars.world.GlobalPlan.Ray;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;

public class GlobalPlanPanel extends JPanel {
	
	private GlobalPlan plan = new GlobalPlan(new GlobalPlan.Info(), Random.create().nextLong());
	
	public GlobalPlanPanel() {}
	
	public void setPlan(GlobalPlan plan) {
		this.plan = plan;
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int maxExtent = Math.min(this.getWidth(), this.getHeight());
		float scale = maxExtent / (float) plan.info.size;
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, maxExtent, maxExtent);
		
		g.setColor(Color.BLUE);
		for(Mare m : plan.mara) {
			for(Circle c : m) {
				int x1 = (int) ((c.center().x - c.radius()) * scale);
				int y1 = (int) ((c.center().y - c.radius()) * scale);
				int w = (int) (c.radius() * 2 * scale);
				
				g.fillOval(x1, y1, w, w);
			}
		}
		
		g.setColor(Color.CYAN);
		for(Ray ray : plan.rays) {
			Vec2f nearPoint = ray.nearPoint();
			Vec2f farPoint = ray.farPoint();
			int x1 = (int) (nearPoint.x * scale);
			int y1 = (int) (nearPoint.y * scale);
			int x2 = (int) (farPoint.x * scale);
			int y2 = (int) (farPoint.y * scale);
			g.drawLine(x1, y1, x2, y2);
		}
		
		g.setColor(Color.LIGHT_GRAY);
		for(Circle city : plan.cities) {
			int x1 = (int) ((city.center().x - city.radius()) * scale);
			int y1 = (int) ((city.center().y - city.radius()) * scale);
			int w = (int) (city.radius() * 2 * scale);
			
			g.fillOval(x1, y1, w, w);
		}
		
		
	}
}
