package nz.ac.vuw.ecs.swen225.gp6.renderer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import javax.swing.JPanel;
import java.awt.Graphics;
import nz.ac.vuw.ecs.swen225.gp6.domain.DomainAccess.DomainController;
import nz.ac.vuw.ecs.swen225.gp6.domain.TileAnatomy.*;
import nz.ac.vuw.ecs.swen225.gp6.domain.Tiles.Hero;
import nz.ac.vuw.ecs.swen225.gp6.domain.Utility.Direction;

/**
 * makes a jPanel that can be added to a JFrame
 * 
 * @author Loki
 */
public class MazeRenderer extends JPanel{
    static final long serialVersionUID = 1L; //serialVersionUID
    private List<TexturePack> textures = getTexturePacksList();
    private TexturePack texturePack = textures.get(0); //default texture pack
    private Tile[][] gameArray; //the array of tiles
    public DomainController maze; //the domain controller
    public BufferedImage background; //the background image
    private int patternSize = 100; //the size of the pattern
    private int renderSize = 7; //the size of the render
    private int minRenderSize = 1, maxRenderSize = 50; //the min and max render size


    /**
     * Constructor. Takes a maze as parameters.
     * 
     * @param maze Maze to be rendered.
     */
    public MazeRenderer(DomainController maze) {
        this.maze = maze;
        this.setOpaque(false);
    }

    /**
     * get a image from the image provided
     * @param String
     * @return BufferedImage
     */
    public BufferedImage getImage(String imgName) {return texturePack.getImage(imgName);}

    /**
     * get a image from the image provided
     * @param Tile
     * @return BufferedImage
     */
    public BufferedImage getImage(Tile tile) {return texturePack.getImage(tile);}
   
    @Override
    public void paintComponent(Graphics g) {
        //call superclass to paint background
        super.paintComponent(g);
        //get the maze array
        gameArray = maze.getGameArray();
        //viewport of the maze
        Tile[][] viewport = Viewport.getViewport(gameArray, renderSize);
        //get the width and height of the maze
        int tileWidth = (getWidth() / viewport.length);
        int tileHeight = (getHeight() / viewport[1].length);
        //loop through the maze array and paint the tiles
        for (int i = 0; i < viewport.length; i++) {
            for (int j = 0; j < viewport[1].length; j++) {
                //clear the floor
                g.drawImage(texturePack.getImage("floor"), i * tileWidth, j * tileHeight, tileWidth, tileHeight, null);
                // if there is a item draw on top of the floor or a wall tile
                Tile tile = viewport[i][j];
                if(tile.type() == TileType.Floor) {continue;}
                //if hero tile then draw the hero depending on the direction
                if(tile.type() == TileType.Hero) {
                    Hero hero = (Hero) tile;
                    BufferedImage img = getHeroImg(hero.dir());
                    g.drawImage(img, i * tileWidth, j * tileHeight, tileWidth, tileHeight, null);
                }else{
                    g.drawImage(texturePack.getImage(tile), i * tileWidth, j * tileHeight, tileWidth, tileHeight, null);
                }
            }
        }
    }

    private BufferedImage getHeroImg(Direction dir) {
        switch(dir) {
            case Up:
                return texturePack.getImage("heroBack");
            case Down:
                return texturePack.getImage("heroFront");
            case Left:
                return texturePack.getImage("heroSide");
            case Right:
                return texturePack.getImage("hero");
            default: return null;
        }
    }

    /**
     * set the current texturePack and returns the new background image
     * 
     * @param texturePack
     */
    public void setTexturePack(TexturePack texturePack) {
        this.texturePack = texturePack;
        patternSize = 100;
    }


    //-----------------------------load in texture packs---------------------------------------------//

    public List<TexturePack> getTexturePacksList() {
        File folder = new File("res/textures");
        File[] listOfFiles = folder.listFiles();
        List<TexturePack> textures = new ArrayList<>();
        //for each texture in the folder add it to the list
        for (File file : listOfFiles) {
            if (file.isFile()) {
            Font title = new Font("Arial", Font.BOLD, 80);
            Font subtitle = new Font("Arial", Font.BOLD, 40);
            Font text =new Font("Arial", Font.BOLD, 30);
            Color colorHover = Color.ORANGE;
            Color colorSelected = Color.RED;
            TexturePack tp = new TexturePack(file.getName(), title, subtitle, text, colorHover, colorSelected);
            textures.add(tp);
            }
        }
        return textures;
    }

    //------------------------------------------------------------------------------------------------//
    //getters and setters

    /**
     * get min render size
     * @return minRenderSize
     */
    public int getMinRenderSize() {return minRenderSize;}
    /**
     * get maximum render size
     * @return maxRenderSize
     */
    public int getMaxRenderSize() {return maxRenderSize;}
    

    /**
     * get render size
     * @return int
     */
    public int getRenderSize() {return renderSize;}

    /**
     * set render size
     * @param renderSize
     */
    public void setRenderSize(int renderSize) {this.renderSize = renderSize;}

    /**
     * getter for patternSize
     * @return patternSize
     */
    public int getPatternSize() {return patternSize;}
    /**
     * get current texture pack
     * @return texturePack
     */
    public TexturePack getCurrentTexturePack(){return texturePack;}

    /**
     * set the maze to be rendered
     * @param maze
     */
    public void setMaze(DomainController maze) {this.maze = maze;}

    //get list of TexturePacks
    public List<TexturePack> getTexturePacks() {return textures;}

}
