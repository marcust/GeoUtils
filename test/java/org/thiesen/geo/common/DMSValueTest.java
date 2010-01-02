package org.thiesen.geo.common;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.thiesen.geo.common.dms.DMSValue;
import org.thiesen.geo.common.dms.Orientation;

public class DMSValueTest {
	
	@Test
	public void testConversion() {
		Assert.assertEquals( DMSValue.valueOf( 38, 53, 55, Orientation.NORTH ).toDouble(), Double.valueOf( 38.89861111111111 ) );
		Assert.assertEquals( DMSValue.valueOf( 77, 2, 16, Orientation.WEST ).toDouble(), Double.valueOf( -77.03777777777778 ) );
	}

	@Test
	public void testConverstionFrom() {
		final Latitude latitude = Latitude.valueOf( 38.89861111111111 );
		DMSValue dms = latitude.toDMS();
		Assert.assertEquals( dms.getDegree(), Long.valueOf( 38 ) );
		Assert.assertEquals( dms.getMinutes(), Long.valueOf( 53 ) );
		Assert.assertEquals( dms.getSeconds(), Double.valueOf( 54.999999999990905 ) );
		Assert.assertEquals( dms.getOrientation(), Orientation.NORTH );
		Assert.assertEquals( dms.toDouble(), latitude.getValue() );
	}
	
}
