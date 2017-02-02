package Controller;

import Models.Location;
import Models.Simulator;
import Models.SimulatorView;
import Models.StatsPie;
import View.StatsGraph;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Optional;

public class Controller {


    //make simulator object
	private Simulator sim;              //makes the central simulator object
    private SimulatorView simView;      //makes the central simulatorview object

    private StatsGraph _statsGraph;     //makes the statistics graph
    private StatsPie _statsPie;         //makes the pie graph

    @FXML
    private VBox _sidebarRight;         //makes the sidebar on the right

    @FXML
    private Canvas _canvas;             //makes the canvas where the garage whil be drawn

    @FXML
    private Button button_operate1;     //makes button 1

    @FXML
    private Button button_operate2;     //makes button 2

    @FXML
    private Button button_operate3;     //makes button 3

    @FXML
    private Button button_operate4;     //makes button 4

    @FXML
    private Button button_operate5;     //makes button 5

    @FXML
    private Button button_operate6;     //makes button 6

    @FXML
    private Label textTarget;           //makes the label textTarget used for debugging
    @FXML
    private Label date;                 //makes the label with the week and day
    @FXML
    private Label clock;                //makes the label with the hours and minutes
    @FXML
    private Label revenue;              //makes the label with the total revenue

    @FXML
    private Timeline timeline;          //makes the timelime object

    /**
     * Initializes all the atributes
     */
    public void initialize() {
        sim = new Simulator(_canvas);
        simView = sim.getSimulatorView();

        _statsPie = new StatsPie();
        _statsGraph = new StatsGraph(_statsPie);

        _statsGraph.setData();
        _statsGraph.generate();
        _sidebarRight.getChildren().add(_statsGraph.getChart());

        getDate();
        clock();
        getRevenue();
    }

    @FXML
    /**
     * Closes the app
     * @param ActionEvent e     The action event
     */
    private void closeApp(ActionEvent e) {
        System.exit(0);
    }

    @FXML
    /**
     * Makes the simulator tick once
     * Uses tickFor method
     */
    private void tick1() {
        //call the simulator object to run for 1 tick
        tickFor(1);
    }


    @FXML
    /**
     * Makes the simulator tick 60 times (1 hour)
     */
    private void tick60() {
        //call simulator object to run for 50 ticks
        tickFor(60);
    }

    @FXML
    /**
     * Makes the simulator tick 1440 times (1 day)
     */
    private void tickDay() {
        //call the simulator object to run for 1000 ticks
        tickFor(1440);
    }

