# NHLStreams
Application to scrape & process NHL data in NRT. See Architecture for details on the planned tech stack. Very little is completed as of yet. 


# Compliation:
The develop branch will have everything you need to compile the program. 
```
//The java code is all in BarDown. 
cd BarDown

//now compile the code using the included gradle wrappper:
./gradlew clean build jar
```

# Runtime Config
Currenlty the project has not bothered to move the runtime configs for game out of the main method. So you have a few (not ideal options).
To get the game data from a PIT/BUF game from 04/18/2021, just build and execute the code as it. To get some game IDs for the current day
it is a bit tricker. 

For current data game data, uncomment lines 19-25 in BarDown's main(...). They will read as so: 
```
try {
  dataCtl.getDailySchedule();
} catch (URISyntaxException | IOException | InterruptedException e) {
  // TODO Auto-generated catch block
  log.atSevere().withCause(e).withStackTrace(StackSize.FULL)
    .log("Failed to get dail schedule...");
}
```
If you compile and run this code it will will print out the two teams & the nhl's data api game ID. That id (will be the one in parentheticals) is the one you want to put in the gameRunner() method. It will look something like the following:
```
//the code in the "" is where you want to put the game id. The DataController will handle the rest...
dataCtl.gameRunner("2020020715");
```
Once you have done this you will be ready to re-compile and run the application!

# Running:
To run Bar down you have two options. If you like and have experience doing so you can execute the BarDown jar by executing the following:
```
java -cp build/libs/BarDown-0.1.0.jar nhlstreams.BarDown
```

However, you can also run this as a gradle application if you so choose. In that case execute the following:
```
./gradlew build run
```

Either will work just fine. Entirely a matter of preference.
