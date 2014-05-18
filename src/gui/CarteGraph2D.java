package gui;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Cette classe represente une carte graphique. En lieu de noeuds et d'arcs, il
 * y a des Point2D et Line2D. Le lien est fait par les id des noeuds et des
 * arcs.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
/**
 * Cette classe represente une carte graphique. Au lieu des noeuds et des arcs, il
 * y a des Point2D et des Line2D. Le lien est fait par les id des noeuds et des
 * arcs.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
/**
 * Cette classe represente une carte graphique. Au lieu des noeuds et des arcs, il
 * y a des Point2D et des Line2D. Le lien est fait par les id des noeuds et des
 * arcs.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class CarteGraph2D {

	/**
	 * Map avec les noeuds de la carte. Key value: l'id du noeud.
	 */
	private HashMap<Long, Point2D> points;

	/**
	 * Ids. de noeuds incendies Red points
	 */
	private HashSet<Long> incendies;

	/**
	 * Ids. de noeuds sources Green points
	 */
	private HashSet<Long> sources;

	/**
	 * Arcs de la carte. La couleur depend du type de chemin.
	 */
	private HashMap<Long, Line2D> edges;

	/**
	 * Constructeur.
	 * 
	 * @param points
	 * @param incendies
	 * @param sources
	 * @param edges
	 */
	public CarteGraph2D(HashMap<Long, Point2D> points, HashSet<Long> incendies,
			HashSet<Long> sources, HashMap<Long, Line2D> edges) {
		this.points = points;
		this.incendies = incendies;
		this.sources = sources;
		this.edges = edges;
	}

	/**
	 * @return le spoints
	 */
	public HashMap<Long, Point2D> getPoints() {
		return points;
	}
	
	/**
	 * @return les incendies
	 */
	public HashSet<Long> getIncendies() {
		return incendies;
	}

	/**
	 * @return les sources
	 */
	public HashSet<Long> getSources() {
		return sources;
	}

	/**
	 * @return les edges
	 */
	public HashMap<Long, Line2D> getEdges() {
		return edges;
	}
	
	/**
	 * Retourne la cle associee a une valeur contenue dans une Map.
	 * 
	 * @param map
	 * @param value
	 * @return la cle de cette valeur. -1 si le point n'est pas dans la carte.
	 */
	public long getIdByPoint(Point2D point) {
		for (Entry<Long, Point2D> entry : this.points.entrySet()) {
			if (point.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return -1;
	}

	/**
	 * Cherche le point le plus proche par distance euclidienne.
	 * 
	 * @param p
	 *            point
	 * @return point le plus proche
	 */
	public Point2D chercherLeNoeudLePlusProche(Point2D p) {
		double distance = Double.POSITIVE_INFINITY;
		Point2D pointLePlusProche = null;
		for (Point2D p1 : this.points.values()) {
			if (p1.distance(p) < distance) {
				distance = p1.distance(p);
				pointLePlusProche = p1;
			}
		}
		return pointLePlusProche;
	}

	/**
	 * 
	 * @return largeur de la carte
	 */
	public int getWidth() {
		int width = 0;
		for (Point2D p : points.values()) {
			width = (int) Math.max(width, p.getX());
		}
		return width;
	}

	/**
	 * 
	 * @return hauteur de la carte
	 */
	public int getHeight() {
		int height = 0;
		for (Point2D p : points.values()) {
			height = (int) Math.max(height, p.getY());
		}
		return height;
	}

}
