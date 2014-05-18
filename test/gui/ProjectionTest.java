package gui;

import static org.junit.Assert.*;

import org.junit.Test;

import io.AccessFichiers;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import org.junit.Before;

import carte.Arc;
import carte.Carte;
import carte.CarteSynchronized;
import carte.Noeud;

public class ProjectionTest {

	private Carte carte;

	/**
	 * Carte necessaire au test
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		this.carte = AccessFichiers.readMap(new File("resources/carte-test.osm"));
	}
	
	/**
	 * On calcule le projete orthogonal d'un noeud de la carte. On regarde si le Point2D obtenu
	 * est le meme que celui retourne par la methode.
	 */
	@Test
	public void orthogonalProj(){
		
		double lat = this.carte.getNoeud(5).getLatitude();
		double longi = Math.log(Math.tan(Math.PI / 4 + 0.5 * this.carte.getNoeud(5).getLatitude()));
		
		assertEquals(Projection.orthogonalProj(this.carte.getNoeud(5)), new Point2D.Double(lat, longi));
	}

}
