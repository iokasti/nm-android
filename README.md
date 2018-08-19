# Network Monitor - Android application

Android application that monitors wireless and cellular networks.
The application scans for wireless networks in the area, and displays information for each one of them. 
Furthermore the application detects the cell that the device is connected to and provides several details about it, including its position on Google maps.
It also provides information about and location of neighboring cells. 
To obtain each cell’s position and further information, the application uses a database containing all the necessary data. 

The excel file which contains the data is automatically downloaded from opencellid.org and stored on a server (https://github.com/iokasti/nm-ocd-daemon).

The application gets the data from the database using a RESTful API (https://github.com/iokasti/nm-flask-rest).
