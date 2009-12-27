package org.thiesen.geo.common;

import org.thiesen.geo.common.unit.Kilometer;

public final class Position {

	private static final Position UNKNOWN_POSITION = Position.from(Latitude.unknownLatitude(), Longitude.unknownLongitude());
	private final Latitude _latitude;
	private final Longitude _longitude;

	private Position( final Latitude latitude, final Longitude longitude ) {
		_latitude = latitude;
		_longitude = longitude;
	}

	public GeoHash asGeoHash() {
		return !isUnknown() ? GeoHash.encodeWithDefaultPrecision(this) : null;
	}
	
	public GeoHash asGeoHash(final int precision) {
		return !isUnknown() ? GeoHash.encodeWithPrecision(this, precision) : null;
	}

	public Latitude getLatitude() {
		return _latitude;
	}

	public Longitude getLongitude() {
		return _longitude;
	}

	public static Position from(double latitude, double longitude) {
		return new Position(Latitude.valueOf(latitude), Longitude.valueOf(longitude));
	}


	public static Position from(Latitude latitude,
			Longitude longitude) {
		return new Position(latitude,longitude);
	}
	
	
	public static Position fromE6(long latitudeE6,
			long longitudeE6) {
		return from( latitudeE6 / 1E6, longitudeE6 / 1E6 );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((_latitude == null) ? 0 : _latitude.hashCode());
		result = prime * result
		+ ((_longitude == null) ? 0 : _longitude.hashCode());
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
		Position other = (Position) obj;
		if (_latitude == null) {
			if (other._latitude != null)
				return false;
		} else if (!_latitude.equals(other._latitude))
			return false;
		if (_longitude == null) {
			if (other._longitude != null)
				return false;
		} else if (!_longitude.equals(other._longitude))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Position [_latitude=" + _latitude + ", _longitude="
		+ _longitude + "]";
	}

	public static Position unknownPosition() {
		return UNKNOWN_POSITION;
	}

	public boolean isUnknown() {
		return this == UNKNOWN_POSITION || this.equals(UNKNOWN_POSITION);
	}

	/* This method is cited from Spatial Lucen LatLng class
	 * by the Apache Software Foundation
	 */
	public Kilometer calculateDistanceTo( Position other ) {
		double lat1 = getLatitude().doubleValue(), lng1 = getLongitude().doubleValue();
		double lat2 = other.getLatitude().doubleValue(), lng2 = other.getLongitude().doubleValue();

		// Check for same position
		if (lat1 == lat2 && lng1 == lng2) {
			return Kilometer.zero();
		}

		// Get the m_dLongitude difference. Don't need to worry about
		// crossing 180 since cos(x) = cos(-x)
		double dLon = lng2 - lng1;

		double a = radians(90.0 - lat1);
		double c = radians(90.0 - lat2);
		double cosB = (Math.cos(a) * Math.cos(c))
		+ (Math.sin(a) * Math.sin(c) * Math.cos(radians(dLon)));

		final double radius = 6378.160187;

		// Find angle subtended (with some bounds checking) in radians and
		// multiply by earth radius to find the arc distance
		if (cosB < -1.0)
			return Kilometer.valueOf( Math.PI * radius );
		else if (cosB >= 1.0)
			return Kilometer.zero();
		else
			return Kilometer.valueOf( Math.acos(cosB) * radius );
	}

	private double radians(double a) {
		return a * 0.01745329251994;
	}
	
	public static Position origin() {
		return Position.from(0.0, 0.0);
	}

}
