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
 */

package org.thiesen.geo.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public final class GeoHash {
	
	private final static char[] BASE_32 = {'0','1','2','3','4','5','6','7','8','9',
		'b','c','d','e','f','g','h','j','k','m',
		'n','p','q','r','s','t','u','v','w','x',
		'y','z'} ;

	
	private final static ImmutableMap<Character, Integer> _decodemap;
	static {
		Builder<Character, Integer> builder = ImmutableMap.<Character,Integer>builder();
		for (int i = 0; i < BASE_32.length; i++ ){
			builder.put(BASE_32[i], i);
		}
		_decodemap = builder.build();
	}

	private final static int[] _bits = {16, 8, 4, 2, 1};
	
	static final int DEFAULT_PRECISION = 15;

	private static final double EARTH_SURFACE_AREA_KM2 = 510072000.0D;

	private final String _hash;

	private GeoHash(final Latitude latitude, final Longitude longitude, final int precision) {
		this( encode(latitude.doubleValue(),longitude.doubleValue(), precision) );
	}

	private GeoHash(final String hash) {
		_hash = hash;
	}
	
	public static GeoHash encodeWithPrecision(final Latitude latitude, final Longitude longitude, final int precision ) {
		return new GeoHash(latitude,longitude,precision);
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
                          if(latitude > mid){
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

	private static Position decode(String geohash) {
		final double[] ge = decode_exactly(geohash);
		double lat, lon;
		final double lat_err, lon_err;
		lat = ge[0];
		lon = ge[1];
		lat_err = ge[2];
		lon_err = ge[3];

		final double lat_precision = Math.max(1, Math.round(- Math.log10(lat_err))) - 1;
		final double lon_precision = Math.max(1, Math.round(- Math.log10(lon_err))) - 1;

		lat = getPrecision(lat, lat_precision);
		lon = getPrecision(lon, lon_precision);

		return Position.from( lat, lon );
	}

	private static double[] decode_exactly(final String geohash){
        double[] lat_interval = {-90.0 , 90.0};
        double[] lon_interval = {-180.0, 180.0};

        double lat_err =  90.0;
        double lon_err = 180.0;
        boolean is_even = true;
        int sz = geohash.length();
        int bsz = _bits.length;
        double latitude, longitude;
        for (int i = 0; i < sz; i++){

                int cd = _decodemap.get(geohash.charAt(i));

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
        latitude  = (lat_interval[0] + lat_interval[1]) / 2;
        longitude = (lon_interval[0] + lon_interval[1]) / 2;

        return new double []{latitude, longitude, lat_err, lon_err};
	}

	private static double getPrecision(double x, double precision) {
		double base = Math.pow(10,- precision);
		double diff = x % base;
		return x - diff;
	}

	static GeoHash encodeWithDefaultPrecision(final Position position) {
		return new GeoHash(position.getLatitude(), position.getLongitude(), DEFAULT_PRECISION);
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

}
