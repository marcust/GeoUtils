package org.thiesen.geo.common;

import java.util.Iterator;

import com.google.common.collect.Iterators;

public class AdjacentGeoHashes implements Iterable<GeoHash> {

	private final GeoHash _top;
	private final GeoHash _left;
	private final GeoHash _right;
	private final GeoHash _bottom;
	
	private final GeoHash _topLeft;
	private final GeoHash _topRight;
	
	private final GeoHash _bottomLeft;
	private final GeoHash _bottomRight;
	
	private AdjacentGeoHashes(GeoHash top, GeoHash left, GeoHash right,
			GeoHash bottom, GeoHash topLeft, GeoHash topRight,
			GeoHash bottomLeft, GeoHash bottomRight) {
		super();
		_top = top;
		_left = left;
		_right = right;
		_bottom = bottom;
		_topLeft = topLeft;
		_topRight = topRight;
		_bottomLeft = bottomLeft;
		_bottomRight = bottomRight;
	}
	
	static AdjacentGeoHashes valueOf(GeoHash top, GeoHash left, GeoHash right,
			GeoHash bottom, GeoHash topLeft, GeoHash topRight,
			GeoHash bottomLeft, GeoHash bottomRight) {
		return new AdjacentGeoHashes(top, left, right, bottom, topLeft, topRight, bottomLeft, bottomRight);
	}
	
	public GeoHash getTop() {
		return _top;
	}

	public GeoHash getLeft() {
		return _left;
	}

	public GeoHash getRight() {
		return _right;
	}

	public GeoHash getBottom() {
		return _bottom;
	}

	public GeoHash getTopLeft() {
		return _topLeft;
	}

	public GeoHash getTopRight() {
		return _topRight;
	}

	public GeoHash getBottomLeft() {
		return _bottomLeft;
	}

	public GeoHash getBottomRight() {
		return _bottomRight;
	}

	@Override
	public Iterator<GeoHash> iterator() {
		return Iterators.forArray(
				getTopLeft(),
				getTop(),
				getTopRight(),
				getLeft(),
				getRight(),
				getBottomLeft(),
				getBottom(),
				getBottomRight());
	}
	
	
}
