package simulation;

import gui.MainFrame;
import io.AccessFichiers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import pathfinder.PathFinder;
import carte.Arc;
import carte.Carte;
import carte.CarteSynchronized;
import carte.Noeud;

/**
 * Main class.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class Simulation {

	final static String USAGE = "java RobOwl -g/-t carte etat-simulation";

	/**
	 * Methode main. <br>
	 * Arguments optionnels: <br>
	 * arg0: "-g" mode graphique; "-t" mode terminal. <br>
	 * arg1: fichier d'entre avec la carte <br>
	 * arg2: fichier avec l'etat de la simulation Default = -g map-isae.osm
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		List<String> argsList = new ArrayList<String>();

		boolean modeGraphique = true;

		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
			case '-':
				if (args[i].equals("-g")) {
					modeGraphique = true;
				} else if (args[i].equals("-t")) {
					modeGraphique = false;
				} else {
					throw new IllegalArgumentException("Not a valid argument: "
							+ args[i]);
				}
				break;
			default:
				argsList.add(args[i]);
				break;
			}
		}

		Carte carte = null;
		if (args.length > 0 && args[0] != null) {
			carte = AccessFichiers.readMap(new File(argsList.get(0)));
		} else {
			carte = AccessFichiers.readMap(new File("resources/map-isae.osm"));
		}

		if (carte == null){
			throw new IOException();
		}
		
		HashMap<Long, Noeud> noeudsMap = carte.getNoeudsMap();
		HashMap<Long, Arc> arcsMap = carte.getArcsMap();
		CarteSynchronized carteSynchro = new CarteSynchronized(noeudsMap,
				arcsMap);
		ArrayList<Robot> robots = new ArrayList<Robot>();

		// Lire sources et incendies
		if (args.length > 1 && args[1] != null) {
			carte = AccessFichiers.readSimulationCarte(
					new File(argsList.get(1)), carte);
			// Lire robots
			robots = AccessFichiers.readSimulationRobots(
					new File(argsList.get(1)), carteSynchro);
		}

		noeudsMap = carte.getNoeudsMap();
		arcsMap = carte.getArcsMap();
		carteSynchro = new CarteSynchronized(noeudsMap, arcsMap);

		Simulation.setLogger("robowl.log");

		Manager man = new Manager(carteSynchro, robots);

		if (modeGraphique) {
			MainFrame frame = new MainFrame(man);
			frame.setVisible(true);
		} else {
			man.start();
			carteSynchro.restart();
		}

	}

	private static void setLogger(String output) throws SecurityException,
			IOException {
		// Logger output file configuration
		FileHandler handler = new FileHandler(output);
		handler.setFormatter(new SimpleFormatter());
		Logger loggerCarte = Logger.getLogger(Carte.class.getName());
		Logger loggerCarteSynchro = Logger.getLogger(CarteSynchronized.class
				.getName());
		Logger loggerRobot = Logger.getLogger(Robot.class.getName());
		Logger loggerManager = Logger.getLogger(Manager.class.getName());
		Logger loggerPathFinder = Logger.getLogger(PathFinder.class.getName());
		loggerCarte.setLevel(Level.ALL);
		loggerCarteSynchro.setLevel(Level.ALL);
		loggerRobot.setLevel(Level.ALL);
		loggerManager.setLevel(Level.ALL);
		loggerPathFinder.setLevel(Level.ALL);
		loggerCarte.addHandler(handler);
		loggerCarteSynchro.addHandler(handler);
		loggerRobot.addHandler(handler);
		loggerManager.addHandler(handler);
		loggerPathFinder.addHandler(handler);
	}

}