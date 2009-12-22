package org.thiesen.geo.common.unit;

public final class Miles extends Number {

	private static final long serialVersionUID = -6707418659926241241L;
	private final Double _value;

	private Miles( final Double value ) {
		_value = value;
	}
	
	public static Miles valueOf( final Double value ) {
		return new Miles( value );
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
	
	public Kilometer asKilometer() {
		return Kilometer.valueOf( _value.doubleValue() * 1.60934D );
	}

	public Feet asFeet() {
		return Feet.valueOf( _value.doubleValue() * 5280D );
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
		Miles other = (Miles) obj;
		if (_value == null) {
			if (other._value != null)
				return false;
		} else if (!_value.equals(other._value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Miles [_value=" + _value + "]";
	}
	
	public static Miles zero() {
		return new Miles( 0.0D );
	}
	
}
