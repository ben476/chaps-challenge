package nz.ac.vuw.ecs.swen225.gp6.domain.Tiles;

import nz.ac.vuw.ecs.swen225.gp6.domain.TileAnatomy.*;

/**
 * A class representing an empty floor tile, which both hero and enemy can move onto.
 */
public class Floor extends AbstractTile{
    /**
     * Create a floor tile
     * @param info tile information
     */
    public Floor (TileInfo info){super(info);}

    @Override public TileType type(){ return TileType.Floor;}
}
