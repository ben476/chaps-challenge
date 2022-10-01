package nz.ac.vuw.ecs.swen225.gp6.domain.Tiles;

import nz.ac.vuw.ecs.swen225.gp6.domain.TileAnatomy.*;
import nz.ac.vuw.ecs.swen225.gp6.domain.TileGroups.*;

public class OrangeKey extends Key{

    public OrangeKey (TileInfo info){super(info);}

    @Override public TileType type(){ return TileType.OrangeKey;}
    @Override public KeyColor color(){return KeyColor.ORANGE;}
}
