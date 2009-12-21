package org.thiesen.geo.common;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


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
	
	
}
