package gui;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import carte.Arc;
import carte.Carte;
import carte.Noeud;

/**
 * Cette classe permet de projeter une carte avec points (latitude, longitude)
 * sur une carte plate et ajustee a un rectangle de pixels.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class Projection {

	/**
	 * Types de projection
	 */
	public enum TypeDeProjection {
		LAMBERT, ORTHOGONAL
	}

	/**
	 * Fait la projection de lambert d'un noeud
	 * 
	 * @param noeud
	 *            a projeter (lat, long)
	 * @return Point2D (x, y)
	 */
	public static Point2D lambertProj(Noeud noeud) {

		final double N = 0.760405966;
		final double C = 11603796.9767;
		final double X = 600000;
		final double Y = 5657616.674;

		double lambert = Math.log(Math.tan((Math.PI / 4)
				+ (noeud.getLatitude())));
		Point2D.Double xy = new Point2D.Double();

		xy.x = X + C * Math.exp(-N * lambert)
				* Math.sin(N * noeud.getLongitude());
		xy.y = Y - C * Math.exp(-N * lambert)
				* Math.cos(N * noeud.getLongitude());

		return xy;
	}

	/**
	 * Fait la projection orthogonale d'un noeud
	 * 
	 * @param noeud
	 *            a projeter (lat, long)
	 * @return Point2D (x, y)
	 */
	public static Point2D orthogonalProj(Noeud noeud) {
		// convert to radian
		double longitude = noeud.getLongitude() * Math.PI / 180;
		double latitude = noeud.getLatitude() * Math.PI / 180;

		Point2D.Double xy = new Point2D.Double();
		xy.x = longitude;
		xy.y = Math.log(Math.tan(Math.PI / 4 + 0.5 * latitude));
		return xy;
	}

	/**
	 * Projette une carte sur un carre de pixels.
	 * 
	 * @param carte
	 *            carte a projeter
	 * @param tp
	 *            type de projection
	 * @param width
	 *            largeur du carre
	 * @return CarteGraph2D projection de la carte
	 */
	public static CarteGraph2D projecterCarte(Carte carte, TypeDeProjection tp,
			int width) {
		HashMap<Long, Point2D> vertex = new HashMap<Long, Point2D>();
		HashMap<Long, Line2D> edges = new HashMap<Long, Line2D>();

		if (tp.equals(TypeDeProjection.LAMBERT)) {
			for (Noeud n : carte.getNoeuds()) {
				vertex.put(n.getId(), lambertProj(n));
			}
		} else if (tp.equals(TypeDeProjection.ORTHOGONAL)) {
			for (Noeud n : carte.getNoeuds()) {
				vertex.put(n.getId(), orthogonalProj(n));
			}
		}
		vertex = coordsToPixels(vertex, width);

		for (Arc arc : carte.getArcs()) {
			edges.put(arc.getId(),
					new Line2D.Double(vertex.get(arc.getNoeuds()[0].getId()),
							vertex.get(arc.getNoeuds()[1].getId())));
		}
		// System.out.println("VERTEX: " + vertex);
		return new CarteGraph2D(vertex, carte.getIncendies(),
				carte.getSources(), edges);
	}

	/**
	 * Ajuste les valeurs des coordonnees pour les mettre dans un rectangle. L'echelle
	 * est faite avec la largeur du carre.
	 * 
	 * @param points
	 * @param largeur
	 *            du carre
	 * @return points scales
	 */
	private static HashMap<Long, Point2D> coordsToPixels(
			HashMap<Long, Point2D> points, int width) {

		/*
		 * Operation à faire:
		 * 
		 * (minX,maxY)-----(maxX,maxY) -> (0,0) ------ (width,0)
		 * (minX,minY)-----(maxX,minY) -> (0,heigth) -- (width,heigth)
		 */

		ArrayList<Double> xs = new ArrayList<Double>();
		ArrayList<Double> ys = new ArrayList<Double>();
		for (Point2D p : points.values()) {
			xs.add(p.getX());
			ys.add(p.getY());
		}
		double minX = xs.get(xs.indexOf(Collections.min(xs)));
		double maxX = xs.get(xs.indexOf(Collections.max(xs)));
		double maxY = ys.get(ys.indexOf(Collections.max(ys)));

		double scaleFactor = width / (maxX - minX);

		HashMap<Long, Point2D> pixels = new HashMap<Long, Point2D>();
		for (Entry<Long, Point2D> entry : points.entrySet()) {
			pixels.put(entry.getKey(), new Point2D.Double((entry.getValue()
					.getX() - minX) * scaleFactor,
					-(entry.getValue().getY() - maxY) * scaleFactor));
		}
		// System.out.println("PIXELS: " + pixels);
		return pixels;
	}
}
