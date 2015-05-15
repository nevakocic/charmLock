/*  started with SimpleSerial example by Tom Igoe and Robyn Overstreet
*/

var app = {
    itemName:"",
    macAddress: "",  // get your mac address from bluetoothSerial.listPorts()
    chars: "",
    //for big Button touch events
    signatureToCheck: false,
    startTimer: false,
    isValid: undefined,
    repeats: 4,   //how many times the signature was repeated for training
    //not using these yet
    startTime: undefined,
    endTime: undefined,
    gbMove: false,
    gbStillTouching: false,
    timerID: undefined,
  
/*
    Application constructor
 */
    initialize: function() {

        this.bindEvents();
        //hide everything except the intro page
        startPage.hidden = true;
        editPage.hidden = true;
        trainingPage.hidden = true;
      
        connectedPage.hidden = true; 
        unlockingPage.hidden = true; 
        validatingPage.hidden =true;
        validPage.hidden = true; 
        invalidPage.hidden = true;

        console.log("Starting BT Serial app");
    },
/*
    bind any events that are required on startup to listeners:

    //For my notes: this is the listeners that connects the html to the java
*/
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
        document.addEventListener("resume", this.onResume, false);

        //events for buttons
        showIntro.addEventListener('touchend', app.showIntroPage, false)
       
        homeButton.addEventListener('touchend', app.goHome, false); 
        buttonLock.addEventListener('touchend', app.send1, false);
        buttonUnlock.addEventListener('touchend', app.send0, false);
        showStart.addEventListener('touchend', app.showStartPage, false);
        showEdit.addEventListener('touchend', app.showEditPage, false);
        showTrain.addEventListener('touchend', app.showTrainPage, false);
        showConnected.addEventListener('touchend', app.showConnectedPage, false);
        showUnlocking.addEventListener('touchend', app.showUnlockingPage, false);
        backUnlock.addEventListener('touchend', app.goHome, false);
        bigButton.addEventListener('touchstart', app.unlockingButton, false);
        //////
        //bigButton.addEventListener('touchstart', app.startRecording, false);
        //bigButton.addEventListener('touchend',function(){app.stopRecording(true);}, false);
        //bigTrainButton.addEventListener('touchstart', app.startRecording, false);
        //bigTrainButton.addEventListener('touchend', function(){app.stopRecording(false);}, false);
        //////
        validPageButton.addEventListener('touchend', app.showValidPage, false);
        showValidating.addEventListener('touchend', app.showValidatingPage, false);
        invalidPageButton.addEventListener('touchend', app.showInvalidPage, false);
        bigInvalidButton.addEventListener('touchend', app.showUnlockingPage, false);
        
        confirmButton.addEventListener('touchend', app.confirmed, false);
        validButton.addEventListener('touchend', app.tryAgain, false);

        //back buttons
        backToUnlocking.addEventListener('touchend', app.showUnlockingPage, false);
        backValid.addEventListener('touchend', app.showUnlockingPage, false);
        backInvalid.addEventListener('touchend', app.goHome, false);
        exitEdit.addEventListener('touchend', app.goHome, false);
        backTrain.addEventListener('touchend', app.showEditPage, false);

    },  

