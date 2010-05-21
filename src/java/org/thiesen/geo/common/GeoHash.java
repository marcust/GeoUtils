/**
 * The algorithms used herein are heavily based on GeoHashUtils from Lucene,
 * which are under the following License terms:
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Adjacent Computation is done using algorithms by Dave Troy, translated
 * from JavaScript http://github.com/davetroy/geohash-js/blob/master/geohash-demo.js
 * 
 */

package org.thiesen.geo.common;

import java.io.Serializable;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.ImmutableMap.Builder;

public final class GeoHash implements Serializable {

    private static final long serialVersionUID = 1600913403150417804L;

    private static enum Orientation { RIGHT, LEFT, TOP, BOTTOM }
	private static enum EvenOdd { EVEN, ODD }

	private final static ImmutableMap<Orientation, ImmutableMap<EvenOdd, String>> NEIGHBORS = ImmutableMap.of(
			Orientation.RIGHT, ImmutableMap.<EvenOdd,String>of( 
					EvenOdd.EVEN, "bc01fg45238967deuvhjyznpkmstqrwx",
					EvenOdd.ODD, "p0r21436x8zb9dcf5h7kjnmqesgutwvy"
			),
			Orientation.LEFT, ImmutableMap.<EvenOdd,String>of(
					EvenOdd.EVEN, "238967debc01fg45kmstqrwxuvhjyznp",
					EvenOdd.ODD, "14365h7k9dcfesgujnmqp0r2twvyx8zb"
			),
			Orientation.TOP, ImmutableMap.<EvenOdd,String>of(
					EvenOdd.EVEN, "p0r21436x8zb9dcf5h7kjnmqesgutwvy",
					EvenOdd.ODD, "bc01fg45238967deuvhjyznpkmstqrwx"
			),
			Orientation.BOTTOM, ImmutableMap.<EvenOdd,String>of(
					EvenOdd.EVEN, "14365h7k9dcfesgujnmqp0r2twvyx8zb",
					EvenOdd.ODD, "238967debc01fg45kmstqrwxuvhjyznp"
			)
	);


	private final static ImmutableMap<Orientation, ImmutableMap<EvenOdd, String>> BORDERS = ImmutableMap.of(
			Orientation.RIGHT, ImmutableMap.<EvenOdd,String>of( 
					EvenOdd.EVEN, "bcfguvyz",
					EvenOdd.ODD, "prxz"
			),
			Orientation.LEFT, ImmutableMap.<EvenOdd,String>of(
					EvenOdd.EVEN, "0145hjnp",
					EvenOdd.ODD, "028b"
			),
			Orientation.TOP, ImmutableMap.<EvenOdd,String>of(
					EvenOdd.EVEN, "prxz",
					EvenOdd.ODD, "bcfguvyz"
			),
			Orientation.BOTTOM, ImmutableMap.<EvenOdd,String>of(
					EvenOdd.EVEN, "028b",
					EvenOdd.ODD, "0145hjnp"
			)
	);

	public static class DecodeResult {

		private final GeoHashRectangle _rectangle;
		private final double _latitude;
		private final double _longitude;
		private final double _latitudeError;
		private final double _longitudeError;

		public DecodeResult(GeoHashRectangle rectangle, double latitude,
				double longitude, double latitudeError, double longitudeError) {
			_rectangle = rectangle;
			_latitude = latitude;
			_longitude = longitude;
			_latitudeError = latitudeError;
			_longitudeError = longitudeError;
		}

		public GeoHashRectangle getRectangle() {
			return _rectangle;
		}

		public double getLatitude() {
			return _latitude;
		}

		public double getLongitude() {
			return _longitude;
		}

		public double getLatitudeError() {
			return _latitudeError;
		}

		public double getLongitudeError() {
			return _longitudeError;
		}
	}

	private final static char[] BASE_32 = {'0','1','2','3','4','5','6','7','8','9',
		'b','c','d','e','f','g','h','j','k','m',
		'n','p','q','r','s','t','u','v','w','x',
		'y','z'} ;

	private static final Pattern VALID_CHAR_PATTERN = Pattern.compile("[" + join(BASE_32) + "]+");

	private final static ImmutableMap<Character, Integer> DECODEMAP;
	static {
		Builder<Character, Integer> builder = ImmutableMap.<Character,Integer>builder();
		for (int i = 0; i < BASE_32.length; i++ ){
			builder.put(Character.valueOf( BASE_32[i] ), Integer.valueOf( i ) );
		}
		DECODEMAP = builder.build();
	}

	private final static int[] _bits = {16, 8, 4, 2, 1};

