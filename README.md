# Jason

## Intro
This is a small java project that processes log files.

The project is nowhere near finished, but the bare bones of an architecture are emerging.

## Design
To be able to process very large files in a mult-threaded way, I came up with the following design:
There is a main JSONLoader class that consumes the log file and then sends it line-by-line to a Load Balancer method. The Load Balancer 
will then send each event line to an instance of a JSONProcessor running in it's own thread. At the moment, the multi-threading hasn't 
been written, but the architecture is there to support it.

When the JSONProcessor receives a log event, it tries to match the incoming event with it's partner 'STARTED' or 'FINISHED' event. If no 
matching event is found, then it records the event to the *raw_event* DB table, in anticipation that it's matching event will be 
processed later. If it does find a matching eventy with the same 'id', then it subtracts one timestamp from the other to calculate the 
duration. Once the calculation is done, the processed event is written to the *processed_event* db table with the relevant 'alert' field 
set to true if the duration is > 4ms.

## Future Steps
I foresee the design evolving as follows:
* The very next step is to make the JSONProcessor class a Runnable, so that we can support multi-threading. The Load Balancer method will 
be changed to be able to send each event to a currently-free JSONProcessor instance.

* In the future, I foresee that the JSONProcessor could be extracted out of the project entirely and turned into it's own microservice. 
This can be accomplished by making the Load Balancer communicate with the JSONProcessor by REST calls. We could then theoritically have 
the JSONProcessor instances running in AWS EC2 or Lambda, and make them scale dynamically depending on the incoming load.

## Lessons Learned
* I didn't accomplish the task in time, but I think I have the start of relatively solid design. The take-away is that I spent too much 
time thinking of the overall system instead of solving the problem at hand.
* There is a small amount of logging, but this needs to be cleaned-up/sanitised.
* There is only one automated test. It's an integration test instead of a unit test. It just runs the whole process from end-to-end.
* I wrote raw SQL queries instead of integrating an ORM framework like Hibernate. I think I made the right call given the time constraints, as I think setting up Hibernate would have taken time.
* I have Maven experience, but not a lot of Gradle, so the initial project setup took a little bit more time than I would have wanted. 

So the main take-away is that I had to prioritise what tasks to spend time on in order to have something worth submitting, so things like unit testing fell by the wayside. Hopefully I accomplished a coherent design that could be supported in the futue