/*
    this runs when the device is ready for user interaction:
*/
    onDeviceReady: function() {
        // check to see if Bluetooth is turned on.         
        var address;
     
        console.log('device ready!');
 
        //if isEnabled(), below, returns success:
        var listPorts = function() {
                app.clear();
                app.display("Searching for BT Serial devices...");
            // list the available BT ports:
            bluetoothSerial.list(
                function(results) {
                    //app.display(JSON.stringify(results)); //to much txt
                   // app.display(results);         //hide for now
   
                    // result is an array of JSON objects. 
                    // iterate over it and pull out relevant elements.
                    // on iOS, address is called uuid. On Android it's called address:                  
                    for (i=0; i<results.length; i++) {
                            if (results[i].uuid) {
                                address = results[i].uuid;
                            }
                    if (results[i].address) {
                                address = results[i].address;
                            }
                

                        //make separate buttons
                        var blName = results[i].name;
                        //append to start page and edit page 
                        app.createButtons(blName, address, "startButtons", app.connect);
                        //these guys on edit page will fail to connect because of address
                        app.createButtons(blName, "e_"+ address, "editButtons", app.blah);
    

                    }//end if address

                    if (results.length == 0) {
                        app.display("No BT Serial devices found");
                    }
                  
                    //add one edit button to Edit page
                    app.createButtons('set signature', 'train', "editButtons");
                    var trainButton = document.getElementById("train");
                    train.setAttribute( "class", "btn btn-success col-xs-6 actions .green");
                },

                function(error) {
                    app.display(JSON.stringify(error));
                }
            );    
        
            //show start page
            setTimeout(app.showStartPage, 4000);

        };

        // if isEnabled returns failure, this function is called:
        var notEnabled = function() {
            app.display("Bluetooth is not enabled.");
        };

         // check if Bluetooth is on:
        bluetoothSerial.isEnabled(
            listPorts,
            notEnabled
        );

        //on every start set is signature valida on false
        AirSigGUIPlugin.resetSignatureValid(function(result){console.log(result);},function(result){console.log(result);},'');
    },

    onResume:function()
    {
        console.log("unlockingPage: "+ unlockingPage.hidden);
        //if unlockingPage is visible then do something
        if(!unlockingPage.hidden)
        {
            app.validating();
        }
    },
  
    createButtons: function(getName, getAddress, towhatDiv, doWhat){
        var btn = document.createElement("BUTTON");        // Create a <button> element
        var t = document.createTextNode(getName);       // Create a text node
        btn.appendChild(t); 
        btn.id = getAddress;
        btn.name = getName;
        btn.setAttribute( "class", "btn btn-success col-xs-6 actions yellow");

        btn.onclick = function() { 
            app.itemName = this.name; 
            app.macAddress = this.id;
            app.clear();
            app.display("selected is: " + app.macAddress);
            //alert("macAddress is: " + app.macAddress);         //ok
       
            //also when connects, change the item name for header for connected page
            var connectedHeaderMiddle = document.getElementById("myItemCon");
            //we already have a name using for button
            connectedHeaderMiddle.innerHTML = app.itemName; 

            //connect.. or edit/train
            doWhat();
          
        };
      

        var toDiv = document.getElementById(towhatDiv);  
        toDiv.appendChild(btn);          // Append <button> to <div>

    },


    appendtoDiv: function(whichButton, whatDiv){
        //append these made buttons to start page div:
        var toDiv = document.getElementById(whatDiv);  
        toDiv.appendChild(whichButton);          // Append <button> to <div>

    },

     connect: function () {
        app.display("disconnecting from others.. ")
        //first disconnect from previous if it was connected
        app.disconnect();
        // if not connected, do this:
        // clear the screen and display an attempt to connect
        console.log("Attempting to connect...");

        app.clear();
        app.display("Attempting to connect. " +
            "Make sure the serial port is open on the target device.");
        // attempt to connect:
        bluetoothSerial.connect(
            app.macAddress,  // device to connect to
            app.openPort,    // start listening if you succeed
            app.showError    // show the error if you fail
        );
    },

    disconnect: function () {
        app.display("attempting to disconnect");
        // if connected, do this:
        bluetoothSerial.disconnect(
            app.closePort,     // stop listening to the port
            app.showError      // show the error if you fail
        );
    },

    goHome: function(){
        app.clear();
        app.display("attempting to disconnect");
        // if connected, do this:
        bluetoothSerial.disconnect(
            app.closePort,     // stop listening to the port
            app.showError      // show the error if you fail
        );
        //go to home page
        app.showStartPage();

    },

/*
    subscribes to a Bluetooth serial listener for newline
    and changes the button:
*/
    openPort: function() {
        // if you get a good Bluetooth serial connection:
        app.display("Connected to: " + app.itemName);
        // change the button's name:
       // connectButton.innerHTML = "Disconnect";
        // set up a listener to listen for newlines
        // and display any new data that's come in since
        // the last newline:
        bluetoothSerial.subscribe('\r\n', function (data) {
               app.clear();
               app.display(data);          
        });

        //when it's connected, switch pages
        app.showConnectedPage();
    },

/*
    unsubscribes from any Bluetooth serial listener and changes the button:
*/
    closePort: function() {
        // if you get a good Bluetooth serial connection:
        app.display("Disconnected");
        // change the button's name:
        //connectButton.innerHTML = "Connect";
        // unsubscribe from listening:
        bluetoothSerial.unsubscribe(
                function (data) {
                    app.display(data);
                },
                app.showError
        );

        //when disconnects, bring the start page
        app.showStartPage();
    },
    
/* 
    send to serial, talk to magnet
*/
    send1: function(){
        //lock
        bluetoothSerial.write('1', function() {
            app.clear();
            app.display(" sending 1");           
        });

    },
    //unlock
    send0: function(){
        bluetoothSerial.write('0', function() {
            app.clear();
            app.display(" sending 0");           
        });

    },

    //when pressing confirmed button, notifying user
    confirmed:function(){
        //show on pressed button
        app.changeButton("confirmButton", " ; )", "#2980b9");
        //give it a sec and go home
        setTimeout(app.goHome, 900);

    },

    tryAgain: function(){
        
        //show it on pressed button .orange?
        app.changeButton("validButton", "trying to unlock..", "#f39c12");
        //send command for unlocking again
        app.send0();
        //give it a sec and reset the button
        setTimeout(function(){ app.changeButton("validButton", "Didn't unlock? Tap!", "#f39c12"); },  2000);
    },

