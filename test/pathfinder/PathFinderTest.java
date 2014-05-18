package pathfinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.AccessFichiers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import pathfinder.PathFinder.Algorithme;
import simulation.Robot;
import simulation.RobotDrone;
import simulation.RobotRoues;
import carte.Arc;
import carte.Carte;
import carte.CarteSynchronized;
import carte.Noeud;

public class PathFinderTest {

	private CarteSynchronized carte;
	
	/**
	 * setUp: on charge une carte de test.
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException{
		Carte carte = AccessFichiers.readMap(new File("resources/carte-test.osm"));
		Noeud[] noeuds = carte.getNoeuds();
		Arc[] arcs = carte.getArcs();
		HashMap<Long, Noeud> noeudsMap = new HashMap<Long, Noeud>();
		HashMap<Long, Arc> arcsMap = new HashMap<Long, Arc>();
		for(Noeud n: noeuds){
			noeudsMap.put(n.getId(), n);
		}
		for(Arc a: arcs){
			arcsMap.put(a.getId(), a);
		}
		
		this.carte = new CarteSynchronized(noeudsMap, arcsMap);
	}
	
	/**
	 * Cette methode permet de s'assurer que l'itineraire retourne est celui espere.
	 * @throws IOException
	 * @throws PathNotFoundException
	 */
	@Test
	public void testFindPath() throws IOException, PathNotFoundException {
		Robot robot = new RobotRoues(1, carte);
		ItineraireRobot ir = PathFinder.findPath(robot, 9,
				Algorithme.DIJKSTRA);
		ArrayList<Long> cheminExpected = new ArrayList<Long>();
		cheminExpected.add(3L);
		cheminExpected.add(6L);
		cheminExpected.add(7L);
		cheminExpected.add(8L);
		cheminExpected.add(9L);
		assertEquals(cheminExpected, ir.getChemin());
		ArrayList<Double> tempsExpected = new ArrayList<Double>();
		tempsExpected.add(28283.19);
		tempsExpected.add(20014.12);
		tempsExpected.add(3329.46);
		tempsExpected.add(3329.46);
		tempsExpected.add(3329.46);
		double tempsTotalExpected = 0;
		for(int i = 0; i < tempsExpected.size(); i++){
			assertEquals(tempsExpected.get(i), ir.getTemps().get(i), 1E-1);
			tempsTotalExpected += tempsExpected.get(i);
		}
		assertEquals(tempsTotalExpected, ir.getTempsTotal(), 1E-1);
	}
	
	/**
	 * Test pour un robot drone
	 * @throws IOException
	 * @throws PathNotFoundException
	 */
	@Test
	public void testFindPathDrone() throws IOException, PathNotFoundException {
		Robot robotdrone = new RobotDrone(1, carte);
		ItineraireRobot ird = PathFinder.findPath(robotdrone, 9,Algorithme.DIJKSTRA);
		if(ird.getChemin().get(0) != 9){
			fail("itineraire incorrecte");
		}
	}
}
