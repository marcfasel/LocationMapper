#Location Mapper
##Introduction
Location Mapper is two application working together to visualise location data accuracy. The motivation is to get an in-depth view of the accuracy of the location data as reported from GPS, network provider, and proximity alerts. 

The main part is an Android application that records location data from GPS and network location provider as well as proximity alerts to a SQLite database. The data can then be emailed as a CSV file. The CSV file contains the latitude, longitude, accuracy, provider name, and time stamp for each location.

The second part is an HTML page to draw the data onto a Google Map. Selectboxes for GPS provider, network provider, and proximity alerts narrow down then data.  Each location is drawn on the map with a circle around it denoting the accuracy, and a label for the time stamp.  

##Android Application
The Android application records location data from GPS and network provider  as well as proximity alerts in a database. It is set up as a service and runs in the background. 
A list of the data recorded can be seen when the application runs in the foreground.

![alt Android Location Mapper Locations List](http://github.com/marcfasel/LocationMapper/raw/master/LocationMapperLocations.png "Android Location Mapper Locations List")

###Settings
The application can be configured via the settings dialog.
 
![alt Android Location Mapper Settings](http://github.com/marcfasel/LocationMapper/raw/master/LocationMapperSettings.png "Android Location Mapper Settings")

For GPS- and network location providers the update interval and the update distance can be set.

For proximity alerts a location can be stored, and a proximity radius can be set.

To visualise the data the email export sends the data as a CSV file. This CSV data can be pasted into the Google Maps application for viewing. 

The settings dialog also has a button to clear the database.
##Google Maps Application
The folder html/ contains the Google Maps app RenderLocation.html.

<center><img src="http://github.com/marcfasel/LocationMapper/raw/master/GoogleMapsLocationMapper.png"></center>
<center>Google Maps Location Mapper</center>


On the page the CSV data emailed previously can be pasted into the texture. Select from network, GPS, or proximity alert data, then press Render to view the data drawn on the map.  