	static final int DEFAULT_PRECISION = 15;

	private static final double EARTH_SURFACE_AREA_KM2 = 510072000.0D;

	private final String _hash;

	private GeoHash(final Latitude latitude, final Longitude longitude, final int precision) {
		this( encode(latitude.doubleValue(),longitude.doubleValue(), precision) );
	}

	private static String join(char[] array) {
		final StringBuilder retval = new StringBuilder(array.length);
		for (final char c : array ) {
			retval.append(c);
		}
		return retval.toString();
	}

	private GeoHash(final String inHash) {
		if ( inHash == null || "".equals(inHash.trim())) {
			throw new IllegalArgumentException("Hash must not be blank");
		}
		final String hash = inHash.trim().toLowerCase();
		if ( !VALID_CHAR_PATTERN.matcher(hash).matches() ) {
			throw new IllegalArgumentException("Hash must contain valid chars only: " + join(BASE_32));
		}


		_hash = hash.toLowerCase();
	}

	public static GeoHash encodeWithPrecision(final Latitude latitude, final Longitude longitude, final int precision ) {
		return new GeoHash(latitude,longitude,precision);
	}

	public static GeoHash encode(final Latitude latitude, final Longitude longitude ) {
		return new GeoHash(latitude,longitude,DEFAULT_PRECISION);
	}

	public static GeoHash encode(final double latitude, final double longitude ) {
		return encode(Latitude.valueOf(latitude), Longitude.valueOf(longitude));
	}

	private static String encode(double latitude, double longitude, final int precision){
		double[] lat_interval = {-90.0 ,  90.0};
		double[] lon_interval = {-180.0, 180.0};

		StringBuilder geohash = new StringBuilder();
		boolean is_even = true;
		int bit = 0, ch = 0;

		while(geohash.length() < precision){
			double mid = 0.0;
			if(is_even){
				mid = (lon_interval[0] + lon_interval[1]) / 2;
				if (longitude > mid){
					ch |= _bits[bit];
					lon_interval[0] = mid;
				} else {
					lon_interval[1] = mid;
				}

			} else {
				mid = (lat_interval[0] + lat_interval[1]) / 2;
				if ( latitude > mid ) {
					ch |= _bits[bit];
					lat_interval[0] = mid;
				} else {
					lat_interval[1] = mid;
				}
			}

			is_even = is_even ? false : true;

			if (bit  < 4){
				bit ++;
			} else {
				geohash.append(BASE_32[ch]);
				bit =0;
				ch = 0;
			}
		}

		return geohash.toString();
	}

	public GeoHashRectangle getRectangle() {
		return decodeExactly(_hash).getRectangle();
	}

	private static Position decode(String geohash) {
		final DecodeResult decodeResult = decodeExactly(geohash);

		final double latitudePrecision = Math.max(1, Math.round(- Math.log10(decodeResult.getLatitudeError()))) - 1;
		final double longitudePrecision = Math.max(1, Math.round(- Math.log10(decodeResult.getLongitudeError()))) - 1;
		return Position.from( getPrecision( decodeResult.getLatitude(), latitudePrecision), getPrecision(decodeResult.getLongitude(), longitudePrecision) );
	}

	private static DecodeResult decodeExactly(final String geohash){
		double[] lat_interval = {-90.0 , 90.0};
		double[] lon_interval = {-180.0, 180.0};

		double lat_err =  90.0;
		double lon_err = 180.0;
		boolean is_even = true;
		int bsz = _bits.length;
		double latitude, longitude;
		for (int i = 0; i < geohash.length(); i++){

			int cd = DECODEMAP.get(Character.valueOf(geohash.charAt(i))).intValue();

			for (int z = 0; z< bsz; z++){
				int mask = _bits[z];
				if (is_even){
					lon_err /= 2;
					if ((cd & mask) != 0){
						lon_interval[0] = (lon_interval[0]+lon_interval[1])/2;
					} else {
						lon_interval[1] = (lon_interval[0]+lon_interval[1])/2;
					}

				} else {
					lat_err /=2;

					if ( (cd & mask) != 0){
						lat_interval[0] = (lat_interval[0]+lat_interval[1])/2;
					} else {
						lat_interval[1] = (lat_interval[0]+lat_interval[1])/2;
					}
				}
				is_even = is_even ? false : true;
			}

		}

		final GeoHashRectangle rectangle = GeoHashRectangle.valueOf(lat_interval, lon_interval);

		latitude  = (lat_interval[0] + lat_interval[1]) / 2;
		longitude = (lon_interval[0] + lon_interval[1]) / 2;

		return new DecodeResult( rectangle, latitude, longitude, lat_err, lon_err );
	}

