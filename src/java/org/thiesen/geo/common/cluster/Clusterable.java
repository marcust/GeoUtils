package org.thiesen.geo.common.cluster;
import org.thiesen.geo.common.Position;


public class Clusterable<T extends HasPosition> {
    
    private static long ID_COUNTER;
    
    private final Long _id;
    private final T _item;
    private Long _clusterId;
    
    private Clusterable ( T item ) {
        super();
        _id = Long.valueOf( ID_COUNTER++ );
        _item = item;
    }
    
    public static <V extends HasPosition> Clusterable<V> create( final V item ) {
        return new Clusterable<V>( item );
    }

    public Long getClusterId() {
        return _clusterId;
    }

    public void setClusterId( Long clusterId ) {
        _clusterId = clusterId;
    }

    public T getItem() {
        return _item;
    }
    
    public Position getPosition() {
        return _item.getPosition();
    }

    public Long getId() {
        return _id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_id == null) ? 0 : _id.hashCode());
        return result;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Clusterable<T> other = (Clusterable<T>) obj;
        if (_id == null) {
            if (other._id != null)
                return false;
        } else if (!_id.equals( other._id ))
            return false;
        return true;
    }
    
    public boolean isCloseTo( final Clusterable<T> other ) {
        return this.getPosition().calculateDistanceTo( other.getPosition() ).asMeter().doubleValue() < 20D;
    }

    public boolean isUnclustered() {
        return _clusterId == null;
    }
    
}
