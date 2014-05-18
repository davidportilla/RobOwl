package carte;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import carte.Arc.TypeArc;

public class ArcTest {

	private static final double EPSILON = 1;

	private Noeud n1;
	private Noeud n2;
	private Noeud n3;
	private Noeud n4;
	private Noeud n5;
	private Noeud n6;
	private Noeud n7;
	private Noeud n8;
	private Arc a12;
	private Arc a34;
	private Arc a56;
	private Arc a78;

	/**
	 * setUp. Cree des arcs et des noeuds pour tester.
	 */
	@Before
	public void setUp() {
		n1 = new Noeud(1, 1, 1, false);
		n2 = new Noeud(2, 1, 2, false);
		n3 = new Noeud(3, -4, 7, false);
		n4 = new Noeud(4, 5, 0, false);
		n5 = new Noeud(5, 45.61, 1.54, false);
		n6 = new Noeud(6, 45.63, 1.54, false);
		n7 = new Noeud(7, 43.5733579, 1.4746687, false);
		n8 = new Noeud(8, 43.5734591, 1.4745666, false);
		a12 = new Arc(n1, n2, TypeArc.NORMAL);
		a34 = new Arc(n3, n4, TypeArc.NORMAL);
		a56 = new Arc(n5, n6, TypeArc.NORMAL);
		a78 = new Arc(n7, n8, TypeArc.NORMAL);
	}

	/**
	 * Teste si la longueur d'un arc est bien calculee. Avec des coordonnees de plus
	 * en plus precises.
	 */
	@Test
	public void testGetLongueur() {
		// (lat1, lon1), (lat2, lon2) -> d km
		// (1,1), (1,2) -> 111.29 km
		assertEquals(111.29, a12.getLongueur(), EPSILON);
		// (-4,7), (5,0) -> 1268.53 km
		assertEquals(1268.53, a34.getLongueur(), EPSILON);
		// (45.61, 1.54), (45.63, 1.54) -> 2.23 km
		assertEquals(2.23, a56.getLongueur(), EPSILON);
		// (43.5733579, 1.4746687), (43.5734591, 1.4745666) -> 0.01 km
		assertEquals(0.01, a78.getLongueur(), EPSILON);
	}

}