/*  
    display messages
*/

    //appends @error to the message div:
    showError: function(error) {
        app.display(error);
    },

    //appends @message to the message div:
    display: function(message) {
        var display = document.getElementById("message"), // the message div
            lineBreak = document.createElement("br"),     // a line break
            label = document.createTextNode(message);     // create the label

        display.appendChild(lineBreak);          // add a line break
        display.appendChild(label);              // add the message node
    },

    //clears the message div:
    clear: function() {
        var display = document.getElementById("message");
        display.innerHTML = "";
    },

/*
    here should TRAIN the airSignature
*/

    saving: function(){
        alert("saving signature");
        // recording signature 4 times?

        // for (i=0; i< 5 ; i++) {
                           
        //     repeated++
        //     console.log('repeated: ' + repeated);
        // }
        
    },

///*
//    here should start recording the airSignature
//*/
//    startRecording: function(event){
//
//        //its bugy without prevetDefault()
//        event.preventDefault();
//        // startTime = new Date().getTime();
//
//        //when starts recording, change the button
//        app.changeButton("bigButton", "Recording..", "#c0392b");
//        app.changeButton("bigTrainButton", "Recording..", "#c0392b");
//
//        //start the timer
//        app.startTimer = true; //clearing things to me (for now)
//        //setTimeout(app.timing, 1);
//        //app.signatureToCheck = false;
//
////        AirSigGUIPlugin.startRecordSensor(
////            //success
////            function(result){app.display(result);},
////            //error
////            function(result){app.display("startRecordSensor: "+result);},
////            //user id
////            '');
//
//        //show show
//        app.clear();
//        app.display("recording started");
//        app.display("signature: " + app.signatureToCheck);
//        console.log('started recording');
//    },

    changeButton:function(buttonId, text, color){
        var thatButton = document.getElementById(buttonId);
        var textToDisplay = text;
        var thatButtonText = document.createTextNode(textToDisplay); // text node
        thatButton.innerHTML = "";
        thatButton.appendChild(thatButtonText); //append textNode to btn
        //recordingButton.setAttribute( "class", "tan"); //me perke no?
        //change the button color to some red
        thatButton.style.backgroundColor = color;
    },

///*
//    stop recording for airSignature
//*/
//    stopRecording:function(shouldValidate){
//
//        //change the button look - css wont' work ?
//        app.changeButton("bigButton", "Signature recorded", "#E74C3C");
//        app.changeButton("bigTrainButton", "Signature recorded, repeat again", "#E74C3C");
//
//        app.startTimer = false; //stop the timer
//
//        //show show
//        app.clear();
//        app.display("stoped recording");
//        console.log("stoped recording");
//
//        //now has signature to check, stop the timer, call validating function
//        app.signatureToCheck = true;
//        app.display("signature to check?- " + app.signatureToCheck);
//       // setTimeout(andWhat, 1000);
//        if(shouldValidate){
//            //alert('will validate');
//            setTimeout(app.validating, 1000);
//        }else{
////            AirSigGUIPlugin.completeRecordSensorToTrainAction(
////                    //success
////                    function(result){app.display(result);},
////                    //error
////                    function(result){app.display("completeRecordSensorToTrainAction: "+result);},
////                    //user id
////                    '');
//
//            //repeat 4 times // train
//            if(app.repeats>0){
//                app.repeats--;
//                app.clear();
//                app.display("repeat: " + app.repeats + " times");
//                console.log(' now repeats: '+ app.repeats);
//            } else{
//                app.changeButton("bigTrainButton", "Signature trained!", "#72ad75");
//                //notify and go back home - reset the value there
//                setTimeout(app.goHome, 1000);
//            }
//            //if stop in the middle of the process
//
//        }
//
//    },

    timing: function(){
        if(!app.signatureToCheck){
            //validate
            // app.validating();
            setTimeout(app.timing, 10); //get time in tenths ?
        }   else{
            //go train
            console.log(' repeat ' + repeate + "times");
        }
    },
