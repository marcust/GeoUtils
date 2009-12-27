package org.thiesen.geo.common.unit;

public final class Feet extends Number {

	private static final long serialVersionUID = -6707418659926241241L;
	private static final Feet ZERO = valueOf( 0.0D );
	private final Double _value;

	private Feet( final Double value ) {
		_value = value;
	}
	
	public static Feet valueOf( final Double value ) {
		return new Feet( value );
	}
	
	public static Feet valueOf( final double value ) {
		return new Feet( Double.valueOf( value ) );
	}
	
	@Override
	public double doubleValue() {
		return _value.doubleValue();
	}

	@Override
	public float floatValue() {
		return _value.floatValue();
	}

	@Override
	public int intValue() {
		return _value.intValue();
	}

	@Override
	public long longValue() {
		return _value.longValue();
	} 
	
	public Miles asMiles() {
		return Miles.valueOf( _value.doubleValue() / 5280D );
	}
	
	public Meter asMeter() {
		return Meter.valueOf( _value.doubleValue() / 3.2808399D );
	}

	@Override
	public String toString() {
		return "Feet [_value=" + _value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_value == null) ? 0 : _value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feet other = (Feet) obj;
		if (_value == null) {
			if (other._value != null)
				return false;
		} else if (!_value.equals(other._value))
			return false;
		return true;
	}
	
	public static Feet zero() {
		return ZERO;
	}

}