	private static double getPrecision(double x, double precision) {
		final double base = Math.pow(10,- precision);
		final double diff = x % base;
		return x - diff;
	}

	static GeoHash encodeWithDefaultPrecision(final Position position) {
		return new GeoHash(position.getLatitude(), position.getLongitude(), DEFAULT_PRECISION);
	}

	static GeoHash encodeWithPrecision(final Position position, final int precision) {
		return new GeoHash(position.getLatitude(), position.getLongitude(), precision);
	}

	public Position asPosition() {
		return decode(_hash);
	}

	public GeoHash withMaximumPrecision( int precision ) {
		if ( precision <= 0 ) {
			throw new IllegalArgumentException("Precision must be greater than zero" );
		}
		if ( precision < _hash.length() ) {
			return new GeoHash(_hash.substring(0, precision));
		}
		return this;
	}

	public double getAreaInSquareMeter() {
		return EARTH_SURFACE_AREA_KM2 * 1000.0D * 1000.0D / Math.pow( 32.0D, _hash.length() );
	}

	public String getValue() {
		return _hash;
	}

	public GeoHash calculateTopAdjacentHash() {
		return new GeoHash(calculateAdjacent(getValue(),Orientation.TOP));
	}

	public GeoHash calculateBottomAdjacentHash() {
		return new GeoHash(calculateAdjacent(getValue(),Orientation.BOTTOM));
	}

	public GeoHash calculateLeftAdjacentHash() {
		return new GeoHash(calculateAdjacent(getValue(),Orientation.LEFT));
	}

	public GeoHash calculateRightAdjacentHash() {
		return new GeoHash(calculateAdjacent(getValue(),Orientation.RIGHT));
	}

	public GeoHash calculateTopLeftAdjacentHash() {
		return new GeoHash(calculateAdjacent( calculateTopAdjacentHash().getValue(), Orientation.LEFT ) );
	}

	public GeoHash calculateTopRightAdjacentHash() {
		return new GeoHash(calculateAdjacent( calculateTopAdjacentHash().getValue(), Orientation.RIGHT ) );
	}

	public GeoHash calculateBottomLeftAdjacentHash() {
		return new GeoHash(calculateAdjacent( calculateBottomAdjacentHash().getValue(), Orientation.LEFT ) );
	}

	public GeoHash calculateBottomRightAdjacentHash() {
		return new GeoHash(calculateAdjacent( calculateBottomAdjacentHash().getValue(), Orientation.RIGHT ) );
	}

	public AdjacentGeoHashes getAdjacentHashes() {
		return AdjacentGeoHashes.valueOf(
				calculateTopAdjacentHash(),
				calculateLeftAdjacentHash(),
				calculateRightAdjacentHash(), 
				calculateBottomAdjacentHash(),
				calculateTopLeftAdjacentHash(),
				calculateTopRightAdjacentHash(),
				calculateBottomLeftAdjacentHash(),
				calculateBottomRightAdjacentHash());
	}

	public Iterator<GeoHash> adjacentHashesIncludingSelfIterator() {
		return Iterators.forArray(
				calculateTopLeftAdjacentHash(),
				calculateTopAdjacentHash(),
				calculateTopRightAdjacentHash(),

				calculateLeftAdjacentHash(),
				this,
				calculateRightAdjacentHash(),

				calculateBottomLeftAdjacentHash(),
				calculateBottomAdjacentHash(),
				calculateBottomRightAdjacentHash()
		);
	}

	private static String calculateAdjacent(final String srcHash, final Orientation dir) {
		final char lastChr = srcHash.charAt(srcHash.length()-1);
		final EvenOdd type = (srcHash.length() % 2) != 0 ? EvenOdd.ODD : EvenOdd.EVEN;
		String base = srcHash.substring(0,srcHash.length()-1);
		if (BORDERS.get(dir).get(type).indexOf(lastChr)!=-1)
			base = calculateAdjacent(base, dir);
		return base + BASE_32[NEIGHBORS.get(dir).get(type).indexOf(lastChr)];
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_hash == null) ? 0 : _hash.hashCode());
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
		GeoHash other = (GeoHash) obj;
		if (_hash == null) {
			if (other._hash != null)
				return false;
		} else if (!_hash.equals(other._hash))
			return false;
		return true;
	}

	public static GeoHash valueOf(final String string) {
		return new GeoHash(string);
	}

	@Override
	public String toString() {
		return "GeoHash [" + _hash + "]";
	}



}
