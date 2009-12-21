package org.thiesen.geo.common;


public class Latitude extends GeoCoordinateValue {

	private static final long serialVersionUID = 2177382803107454370L;

	Latitude(double value) {
		super(value);
		if (value < -90.0D || value > 90.0D ) {
			throw new IllegalArgumentException("Latitude is only defined between -90° and +90°");
		}
	}

	public static Latitude valueOf(double i) {
		return new Latitude(i);
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
		Latitude other = (Latitude) obj;
		if (getValue() == null) {
			if (other.getValue() != null)
				return false;
		} else if (!getValue().equals(other.getValue()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Latitude [getValue()=" + getValue() + "]";
	}
	
	

	
}
