package simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import io.AccessFichiers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import carte.Carte;
import carte.CarteSynchronized;

public class ManagerTest {

	private CarteSynchronized carte;
	private ArrayList<Robot> robots;
	private Manager manager;

	/**
	 * setUp. Charge le scenario du test.
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		Carte carte = AccessFichiers.readMap(new File(
				"resources/carte-test.osm"));
		this.carte = new CarteSynchronized(carte.getNoeudsMap(),
				carte.getArcsMap());
		this.robots = new ArrayList<Robot>();
		robots.add(new RobotRoues(7, this.carte));
		robots.add(new RobotRoues(6, this.carte));
		this.manager = new Manager(this.carte, robots);
	}

	/**
	 * Teste si le robot trouve est le plus proche, selon differents etats du robots,
	 * charge ou non, disponible ou non.
	 */
	@Test
	public void rechercherRobotTest() {
		// Changer les emplacement des robots si besoin
		// 5 ---- 6 ---- 7
		// le robot en 6 va a l'incendie en 5
		// Test de simple recherche -> OK
		assertEquals(robots.get(1).getId(), manager.rechercherRobot(5).getId());
		assertEquals(robots.get(1).getId(), manager.rechercherRobot(6).getId());
		
		// Test avec la quantite d eau restante insuffisante -> OK
		// Va le robot en 7
		this.robots.get(1).setEauRestante(2);
		this.carte.augmenterFeu(5, 5);
		assertEquals(robots.get(0).getId(), manager.rechercherRobot(5).getId());

		// Test avec isDisponible faux -> OK
		this.robots.get(0).setIsDisponible(false);
		this.robots.get(1).setIsDisponible(false);
		if (manager.rechercherRobot(5) != null) {
			fail("robot no disponible");
		}
	}

}