    @FXML
    /**
     * Makes the simulator tick for any number of ticks
     * @param int ticks     The number of ticks the simulation should do
     */
    private void tickFor(int ticks) {
        setText("I should be running for " + ticks + " ticks now");
        disableButtons(true);

        timeline = new Timeline();
        timeline.setCycleCount(ticks);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), e -> {sim.tick();
                                                                             getDate();
                                                                             getRevenue();
                                                                             clock(); }));

        timeline.play();
        timeline.setOnFinished(e -> {
            updateGraph();
            disableButtons(false);
        });
    }

    @FXML
    /**
     * Opens a dialog that lets you enter an integer which corresponds to the number of placeholder spots that will be assigned
     */
    private void makePassHolderSpots() {
        //setText("I should be opening a popup window now.");

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Number Input Dialog");
        dialog.setContentText("Number of spots:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String result2 = result.get();
            // Parses a integer from a String and tries to catch errors.
            int spotAmount = 0;
            try {
                spotAmount = Integer.parseInt(result2);
            } catch(NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if(spotAmount < 1) {
                    setText("Please enter an positive whole number!");
                } else {
                    simView.makePassHolderSpots(spotAmount);
                }
            }
        }
    }

    @FXML
    /**
     * Opens op a dialog which lets you enter a double. The price per minute of the parking garage will be set to that double
     */
    private void setPricePerMinute() {
        //setText("I should be opening a popup window now.");

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Number Input Dialog");
        dialog.setContentText("Price per minute:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String result2 = result.get();
            // Parses a integer from a String and tries to catch errors.
            double priceAmount = 0;
            try {
                priceAmount = Double.parseDouble(result2);
            } catch(NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if(priceAmount<=0) {
                    setText("Please enter an positive whole number!");
                } else {
                    sim.setCost(priceAmount);
                }
            }
        }
    }

    @FXML
    /**
     * Sets a reservation at a specific location;
     * uses insertFloor(), insertRow() and insertPlace()
     */
    private void makeReservationsAt() {
        // make reservations at a prompted location
        int floor = insertFloor();
        int row = insertRow();
        int place = insertPlace();

        // illegal answers return -1
        if (floor == -1 || row == -1 || place == -1) {
            setText("One or more arguments were not filled in correctly!");
        } else {
            int[] time = sim.getTime();
            simView.makeReservationsAt(new Location(floor, row, place),time[0], time[1]);
        }
    }

    @FXML
    /**
     * Sets the time by letting the user specify the week, day, hour and minute
     * uses givenWeek(), givenDay(), givenHour() and givenMinute()
     */
    private void setTime() {
        int week = givenWeek();
        int day = givenDay();
        int hour = givenHour();
        int minute = givenMinute();

        if(week == -1 || day == -1 || hour == -1 || minute == -1) {
            setText("Please enter positive numbers.");
        } else {
            sim.setTime(week,day,hour,minute);
            getDate();
        }
    }

    /**
     * Opens up a dialog that lets the user enter an integer which will become the new week number
     * @return int:     the new week number that the user entered or -1 if the number is invalid
     */
    private int givenWeek() {
        int week = -1;

        TextInputDialog WeekDialog = new TextInputDialog("1");
        WeekDialog.setTitle("Week Input Dialog");
        WeekDialog.setHeaderText("Please enter any week number Between 1 and 52");
        WeekDialog.setContentText("Week:");
        Optional<String> WeekResult = WeekDialog.showAndWait();
        if (WeekResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String WeekResult2 = WeekResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                week = Integer.parseInt(WeekResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (week <= 0) {
                    setText("Please enter an positive whole number bigger than 0.");
                } else {
                    // check if the entered integer is between bounds
                    if (week <= 52) {
                        return (week);
                    } else {
                        return (-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return (week);
    }

    /**
     * Opens up a dialog that lets the user enter an integer which will become the new day number
     * @return int:     the new day number that the user entered or -1 if the number is invalid
     */
    private  int givenDay() {
        int day = -1;

        TextInputDialog DayDialog = new TextInputDialog("1");
        DayDialog.setTitle("Day Input Dialog");
        DayDialog.setHeaderText("Please enter any day number Between 1 and 7");
        DayDialog.setContentText("Day:");
        Optional<String> DayResult = DayDialog.showAndWait();
        if (DayResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String DayResult2 = DayResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                day = Integer.parseInt(DayResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (day <= 0) {
                    setText("Please enter an positive whole number bigger than 0.");
                } else {
                    // check if the entered integer is between bounds
                    if (day <= 7) {
                        return(day - 1);
                    } else {
                        return(-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return(day);
    }

    /**
     * Opens up a dialog that lets the user enter an integer which will become the new hour number
     * @return int:     the new hour number that the user entered or -1 if the number is invalid
     */
    private  int givenHour() {
        int hour = -1;

        TextInputDialog HourDialog = new TextInputDialog("1");
        HourDialog.setTitle("Hour Input Dialog");
        HourDialog.setHeaderText("Please enter any Hour between 1 and 24");
        HourDialog.setContentText("Hour:");
        Optional<String> HourResult = HourDialog.showAndWait();
        if (HourResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String HourResult2 = HourResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                hour = Integer.parseInt(HourResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (hour <= 0) {
                    setText("Please enter an positive whole number bigger than 0.");
                } else {
                    // check if the entered integer is between bounds
                    if (hour <= 24) {
                        return(hour - 1);
                    } else {
                        return(-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return(hour);
    }

    /**
     * Opens up a dialog that lets the user enter an integer which will become the new minute number
     * @return int:     the new minute number that the user entered or -1 if the number is invalid
     */
    private  int givenMinute() {
        int minute = -1;

        TextInputDialog MinuteDialog = new TextInputDialog("1");
        MinuteDialog.setTitle("Minute Input Dialog");
        MinuteDialog.setHeaderText("Please enter any minute between 1 and 60");
        MinuteDialog.setContentText("Minute:");
        Optional<String> MinuteResult = MinuteDialog.showAndWait();
        if (MinuteResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String MinuteResult2 = MinuteResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                minute = Integer.parseInt(MinuteResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (minute <= 0) {
                    setText("Please enter an positive whole number bigger than 0.");
                } else {
                    // check if the entered integer is between bounds
                    if (minute <= 60) {
                        return(minute - 1);
                    } else {
                        return(-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return(minute);
    }

    /**
     * Opens op a dialog that lets the user enter an integer that is used as the floor for setting passholderspaces and making reservations
     * @return int:     The number of the floor or -1 if the number is invalid
     */
    private int insertFloor() {
        // input a floor
        int floor = -1;

        TextInputDialog floorDialog = new TextInputDialog("0");
        floorDialog.setTitle("Floor Input Dialog");
        floorDialog.setHeaderText("Please enter the floor number for your reservation below. Between 0 and " + (simView.getNumberOfFloors() - 1));
        floorDialog.setContentText("Floor:");
        Optional<String> floorResult = floorDialog.showAndWait();

        if (floorResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String floorResult2 = floorResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                floor = Integer.parseInt(floorResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (floor < 0) {
                    setText("Please enter an positive whole number!");
                } else {
                    // check if the entered integer is actually in this garage
                    if (floor < simView.getNumberOfFloors()) {
                        return(floor);
                    } else {
                        return(-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return(floor);
    }

    /**
     * Opens op a dialog that lets the user enter an integer that is used as the row for setting passholderspaces and making reservations
     * @return int:     The number of the row or -1 if the number is invalid
     */
    private int insertRow() {
        // input a row
        int row = -1;

        TextInputDialog rowDialog = new TextInputDialog("0");
        rowDialog.setTitle("Row Input Dialog");
        rowDialog.setHeaderText("Please enter the row number for your reservation below. Between 0 and " + (simView.getNumberOfRows() - 1));
        rowDialog.setContentText("Row:");
        Optional<String> rowResult = rowDialog.showAndWait();

        if (rowResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String rowResult2 = rowResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                row = Integer.parseInt(rowResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (row < 0) {
                    setText("Please enter an positive whole number!");
                } else {
                    // check if the entered integer is actually in this garage
                    if (row < simView.getNumberOfRows()) {
                        return(row);
                    } else {
                        return(-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return(row);
    }

    /**
     * Opens op a dialog that lets the user enter an integer that is used as the place for setting passholderspaces and making reservations
     * @return int:     The number of the place or -1 if the number is invalid
     */
    private int insertPlace() {
        // input a place
        int place = -1;

        TextInputDialog placeDialog = new TextInputDialog("0");
        placeDialog.setTitle("Place Input Dialog");
        placeDialog.setHeaderText("Please enter the floor number for your reservation below. Between 0 and " + (simView.getNumberOfPlaces() - 1));
        placeDialog.setContentText("Place:");
        Optional<String> placeResult = placeDialog.showAndWait();

        if (placeResult.isPresent()) {
            // Turns Optional<String> into a normal String
            String placeResult2 = placeResult.get();
            // Parses a integer from a String and tries to catch errors.
            try {
                place = Integer.parseInt(placeResult2);
            } catch (NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if (place < 0) {
                    setText("Please enter an positive whole number!");
                } else {
                    // check if the entered integer is actually in this garage
                    if (place < simView.getNumberOfPlaces()) {
                        return (place);
                    } else {
                        return (-1);
                    }
                }
            }
        }
        // if no acceptable input was found, this will return -1 and stop the method
        return (place);
    }

    @FXML
    /**
     * Opens up a dialog that lets the user enter an integer to choose the amount of ticks the simulator must run
     */
    private void submit() {
        // Opening a pop-up dialog window to ask for the amount of ticks, converting it to integer and calling on tickFor
        setText("I should be opening a popup window now.");

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Minute Input Dialog");
        dialog.setHeaderText("Please enter the amount of minutes this program should be running for below.");
        dialog.setContentText("Number of minutes:");
        Optional<String> result = dialog.showAndWait();

        // Checking if something was filled in. No answer does nothing.
        if (result.isPresent()){
            // Turns Optional<String> into a normal String
            String result2 = result.get();
            // Parses a integer from a String and tries to catch errors.
            int ticksAmount = -1;
            try {
                ticksAmount = Integer.parseInt(result2);
            } catch(NumberFormatException exception) {
                setText("Please enter an positive whole number!");
            } finally {
                if(ticksAmount < 1) {
                    setText("Please enter an positive whole number!");
                } else {
                    tickFor(ticksAmount);
                }
            }
        }
    }

    @FXML
    /**
     * gets the current time from simulator, assigns the right day name to the day number and displays the date
     */
    private void getDate(){
        int[] time = sim.getTime();
        String day = null;
        switch (time[2]) {
            case 0 : {
                day = "Monday";
                break;
            }
            case 1 : {
                day = "Tuesday";
                break;
            }
            case 2 : {
                day = "Wednesday";
                break;
            }
            case 3 : {
                day = "Thursday";
                break;
            }
            case 4 : {
                day = "Friday";
                break;
            }
            case 5 : {
                day = "Saturday";
                break;
            }
            case 6 : {
                day = "Sunday";
                break;
            }
        }
        showDate("Week " + time[3] + "\nDay: " + day);
    }
    @FXML
    /**
     * gets the current date from the simulator and displays the hours and minutes in digital clock form
     */
    private void clock(){
        int time[] = sim.getTime();
        String hours;
        String minutes;
        if(time[1] < 10) {
            hours = "0" + time[1];
        } else {
            hours = "" + time[1];
        }
        if(time[0] < 10) {
            minutes = "0" + time[0];
        } else {
            minutes = "" +  time[0];
        }
        clock.setText("CLOCK" + "\n" + hours + " " + minutes);

    }

    @FXML
    /**
     * Resets the simulation
     */
    private void reset() {
        // resets all parking spots to empty on click
        setText("I should be removing cars now.");
        sim.resetRevenue();
        sim.resetTime();
        _statsPie.reset();
        simView.reset();
        setText("All cars should be gone now");
        getDate();
        clock();
        getRevenue();
        button_operate6.setDisable(true);
    }

    @FXML
    /**
     * Gets the total revenue from the simulation and displays it
     */
    private void getRevenue(){
        showRevenue("The total revenue since the start is:\n€" + sim.getRevenue());
    }

    @FXML
    /**
     * Opens up a text dialog that displays program information to the user
     */
    private void showAbout() {
        //show about information
        setText("Parking Simulator is a program that lets city parking Groningen see how some changes to their Parking Garage might affect business.");
    }

    /**
     * sets the text in textTarget
     * @param txt:  A string that will be shown in the textTarget
     */
    private void setText(String txt) {
        textTarget.setText(txt);
    }

    /**
     * sets the text in the label date
     * @param t:    A string that will be shown in date
     */
    private void showDate(String t) {date.setText(t); }

    /**
     * sets the text in the label revenue
     * @param r:    A string that will be shown in revenue
     */
    private void showRevenue(String r) {revenue.setText(r);}

    /**
     * Disables or enables the buttons
     * @param doDisable:    A boolean that dictates to whether dis- or enable a button
     */
    private void disableButtons(boolean doDisable) {
        button_operate1.setDisable(doDisable);
        button_operate2.setDisable(doDisable);
        button_operate3.setDisable(doDisable);
        button_operate4.setDisable(doDisable);
        button_operate5.setDisable(!doDisable);
        button_operate6.setDisable(doDisable);
    }

    @FXML
    /**
     * Stops the current running simulation.
     */
    private void stop() {
        if (timeline != null) {
            timeline.stop();
            disableButtons(false);
        }
    }

    /**
     * updates the statistics graph
     */
    private void updateGraph() {
        _statsPie.update(
            simView.getNumberOfFloors() * simView.getNumberOfRows() * simView.getNumberOfPlaces(),
            simView.getNumberOfOpenSpots(),
            simView.getNumberOfPassHolders(),
            simView.getNumberOfAdHoc(),
            simView.getNumberOfCarsWithReservation()
        );

        _statsGraph.setData();
    }
}