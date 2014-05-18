package simulation;

import gui.MainFrame;

import java.util.ArrayList;
import java.util.logging.Logger;

import pathfinder.ItineraireRobot;
import pathfinder.PathFinder;
import pathfinder.PathNotFoundException;
import carte.CarteSynchronized;

/**
 * Manager pour controler les robots. Extends Thread.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class Manager extends Thread {

	private static final Logger LOGGER = Logger.getLogger(Manager.class
			.getName());

	/**
	 * Carte qui sert de moniteur.
	 */
	private CarteSynchronized carte;

	/**
	 * @return la carte
	 */
	public CarteSynchronized getCarteSynchronized() {
		return carte;
	}

	/**
	 * Robot sous le controle du manager
	 */
	private ArrayList<Robot> robots;

	/**
	 * @return les robots
	 */
	public ArrayList<Robot> getRobots() {
		return robots;
	}

	/**
	 * Construit le manager des robots
	 * 
	 * @param robot
	 *            la liste de robots consideres
	 * @param carte
	 *            la carte de la simulation
	 */
	public Manager(CarteSynchronized carte, ArrayList<Robot> robots) {
		this.carte = carte;
		this.robots = robots;
		for (Robot r : robots) {
			r.start();
		}
	}

	/**
	 * Cherche le robot disponible et avec assez d'eau pour aller à l'incendie
	 * 
	 * @param noeud
	 * @return Robot retrouvé
	 */
	public Robot rechercherRobot(long noeud) {
		double minTemps = Double.POSITIVE_INFINITY;
		double robotTemps = Double.POSITIVE_INFINITY;
		ItineraireRobot itineraire = null;
		ItineraireRobot itineraireRobotPompier = null;
		Robot robotPompier = null;
		for (int i = 0; i < this.robots.size(); i++) {
			if (this.robots.get(i).isDisponible()
					&& this.robots.get(i).getEauRestante() >= carte.getNoeud(
							noeud).getIntensiteFeu()) {
				try {
					itineraire = PathFinder.findPath(this.robots.get(i), noeud,
							MainFrame.getAlgorithme());
					robotTemps = itineraire.getTempsTotal();
					if (robotTemps < minTemps) {
						minTemps = robotTemps;
						robotPompier = this.robots.get(i);
						itineraireRobotPompier = itineraire;
					}
				} catch (PathNotFoundException e) {
					// e.printStackTrace();
				}
			}
		}
		if (robotPompier != null && robotPompier.isDisponible()) {
			robotPompier.setItineraire(itineraireRobotPompier);
			return robotPompier;
		} else {
			return null;
		}
	}

	/**
	 * Cycle du manager
	 */
	@Override
	public void run() {
		while (true) {
			long incendie = this.carte.gererIncendie();
			Robot robot = this.rechercherRobot(incendie);
			if(robot != null){
				this.carte.assignerIncendie(robot.getId(), incendie);
			} else {
				this.carte.addIncendieAGerer(incendie);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//this.carte.waitForNotify();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Manager [robots=" + robots + ", incendiesAGerer="
				+ this.carte.getIncendiesAGerer() + "]";
	}

	/**
	 * Ajoute un robot a la liste de robots
	 * 
	 * @param robot
	 *            le robot a ajouter a la liste de robots
	 */
	public void addRobot(Robot robot) {
		this.robots.add(robot);
		this.carte.notifierObservateurs("robot ajouté a la liste du manager");
		robot.start();
	}

}