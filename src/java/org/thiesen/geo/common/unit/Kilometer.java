package org.thiesen.geo.common.unit;

public final class Kilometer extends Number {

	private static final long serialVersionUID = -6707418659926241241L;
	private static final Kilometer ZERO = valueOf( 0.0D );
	private final Double _value;

	private Kilometer( final Double value ) {
		_value = value;
	}
	
	public static Kilometer valueOf( final Double value ) {
		return new Kilometer( value );
	}
	
	public static Kilometer valueOf( final double value ) {
		return new Kilometer( Double.valueOf( value ) );
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
	
	public Meter asMeter() {
		return Meter.valueOf(_value.doubleValue() * 1000);
	}
	
	public Miles asMiles() {
		return Miles.valueOf( _value.doubleValue() / 1.60934D );
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
		Kilometer other = (Kilometer) obj;
		if (_value == null) {
			if (other._value != null)
				return false;
		} else if (!_value.equals(other._value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Kilometer [_value=" + _value + "]";
	}

	public static Kilometer zero() {
		return ZERO;
	}
}
