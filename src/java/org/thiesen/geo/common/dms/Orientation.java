package org.thiesen.geo.common.dms;

public enum Orientation {

	NORTH(1D, Character.valueOf( 'N' ) ),
	EAST(1D,  Character.valueOf('E') ),
	SOUTH(-1D,  Character.valueOf( 'S' ) ),
	WEST(-1D,  Character.valueOf('W' ) );
	
	private final double _factor;
	private final Character _displayChar;
	
	private Orientation( final double factor, final Character displayChar ) {
		_factor = factor;
		_displayChar = displayChar;
	}

	public double getFactor() {
		return _factor;
	}

	public static Orientation fromLatitude(double value) {
		return value < 0.0D ? SOUTH : NORTH;
	}
	
	public static Orientation fromLongitude(double value) {
		return value < 0.0D ? WEST : EAST;
	}

	public Character getDisplayChar() {
		return _displayChar;
	}
	
}
