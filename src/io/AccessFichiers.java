package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import simulation.Manager;
import simulation.Robot;
import simulation.RobotChenilles;
import simulation.RobotDrone;
import simulation.RobotPattes;
import simulation.RobotRoues;
import carte.Arc;
import carte.Arc.TypeArc;
import carte.Carte;
import carte.CarteSynchronized;
import carte.Noeud;

/**
 * Avec les methodes statiques de cette classe on peut charger une carte a
 * partir d'un fichier et afficher l'etat d'une simulation.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla
 * @author Patricia Ventura
 * @version 25-11-2013
 */
public class AccessFichiers {

	private static double minLat;
	private static double minLon;
	private static double maxLat;
	private static double maxLon;

	/**
	 * Prend les donnees d'un fichier TXT.
	 * 
	 * @param br
	 *            BufferedReader
	 * @return Carte avec les donnees
	 * @throws IOException
	 */
	private static Carte acquerirTXTDonnees(BufferedReader br)
			throws IOException {
		HashMap<Long, Noeud> noeuds = new HashMap<Long, Noeud>();
		HashMap<Long, Arc> arcs = new HashMap<Long, Arc>();
		String line = br.readLine();
		boolean node = false;
		boolean edge = false;
		while (line != null) {
			String[] matches = line.split("\\:|\\,|\\-|\\>|\\s");
			if (matches[0].equals("begin_nodes")) {
				node = true;
			} else if (matches[0].equals("end_nodes")) {
				node = false;
			} else if (matches[0].equals("begin_edges")) {
				edge = true;
			} else if (matches[0].equals("end_edges")) {
				edge = false;
			} else {
				if (node) {
					Long idNoeud = Long.parseLong(matches[0]);
					double lat = Double.parseDouble(matches[2]);
					double lon = Double.parseDouble(matches[1]);
					Noeud n = new Noeud(idNoeud, lat, lon, false);
					noeuds.put(idNoeud, n);
				} else if (edge) {
					long idNoeud1 = Long.parseLong(matches[0]);
					long idNoeud2 = Long.parseLong(matches[matches.length - 1]);
					Arc a = new Arc(noeuds.get(idNoeud1), noeuds.get(idNoeud2),
							TypeArc.NORMAL);
					arcs.put(a.getId(), a);
				}
			}
			line = br.readLine();
		}
		br.close();
		return new Carte(noeuds, arcs);
	}

	/**
	 * Prend l'etat d'une simulation d'un fichier.
	 * 
	 * @param file
	 *            le fichier.
	 * @param carte
	 *            la carte ou l'on chargera la simulation.
	 * @return carte modifiee avec la simulation chargee.
	 * @throws IOException
	 */
	public static Carte readSimulationCarte(File file, Carte carte)
			throws IOException {
		HashMap<Long, Noeud> noeudsMap = carte.getNoeudsMap();
		HashMap<Long, Arc> arcsMap = carte.getArcsMap();
		Carte carteModifiee = new Carte(noeudsMap, arcsMap);

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		boolean incendie = false;
		boolean source = false;
		while (line != null) {

			String[] matches = line.split("\\,|\\(|\\)|\\s");
			if (matches[0].equals("begin_incendies")) {
				incendie = true;
			} else if (matches[0].equals("end_incendies")) {
				incendie = false;
			} else if (matches[0].equals("begin_sources")) {
				source = true;
			} else if (matches[0].equals("end_sources")) {
				source = false;
			} else {
				if (incendie) {
					long idIncendie = Long.parseLong(matches[0]);
					int intensite = Integer.parseInt(matches[2]);
					for (int i = 0; i < intensite; i++) {
						carteModifiee.augmenterFeu(idIncendie);
						// add the fire
					}
				} else if (source) {
					long idSource = Long.parseLong(matches[0]);
					carteModifiee.addSource(idSource);
					// add the source
				}
			}

			line = br.readLine();
		}

		br.close();
		return carteModifiee;

	}

	/**
	 * Prend l'etat d'une simulation d'un fichier.
	 * 
	 * @param file
	 *            le fichier
	 * @param carte
	 *            la carte ou l'on chargera la simulation
	 * @return liste de robots places sur la carte
	 * @throws IOException
	 */
	public static ArrayList<Robot> readSimulationRobots(File file,
			CarteSynchronized carteSyn) throws IOException {

		ArrayList<Robot> robots = new ArrayList<Robot>();

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();

		boolean robot = false;
		while (line != null) {
			String[] matches = line.split("\\,|\\(|\\)|\\s");

			if (matches[0].equals("begin_robots")) {
				robot = true;
			} else if (matches[0].equals("end_robots")) {
				robot = false;
			} else {
				if (robot) {
					// add the robots
					String typeRobot = matches[0];
					long idPosition = Long.parseLong(matches[2]);
					int eauRestante = Integer.parseInt(matches[4]);
					if (typeRobot.equals("RobotRoues")) {
						Robot r = new RobotRoues(idPosition, carteSyn,
								eauRestante);
						robots.add(r);
					} else if (typeRobot.equals("RobotPattes")) {
						Robot r = new RobotPattes(idPosition, carteSyn,
								eauRestante);
						robots.add(r);
					} else if (typeRobot.equals("RobotChenilles")) {
						Robot r = new RobotChenilles(idPosition, carteSyn,
								eauRestante);
						robots.add(r);
					} else if (typeRobot.equals("RobotDrone")) {
						Robot r = new RobotDrone(idPosition, carteSyn,
								eauRestante);
						robots.add(r);
					}
				}
			}
			line = br.readLine();
		}
		br.close();
		return robots;
	}

