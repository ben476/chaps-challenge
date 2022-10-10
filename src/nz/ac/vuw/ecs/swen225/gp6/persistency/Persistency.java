package nz.ac.vuw.ecs.swen225.gp6.persistency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import nz.ac.vuw.ecs.swen225.gp6.domain.Domain;
import nz.ac.vuw.ecs.swen225.gp6.persistency.Helper;
import nz.ac.vuw.ecs.swen225.gp6.recorder.Record;
import nz.ac.vuw.ecs.swen225.gp6.recorder.datastructures.Pair;
import nz.ac.vuw.ecs.swen225.gp6.domain.Inventory;
import nz.ac.vuw.ecs.swen225.gp6.domain.Maze;
import nz.ac.vuw.ecs.swen225.gp6.domain.TileAnatomy.Tile;
import nz.ac.vuw.ecs.swen225.gp6.domain.TileAnatomy.TileInfo;
import nz.ac.vuw.ecs.swen225.gp6.domain.TileAnatomy.TileType;
import nz.ac.vuw.ecs.swen225.gp6.domain.Utility.Loc;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import nz.ac.vuw.ecs.swen225.gp6.app.*;
import nz.ac.vuw.ecs.swen225.gp6.app.utilities.Actions;
import nz.ac.vuw.ecs.swen225.gp6.app.utilities.Configuration;
import nz.ac.vuw.ecs.swen225.gp6.app.utilities.Controller.Key;

public class Persistency {
    public record Log(LocalDateTime date, String message) {
    }

    /**
     * Log the string to the log file
     * 
     * @param string The string to log
     */
    public static void log(String message) throws IOException {
        // get time and date string
        String time = LocalDateTime.now().toString();
        // write to file
        FileWriter out = null;
        out = new FileWriter("res/log.txt", true);
        out.write(time + ": " + message + "\n");
        out.close();
    }

