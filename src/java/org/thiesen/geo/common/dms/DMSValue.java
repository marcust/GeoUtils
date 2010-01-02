package org.thiesen.geo.common.dms;

import org.thiesen.geo.common.GeoCoordinateValue;
import org.thiesen.geo.common.Latitude;
import org.thiesen.geo.common.Longitude;

public class DMSValue {

	private static final double SECONDS_PER_MINUTE = 60.0D;
	private static final double SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60.0D;

	private final Long _degree;
	private final Long _minutes;
	private final Double _seconds;
	private final Orientation _orientation;

	private DMSValue(Long degree, Long minutes, Double seconds, Orientation orientation ) {
		super();
		_degree = degree;
		_minutes = minutes;
		_seconds = seconds;
		_orientation = orientation;
	}

	public static DMSValue valueOf( final long degree, final long minutes, final double seconds, final Orientation orientation ) {
		return new DMSValue( Long.valueOf( Math.abs( degree ) ),
							 Long.valueOf( Math.abs( minutes ) ),
							 Double.valueOf( Math.abs( seconds ) ),
							 orientation );
	}

	public GeoCoordinateValue toGeoCoordinateValue() {
		switch ( _orientation ) {
		case EAST: 
		case WEST: return toLongitude();
		case NORTH:
		case SOUTH: return toLatitude();
		}
		throw new RuntimeException( "Unmapped orientation " + _orientation );
	}

	private GeoCoordinateValue toLatitude() {
		return Latitude.valueOf(doubleValue());
	}

	private GeoCoordinateValue toLongitude() {
		return Longitude.valueOf(doubleValue());
	}

	public double doubleValue() {
		return _orientation.getFactor() * 
		(_degree.longValue() + 
				(_minutes.doubleValue() / SECONDS_PER_MINUTE ) + 
				(_seconds.doubleValue() / SECONDS_PER_HOUR ) );
	}

	public Double toDouble() {
		return Double.valueOf(doubleValue());
	}

	public static DMSValue valueOf(Latitude latitude) {
		final double value = latitude.doubleValue();

		return valueOf( value, Orientation.fromLatitude( value ) );
	}

	public static DMSValue valueOf(Longitude longitude) {
		final double value = longitude.doubleValue();

		return valueOf( value, Orientation.fromLongitude( value ) );
	}

	private static DMSValue valueOf(double value, Orientation orientation) {
		final long degree = (long)value;

		final double degreeRest = value - degree;

		final double degreeRestValue = degreeRest * SECONDS_PER_MINUTE;

		final long minutes = (long)degreeRestValue;
		final double minutesRest = degreeRestValue - minutes;

		final double seconds = minutesRest * SECONDS_PER_MINUTE;

		return valueOf( degree, minutes, seconds, orientation);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_degree == null) ? 0 : _degree.hashCode());
		result = prime * result
				+ ((_minutes == null) ? 0 : _minutes.hashCode());
		result = prime * result
				+ ((_orientation == null) ? 0 : _orientation.hashCode());
		result = prime * result
				+ ((_seconds == null) ? 0 : _seconds.hashCode());
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
		DMSValue other = (DMSValue) obj;
		if (_degree == null) {
			if (other._degree != null)
				return false;
		} else if (!_degree.equals(other._degree))
			return false;
		if (_minutes == null) {
			if (other._minutes != null)
				return false;
		} else if (!_minutes.equals(other._minutes))
			return false;
		if (_orientation == null) {
			if (other._orientation != null)
				return false;
		} else if (!_orientation.equals(other._orientation))
			return false;
		if (_seconds == null) {
			if (other._seconds != null)
				return false;
		} else if (!_seconds.equals(other._seconds))
			return false;
		return true;
	}

	public Long getDegree() {
		return _degree;
	}

	public Long getMinutes() {
		return _minutes;
	}

	public Double getSeconds() {
		return _seconds;
	}

	public Orientation getOrientation() {
		return _orientation;
	}
	
	@Override
	public String toString() {
		return _degree + "° " + _minutes + "' " + _seconds + "\" " + _orientation.getDisplayChar(); 
	}

}