	/**
	 * Cette methode s'utilise pour lire un fichier en TXT et retourne le texte.
	 * 
	 * @param File
	 *            file - le fichier a lire.
	 * @return String string avec le texte du fichier.
	 */
	public static String readTextRobowl(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine() + "\n";
		String text = line;

		while (line != null) {
			text = text + line + "\n";

			line = br.readLine();
		}
		br.close();
		return text;
	}

	/**
	 * 
	 * @param fileName
	 *            nom du fichier
	 * @return l'extension du fichier
	 */
	private static String getExtension(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	/**
	 * Sauvegarde l'etat d'une simulation dans un fichier.
	 * 
	 * @param fileName
	 *            nom du fichier.
	 * @param carte
	 *            avec les incendies et sources a sauvegarder.
	 * @param manager
	 *            Manager avec les robots a sauvegarder.
	 * @throws IOException
	 */
	public static void writeSimulation(File file, Carte carte, Manager manager)
			throws IOException {
		System.out.println("saving.....");
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter
				.write("begin_robots (type, idNoeudPosition, eauRestante)");
		bufferedWriter.newLine();
		for (Robot r : manager.getRobots()) {
			String s = "";
			if (r instanceof RobotRoues) {
				s += "RobotRoues, ";
			} else if (r instanceof RobotPattes) {
				s += "RobotPattes, ";
			} else if (r instanceof RobotDrone) {
				s += "RobotDrone, ";
			} else if (r instanceof RobotChenilles) {
				s += "RobotChenilles, ";
			}
			s += r.getPosition() + ", " + r.getEauRestante();
			bufferedWriter.write(s);
			bufferedWriter.newLine();
		}
		bufferedWriter.write("end_robots");
		bufferedWriter.newLine();
		bufferedWriter.write("begin_incendies (idNoeudPosition, intensiteFeu)");
		bufferedWriter.newLine();
		for (long idNoeud : carte.getIncendies()) {
			bufferedWriter.write(idNoeud + ", "
					+ carte.getNoeud(idNoeud).getIntensiteFeu());
			bufferedWriter.newLine();
		}
		bufferedWriter.write("end_incendies");
		bufferedWriter.newLine();
		bufferedWriter.write("begin_sources (idNoeudPosition)");
		bufferedWriter.newLine();
		for (long idNoeud : carte.getSources()) {
			bufferedWriter.write("" + idNoeud);
			bufferedWriter.newLine();
		}
		bufferedWriter.write("end_sources");
		System.out.println("file enregistered........");
		bufferedWriter.close();
	}

	/**
	 * Explore un fichier osm/xml OpenStreetMaps pour charger une carte.
	 * 
	 * @param file
	 *            le fichier
	 * @return Carte avec les donnees dans le fichier OSM
	 */
	private static Carte acquerirOSMDonnees(File file) {
		HashMap<Long, Noeud> noeuds = new HashMap<Long, Noeud>();
		HashMap<Long, Arc> arcs = new HashMap<Long, Arc>();
		// Step 1: create a DocumentBuilderFactory and setNamespaceAware
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		// Step 2: create a DocumentBuilder
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			// Step 3: parse the input file to get a Document object
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList childNodes = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node n = childNodes.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					// I take the nodes of the map
					if (n.getNodeName().equals("bounds")) {
						minLat = Double.parseDouble(n.getAttributes()
								.getNamedItem("minlat").getNodeValue());
						minLon = Double.parseDouble(n.getAttributes()
								.getNamedItem("minlon").getNodeValue());
						maxLat = Double.parseDouble(n.getAttributes()
								.getNamedItem("maxlat").getNodeValue());
						maxLon = Double.parseDouble(n.getAttributes()
								.getNamedItem("maxlon").getNodeValue());
					}
					if (n.getNodeName().equals("node")) {
						long id = Long.parseLong(n.getAttributes()
								.getNamedItem("id").getNodeValue());
						Noeud noeud = noeudOSMtoNoeudCarte(id, n);
						if (noeud != null) {
							noeuds.put(noeud.getId(), noeud);
						}
					}
					if (n.getNodeName().equals("way")) {
						arcs.putAll(arcOSMtoArcCarte(n, noeuds));
					}
				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		/*
		 * for(Arc a: arcs.values()){ System.out.println("tipo de arco: " +
		 * a.getType()); }
		 */
		return new Carte(noeuds, arcs);
	}

	/**
	 * Prend un noeud OSM du type way, et retourne un HashMap d'arcs et ses id
	 * pour la carte. <br>
	 * Critère pris<br>
	 * 
	 * <pre>
	 * highway: terrain plat
	 * waterway: terrain inonde
	 * autres: terrain escarpe
	 * boolean oneway et int maxspeed (km/h)
	 * </pre>
	 * 
	 * @param node
	 *            way en XML
	 * @param noeuds
	 *            Map de noeuds de la carte
	 * @return map avec les arcs et les ids lus
	 */
	private static HashMap<Long, Arc> arcOSMtoArcCarte(Node node,
			HashMap<Long, Noeud> noeuds) {
		ArrayList<Arc> way = new ArrayList<Arc>();
		TypeArc type = TypeArc.ESCARPE; // default
		boolean oneWay = false; // default
		int maxSpeed = 50; // default
		ArrayList<Long> listeDeNoeudId = new ArrayList<Long>();
		if (node.hasChildNodes()) {
			for (int j = 0; j < node.getChildNodes().getLength(); j++) {
				oneWay = false;
				maxSpeed = 50;
				// we are in a way with a lot of joined nodes
				Node m = node.getChildNodes().item(j);
				// we take the type and the max speed for all the arcs in the
				// way
				if (m.getNodeName().equals("tag")) {
					if (m.getAttributes().getNamedItem("k").getNodeValue()
							.equals("waterway")) {
						type = TypeArc.INONDE;
					} else if (m.getAttributes().getNamedItem("k")
							.getNodeValue().equals("highway")) {
						type = TypeArc.NORMAL;
					}
					// Set the max speed.
					if (m.getAttributes().getNamedItem("k").getNodeValue()
							.equals("maxspeed")) {
						maxSpeed = Integer.parseInt(m.getAttributes()
								.getNamedItem("v").getNodeValue());
					}
					// One way or not
					if (m.getAttributes().getNamedItem("k").getNodeValue()
							.equals("oneway")) {
						oneWay = m.getAttributes().getNamedItem("v")
								.getNodeValue().equals("yes");
					}
				} else if (m.getNodeName().equals("nd")) {
					listeDeNoeudId.add(Long.parseLong(m.getAttributes()
							.getNamedItem("ref").getNodeValue()));
				}
			}
			for (int i = 0; i < listeDeNoeudId.size() - 1; i++) {
				// creer l'arc node[i] -> node[i+1]
				Noeud noeud1 = noeuds.get(listeDeNoeudId.get(i));
				Noeud noeud2 = noeuds.get(listeDeNoeudId.get(i + 1));
				try {
					Arc arc = new Arc(noeud1, noeud2, type, oneWay, maxSpeed);
					// System.out.println("tipo de arco encontrado: " +
					// arc.getType());
					way.add(arc);
				} catch (NullPointerException e) {
					// Noeud normally out of the bounds of the map
					// e.printStackTrace();
				}
			}

		}
		HashMap<Long, Arc> wayMap = new HashMap<Long, Arc>();
		for (Arc a : way) {
			// System.out.println("tipo de arco guardado: " + a.getType());
			wayMap.put(a.getId(), a);
		}

		return wayMap;
	}

	/**
	 * Prend un noeud XML du type node, et retourne un noeud pour la carte. Si
	 * le noeud n'est pas dans les dimensions de la carte, retourne null;
	 * 
	 * @param node
	 *            Noeud en XML
	 * @return Noeud de Carte
	 */
	private static Noeud noeudOSMtoNoeudCarte(long id, Node node) {
		double latitude = Double.parseDouble(node.getAttributes()
				.getNamedItem("lat").getNodeValue());
		double longitude = Double.parseDouble(node.getAttributes()
				.getNamedItem("lon").getNodeValue());
		boolean isSource = false;
		if (minLat <= latitude && latitude <= maxLat && minLon <= longitude
				&& longitude <= maxLon) {
			return new Noeud(id, latitude, longitude, isSource);
		} else {
			return null;
		}
	}

	/**
	 * Prends une carte d'un fichier
	 * 
	 * @param file
	 *            le fichier
	 * @return Carte La carte lue dans le fichier
	 * @throws IOException
	 */
	public static Carte readMap(File file) throws IOException {
		String extension = getExtension(file.getName());
		if (extension.equals("osm") || extension.equals("xml")) {
			return acquerirOSMDonnees(file);
		} else if (extension.equals("txt")) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			return acquerirTXTDonnees(br);
		}
		return null;
	}

}
