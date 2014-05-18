package carte;

import static org.junit.Assert.assertEquals;
import io.AccessFichiers;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author David
 *
 */
public class NoeudTest {
	
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
	public void augmenterFeuTest() {
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
	 * Teste si le noeud station est bien designe comme tel.
	 */
	@Test
	public void isStationTest(){
		this.carte.addSource(5);
		assertEquals(true, this.carte.getNoeud(5).isSource());
	}
	
}
