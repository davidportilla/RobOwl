package pathfinder;

import java.util.ArrayList;

/**
 * Classe ItineraireRobot
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class ItineraireRobot {

	/**
	 * Chemin de noeuds.
	 */
	private ArrayList<Long> chemin;
	
	/**
	 * Temps necessaire pour arriver au noeud suivant. Le noeud suivant est le
	 * noeud avec la position suivante dans le chemin.
	 * 
	 * Temps en secondes.
	 */
	private ArrayList<Double> temps;

	/**
	 * Temps total pour arriver a l'objectif en secondes.
	 */
	private double tempsTotal;
	
	/**
	 * Constructeur
	 * @param chemin
	 * @param temps
	 */
	public ItineraireRobot(ArrayList<Long> chemin, ArrayList<Double> temps) {
		this.chemin = chemin;
		this.temps = temps;
		this.tempsTotal = 0;
		for (int i = 0; i < this.temps.size(); i++) {
			this.tempsTotal += this.temps.get(i);
		}
	}

	/**
	 * @return le chemin
	 */
	public ArrayList<Long> getChemin() {
		return chemin;
	}

	/**
	 * @return le temps
	 */
	public ArrayList<Double> getTemps() {
		return temps;
	}

	/**
	 * @return le tempsTotal
	 */
	public double getTempsTotal() {
		return tempsTotal;
	}

	/**
	 * Efface le premier element de chemin et de temps.
	 */
	public void actualiserItineraire() {
		this.tempsTotal -= this.temps.get(0);
		this.temps.remove(0);
		this.chemin.remove(0);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ItineraireRobot [chemin=" + chemin + ", temps=" + temps
				+ ", tempsTotal=" + tempsTotal + "]";
	}

}
