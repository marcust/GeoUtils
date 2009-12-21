package org.thiesen.geo.common;

public class GeoRectangle {

	private final Position _topLeft;
	private final Position _topRight;
	private final Position _bottomLeft;
	private final Position _bottomRight;

	protected GeoRectangle(Position topLeft, Position topRight,
			Position bottomLeft, Position bottomRight) {
		super();
		_topLeft = topLeft;
		_topRight = topRight;
		_bottomLeft = bottomLeft;
		_bottomRight = bottomRight;
	}

	static GeoRectangle valueOf(double[] latitude,
			double[] longitude) {
		return new GeoRectangle( Position.from(latitude[0], longitude[0]),
				Position.from(latitude[1], longitude[0]),
				Position.from(latitude[0], longitude[1]),
				Position.from(latitude[1], longitude[0]) );
	}

	public Position getTopLeft() {
		return _topLeft;
	}

	public Position getTopRight() {
		return _topRight;
	}

	public Position getBottomLeft() {
		return _bottomLeft;
	}

	public Position getBottomRight() {
		return _bottomRight;
	}
	
	
	
}
