# Making custom tiles
Here's how you can add custom tiles to the game without having to edit the source code.

## Writing the custom tile
The Enemy.java file is an example of a custom tile that implements the Tile interface. It's a simple enemy that moves around the map and kills the player if it touches them. You can use this as a template for your own custom tiles.

## Adding the custom tile to the game
First, we compile the custom tiles. In the src/custom/Tiles directory, run the following command:
```
javac -cp ../../ *.java
```
Now, put the entire custom directory into a .jar file. You can do this by running the following command:
```
jar -cf custom.jar ../../custom
```
Finally, put the .jar file into the lib directory. Now, when you run the game, it will load the custom tiles from the .jar file.

Note: if you use IntelliJ, you might need to right-click the jar file and select "Add as Library" in order for the game to load the custom tiles.