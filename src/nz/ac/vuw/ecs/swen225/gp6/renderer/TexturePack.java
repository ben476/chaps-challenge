package nz.ac.vuw.ecs.swen225.gp6.renderer;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;

import nz.ac.vuw.ecs.swen225.gp6.domain.Tiles.Tile;

//import nz.ac.vuw.ecs.swen225.gp6.renderer.tempDomain.Tiles.Tile;

public enum TexturePack{
    /**
     * The Original texture pack.
     */
    Original(new Font("Arial", Font.BOLD, 80),
            new Font("Arial", Font.BOLD, 40),
            new Font("Arial", Font.BOLD, 30),
            Color.BLACK, Color.ORANGE, Color.RED),
    /**
     * The Cats texture pack.
     */
    Cats(new Font("Agency FB", Font.BOLD, 80),
            new Font("Agency FB", Font.BOLD, 40),
            new Font("Agency FB", Font.BOLD, 30),
            Color.BLACK, Color.ORANGE, Color.RED),
    /**
     * The Dogs texture pack.
     */
    Dogs(new Font("Agency FB", Font.BOLD, 80),
            new Font("Agency FB", Font.BOLD, 40),
            new Font("Agency FB", Font.BOLD, 30),
            Color.BLACK, Color.ORANGE, Color.RED),
    /**
     * The Emoji texture pack.
     */
    Emoji(new Font("Comic Sans MS", Font.BOLD, 80),
            new Font("Comic Sans MS", Font.BOLD, 40),
            new Font("Comic Sans MS", Font.BOLD, 30),
            Color.BLACK, Color.ORANGE, Color.RED);

    private final Font titleFont;
    private final Font subtitleFont;
    private final Font textFont;
    private final Color colorDefault;
    private final Color colorHover;
    private final Color colorSelected;

    public Font getTitleFont()      {return titleFont;}
    public Font getSubtitleFont()   {return subtitleFont;}
    public Font getTextFont()       {return textFont;}
    public Color getColorDefault()  {return colorDefault;}
    public Color getColorHover()    {return colorHover;}
    public Color getColorSelected() {return colorSelected;}

    /**
     * Constructor for texture packs
     * @param tile
     * @param subtitle
     * @param text
     * @param colorDefault
     * @param colorHover
     * @param colorSelected
     */
    TexturePack(Font title,Font subtitle, Font text, Color colorDefault, Color colorHover, Color colorSelected){
        this.titleFont = title;
        this.subtitleFont = subtitle;
        this.textFont = text;
        this.colorDefault = colorDefault;
        this.colorHover = colorHover;
        this.colorSelected = colorSelected;
    }

    public enum Images{

        /**
         * The background image for the game.
         */
        Background("background"),

        /**
         * The image for the repeatable pattern background.
         */
        Pattern("pattern"),
        /**
         * The image for the repeatable pattern background.
         */
        Pattern_2("pattern2"),

        /**
         * The image for the floor.
         */
        Floor("floor"),
        /**
         * The image for the hero.
         */
        Hero("hero"),
        /**
         * The image for the enemy.
         */
        Enemy("enemy"),
        
        /**
         * The image for the coin.
         */
        Coin("coin"),
        /**
         * The image for the blue key.
         */
        BlueKey("blueKey"),
        /**
         * The image for the green key.
         */
        GreenKey("greenKey"),
        /**
         * The image for the orange key.
         */
        OrangeKey("orangeKey"),
        /**
         * The image for the yellow key.
         */
        YellowKey("yellowKey"),
        /**
         * The image for the empty tile.
         */
        Empty_tile("empty_tile"),
        /**
         * The image for the wall.
         */
        Wall("wall_tile"),
        /**
         * The image for the blueLock
         */
        BlueLock("blueLock"),
        /**
         * The image for the greenLock
         */
        GreenLock("greenLock"),
        /**
         * The image for the orangeLock
         */
        OrangeLock("orangeLock"),
        /**
         * The image for the yellowLock
         */
        YellowLock("yellowLock"),
        /**
         * The image for the exit
         */
        Exit("exitDoor");
        
        //name of the image
        private String name;
        //the image we store in ram.
        private BufferedImage img;
        
        /*constructor for the enum, loads the image and keeps it as a veraible so we dont need to 
          reload the file from disk everytime we redraw */
        Images(String path){this.img = loadImg(path);}
        /**
         * get the name
         * @return String
         */
        public String getName(){return name;}
        /**
         * get the image that is cashed
         * @return BufferedImage
         */
        public BufferedImage getImg(){return img;}
        
        /**
         * get the image.
         * @param path
         * @return BufferedImage
         */
        public static BufferedImage getImage(Images img){return img.getImg();}
        
        /**
         * get the image for the Tile provided.
         * @param tile
         * @return BufferedImage
         */
        public static BufferedImage getImage(Tile tile){
            return switch(tile.type()){
                case Floor -> getImage(Empty_tile);
                case Empty -> Images.Empty_tile.getImg();
                case Hero -> Images.Hero.getImg();
                case Enemy -> Images.Enemy.getImg();
                case Wall -> Images.Wall.getImg();
                case BlueKey -> Images.BlueKey.getImg();
                case GreenKey -> Images.GreenKey.getImg();
                case YellowKey -> Images.YellowKey.getImg();
                case OrangeKey -> Images.OrangeKey.getImg();
                case BlueLock -> Images.BlueLock.getImg();
                case GreenLock -> Images.GreenLock.getImg();
                case YellowLock -> Images.YellowLock.getImg();
                case OrangeLock -> Images.OrangeLock.getImg();
                case ExitDoor -> Images.Exit.getImg();
                case Coin -> Images.Coin.getImg();
                default -> throw new IllegalArgumentException("Unexpected value: " + tile.getClass().getName() + " : " + tile.type());
            };
        }
        
        /**
         * load the image from the disk
         * @param path
         * @return BufferedImage
         */
        public BufferedImage loadImg(String imageName){
            this.name = imageName;
            System.out.print("Loading " + imageName + "...    -> ");
            try {
                BufferedImage img = ImageIO.read(getClass().getResource("/nz/ac/vuw/ecs/swen225/gp6/renderer/textures/" + MazeRenderer.currentTP + "/" + imageName + ".png"));
                System.out.println("Loaded!");
                return img;
            } catch (IOException e) {
                throw new RuntimeException(e);}
        }
        
        /**
         * reload all the images and cashs them when changing texture packs.
         */
        public static void reloadAllTexturepack(){
            for(Images i : Images.values()){
                i.img = i.loadImg(i.getName());
            }
        }
        
    }
}