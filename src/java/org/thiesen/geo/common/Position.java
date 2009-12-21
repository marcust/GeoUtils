package org.thiesen.geo.common;

public final class Position {

	private static final Position UNKNOWN_POSITION = Position.from(Latitude.unknownLatitude(), Longitude.unknownLongitude());
	private final Latitude _latitude;
	private final Longitude _longitude;
	
	private Position( final Latitude latitude, final Longitude longitude ) {
		_latitude = latitude;
		_longitude = longitude;
	}

	public GeoHash asGeoHash() {
		return GeoHash.encodeWithDefaultPrecision(this);
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
	
	
	
}
