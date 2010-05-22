package org.thiesen.geo.common.cluster;
import java.util.Set;

import org.thiesen.geo.common.Latitude;
import org.thiesen.geo.common.Longitude;
import org.thiesen.geo.common.Position;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;


public class ClusterBuilder<T extends HasPosition> {

    private final Function<Clusterable<T>, Position> GET_POSITION = new Function<Clusterable<T>, Position>() {

        @Override
        public Position apply( Clusterable<T> event ) {
            return event.getPosition();
        }
    };
    private static final Function<Position, Latitude> GET_LATITUDE = new Function<Position, Latitude>() {

        @Override
        public Latitude apply( Position pos ) {
            return pos.getLatitude();
        }
    };


    private static final Function<Position, Longitude> GET_LONGITUDE = new Function<Position, Longitude>() {

        @Override
        public Longitude apply( Position pos ) {
            return pos.getLongitude();
        }
    };
    private final Function<Clusterable<T>, T> TO_BEAN_FUNCTION = new Function<Clusterable<T>, T>() {

        @Override
        public T apply( Clusterable<T> arg0 ) {
            return arg0.getItem();
        }

    };

    private static long ID_COUNTER;

    private final Long _id;
    private Set<Clusterable<T>> _items;

    public ClusterBuilder ( Iterable<Clusterable<T>> items ) {
        _id = Long.valueOf( ID_COUNTER++ );
        _items = Sets.newHashSet(items );
        for ( final Clusterable<T> item : items ) {
            item.setClusterId( _id );
        }
    }

    public static <V extends HasPosition> ClusterBuilder<V> create( final Iterable<Clusterable<V>> items ) {
        return new ClusterBuilder<V>( items );
    }

    public Position computeMeanPosition() {
        final Iterable<Position> positions = Iterables.transform(_items, GET_POSITION );

        final Number meanLat = mean( Iterables.transform( positions, GET_LATITUDE ) );
        final Number meanLong = mean( Iterables.transform( positions, GET_LONGITUDE ) );

        return Position.from( meanLat.doubleValue(), meanLong.doubleValue() );

    }

    private Number mean( Iterable<? extends Number> numbers ) {
        double value = 0.0;
        long count = 0;
        for ( final Number x : numbers ) {
            value += x.doubleValue();
            count++;
        }

        return value / count;
    }

    public int getSize() {
        return _items.size();
    }

    public boolean merge( final Iterable<Clusterable<T>> items ) {
        boolean added = false;
        for ( final Clusterable<T> item : items ) {
            for ( final Clusterable<T> ownItem : items ) {
                if ( item.isCloseTo( ownItem ) ) {
                    add( item );
                    added = true;
                }
            }
        }

        return added;
    }

    private void add( Clusterable<T> item ) {
        _items.add( item );
        item.setClusterId( _id );
    }

    public Iterable<T> getItems() {
        return Iterables.transform( _items, TO_BEAN_FUNCTION );
    }

}
