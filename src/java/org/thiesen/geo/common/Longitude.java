package org.thiesen.geo.common;

import org.thiesen.geo.common.dms.DMSValue;

public class Longitude extends GeoCoordinateValue {

	private static final long serialVersionUID = 5631264172357672045L;
	private static final Longitude UNKNOWN_LONGITUDE = new Longitude();

	Longitude(double value) {
		super(value);
		if (value < -180.0D || value > 180.0D ) {
			throw new IllegalArgumentException("Latitude is only defined between -90° and +90°");
		}
	}
	
	private Longitude() {
		super(Double.NaN);
	}

	public static Longitude valueOf(double value) {
		return new Longitude(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
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
		Longitude other = (Longitude) obj;
		if (getValue() == null) {
			if (other.getValue() != null)
				return false;
		} else if (!getValue().equals(other.getValue()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Longitude:" + getValue();
	}

	public static Longitude unknownLongitude() {
		return UNKNOWN_LONGITUDE;
	}

	public boolean isUnknown() {
		return UNKNOWN_LONGITUDE == this;
	}
	
	@Override
	public DMSValue toDMS() {
		return DMSValue.valueOf(this);
	}
	
}