    /**
     * Get the log file
     * 
     * @return List of log entries
     */
    public static List<Log> getLogs() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("res/log.txt"));

        return lines.stream().map(line -> {
            if (!line.contains(": ")) {
                return null;
            }
            String dateString = line.substring(0, line.indexOf(": "));
            LocalDateTime date = LocalDateTime.parse(dateString);
            String message = line.substring(line.indexOf(": ") + 1).strip();
            return new Log(date, message);
        }).filter(Objects::nonNull).toList();
    }

    /**
     * Serialise the configuration file
     * 
     * @param config The configuration object
     * @return The xml element
     */
    public static Element serialiseConfiguration(Configuration config) {
        Element root = DocumentHelper.createElement("configuration");

        // add texture pack
        Element texturePack = root.addElement("texturePack");
        texturePack.setText(config.getTexturePack());

        // add key bindings
        Element keyBindings = root.addElement("keyBindings");
        for (Actions action : Actions.values()) {
            Key key = config.getUserKeyBindings().get(action);
            if (key == null)
                continue;
            Element keyElement = keyBindings.addElement(action.name());
            keyElement.addAttribute("modifier", Integer.toString(key.modifier()));
            keyElement.addAttribute("key", Integer.toString(key.key()));
        }

        // add music enabled
        Element musicEnabled = root.addElement("musicEnabled");
        musicEnabled.setText(Boolean.toString(config.isMusicOn()));

        // add view distance
        Element viewDistance = root.addElement("viewDistance");
        viewDistance.setText(Integer.toString(config.getViewDistance()));

        return root;
    }

    /**
     * Deserialise the configuration file
     * 
     * @param element The xml element
     * @return Configuration object
     */
    public static Configuration deserialiseConfiguration(Element root) {
        // get texture pack
        String texturePack = root.element("texturePack").getText();

        // get key bindings
        EnumMap<Actions, Key> keyBindings = new EnumMap<>(Actions.class);
        for (Actions action : Actions.values()) {
            Element keyElement = root.element("keyBindings").element(action.name());
            if (keyElement == null)
                continue;
            int modifier = Integer.parseInt(keyElement.attributeValue("modifier"));
            int key = Integer.parseInt(keyElement.attributeValue("key"));
            keyBindings.put(action, new Key(modifier, key));
        }

        // get music enabled
        Boolean musicEnabled = Boolean.parseBoolean(root.element("musicEnabled").getText());

        // get view distance
        Integer viewDistance = Integer.parseInt(root.element("viewDistance").getText());

        return new Configuration(musicEnabled, texturePack, viewDistance, keyBindings);
    }

    /**
     * Serialise a domain to an XML document
     *
     * @param domain The domain to serialise
     *
     * @return The serialised domain as an XML document
     */
    public static Document serialiseDomain(Domain domain) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("domain");
        Element levels = root.addElement("levels");
        for (int i = 0; i < domain.getMazes().size(); i++) {
            Maze maze = domain.getMazes().get(i);
            Document mazeDoc = serialiseMaze(maze, i);
            levels.add(mazeDoc.getRootElement());
        }
        levels.addAttribute("current", Integer.toString(domain.getCurrentLevel()));
        root.add(serialiseInventory(domain.getInv()).getRootElement());

        return document;
    }

    /**
     * Deserialise a domain from an XML document
     * 
     * @param document The XML document to deserialise
     * @return The deserialised domain
     */
    public static Domain deserialiseDomain(Document document) {
        Element root = document.getRootElement();
        Element levels = root.element("levels");
        List<Maze> mazes = new ArrayList<>();
        for (Element level : levels.elements()) {
            mazes.add(deserialiseMaze(level));
        }
        int currentLevel = Integer.parseInt(levels.attributeValue("current"));
        Inventory inv = deserialiseInventory(root.element("inventory"));
        return new Domain(mazes, inv, currentLevel);
    }

    /**
     * Load configuration from res/config.xml
     * 
     * @return Configuration object
     */
    public static Configuration loadConfiguration() {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read("res/config.xml");
            return deserialiseConfiguration(document.getRootElement());
        } catch (Throwable e) {
            try {
                log("Failed to load configuration: " + e.getMessage());
                Document document = reader.read("res/defaultConfig.xml");
                return deserialiseConfiguration(document.getRootElement());
            } catch (Throwable f) {
                f.printStackTrace();
                return Configuration.getDefaultConfiguration();
            }
        }
    }

    /**
     * Serialise a maze to an XML document
     * 
     * Example:
     * <level index="1" name="Level 1">
     * <grid width="10" height="10">
     * <cell x="5" y="0">
     * <wall />
     * </cell>
     * </grid>
     * </level>
     * 
     * @param maze The maze to serialise
     */
    public static Document serialiseMaze(Maze maze, int i) {
        Document document = DocumentHelper.createDocument();
        Element level = document.addElement("level");
        level.addAttribute("index", Integer.toString(i));
        level.addAttribute("name", "Level " + (i + 1));
        Element grid = level.addElement("grid");
        grid.addAttribute("width", Integer.toString(maze.width()));
        grid.addAttribute("height", Integer.toString(maze.height()));
        for (int x = 0; x < maze.width(); x++) {
            for (int y = 0; y < maze.height(); y++) {
                Tile tile = maze.getTileAt(x, y);
                if (tile != null && tile.type() != TileType.Null) {
                    Element cell = grid.addElement("cell");
                    cell.addAttribute("x", Integer.toString(x));
                    cell.addAttribute("y", Integer.toString(y));
                    cell.add(serialiseTile(tile).getRootElement());
                }
            }
        }

        return document;
    }

    /**
     * Serialise a tile to an XML element
     * 
     * @param tile The tile to serialise
     * 
     * @return The serialised tile as an XML element
     */
    public static Document serialiseTile(Tile tile) {
        Document document = DocumentHelper.createDocument();
        String name = Helper.typeToString.get(tile.type());
        if (name.contains("Key") || name.contains("Lock") && name.equals("exitLock")) {
            Element element = document.addElement(name.contains("Key") ? "key" : "lock");
            element.addAttribute("color", name.replace("Key", "").replace("Lock", "").toLowerCase());
        } else {
            document.addElement(name);
        }
        return document;
    }

    /**
     * Serialise an inventory to an XML document
     * 
     * Example:
     * <inventory>
     * <key color="green" />
     * </inventory>
     * 
     * @param inventory The inventory to serialise
     */
    public static Document serialiseInventory(Inventory inventory) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("inventory");
        for (Tile item : inventory.getItems()) {
            root.add(serialiseTile(item).getRootElement());
        }
        root.addAttribute("size", inventory.size() + "");
        return document;
    }

    /**
     * Deserialise inventory from an XML document
     * 
     * @param document The XML document to deserialise
     * @return The deserialised inventory
     */
    public static Inventory deserialiseInventory(Element root) {
        Inventory inv = new Inventory(Integer.parseInt(root.attributeValue("size")));
        for (Element item : root.elements()) {
            inv.addItem(TileType.makeTile(deserialiseTileType(item), new TileInfo(new Loc(0, 0))));
        }
        return inv;
    }

    /**
     * Deserialise a tile type from an XML element
     * 
     * @param element The XML element to deserialise
     * @return The deserialised tile type
     */
    private static TileType deserialiseTileType(Element element) {
        String name = element.getName();
        if (name.equals("key")) {
            String color = element.attributeValue("color");
            return Helper.stringToType.get(color + "Key");
        } else if (name.equals("lock")) {
            String color = element.attributeValue("color");
            return Helper.stringToType.get(color + "Lock");
        } else {
            return Helper.stringToType.get(name);
        }
    }

    /**
     * Deserialise a maze from an XML document
     * 
     * @param xml
     * @return The unserialised maze
     */
    public static Maze deserialiseMaze(Element root) {
        String thing = root.asXML();
        Element grid = root.element("grid");
        int width = Integer.parseInt(grid.attributeValue("width"));
        int height = Integer.parseInt(grid.attributeValue("height"));
        Maze maze = new Maze(new Tile[width][height]);
        // fill maze with null tiles
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                maze.setTileAt(new Loc(x, y), TileType.Floor);
            }
        }
        for (Element cell : grid.elements("cell")) {
            int x = Integer.parseInt(cell.attributeValue("x"));
            int y = Integer.parseInt(cell.attributeValue("y"));
            Element tile = cell.elements().get(0);
            if (tile != null) {
                String name = tile.getName();
                if (name.equals("key")) {
                    String color = tile.attributeValue("color");
                    maze.setTileAt(new Loc(x, y), Helper.stringToType.get(color + "Key"));
                } else if (name.equals("lock")) {
                    String color = tile.attributeValue("color");
                    maze.setTileAt(new Loc(x, y), Helper.stringToType.get(color + "Lock"));
                } else {
                    maze.setTileAt(new Loc(x, y), Helper.stringToType.get(name));
                }
            }
        }
        return maze;
    }

    /**
     * serialise a record timeline object to an XML document
     * 
     * @param timeline The timeline to serialise
     * @return The serialised timeline
     */
    public static Document serialiseRecordTimeline(Stack<Pair<Long, Actions>> timeline) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("recorder");
        root.addAttribute("size", timeline.size() + "");
        for (Pair<Long, Actions> pair : timeline) {
            Element action = root.addElement(pair.getValue().toString());
            action.addAttribute("time", pair.getKey() + "");
        }
        return document;
    }

    /**
     * Deserialise a record timeline object from an XML document
     * 
     * @param document The XML document to deserialise
     * @return The deserialised timeline
     */
    public static Stack<Pair<Long, Actions>> deserialiseRecordTimeline(Document document) {
        Element root = document.getRootElement();
        Stack<Pair<Long, Actions>> timeline = new Stack<Pair<Long, Actions>>();
        for (Element action : root.elements()) {
            timeline.add(new Pair<Long, Actions>(Long.parseLong(action.attributeValue("time")),
                    Actions.valueOf(action.getName())));
        }
        return timeline;
    }

    /**
     * Save a domain to a file
     *
     * @param domain The domain to save
     *
     * @param path   The file path to save to
     */
    public static void saveDomain(Domain domain, int slot) throws IOException {
        Document document = serialiseDomain(domain);

        File dir = new File("res/saves");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileWriter out = new FileWriter("res/saves/" + slot + ".xml");
        document.write(out);
        out.close();
    }

    /**
     * Load a maze from a file
     * 
     * @param path The file path to load from
     * @return The loaded maze
     */
    public static Domain loadSave(int slot) throws DocumentException {
        SAXReader reader = new SAXReader();
        try {
            InputStream in = new FileInputStream("res/saves/" + slot + ".xml");
            Document document = reader.read(in);
            return deserialiseDomain(document);
        } catch (FileNotFoundException e) {
            return getInitialDomain();
        }
    }

    /**
     * Delete a save file
     */
    public static void deleteSave(int slot) throws IOException {
        File file = new File("res/save/" + slot + ".xml");
        if (!file.delete()) {
            throw new IOException("Could not delete file");
        }
    }

    /**
     * Load saves 1, 2, 3 to a list
     * 
     * @return The list of saves
     */
    @Deprecated
    public static List<Domain> loadSaves() throws DocumentException {
        List<Domain> saves = new ArrayList<Domain>();
        for (int i = 1; i <= 3; ++i) {
            saves.add(loadSave(i));
        }
        return saves;
    }

    /**
     * Get the initial domain
     * 
     * @return The initial domain
     */
    public static Domain getInitialDomain() {
        try {
            SAXReader reader = new SAXReader();
            // list files in res/levels
            File dir = new File("res/levels");
            File[] files = dir.listFiles();
            List<Maze> mazes = new ArrayList<Maze>();
            for (File file : files) {
                if (file.getName().endsWith(".xml")) {
                    Document document = reader.read(file);
                    mazes.add(deserialiseMaze(document.getRootElement()));
                }
            }
            return new Domain(mazes, new Inventory(8), 1);
        } catch (DocumentException e) {
            e.printStackTrace();
            return new Domain(List.of(nz.ac.vuw.ecs.swen225.gp6.domain.Helper.makeMaze(),
                    nz.ac.vuw.ecs.swen225.gp6.domain.Helper.makeMaze()), new Inventory(8), 1);
        }
    }

}