/*
    here is where airSignature should check if it is valid or not
*/

    validating:function(){
        //show show
        //app.clear();
        app.display("validating signature ..\nhang on");

        // validate signature here 
        // call the AirSig and return true or false
         AirSigGUIPlugin.isSignatureValid(
            function(result){app.signatureValid();},
            function(result){
                app.display(result);
                app.signatureInvalid();
                }
            ,'');

        //or spit the error - connection?
        
    },


    signatureValid: function(){
        //alert(" signature valid");
        //notify, 
        //change the button look
        app.changeButton("bigButton", "unlocking your bike :)", "#2ecc71");

        //show show
        app.clear();
        app.display("signature valid :)");
        app.display("will unlock now");

        //send 0 to open the lock
        app.send0();

        //maybe confirm you got the bike?

        //reset the app

        //go to home page


    },

    signatureInvalid: function(){
        //alert('signature NOT valid');
        // alert and reset to try again
        app.display("Signature is not valid :(");
        app.changeButton("bigButton", "Validate signature", "#E74C3C");
    },

    goHome: function(){
        //disconnect from device
        app.disconnect();
        //show start page
        app.showStartPage();
        app.clear();

        //reset possibly pressed buttons to staring state
        app.changeButton("confirmButton", "GOT IT!", "#337ab7");
        app.changeButton("validButton", "Didn't unlock? Tap!", "#f39c12");
        app.changeButton("bigTrainButton", "Signature recorded, repeat again", "#E74C3C");
        app.repeats = 4;

    },

    unlockingButton:function()
    {
        AirSigGUIPlugin.isSignatureValid(
                function(result){app.signatureValid();},
                function(result){ AirSigGUIPlugin.showVerifyActivity('');}
                ,'');
    },

    prepareUnlockingPage:function()
    {
        AirSigGUIPlugin.isSignatureValid(
                function(result){app.changeButton("bigButton", "Tap to unlock", "#337ab7");},
                function(result){app.changeButton("bigButton", "Validate signature", "#E74C3C");}
                ,'');
    },

/*
    switching pages:
*/  
    showIntroPage:function(){
        introPage.hidden = false;   //show
        startPage.hidden = true;

        editPage.hidden = true;
        trainingPage.hidden = true;

        connectedPage.hidden = true;
        unlockingPage.hidden = true;

        validatingPage.hidden = true; 
        validPage.hidden = true;
        invalidPage.hidden = true; 

    },

    showStartPage: function() {
        introPage.hidden = true;
        startPage.hidden = false; //show just this one

        editPage.hidden = true;  
        trainingPage.hidden = true;

        connectedPage.hidden = true;
        unlockingPage.hidden = true;

        validatingPage.hidden = true; 
        validPage.hidden = true;
        invalidPage.hidden = true; 
        //reset this, sometimes just jumps to startPage
        app.repeats = 4;

    },

    showEditPage:function(){
        introPage.hidden = true;
        startPage.hidden = true;

        editPage.hidden = false;    //show
        trainingPage.hidden = true;

        connectedPage.hidden = true;
        unlockingPage.hidden = true;   

        validatingPage.hidden = true; 
        validPage.hidden = true; 
        invalidPage.hidden = true;
        //reset this
        app.repeats = 4;

    },

    showTrainPage:function(){
        introPage.hidden = true;
        startPage.hidden = true;

        editPage.hidden = true;
        //trainingPage.hidden = false; //show
        AirSigGUIPlugin.showTrainingActivity(''); //show training page

        connectedPage.hidden = true;  
        unlockingPage.hidden = true;

        validatingPage.hidden = true; 
        validPage.hidden = true;
        invalidPage.hidden = true; 
    },

    showConnectedPage: function() {
        introPage.hidden = true;
        startPage.hidden = true;

        editPage.hidden = true;
        trainingPage.hidden = true;

        connectedPage.hidden = false;  //show
        unlockingPage.hidden = true;

        validatingPage.hidden = true; 
        validPage.hidden = true;
        invalidPage.hidden = true; 
    },

    showUnlockingPage: function(){
        introPage.hidden = true;
        startPage.hidden = true;

        editPage.hidden = true;
        trainingPage.hidden = true;

        connectedPage.hidden = true;
        unlockingPage.hidden = false;   //show

        validatingPage.hidden = true; 
        validPage.hidden = true; 
        invalidPage.hidden = true;

        app.prepareUnlockingPage();
    },

    showValidatingPage: function(){
        introPage.hidden = true;
        startPage.hidden = true;

        editPage.hidden = true;
        trainingPage.hidden = true;

        connectedPage.hidden = true;
        unlockingPage.hidden = true;   

        //validatingPage.hidden = false; //show
        AirSigGUIPlugin.showVerifyActivity('');

        validPage.hidden = true; 
        invalidPage.hidden = true;

    },

       showValidPage: function(){
        introPage.hidden = true;
        startPage.hidden = true;

        editPage.hidden = true;
        trainingPage.hidden = true;

        connectedPage.hidden = true;
        unlockingPage.hidden = true;

        validatingPage.hidden = true; 
        validPage.hidden = false; //show
        invalidPage.hidden = true;
    },

       showInvalidPage: function(){
        introPage.hidden = true;
        startPage.hidden = true;

        editPage.hidden = true;
        trainingPage.hidden = true;

        connectedPage.hidden = true;
        unlockingPage.hidden = true;

        validatingPage.hidden = true; 
        validPage.hidden = true; 
        invalidPage.hidden = false; //show
    }


};// end of app

