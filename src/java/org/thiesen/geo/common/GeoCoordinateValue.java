package org.thiesen.geo.common;

import org.thiesen.geo.common.dms.DMSValue;

public abstract class GeoCoordinateValue extends Number {

	private static final long serialVersionUID = -77606516065835024L;

	private final Double _value;
	
	GeoCoordinateValue(double value) {
		_value = Double.valueOf(value);
	}

	public Double getValue() {
		return _value;
	}
	
	public int getValueE6() {
		return (int)Math.round(_value.doubleValue() * 1E6);
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
	
	public String stringValue() {
		return String.valueOf(getValue());
	}
	
	protected boolean valueBetween( final Number min, final Number max ) {
        final double val = _value.doubleValue();
        
        return val >= min.doubleValue() && val <= max.doubleValue();
    }

	public abstract DMSValue toDMS();
	
    
}
