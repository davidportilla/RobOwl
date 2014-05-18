package io;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import carte.Arc;
import carte.Carte;
import carte.Noeud;
import carte.Arc.TypeArc;

public class AccessFichiersTest {

	/**
	 * Teste qu'il n'y a pas des arcs repetes.
	 * @throws IOException
	 */
	@Test
	public void testReadMapTXT() throws IOException {
		Carte carte = AccessFichiers
				.readMap(new File("resources/map-isae.txt"));
		for (Arc a : carte.getArcs()) {
			Noeud[] noeudsA = a.getNoeuds();
			for (Arc b : carte.getArcs()) {
				Noeud[] noeudsB = b.getNoeuds();
				if (a != b) {
					if (noeudsA.equals(noeudsB)) {
						fail("Arc repete");
					}
				}
			}
		}
	}

	/**
	 * Teste une carte OSM.
	 * @throws IOException
	 */
	@Test
	public void testReadMapOSM() throws IOException {
		Carte carte = AccessFichiers.readMap(new File(
				"resources/carte-test.osm"));
		// TEST ARCS
		if (carte.getArcs().length != 10) {
			System.out.println("Nombre d'arcs: " + carte.getArcs().length);
			fail("arcs repetes");
		}
		int waterways = 0;
		int normals = 0;
		int escarpes = 0;
		for (Arc a : carte.getArcs()) {
			if (a.getType().equals(TypeArc.ESCARPE))
				escarpes++;
			if (a.getType().equals(TypeArc.NORMAL))
				normals++;
			if (a.getType().equals(TypeArc.INONDE))
				waterways++;
		}
		if (waterways != 2 || normals != 4 || escarpes != 4)
			fail("types d'arc mal");
		// TEST NOEUDS
		if(carte.getNoeuds().length != 10){
			fail("Noeuds repetes");
		}
		
		// CARTE SUPAERO
		carte = AccessFichiers.readMap(new File("resources/map-isae.osm"));
		waterways = 0;
		normals = 0;
		escarpes = 0;
		for (Arc a : carte.getArcs()) {
			if (a.getType().equals(TypeArc.ESCARPE))
				escarpes++;
			if (a.getType().equals(TypeArc.NORMAL))
				normals++;
			if (a.getType().equals(TypeArc.INONDE))
				waterways++;
		}
		System.out.println("inondes isae: " + waterways);
		System.out.println("normals isae: " + normals);
		System.out.println("escarpes isae: " + escarpes);
		if (waterways < 20 || normals < 20 || escarpes < 20)
			fail("types d'arc mal");
		
	}

}
