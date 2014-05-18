package gui;

import gui.Projection.TypeDeProjection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map.Entry;

import javax.swing.JPanel;

import pathfinder.ItineraireRobot;
import simulation.Manager;
import simulation.Robot;
import simulation.RobotChenilles;
import simulation.RobotDrone;
import simulation.RobotPattes;
import simulation.RobotRoues;
import carte.Arc;
import carte.Arc.TypeArc;
import carte.Carte;
import carte.Noeud;

/**
 * Panel pour representer une simulation. Il affiche une CarteGraph2D, les
 * robots, incendies et sources.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class JCartePanel extends JPanel {

	private static final long serialVersionUID = -6741233450599648552L;
	private CarteGraph2D carteGraph2D;
	private Carte carte;
	private Manager manager;
	private boolean initialized;

	/**
	 * Constructeur.
	 */
	public JCartePanel() {
		super();
		this.initialized = false;
	}

	/**
	 * Initialise le panel. La methode paint n'affiche rien avant initialisation.
	 * 
	 * @param carte
	 * @param manager
	 */
	public void init(Carte carte, Manager manager) {
		this.carte = carte;
		this.manager = manager;
		this.carteGraph2D = Projection.projecterCarte(carte,
				TypeDeProjection.ORTHOGONAL, 550);
		//this.carteGraph2D = Projection.projecterCarte(carte,
		//TypeDeProjection.ORTHOGONAL, 400);

		this.setPreferredSize(new Dimension(this.carteGraph2D.getWidth(),
				this.carteGraph2D.getHeight()));
		this.initialized = true;
	}

	/**
	 * @return la carteGraph2D
	 */
	public CarteGraph2D getCarteGraph2D() {
		return carteGraph2D;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// setBackground(new Color(255,255,180));
		setBackground(Color.WHITE);
		if (this.initialized) {
			Graphics2D g2 = (Graphics2D) g.create();
			for (Entry<Long, Line2D> entry : this.carteGraph2D.getEdges().entrySet()) {
				Arc a = carte.getArc(entry.getKey());
				if (a.getType().equals(TypeArc.ESCARPE)) {
					g2.setColor(new Color(247, 129, 10));
				} else if (a.getType().equals(TypeArc.NORMAL)) {
					g2.setColor(new Color(0, 153, 0));
					g2.setColor(Color.BLACK);
				} else if (a.getType().equals(TypeArc.INONDE)) {
					g2.setColor(Color.BLUE);
				} else {
					g2.setColor(Color.BLACK);
				}
				g2.draw(entry.getValue());
			}
			for (Entry<Long, Point2D> entry : this.carteGraph2D.getPoints()
					.entrySet()) {
				Ellipse2D circle = getEllipseFromCenter(
						entry.getValue().getX(), entry.getValue().getY(), 5, 5);
				Noeud n = carte.getNoeud(entry.getKey());
				if (n.isSource()) {
					g2.setColor(Color.CYAN);
					g2.draw(circle);
					g2.fill(circle);
				} else if (n.getIntensiteFeu() > 0) {
					circle = getEllipseFromCenter(entry.getValue().getX(),
							entry.getValue().getY(), n.getIntensiteFeu() * 2,
							n.getIntensiteFeu() * 2);
					g2.setColor(Color.RED);
					g2.draw(circle);
					g2.fill(circle);
				}
				// else { g2.setColor(Color.BLACK); }
				// pour afficher les noeuds
			}
			for (Robot r : this.manager.getRobots()) {
				double x = carteGraph2D.getPoints().get(r.getPosition()).getX();
				double y = carteGraph2D.getPoints().get(r.getPosition()).getY();
				Rectangle2D rectangle = getRectangleFromCenter(x, y, 5, 5);
				if (r instanceof RobotPattes) {
					g2.setColor(Color.GREEN);
				}
				if (r instanceof RobotChenilles) {
					g2.setColor(Color.YELLOW);
				}
				if (r instanceof RobotRoues) {
					g2.setColor(Color.MAGENTA);
				}
				if (r instanceof RobotDrone) {
					g2.setColor(Color.ORANGE);
				}
				g2.draw(rectangle);
				g2.fill(rectangle);
				if (r.getItineraire() != null && !(r instanceof RobotDrone)) {
					ItineraireRobot ir = r.getItineraire();
					if (!ir.getChemin().isEmpty()) {
						long arc = this.carte.getArcId(r.getPosition(), ir
								.getChemin().get(0));
						g2.setColor(Color.RED);
						g2.draw(this.carteGraph2D.getEdges().get(arc));
						for (int i = 1; i < ir.getChemin().size(); i++) {
							arc = this.carte.getArcId(
									ir.getChemin().get(i - 1), ir.getChemin()
											.get(i));
							g2.draw(this.carteGraph2D.getEdges().get(arc));
						}
					}
				}
			}
		}
	}

	/**
	 * Donne une ellipse centree autour un point.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private Ellipse2D getEllipseFromCenter(double x, double y, double width,
			double height) {
		double newX = x - width / 2.0;
		double newY = y - height / 2.0;
		Ellipse2D ellipse = new Ellipse2D.Double(newX, newY, width, height);
		return ellipse;
	}

	/**
	 * Donne un rectangle centre autour un point.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private Rectangle2D getRectangleFromCenter(double x, double y,
			double width, double height) {
		double newX = x - width / 2.0;
		double newY = y - height / 2.0;
		Rectangle2D rectangle = new Rectangle2D.Double(newX, newY, width,
				height);
		return rectangle;
	}

	/**
	 * 
	 * @return largeur de la carte affichee.
	 */
	public int getAdjustedWidth() {
		return this.carteGraph2D.getWidth() + 1;
	}

	/**
	 * 
	 * @return hauteur de la carte affichee.
	 */
	public int getAdjustedHeight() {
		return this.carteGraph2D.getHeight() + 1;
	}

}
