	
	var htmlTimer= document.getElementById('timer)');
    var seconds= 0; 
    var tenths= 0;
    var t= undefined;

var stopwatch = {	

    //constructor
    initialize: function() {
        this.bindEvents();
        console.log("initializing stopwatch :)");
    },

    bindEvents: function() {
    	// document.addEventListener('deviceready', this.onDeviceReady, false); //runs onDeviceReady function whenever the device is ready (loaded)

    	startTime.addEventListener('mousedown', stopwatch.startWatch, false); 
    	stopTime.addEventListener('mousedown', stopwatch.stopWatch, false); 
    	clearTime.addEventListener('mousedown', stopwatch.clearWatch, false); 
    },


    startWatch: function(){
    	console.log(" startint the timer: " + t);
    	// keep on counting time every..
    	t = setTimeout(stopwatch.addTime, 10);

    },

    addTime: function(){
    	//change the HTML
    	tenths++;
	    if (tenths >= 100) {
	        tenths = 0;
	        seconds++;
	    }
	    htmlTimer.textContent = (seconds ? (seconds > 9 ? seconds : "0" + seconds) : "00") + ":" + (tenths > 9 ? tenths : "0" + tenths);
	    //call the timer again? khm....
	    stopwatch.startWatch();
    },

    stopWatch: function(){
    	console.log("will stop timing now: " + t);
    	clearTimeout(t);
    }, 

    clearWatch: function(){
    	console.log("will clear the timer: " +t);
    	//clear the html and 
    	htmlTimer.textContent = "00:00";
	    tenths = 0; seconds = 0; 
	    t = 0;
	    console.log("timer is: " +t);
    }

}// end of stopwatch 'class'