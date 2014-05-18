package simulation;

import gui.MainFrame;

import java.util.ArrayList;
import java.util.logging.Logger;

import pathfinder.ItineraireRobot;
import pathfinder.PathFinder;
import pathfinder.PathNotFoundException;
import carte.Arc.TypeArc;
import carte.Carte;
import carte.CarteSynchronized;

/**
 * Robot abstract. Un robot c'est un thread sur un noeud qui peut bouger sur
 * une carte, eteindre des feux et se recharger en eau.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public abstract class Robot extends Thread {

	private static final Logger LOGGER = Logger
			.getLogger(Robot.class.getName());

	/**
	 * Id du noeud ou le robot se trouve.
	 */
	private long position;

	/**
	 * Quantite d'eau restante.
	 */
	private int eauRestante;

	/**
	 * @param eauRestante
	 *            the eauRestante to set
	 */
	public void setEauRestante(int eauRestante) {
		this.eauRestante = eauRestante;
	}

	/**
	 * Un robot n'est pas disponible s'il est en train d'eteindre un feu ou en
	 * train de se recharger.
	 */
	private boolean isDisponible;

	/**
	 * Itineraire du robot.
	 */
	private ItineraireRobot itineraire;

	/**
	 * Carte moniteur ou le robot agit.
	 */
	private CarteSynchronized carte;

	/**
	 * @param itineraire
	 *            l'itineraire a changer
	 */
	public void setItineraire(ItineraireRobot itineraire) {
		if (this.isDisponible == false) {
			if (Thread.currentThread().getId() != this.getId()) {
				throw new RuntimeException("manager se entromete");
			}
		}
		if (itineraire == null) {
			this.itineraire = new ItineraireRobot(new ArrayList<Long>(),
					new ArrayList<Double>());

		} else {
			this.itineraire = itineraire;
		}
		this.carte.notifierObservateurs("Itineraire setted");
	}

	/**
	 * methode pour les tests JUnit.
	 * 
	 * @param etat
	 *            disponible ou non
	 */
	public void setIsDisponible(boolean etat) {
		this.isDisponible = etat;
	}

	/**
	 * 
	 * @return itineraire
	 */
	public ItineraireRobot getItineraire() {
		return this.itineraire;
	}

	/**
	 * Constructeur.
	 * 
	 * @param position
	 * @param carte
	 */
	public Robot(long position, CarteSynchronized carte) {
		this.position = position;
		this.carte = carte;
		this.isDisponible = true;
		this.itineraire = new ItineraireRobot(new ArrayList<Long>(),
				new ArrayList<Double>());
		LOGGER.info("Robot cree en " + this.position);
	}

	public Robot(long position, CarteSynchronized carte, int eauRestante) {
		this(position, carte);
		this.eauRestante = eauRestante;
	}

	/**
	 * Renvoie la carte sur laquelle est le robot.
	 * 
	 * @return carte du robot
	 */
	public Carte getCarte() {
		return this.carte;
	}

	/**
	 * Renvoie la position du robot considere.
	 * 
	 * @return l'id du noeud sur lequel se trouve le robot
	 */
	public long getPosition() {
		return this.position;
	}

	/**
	 * Renvoie la quantite d'eau que le robot possede.
	 * 
	 * @return eauRestante l'entier quantifiant l'eau que le robot transporte
	 */
	public int getEauRestante() {
		return this.eauRestante;
	}

	/**
	 * Renvoie l'etat du robot considere.
	 * 
	 * @return true libre, false occupe
	 */
	public boolean isDisponible() {
		return this.isDisponible;
	}

	/**
	 * Fait se deplacer le robot le long de son itineraire.
	 * 
	 */
	private void seDeplacer() {
		// le robot dort le temps du trajet sur un arc et on actualise sa
		// position ensuite
		while (!this.itineraire.getChemin().isEmpty()) {
			if (!this.carte.isPaused()) {
				try {
					Thread.sleep((long) (double) this.itineraire.getTemps()
							.get(0) * 1000 / MainFrame.getEcheleTemporel());

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.position = this.itineraire.getChemin().get(0);
				this.carte.notifierObservateurs("Le robot " + this.getId()
						+ " bouge");
				this.itineraire.actualiserItineraire();
			}
		}
	}

	/**
	 * Cheche l'itineraire pour aller à la station la plus proche
	 */
	private void chercherItineraireStation() {
		double minTemps = Double.POSITIVE_INFINITY;
		double stationTemps = Double.POSITIVE_INFINITY;
		ItineraireRobot itineraireStation = null;
		Long[] copieStations = carte.getSources().toArray(new Long[carte.getSources().size()]);
		for (long station : copieStations) {
			try {
				stationTemps = PathFinder.findPath(this, station,
						MainFrame.getAlgorithme()).getTempsTotal();
				if (stationTemps < minTemps) {
					minTemps = stationTemps;
					itineraireStation = PathFinder.findPath(this, station,
							MainFrame.getAlgorithme());
				}
			} catch (PathNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.setItineraire(itineraireStation);
	}

	/**
	 * Cycle d'un robot. Appele dans les classes héritees dedans un while(true)
	 * dans la methode run.
	 * 
	 * @param eauMaxDuRobot
	 */
	protected void cycleDuRobot(int eauMaxDuRobot) {
		this.isDisponible = true;
		long incendie = this.carte.getAssignement();
		this.isDisponible = false;
		System.out.println("voy a " + incendie);
		LOGGER.info("Le robot " + this.getId() + " va eteindre l'incendie en "
				+ incendie);
		this.seDeplacer();
		LOGGER.info("Le robot " + this.getId() + " arrive a l'incendie en "
				+ incendie);
		while (this.carte.getIncendies().contains(this.position)
				&& this.carte.getNoeud(this.position).getIntensiteFeu() > 0
				&& this.eauRestante > 0) {
			try {
				Thread.sleep(20000 / MainFrame.getEcheleTemporel());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.carte.reduireFeu(this.position);
			this.eauRestante--;
		}
		if (this.getEauRestante() < eauMaxDuRobot) {
			this.chercherItineraireStation();
			if (this.itineraire != null
					&& !this.itineraire.getChemin().isEmpty()) {
				LOGGER.info("Le robot " + this.getId() + " va se recharger");
				this.seDeplacer();
				this.carte.recharger(this.position);
				this.eauRestante = eauMaxDuRobot;
				LOGGER.info("Le robot " + this.getId() + " est recharge");
			}
		}
	}

	/**
	 * 
	 * @param arc
	 * @return vitesse du robot par l'arc
	 */
	public abstract int getVitesse(TypeArc arc);

	/*
	 * (NON-JAVADOC)
	 * 
	 * @SEE JAVA.LANG.OBJECT#TOSTRING()
	 */
	@Override
	public String toString() {
		return "Robot [id=" + this.getId() + "position=" + position
				+ ", eauRestante=" + eauRestante + ", isDisponible="
				+ isDisponible + "]";
	}

}