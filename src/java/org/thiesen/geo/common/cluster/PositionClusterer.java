package org.thiesen.geo.common.cluster;
import java.util.Iterator;
import java.util.List;

import org.thiesen.geo.common.GeoHash;
import org.thiesen.geo.common.Position;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;



public class PositionClusterer<T extends HasPosition> {

    private class MinSizeFilter implements Predicate<ClusterBuilder<T>> {

        private final int _minSize;

        public MinSizeFilter ( int minSize ) {
            _minSize = minSize;
        }

        @Override
        public boolean apply( ClusterBuilder<T> cluster ) {
            return cluster.getSize() >= _minSize;
        }

    }

    private final Predicate<Clusterable<T>> UNCLUSTERED = new Predicate<Clusterable<T>>() {

        @Override
        public boolean apply( Clusterable<T> arg0 ) {
            return arg0.isUnclustered();
        }
    };

    private final Function<T, Clusterable<T>> TO_CLUSTERABLE = new Function<T, Clusterable<T>>() {

        @Override
        public Clusterable<T> apply( T arg0 ) {
            return Clusterable.create( arg0 );
        }
    };

    private final int maxDistanceDepth;
    private final int clusterHashPrecision;
    private final Iterable<Clusterable<T>> positions;

    private PositionClusterer ( Iterable<T> items, int clusterHashPrecision, int maxDistanceDepth ) {
        this.clusterHashPrecision = clusterHashPrecision;
        this.maxDistanceDepth = maxDistanceDepth;
        this.positions = ImmutableList.<Clusterable<T>>copyOf( Iterables.transform( items, TO_CLUSTERABLE ) ) ;
    }

    public static <T extends HasPosition> PositionClusterer<T> withPositions( Iterable<T> positions,
            int clusterHashPrecision, int maxDistanceDepth ) {
        return new PositionClusterer<T>( positions, clusterHashPrecision, maxDistanceDepth );
    }

    public Iterable<ClusterBuilder<T>> findClusters(int minSize) {
        final Multimap<GeoHash, Clusterable<T>> itemsByGeoHash = HashMultimap.create();

        for ( final Clusterable<T> item : positions ) {
            final Position position = item.getPosition();
            final GeoHash hash = position.asGeoHash( clusterHashPrecision );

            itemsByGeoHash.put( hash, item );
        }

        final List<ClusterBuilder<T>> clusters = Lists.newLinkedList();
        for ( final GeoHash hash : itemsByGeoHash.keySet() ) {
            final Iterable<Clusterable<T>> clusterables = Iterables.filter( itemsByGeoHash.get( hash ), UNCLUSTERED );
            if ( Iterables.isEmpty( clusterables ) ) {
                continue;
            }


            final ClusterBuilder<T> baseCluster = ClusterBuilder.create( itemsByGeoHash.get( hash ) );
            clusters.add( baseCluster );

            addAdjacentPoints( itemsByGeoHash, baseCluster, hash, 0 );
        }

        return filterMinSize( clusters, minSize );

    }

    private Iterable<ClusterBuilder<T>> filterMinSize( List<ClusterBuilder<T>> clusters, int minSize ) {
        return Iterables.filter( clusters, new MinSizeFilter( minSize ) );
    }

    private void addAdjacentPoints( Multimap<GeoHash, Clusterable<T>> itemsByGeoHash,
            ClusterBuilder<T> baseCluster, GeoHash hash, int distance ) {
        if ( distance == maxDistanceDepth ) {
            return;
        }

        final Iterator<GeoHash> iterator = hash.adjacentHashesIncludingSelfIterator();
        while ( iterator.hasNext() ) {
            final GeoHash currentHash = iterator.next().withMaximumPrecision( clusterHashPrecision );
            final Iterable<Clusterable<T>> clusterables = Iterables.filter( itemsByGeoHash.get( hash ), UNCLUSTERED );

            if ( baseCluster.merge( clusterables ) ) {
                addAdjacentPoints( itemsByGeoHash, baseCluster, currentHash, 0 );
            } else {
                addAdjacentPoints( itemsByGeoHash, baseCluster, currentHash, distance + 1 );
            }
        }
    }


}
