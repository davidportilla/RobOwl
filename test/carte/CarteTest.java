package carte;

import static org.junit.Assert.assertEquals;
import io.AccessFichiers;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class CarteTest {

	private Carte carte;

	/**
	 * setUp. Charge une carte de test.
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		this.carte = AccessFichiers.readMap(new File("resources/carte-test.osm"));
	}
	
	
	/**
	 * Teste si le feu est bien augmente.
	 */
	@Test
	public void augmenterFeuTest(){
		this.carte.getNoeud(5).augmenterFeu();
		assertEquals(1, this.carte.getNoeud(5).getIntensiteFeu());
	}
	
	/**
	 * Teste si le feu est bien reduit.
	 */
	@Test
	public void reduireFeuTest(){
		this.carte.getNoeud(5).augmenterFeu();
		this.carte.getNoeud(5).reduireFeu();
		assertEquals(0, this.carte.getNoeud(5).getIntensiteFeu());
	}
	
	/**
	 * Teste si la source est bien ajoutee.
	 */
	@Test
	public void ajouterStationTest(){
		this.carte.addSource(5);
		assertEquals(true, this.carte.getNoeud(5).isSource());	
	}
	
	/**
	 * Teste les noeuds voisins d'un noeud.
	 */
	@Test
	public void getVoisinsTest(){
		HashSet<Long> voisins = new HashSet<Long>();
		voisins.add((long) 7);
		voisins.add((long) 9);
		voisins.add((long) 4);	
		assertEquals(voisins, this.carte.getVoisins(8));
	}
	
}
