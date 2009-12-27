package org.thiesen.geo.common;

public class GeoHashRectangle extends GeoRectangle {

	private final Position _center;

	protected GeoHashRectangle(Position topLeft, Position topRight,
			Position bottomLeft, Position bottomRight, Position center) {
		super(topLeft, topRight, bottomLeft, bottomRight);
		_center = center;
	}

	static GeoHashRectangle valueOf(double[] latitude,
			double[] longitude) {
				return new GeoHashRectangle( 
						Position.from(latitude[1], longitude[0]),
						Position.from(latitude[1], longitude[1]),
						Position.from(latitude[0], longitude[0]),
						Position.from(latitude[0], longitude[1]),
						Position.from((latitude[0] + latitude[1]) / 2.0D,
							         (longitude[0] + longitude[1]) / 2.0D ) 
							      );

	}

	public Position getCenter() {
		return _center;
	}

	


}
