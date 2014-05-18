package simulation;

import static org.junit.Assert.assertEquals;
import io.AccessFichiers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import carte.Arc;
import carte.Carte;
import carte.CarteSynchronized;
import carte.Noeud;

public class RobotTest {

	private CarteSynchronized carte;
	private ArrayList<Robot> robots;

	/**
	 * Charge le scenario du test.
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		Carte carte = AccessFichiers.readMap(new File("resources/carte-test.osm"));
		Noeud[] noeuds = carte.getNoeuds();
		Arc[] arcs = carte.getArcs();
		HashMap<Long, Noeud> noeudsMap = new HashMap<Long, Noeud>();
		HashMap<Long, Arc> arcsMap = new HashMap<Long, Arc>();
		for (Noeud n : noeuds) {
			noeudsMap.put(n.getId(), n);
		}
		for (Arc a : arcs) {
			arcsMap.put(a.getId(), a);
		}
		this.carte = new CarteSynchronized(noeudsMap, arcsMap);
		this.robots = new ArrayList<Robot>();
		robots.add(new RobotRoues(1, this.carte));
		robots.add(new RobotRoues(5, this.carte));
	}

	/**
	 * Teste la disponibilite du robot.
	 */
	@Test
	public void isDisponibleTest() {
		assertEquals(robots.get(0).isDisponible(), true);
	}

}
