package org.thiesen.geo.common;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;


public class GeoCoodinateTests {

	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void testInvalidLat() {
		Latitude.valueOf(300);
	}
	
	@Test(expectedExceptions = {IllegalArgumentException.class})
	public void testInvalidLong() {
		Longitude.valueOf(300);
	}
	
	@Test
	public void testEqualPositions() {
		final Position pos1 = Position.from(10, 20);
		final Position pos2 = Position.from(10, 20);
		assertEquals(pos1, pos1);
		assertEquals(pos1, pos2);
	}
	
	@Test
	public void testGeoHash() {
		final Position pos = Position.from(10.1D, 20.1D);
		
		assertEquals(pos.asGeoHash().asPosition(),pos);
	}
	
	@Test
	public void testDefaultPrecision() {
		final GeoHash hash = Position.from(10.1D, 20.1D).asGeoHash();
		
		assertEquals(hash,hash.withMaximumPrecision(GeoHash.DEFAULT_PRECISION));
	}
	
	@Test(expectedExceptions = { IllegalArgumentException.class})
	public void testToSmallPrecision() {
		final GeoHash hash = Position.from(10.1D, 20.1D).asGeoHash();
		hash.withMaximumPrecision(0);
	}
	
	@Test
	public void testGeoHashArea() {
		final GeoHash hash = Position.from(10.1D, 20.1D).asGeoHash();
		
		double lastSize = Double.MIN_VALUE;
		for ( int i = GeoHash.DEFAULT_PRECISION; i > 0; i-- ) {
			double size = hash.withMaximumPrecision(i).getAreaInSquareMeter();
			System.out.println(i + ": " + size );
			assertTrue(lastSize < size,"Less precision leads to greater areas");
			lastSize = size;
		}
		
	}
	
	@Test
	public void testUnknown() {
		final Position pos1 = Position.unknownPosition();
		final Position pos2 = Position.from(Latitude.unknownLatitude(), Longitude.unknownLongitude());
	
		assertEquals(pos1, pos2);
		assertEquals(pos1, pos1);
		assertEquals(pos2, pos2);
	}

	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testValueOf() {
		GeoHash.valueOf("    ");
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testValueOf2() {
		GeoHash.valueOf("foo");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testValueOf3() {
		GeoHash.valueOf(null);
	}

	@Test
	public void testAdjacent() {
		final GeoHash hash = GeoHash.valueOf("DQCJQCPG");
		
		assertEquals(hash.calculateTopLeftAdjacentHash(),GeoHash.valueOf("dqcjqcps"));
		assertEquals(hash.calculateTopAdjacentHash(),GeoHash.valueOf("dqcjqcpu"));
		assertEquals(hash.calculateTopRightAdjacentHash(),GeoHash.valueOf("dqcjr10h"));
		
		assertEquals(hash.calculateLeftAdjacentHash(),GeoHash.valueOf("dqcjqcpe"));
		assertEquals(hash.calculateRightAdjacentHash(),GeoHash.valueOf("dqcjr105"));

		assertEquals(hash.calculateBottomLeftAdjacentHash(),GeoHash.valueOf("dqcjqcpd"));
		assertEquals(hash.calculateBottomAdjacentHash(),GeoHash.valueOf("dqcjqcpf"));
		assertEquals(hash.calculateBottomRightAdjacentHash(),GeoHash.valueOf("dqcjr104"));
	}
	
	
	@Test
	public void testRectangle() {
		final GeoHash hash = Position.from(0.0D, 0.0D).asGeoHash(4);
		GeoHashRectangle rectangle = hash.getRectangle();
		
		assertTrue(rectangle.getTopLeft().isLeftOf( rectangle.getCenter() ), rectangle.getTopLeft() + " should be left of " + rectangle.getCenter() );
		assertTrue(rectangle.getTopLeft().isTopOf( rectangle.getCenter() ), rectangle.getTopLeft() + " should be greater than " + rectangle.getCenter() );
		
		assertTrue(rectangle.getTopRight().isOnSameLatitudeAs( rectangle.getTopLeft() ), rectangle.getTopRight() + " shoid lbe on same latitude as " + rectangle.getTopLeft() );
		assertTrue(rectangle.getTopRight().isRightOf( rectangle.getTopLeft() ) );
		
		assertTrue(rectangle.getTopRight().isRightOf( rectangle.getCenter() ),  rectangle.getTopRight() + " should be right of " + rectangle.getCenter() );
		assertTrue(rectangle.getTopRight().isTopOf( rectangle.getCenter() ) );
		
		assertTrue(rectangle.getBottomLeft().isLeftOf( rectangle.getCenter() ) );
		assertTrue(rectangle.getBottomLeft().isBelowOf( rectangle.getCenter() ), rectangle.getBottomLeft() + " shoild be below " + rectangle.getCenter() );
		
		assertTrue(rectangle.getBottomRight().isOnSameLatitudeAs( rectangle.getBottomLeft() ) );
		assertTrue(rectangle.getBottomRight().isRightOf( rectangle.getBottomLeft() ) );
		
		assertTrue(rectangle.getBottomRight().isRightOf( rectangle.getCenter() ),  rectangle.getBottomRight() + " should be right of " + rectangle.getCenter() );
		assertTrue(rectangle.getBottomRight().isBelowOf( rectangle.getCenter() ) );
		
		
	
	}
	
	
}
